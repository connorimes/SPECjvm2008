/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 */
package spec.harness;

import java.io.PrintStream;

import spec.harness.results.BenchmarkResult;
import spec.harness.results.IterationResult;
import spec.harness.results.LoopResult;
import spec.harness.results.TestResult;
import spec.io.ValidityCheckOutputStream;

public abstract class BenchmarkThread extends Thread {
    
    private BenchmarkResult bmResult;
    private boolean printProgress = true;
    private int btid;
    
    private IterationResult itResult = null;
    
    /**
     * SpecJava can be run as application to create the validity files. This
     * flags indicates whether SpecJVMClient98 is being run in the "Generating
     * Validity file mode"
     */
    public static boolean createValidityCheckFiles = false;
    
    BenchmarkThread(BenchmarkResult bmResult, int threadId) {
        super("BenchmarkThread " + bmResult.getLogicalName() + " " + threadId);
        this.bmResult = bmResult;
        this.btid = threadId;
        /*System.out.println( "New BenchmarkThread with benchmark of type " +
                        getClass().getName());*/
        createValidityCheckFiles = Util.getBoolProperty(Constants.GEN_VALIDITY_FILE_PROP, bmResult.getLogicalName());
    }
    
    public int getThreadId() {
        return btid;
    }
    
    public String[] getArgs() {
        return bmResult.getArgs();
    }
    
    public void setItResult(IterationResult itResult) {
        this.itResult = itResult;
    }
    
    public void setPrintProgress(boolean printProgress) {
        this.printProgress = printProgress;
    }
    
    public void run() {
        try {
            executeIteration();
        } catch (Throwable t) {
            System.err.print(t);
        }
    }
    public void setInvalidStartupResult(BenchmarkResult invalidBenchmarkResult, IterationResult invalidIterationResult){
        bmResult = invalidBenchmarkResult;
        itResult = invalidIterationResult;
    }
    
    public boolean executeIteration() {
        long lastLoopDoneTime = itResult.getStartTime();
        long lastLoopStartTime = itResult.getStartTime();
        boolean valid = true;
        int ops = 0;
        int bmts = ProgramRunner.getNoBmHarnessThreads(bmResult);
        
        while (itResult.continueToRun()) {

        	if (bmResult.getRunMode() == TestResult.ITER && ops >= itResult.getExpectedLoops()) {
        		break;
        	}
            
            LoopResult lResult = runLoop(itResult);
            lResult.setLoopCnt(++ops);
            itResult.addLoopResults(lResult);
            valid = lResult.isValid();
            lastLoopStartTime = lResult.getStartTime();
            lastLoopDoneTime = lResult.getEndTime();
            
            if (!valid) {
            	// Flag other benchmark threads to stop
            	itResult.abortRun();
            	
            	// Interrupt timer thread.
            	ProgramRunner.interruptProgramRunner();
            	
            	// And abort.
                break;
            }
            
            if (printProgress) {
                System.out.print(".");
            }
        }
        
        double dops = (double) ops;
        
        // Check in and say that this bm thread is done.
        boolean lastWorker = itResult.incNumberBmThreadsDone();
        if (lastWorker && bmResult.getRunMode() == TestResult.ITER) {
            itResult.setEndTime(lastLoopDoneTime);
        }
        long iterEndTime = itResult.getEndTime();
        
        if (bmResult.getRunMode() == TestResult.TIMED && iterEndTime < lastLoopDoneTime) {
            // Adjust how much of last operation actually counted.
            // Remove the part of the op that was not inside the measurement period.
            // Assume an ops work is done in equal amount all the time.
            // All bm threads will keep working until all is checked in,
            // so none should get better behavior in the end.
            double extra = ((double) (lastLoopDoneTime - iterEndTime)) / ((double) (lastLoopDoneTime - lastLoopStartTime));
            dops -= extra;
        }
        
        itResult.incOperations(dops);
        
        if (bmResult.getRunMode() == TestResult.ITER) {
            return valid;
        }
        
        // Keep running, so threads who finish their last iteration want be running alone.
        while (bmts > itResult.getNumberBmThreadsDone()) {
            LoopResult lResult = runLoop(itResult);
            // Don't add loopresults passed endtime.
            // itResult.addLoopResults(lResult);
            valid = lResult.isValid();
            
            if (!valid) {
                break;
            }
            if (printProgress) {
                System.out.print(".");
            }
        }
        return valid;
    }
    
	/**
     * Runs the benchmark one loop.
     */
    public LoopResult runLoop(IterationResult iResult) {
        
        LoopResult lResult = new LoopResult();
        lResult.setStartTime(System.currentTimeMillis());
        lResult.setBmThreadId(this.btid);
        lResult.setIteration(iResult.getIteration());
        
        try {
            
            ValidityCheckOutputStream vcos = null;
            java.io.PrintStream savedOutStream = null;
            
            // Trust but verify - Ronald Reagan
            if (Context.getVerify()) {
                savedOutStream = Context.getOut();
                vcos = new ValidityCheckOutputStream(bmResult.getRunName());
                Context.setOut(new PrintStream(vcos));
            }
            
            // This is the benchmark invocation...
            harnessMain();
            
            if (Context.getVerify()) {
                // spec.io.FileOutputStream.printCount(checksum);
                Context.getOut().close();
                Context.setOut(savedOutStream);
                
                if (createValidityCheckFiles) {
                    vcos.createValidityFile();
                    lResult.addError("Not valid, generate output file run only.");
                    return lResult;
                }
                
                if (Context.getVerify()) {
                    boolean pass = vcos.validityCheck(lResult);
                    
                    if (!pass) {
                    	// Known issue in apache xml transform library shipped with JDK 5.0 with several JVMs.
                    	if (iResult.getBenchmarkResult().getLogicalName().equals("xml.transform")) {
                    		if (System.getProperty("java.specification.version", "not_valid").equals("1.5")) {
                    			KnownIssues.isKnownIssueXmlTransformRace = true;
                    		}
                    	}
                    }
                }
            }
        } catch (Throwable t) {

        	if (t instanceof OutOfMemoryError) {
        		KnownIssues.isKnownIssueOutOfMemoryError = true;
			}
        	
            String msg = t.getClass().getName() + ": " + t.getMessage();
            lResult.addError(msg);
            Context.getOut().println(msg);
            t.printStackTrace(Context.getOut());
        }
        
        lResult.setEndTime(System.currentTimeMillis());
        
        return lResult;
    }
    
    /**
     * Starts the benchmark. Each benchmark implements this method. Given a
     * class name, the ProgramRunner class dynamically creates the instance of
     * the benchmark class and calls this method.
     *
     * @see spec.harness.ProgramRunner
     * @see spec.harness.BenchmarkThread
     */
    public abstract void harnessMain();
    
}
