/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 */
package spec.harness;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Iterator;

import spec.harness.analyzer.AnalyzerResult;
import spec.harness.analyzer.AnalyzersRunner;
import spec.harness.results.BenchmarkResult;
import spec.harness.results.IterationResult;
import spec.harness.results.TestResult;
import spec.io.ValidityCheckOutputStream;

/**
 * This class is derived from java.lang.Thread. The benchmark is started in a
 * seperate thread. The benchmark implements a method harnessMain(). The run
 * method of the Programmer runner calls the harnessMain() of the class that it
 * dynamically creates, given its name.
 */
public class ProgramRunner extends Thread {
    
    private static final boolean DEBUG = false;
    
    // Singleton to be interrupted upon. 
    private static ProgramRunner myself = null;
    
    /**
     * Flag to set if progress should be printed, one dot per iteration...
     */
    private static boolean printProgress = false;
    
    /**
     * The test result
     */
    public BenchmarkResult bmResult;
    
    /**
     * The tenchmark threads.
     */
    public BenchmarkThread [] bmts;
    
    private boolean isStartup;
    
    // References to the static methods of the benchmark class.
    private Class bmClass = null;
    
    private Constructor bmConstructor = null;
    
    private Method bmSetupBenchmarkMethod = null;
    
    private Method bmSetupIterationMethod = null;
    
    private Method bmTearDownIterationMethod = null;
    
    private Method bmTearDownBenchmarkMethod = null;
    
    private Method bmTestTypeMethod = null;
    
    /**
     * Overloaded constructor for the class.
     *
     * @param result
     *            The Benchmark result where all results are stored. This also
     *            includes configuration for the run.
     */
    public ProgramRunner(BenchmarkResult result, int mode) {
        super("Program Runner for " + result.getLogicalName());        
        this.bmResult = result;
        if (mode == Launch.startupMode)
            this.isStartup = true;
        //this.isStartup = isStartup;
        
        myself = this;
        
        setupBenchmarkMethods();
    }
    
    public static void setPrintProgress(boolean printProgress) {
        ProgramRunner.printProgress = printProgress;
    }
    
    /**
     * The overloaded run method for the BenchmarkRunner. This method just set
     * the thread priority and calls the runBenchmark method
     */
    public void run() {
        boolean oldValue = Context.getVerify();
        if (isStartup) {
            Context.setVerify(false);
        }
        runBenchmark();
        if (isStartup) {
            Context.setVerify(oldValue);
        }
        if (DEBUG) {
            System.out.println("Benchmark result:");
            bmResult.toXml(System.out, 1);
        }
    }
    
    @SuppressWarnings(value={"unchecked"})
    private boolean setupBenchmarkMethods() {
        
        if (bmResult == null) {
            Context.getOut().println("The benchmark is not setup properly. There is no BenchmarkResult with configuration.");
            throw new StopBenchmarkException("The benchmark is not setup properly. There is no BenchmarkResult with configuration.");
        }
        
        String className = bmResult.getLogicalName();        
        if (className.startsWith(Constants.STARTUP_BNAME_PREFIX)) {
            bmResult.setRunName(Constants.STARTUP_BNAME_PREFIX);
        }
        
        if (Util.isScimarkAndNotMonteCarlo(className)) {
            if (!(className.indexOf(".") == className.lastIndexOf(".")))
                className = className.substring(0, className.lastIndexOf("."));
                bmResult.setRunName(className);
        }
        
        if (className == null) {
            String message = "No class selected in runBenchmark. Aborting.";
            Context.getOut().println(message);
            bmResult.addError(message);
            return false;
        }
        
        String fullName = "spec.benchmarks." + bmResult.getRunName() + ".Main";
        
        try {        	
            bmClass = Class.forName(fullName);            
            if (bmClass == null) {
                String message = "Could not load class " + fullName + ". Aborting.";
                Context.getOut().println(message);
                bmResult.addError(message);
            }
             
            bmConstructor = bmClass.getConstructor(new Class[] { BenchmarkResult.class, int.class });
            bmSetupBenchmarkMethod = bmClass.getMethod("setupBenchmark", new Class[] {});
            bmSetupIterationMethod = bmClass.getMethod("setupIteration", new Class[] {});
            bmTearDownIterationMethod = bmClass.getMethod("tearDownIteration", new Class[] {});
            bmTearDownBenchmarkMethod = bmClass.getMethod("tearDownBenchmark", new Class[] {});
            bmTestTypeMethod = bmClass.getMethod("testType", new Class[] {});            
            return true;
        } catch (Exception e) {
        	
            throw new StopBenchmarkException("Error setting up Benchmark Class.", e);
        }
    }
    
