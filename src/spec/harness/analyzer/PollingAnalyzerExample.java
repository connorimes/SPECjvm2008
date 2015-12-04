/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 */
package spec.harness.analyzer;

/**
 * This Analyzer is an example of an Analyzer that is based on the polling mechanism
 * available in the SPECjvm2008 analyzer functionality.
 * 
 * The harness will call the method execute at a regular time interval.
 * The time interval is controlled by the property 
 * specjvm.benchmark.analyzer.frequency
 * which by default is 1 second.
 *
 * This Analyzer type is good to use when for example a resource should be 
 * monitored throughout the run.
 * 
 * In this example the number of operations done will be gathered.
 * It is a contrived example.
 */
public class PollingAnalyzerExample extends AnalyzerBase {
    
    private static String name = "Number of operations performed";
    private static String unit = "ops";
    
    public void execute(long time) {
        // Find out how many operations are done so far in this iteration.
        // This information is exposed via AnalazerBase
        long value = this.getNoOps();
        report(new NumberOfOperations(time, value));
    }
    
    public static class NumberOfOperations extends TYInfo {
        
        public NumberOfOperations(long time, long value) {
            super(time, value);
        }
        
        public String getName() {
            return PollingAnalyzerExample.name;
        }
        
        public String getUnit() {
            return PollingAnalyzerExample.unit;
        }
    }
}
