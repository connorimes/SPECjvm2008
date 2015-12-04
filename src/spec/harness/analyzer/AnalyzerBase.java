package spec.harness.analyzer;

import spec.harness.Context;
import spec.harness.results.IterationResult;
import spec.harness.results.TestResult;

public abstract class AnalyzerBase {
    
    private IterationResult ir = null;
    
    public AnalyzerBase() {
        ; // default constructor
    }
    
    /**
     * Execution method called at regular intervals for polling stats.
     * @param time when the call is made
     */
    public abstract void execute(long time);
    
    public final void setIterationResult(IterationResult iterationResult) {
        this.ir = iterationResult;
    }
    
    /**
     * Setup method for the analyzer class, which 
     * is called when an analyzer class is created.
     * Static initiation work can be done in this one.
     * This is done once for the whole benchmark suite.
     */
    public static void setupAnalyzerClass() {
        ; //Empty default implementation
    }

    /**
     * Setup method called when analyzer is created.
     * Initiation work should be done in this one.
     * This is done once per iteration, each time 
     * in a new Analyzer instance. 
     */
    public void setup() {
        ; //Empty default implementation
    }

    /**
     * This method is called at the start of the measurement interval.
     * It is called by the ProgramRunner thread.
     */
    public void startMeasurementInterval() {
        ; //Empty default implementation
    }

    /**
     * This method is called at the end of the measurement interval.
     * It is called by the ProgramRunner thread.
     */
    public void endMeasurementInterval() {
        ; //Empty default implementation
    }
    
    /**
     * Tear down method called when analyzer is brought down.
     * Reporting work should be done in this one.
     * This is done once per iteration, each time 
     * in a new Analyzer instance. 
     */
    public void tearDown() {
        ; //Empty default implementation
    }
    
    /**
     * Tear down method for the analyzer class, which 
     * is called when an analyzer class is created.
     * Static post-benchmarks-work can be done in this one.
     * This is done once for the whole benchmark suite.
     */
    public static void tearDownAnalyzerClass() {
        ; //Empty default implementation
    }

    public final void report(TYInfo tyi) {
        ir.addAnalyzer(tyi);
    }

    public final void report(AnalyzerResult aResult) {
        ir.addAnalyzerSummary(aResult);
    }
    
    protected boolean isTimedRun() {
        return ir.getBenchmarkResult().getRunMode() == TestResult.TIMED;
    }
    
    protected long getBenchmarkDuration() {
        return ir.getDuration();
    }

    protected String getBenchmarkName() {
        return ir.getBenchmarkResult().getLogicalName();
    }

    protected long getNoOps() {
        return ir.getLoopResults().size();
    }

    protected void addError(String msg) {
        ir.addError(msg);
    }

    protected static void addErrorToSuiteResult(String msg) {
        Context.getSuiteResult().addError(msg);
    }
    protected static void addViolationToSuiteResult(String msg) {
        Context.getSuiteResult().addViolation(msg);
    }
}