    private SpecJVMBenchmarkBase createBmInstance(BenchmarkResult br, int threadid) {
        try {
            if (Util.isScimarkAndNotMonteCarlo(br.getLogicalName())) {
                String name = br.getLogicalName();
                if (!(name.indexOf(".") == name.lastIndexOf("."))) {
                    name = name.substring(0, name.lastIndexOf("."));
                    br.setRunName(name);
                }
            }
            Object[] args = { br, new Integer(threadid) };
            return (SpecJVMBenchmarkBase) bmConstructor.newInstance(args);
        } catch (Exception e) {
            throw new StopBenchmarkException("Error creating benchmark instance for " + bmClass.getName(), e);
        }
    }
    
    private String invokeBmTestTypeMethod() {
        try {
            return (String) bmTestTypeMethod.invoke(null, new Object[] {});
        } catch (Exception e) {
            throw new StopBenchmarkException("Error invoking bmTestTypeMethod", e);
        }
    }
    
    private void invokeBmSetupBenchmark() {
        try {
            bmSetupBenchmarkMethod.invoke(null, new Object[] {});
        } catch (Exception e) {
            throw new StopBenchmarkException("Error invoking bmSetupBenchmarkMethod", e);
        }
    }
    
    private void invokeBmSetupIteration() {
        try {
            bmSetupIterationMethod.invoke(null, new Object[] {});
        } catch (Exception e) {
            throw new StopBenchmarkException("Error invoking bmSetupIterationMethod", e);
        }
    }
    
    private void invokeBmTearDownIteration() {
        try {
            bmTearDownIterationMethod.invoke(null, new Object[] {});
        } catch (Exception e) {
            throw new StopBenchmarkException("Error invoking bmTearDownIterationMethod", e);
        }
    }
    
    private void invokeBmTearDownBenchmark() {
        try {
            bmTearDownBenchmarkMethod.invoke(null, new Object[] {});
        } catch (Exception e) {
            throw new StopBenchmarkException("Error invoking bmTearDownBenchmarkMethod", e);
        }
    }
    
