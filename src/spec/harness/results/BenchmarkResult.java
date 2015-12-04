/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 */
package spec.harness.results;

import java.util.List;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.LinkedList;

import spec.harness.Constants;
import spec.harness.StopBenchmarkException;

public class BenchmarkResult extends TestResult {
    private String name;
    private String runName;
    private String category;
    private String [] args;
    private int minIter = 1;
    private int maxIter = 1;
    private int numberBmThreads = 1;
    private long warmupTime = -1;
    private long iterationTime = -1;
    private boolean forcedIterationTime = false;
    private int numberOfLoops = -1;
    private long benchmarkDelay = -1;
    private boolean benchmarkForcegc = false;
    private long iterationDelay = -1;
    private boolean iterationForcegc = false;
    private boolean checksum = false;
    private int runMode = -1;
    private String bmType = null;
    private String analyzers;
    private long analyzerFreq;
    private IterationResult warmupResult = null;
    private List<IterationResult> iterationResults = new LinkedList<IterationResult>();
    
    public String getLogicalName() {
        return name;
    }
    
    public String getRunName() {
        return runName;
    }
    
    public void setNames(String name) {
        this.name = name;
        runName = name;
    }
    
    public void setRunName(String name) {
        runName = name;
    }
    
    public String [] getArgs() {
        return args;
    }
    public void setArgs(String [] args) {
        this.args = args;
    }
    public boolean doChecksum() {
        return checksum;
    }
    public void setChecksum(boolean checksum) {
        this.checksum = checksum;
    }
    public long getIterationDelay() {
        return iterationDelay;
    }
    public void setIterationDelay(long iterationDelay) {
        this.iterationDelay = iterationDelay;
    }
    public boolean getIterationForcegc() {
        return iterationForcegc;
    }
    public void setIterationForcegc(boolean iterationForcegc) {
        this.iterationForcegc = iterationForcegc;
    }
    public long getIterationTime() {
        return iterationTime;
    }
    
    public void setIterationTime(long iterationTime) {
        setIterationTime(iterationTime, false);
    }
    
    public void setIterationTime(long iterationTime, boolean forced) {
        this.iterationTime = iterationTime;
        this.forcedIterationTime = forced;
    }
    
    public boolean getForcedIterationTime() {
        return forcedIterationTime;
    }
    
    public long getWarmupTime() {
        return warmupTime;
    }
    public void setWarmupTime(long warmupTime) {
        this.warmupTime = warmupTime;
    }
    
    public int getMaxIter() {
        return maxIter;
    }
    public void setMaxIter(int maxIter) {
        this.maxIter = maxIter;
    }
    
    public int getMinIter() {
        return minIter;
    }
    public void setMinIter(int minIter) {
        this.minIter = minIter;
    }
    
    public int getNumberOfLoops() {
        return numberOfLoops;
    }
    
    public int getNumberBmThreads() {
        return numberBmThreads;
    }
    
    public void setNumberBmThreads(int numberBmThreads) {
        this.numberBmThreads = numberBmThreads;
    }
    
    public int getRunMode() {
        return runMode;
    }
    
    public void setRunMode(int runMode) {
        this.runMode = runMode;
    }
    
    public String getBmType() {
        return bmType;
    }
    public void setBmType(String bmType) {
        this.bmType = bmType;
    }
    
    public IterationResult getWarmupResult() {
        return warmupResult;
    }
    public void setWarmupResult(IterationResult warmupResult) {
        this.warmupResult = warmupResult;
    }
    
    public void setNumberOfLoops(int numberOfLoops) {
        this.numberOfLoops = numberOfLoops;
    }
    
    public long getBenchmarkDelay() {
        return benchmarkDelay;
    }
    public void setBenchmarkDelay(long benchmarkDelay) {
        this.benchmarkDelay = benchmarkDelay;
    }
    public boolean getBenchmarkForcegc() {
        return benchmarkForcegc;
    }
    public void setBenchmarkForcegc(boolean benchmarkForcegc) {
        this.benchmarkForcegc = benchmarkForcegc;
    }
    public List getIterationResults() {
        return iterationResults;
    }
    public void addIterationResult(IterationResult iterationResult) {
        this.iterationResults.add(iterationResult);
    }
    public String getAnalyzers() {
        return analyzers != null ? analyzers : "";
    }
    public void setAnalyzers(String analyzers) {
        this.analyzers = analyzers;
    }
    public long getAnalyzerFreq() {
        return analyzerFreq;
    }
    public void setAnalyzerFreq(long analyzerFreq) {
        this.analyzerFreq = analyzerFreq;
    }
    
    public String durDesc() {
        if (runMode == TestResult.TIMED) {
            return TestResult.millisAsSec(getIterationTime());
        } else if (runMode == TestResult.ITER) {
            if (getNumberOfLoops() <= 1) {
                return getNumberOfLoops() + " operation";
            } else {
                return getNumberOfLoops() + " operations";
            }
        } else {
            throw new StopBenchmarkException("Unknown run mode " + runMode);
        }
    }
    
    public double getScore() {
        double bScore = 0;
        for (Iterator iter = iterationResults.iterator(); iter.hasNext(); ) {
            IterationResult res = (IterationResult) iter.next();
            if (res.isValid()) {
                if (res.getScore() > bScore) {
                    bScore = res.getScore();
                }
            }
        }
        return bScore;
    }
    