    /**
     * This function loads the instance of the class given the benchmark name.
     * It calls the harnessMain method of the class instance, the start time is
     * noted before calling the harnessMain method . If the benchmark runs
     * successfully, the benchmark run parameters are returned. The system
     * garbage collector is also called after finishing the benchmark run
     *
     * It updates the BenchmarkResult with info.
     */
    private boolean runBenchmark() {
        boolean valid = true;
        
        if (bmResult == null) {
            throw new StopBenchmarkException("The benchmark is not setup properly. There is no BenchmarkResult with configuration.");
        }
        
        try {
            int miniter = bmResult.getMinIter();
            int maxiter = bmResult.getMaxIter();
            
            String bmType = invokeBmTestTypeMethod();
            bmResult.setBmType(bmType);
            String bmName = bmResult.getLogicalName();
            // Special case functional tests...
            if (bmType.equals(SpecJVMBenchmark.FUNCTIONAL)) {
                bmResult.setNumberBmThreads(1);
                bmResult.setRunMode(TestResult.ITER);
                bmResult.setNumberOfLoops(1);
                bmResult.setMinIter(1);
                bmResult.setMaxIter(1);
                bmResult.setNumberOfLoops(1);
                bmResult.setIterationTime(-1);
                bmResult.setWarmupTime(-1);
            }
            
            Context.getOut().println();
            Context.getOut().println("--- --- --- --- --- --- --- --- ---");
            Context.getOut().println();
            Context.getOut().println("  Benchmark:   " + bmResult.getLogicalName());
            Context.getOut().println("  Run mode:    " + TestResult.getRunModeDescription(bmResult.getRunMode()));
            Context.getOut().println("  Test type:   " + bmResult.getBmType());
            
            if (bmResult.getNumberBmThreads() > 0) {
                Context.getOut().println("  Threads:     " + bmResult.getNumberBmThreads());
            }
            if (bmResult.getWarmupTime() > 0) {
                Context.getOut().println("  Warmup:      " + TestResult.millisAsSec(bmResult.getWarmupTime()));
            }
            Context.getOut().println(
                    "  Iterations:  " + (miniter < maxiter ? "Auto run mode " + miniter + ".." + maxiter : (maxiter == -1 ? "infinite" : "" + maxiter)));
            Context.getOut().println("  Run length:  " + bmResult.durDesc());
            
            if (Util.getBoolProperty(Constants.CREATE_XML_REPORT_PROP, null)) {
                bmResult.headerToXml(Context.getXmlResultFile(), 2 * TestResult.TAB);
            }
            
            // Clear file cache from previous benchmarks.
            Context.getFileCache().clearCache();
            
            // Allow VM to clean heap, if specified.
            if (bmResult.getBenchmarkForcegc() || bmResult.getBenchmarkDelay() > 0) {
                Context.getOut().println();
                // Allow a stable state before start.
                if (bmResult.getBenchmarkForcegc()) {
                    Context.getOut().println("Requests gc before starting.");
                    System.gc();
                    System.runFinalization();
                }
                
                // Allow a period of rest before start.
                if (bmResult.getBenchmarkDelay() > 0) {
                    Context.getOut().println("Pause for " + TestResult.millisAsSec(bmResult.getBenchmarkDelay()));
                    pause(bmResult.getBenchmarkDelay());
                }
            }
            
            // Initiate things for the benchmark (if any).
            try {
                // Put validity file in cache
                if (Context.getVerify()) {
                    //String bmName = bmResult.getName();
                    if (Util.isScimarkAndNotMonteCarlo(bmName)) {
                        Context.getFileCache().loadFile(ValidityCheckOutputStream.getValidityFileName(bmName.substring(0, bmName.lastIndexOf("."))));
                    } else {
                        Context.getFileCache().loadFile(ValidityCheckOutputStream.getValidityFileName(bmName));
                    }
                }
                // Run potential setup method
                invokeBmSetupBenchmark();
            } catch (Throwable t) {
                String msg = "Error in setup of Benchmark.";;
                bmResult.addError(msg);
                Context.getOut().println(msg);
                t.printStackTrace(Context.getOut());
            }
            
            if (bmResult.getWarmupTime() > 0) {
                IterationResult itResult = new IterationResult();
                itResult.setBenchmarkResult(bmResult);
                itResult.setExpectedDuration(bmResult.getWarmupTime());
                itResult.setIteration(0);
                bmResult.setWarmupResult(itResult);
                
                valid = runIteration("Warmup (" + itResult.durDesc() + ")", itResult, bmResult);
                
                // Adjust run tme based on warmup results, if needed.
                if (!bmResult.getForcedIterationTime()) {
                    if (valid && itResult.getScore() < 5 / (((double) bmResult.getIterationTime()) / (1000 * 60))) {
                        long dur = Math.round(60 * 5d / itResult.getScore());
                        if (dur > bmResult.getIterationTime()) {
                            bmResult.setIterationTime(1000 * dur);
                            Context.getOut().println("Warning: Current run time for one iteration is too low.");
                            Context.getOut().println("Increasing iteration time to " + TestResult.millisAsSec(bmResult.getIterationTime()));
                        }
                    }
                }
            }
            
            if (Util.getBoolProperty(Constants.CREATE_XML_REPORT_PROP, null)) {
                bmResult.middleToXml(Context.getXmlResultFile(), 2 * TestResult.TAB);
            }
            
            // Initiate for automation run
            double preRes1 = Double.MIN_VALUE;
            double preRes2 = Double.MIN_VALUE;
            
            for (int iter = 1; valid && (maxiter == -1 || iter <= maxiter); iter++) {
                
                IterationResult itResult = new IterationResult();
                itResult.setBenchmarkResult(bmResult);
                itResult.setExpectedDuration(bmResult.getIterationTime());
                itResult.setExpectedLoops(bmResult.getNumberOfLoops());
                itResult.setIteration(iter);
                bmResult.addIterationResult(itResult);
                
                valid = runIteration("Iteration " + iter + " (" + itResult.durDesc() + ")", itResult, bmResult);
                
                // This is an autorun break
                // Check for run time threshold termination of sequence
                // If we haven't improved more than 1% compared to the two
                // previous one.
                // Time to call it a day.
                if (iter >= miniter) {
                    if (itResult.getScore() < 1.01d * preRes1 && itResult.getScore() < 1.01d * preRes2) {
                        // Context.getOut().println("\nScore is considered
                        // stable. Break.");
                        break;
                    }
                }
                
                preRes2 = preRes1;
                preRes1 = itResult.getScore();
                
                if (valid && iter + 1 <= maxiter) { // Skip pause after last
                    // iteration.
                    if (bmResult.getBenchmarkForcegc() || bmResult.getBenchmarkDelay() > 0) {
                        
                        Context.getOut().println();
                        
                        // Allow a stable state before start.
                        if (bmResult.getIterationForcegc()) {
                            Context.getOut().println("Requests gc between iterations.");
                            System.gc();
                            System.runFinalization(); /* DWM */
                        }
                        
                        // Allow a period of rest before start.
                        if (bmResult.getIterationDelay() > 0) {
                            Context.getOut().println("Pause for " + TestResult.millisAsSec(bmResult.getIterationDelay()));
                            sleep(bmResult.getIterationDelay());
                        }
                    }
                }
            }
            
            // Tear down things for the benchmark (if any).
            try {
                invokeBmTearDownBenchmark();
            } catch (Throwable t) {
                String msg = "Error in tear down of Benchmark.";;
                bmResult.addError(msg);
                Context.getOut().println(msg);
                t.printStackTrace(Context.getOut());
            }
            
        } catch (Throwable e) {
            String msg = "#### " + bmResult.getLogicalName() + " exited with exception: " + e.getClass().getName() + ": " + e.getMessage() + " ####\n";
            bmResult.addError(msg);
            Context.getOut().print(msg);
            e.printStackTrace(Context.getOut());
            Context.getOut().println("");
        }
        
        Context.getOut().println();
        valid = bmResult.isValid();
        if (!valid) {
            Context.getOut().println("Errors in benchmark: " + bmResult.getLogicalName());
            for (Iterator ei = bmResult.getAllErrors("").iterator(); ei.hasNext();) {
                Context.getOut().println("  " + (String) ei.next());
            }
        } else {
            Context.getOut().println("Valid run!");
        }
        
        if (Util.getBoolProperty(Constants.CREATE_XML_REPORT_PROP, null)) {
            bmResult.footerToXml(Context.getXmlResultFile(), 2 * TestResult.TAB);
        }
        
        if (!bmResult.getBmType().equals(SpecJVMBenchmark.FUNCTIONAL)) {
            Context.getOut().println("Score on " + bmResult.getLogicalName() + ": " + bmResult.resultString());
        }
        Context.getOut().println();
        
        return bmResult.isValid();
    }
    
    private boolean runIteration(String runDesc, IterationResult itResult, BenchmarkResult bmResult) {
        
        try {       
            
            Context.getOut().println();
            Context.getOut().println(runDesc + " begins: " + new Date());
            Context.getOut().flush();
            
            // Create analyzers and start analyzer thread
            AnalyzersRunner.setupAnalyzers(itResult, bmResult.getAnalyzerFreq());
                        
            // Setup things for the iteration (if any).
            try {
                invokeBmSetupIteration();
            } catch (Throwable t) {
                String msg = "Error in setup of iteration.";
                itResult.addError(msg);
                itResult.addError(getStackTraceAsString(t));
                throw new StopBenchmarkException(msg, t);
            }
            
            int nbmts = getNoBmHarnessThreads(bmResult);
            bmts = new SpecJVMBenchmarkBase[nbmts];
            for (int i = 0; i < bmts.length; i++) {
                // Setup things for the iteration (if any).
                try {
                    bmts[i] = createBmInstance(bmResult, i + 1);
                } catch (Throwable t) {
                    String msg = "Error in setup of iteration.";;
                    itResult.addError(msg);
                    itResult.addError(getStackTraceAsString(t));
                    throw new StopBenchmarkException(msg, t);
                }
                bmts[i].setItResult(itResult);
                bmts[i].setPrintProgress(printProgress);
            }
            
            // Flag analyzers that measurement period starts here
            AnalyzersRunner.invokeStartMeasurementIntervalForAnalyzers();
            
            long startTime = System.currentTimeMillis();
            long stopTime = startTime + itResult.getExpectedDuration();
            
            itResult.setStartTime(startTime);
            itResult.setStopTime(stopTime);
            
            for (int i = 0; i < bmts.length; i++) {
                bmts[i].start();
            }

            // And here all the work run in the "background"...

            if (bmResult.getRunMode() == TestResult.TIMED) {
				try {
					sleep(itResult.getExpectedDuration());
				} catch (InterruptedException e1) {
					String msg = "Harness interruped during measurement period.";
					itResult.addError(msg);

					// The expected cause is a failure in a benchmark thread.
					// Signal all (other?) bm threads to end as usual below.
					// Then gather them all.
				}

				// Record end of measurement interval
                itResult.setEndTime(itResult.getStopTime());
				endOfMeasurementInterval(System.currentTimeMillis());

				// Tell benchmark threads to call it a day
				itResult.abortRun();
			}
			
            // Then join with the benchmark threads
            for (int i = 0; i < bmts.length; i++) {
                try {
                    bmts[i].join();
                } catch (InterruptedException e) {
                    String msg = "Interrupted when joining benchmark thread " + i + ": " + e.getMessage();
                    itResult.addError(msg);
                    itResult.addError(getStackTraceAsString(e));
                    itResult.setEndTime(itResult.getStopTime());
                    throw new StopBenchmarkException(msg, e);
                }
            }
            
            if (printProgress) {
                Context.getOut().println("\n");
            }
            
            // End time is defined by when the first bm thread is done.
            // long endTime = System.currentTimeMillis();
            // itResult.setEndTime(endTime);
            
            // tear down things for the iteration (if any).
            try {
                invokeBmTearDownIteration();
            } catch (Throwable t) {
                String msg = "Error in tear down of iteration.";;
                itResult.addError(msg);
                itResult.addError(getStackTraceAsString(t));
                throw new StopBenchmarkException(msg, t);
            }
            
            AnalyzersRunner.tearDownAnalyzers(itResult);
        } catch (StopBenchmarkException sbe) {
            itResult.addError("Iteration failed.");
        }
        
        Context.getOut().println(runDesc + " ends:   " + new Date());
        Context.getOut().println(runDesc + " result: " + itResult.resultString(bmResult.getBmType()));
        for (Iterator<AnalyzerResult> iter = itResult.getAnalyzerSummaries(); iter.hasNext(); ) {
            Context.getOut().println(runDesc + " analyzer result " + iter.next().getDescription());
        }
        
        if (Util.getBoolProperty(Constants.CREATE_XML_REPORT_PROP, null)) {
            itResult.toXml(Context.getXmlResultFile(), 3 * BenchmarkResult.TAB);
        }
        
        return itResult.isValid();
    }
    