    public String resultString() {
        // return doubleAsRes(getScore()) + " " + Constants.WORKLOAD_METRIC + (isValid() ? "" : " **NOT VALID**");
        if (isValid()) {
            return doubleAsRes(getScore()) + " " + Constants.WORKLOAD_METRIC;
        } else {
        	return " **NOT VALID**";
        }
    }
    
    public boolean isValid() {
        if (hasErrors()) {
            return false;
        }
        if (warmupResult != null && !warmupResult.isValid()) {
            return false;
        }
        for (Iterator iter = iterationResults.iterator(); iter.hasNext(); ) {
            IterationResult res = (IterationResult) iter.next();
            if (!res.isValid()) {
                return false;
            }
        }
        return true;
    }
    
    public List<String> getAllErrors(String tag) {
        List<String> tmp = new LinkedList<String>();
        if (errors != null) {
            for (Iterator eit = errors.iterator(); eit.hasNext();) {
                tmp.add(tag + " " + eit.next());
            }
        }
        if (warmupResult != null) {
            tmp.addAll(warmupResult.getAllErrors(tag + "[warmup]"));
        }
        for (Iterator iter = iterationResults.iterator(); iter.hasNext(); ) {
            IterationResult ir = (IterationResult) iter.next();
            tmp.addAll(ir.getAllErrors(tag + "[iter=" + ir.getIteration() + "]"));
        }
        return tmp;
    }
    
    public void headerToXml(PrintStream ps, int indent) {
        ptxElementStartOpen(ps, indent, Constants.BM_RESULT_ENAME, true);
        indent += TAB;
        ptxAttrib(ps, indent, Constants.NAME_ENAME, name, true, "\n");
        ptxAttrib(ps, indent, Constants.CATEGORY_ENAME, category, true, "\n");
        ptxAttrib(ps, indent, Constants.ARGS_ENAME, args, true, "\n");
        ptxAttrib(ps, indent, Constants.MIN_ITER_ENAME, minIter, true, "\n");
        ptxAttrib(ps, indent, Constants.MAX_ITER_ENAME, maxIter, true, "\n");
        ptxAttrib(ps, indent, Constants.NUM_BM_THREADS_ENAME, numberBmThreads, true, "\n");
        ptxAttrib(ps, indent, Constants.WARMUP_TIME_ENAME, warmupTime, warmupTime > 0, "\n");
        ptxAttrib(ps, indent, Constants.ITERATION_TIME_ENAME, millisAsSec(iterationTime), runMode == TestResult.TIMED, "\n");
        ptxAttrib(ps, indent, Constants.FORCED_ITERATION_TIME_ENAME, forcedIterationTime, runMode == TestResult.TIMED && forcedIterationTime, "\n");
        ptxAttrib(ps, indent, Constants.NUMBER_OF_LOOPS_ENAME, numberOfLoops, runMode == TestResult.ITER, "\n");
        ptxAttrib(ps, indent, Constants.BM_DELAY_ENAME, millisAsSec(benchmarkDelay), benchmarkDelay > 0, "\n");
        ptxAttrib(ps, indent, Constants.BENCHMARK_FORCE_GC_ENAME, benchmarkForcegc, benchmarkForcegc, "\n");
        ptxAttrib(ps, indent, Constants.ITER_DELAY_ENAME, millisAsSec(iterationDelay), iterationDelay > 0, "\n");
        ptxAttrib(ps, indent, Constants.ITERATION_FORCE_GC_ENAME, iterationForcegc, iterationForcegc, "\n");
        ptxAttrib(ps, indent, Constants.ANALYZERS_ENAME, analyzers, analyzers != null && analyzers.length() > 0, "\n");
        ptxAttrib(ps, indent, Constants.ANALYZER_FREQ_ENAME, millisAsSec(analyzerFreq), analyzers != null && analyzers.length() > 0, "\n");
        ptxAttrib(ps, indent, Constants.BM_TYPE_ENAME, bmType, true, "\n");
        ptxAttrib(ps, indent, Constants.RUN_MODE_ENAME, runMode, true, ">\n");
        ptxElementOpen(ps, indent, Constants.WARMUP_RESULT_ENAME);
    }
    
    public void toXml(PrintStream ps, int indent) {
        headerToXml(ps, indent);
        if (warmupResult != null) {
            warmupResult.toXml(ps, indent + TAB);
        }
        middleToXml(ps, indent);
        for (Iterator iter = iterationResults.iterator(); iter.hasNext();) {
            ((IterationResult) iter.next()).toXml(ps, indent + TAB);
        }
        footerToXml(ps, indent);
    }
    
    public void middleToXml(PrintStream ps, int indent) {
        ptxElementClose(ps, indent, Constants.WARMUP_RESULT_ENAME);
        ptxElementOpen(ps, indent, Constants.ITERATIONS_ENAME);
    }
       
    public void footerToXml(PrintStream ps, int indent) {
        ptxElementClose(ps, indent, Constants.ITERATIONS_ENAME);
        printErrorsToXml(ps, indent + TAB);
        ptxElementClose(ps, indent, Constants.BM_RESULT_ENAME);
    }
 }