    public static String getStackTraceAsString(Throwable t) {
        StringBuilder sb = new StringBuilder(t.getClass().getName() + ": " + t.getMessage() + "\n");
        StackTraceElement[] ste = t.getStackTrace();
        for (int i = 0; i < ste.length; i++) {
            sb.append(ste[i].toString() + "\n");
        }
        return sb.toString();
    }
    
    public static int getNoBmHarnessThreads(BenchmarkResult bmResult) {
        int nbmts = 0;
        if (bmResult.getBmType().equals(SpecJVMBenchmark.MULTI)) {
            nbmts = bmResult.getNumberBmThreads();
        } else if (bmResult.getBmType().equals(SpecJVMBenchmark.SINGLE)) {
            nbmts = 1;
        } else if (bmResult.getBmType().equals(SpecJVMBenchmark.FUNCTIONAL)) {
            nbmts = 1;
        } else {
            throw new StopBenchmarkException("Unknown benchmark type: " + bmResult.getBmType());
        }
        return nbmts;
    }
    
    void pause(long pauseTime) {
        try {
            sleep(pauseTime);
        } catch (InterruptedException ie) {}
    }
    
    // Token to make sure not more than one thread tries to interrupt at the same time.
    private static Object token = new Object();
    
    // Flag that says if the ProgramRunner is interrupted or not.
    // This happens when one benchmark fails.
    // Don't want to be interrupted by each benchmark thread failing.
    private boolean isInterrupted = false;
    
    public static void interruptProgramRunner() {
		synchronized (token) {
			if (!myself.isInterrupted) {
				myself.isInterrupted = true;
				myself.interrupt();
			}
		}
	}

	public static void endOfMeasurementInterval(long iterEndTime) {
        AnalyzersRunner.invokeEndMeasurementIntervalForAnalyzers();
    }
}
