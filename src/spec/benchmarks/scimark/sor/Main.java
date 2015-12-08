/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 */

package spec.benchmarks.scimark.sor;

import edu.uchicago.cs.heprofiler.HEProfiler;
import edu.uchicago.cs.heprofiler.HEProfilerEvent;
import edu.uchicago.cs.heprofiler.HEProfilerEventFactory;

import spec.benchmarks.scimark.utils.kernel;
import spec.harness.SpecJVMBenchmarkBase;
import spec.harness.results.BenchmarkResult;

public class Main extends SpecJVMBenchmarkBase {
    
    /** Run this in multi mode, next to each other. */
    public static String testType() {
        return MULTI;
    }
    
    static void runBenchmark() {
        HEProfilerEvent event = HEProfilerEventFactory.createHEProfilerEvent(true);
        for (int i = kernel.SOR_LOOPS; i > 0; i --) {
            SOR.main(i);
            event.eventEndBegin(Profiler.SOR, i);
        }
        event.dispose();
    }
    
    public static void Main() {
        runBenchmark();
    }
    
    public void harnessMain() {
        runBenchmark();
    }
    
    public static void setupBenchmark() {
        HEProfiler.init(Profiler.class, Profiler.APPLICATION, 20, "SOR", null);
        kernel.init();
    }

    public static void tearDownBenchmark() {
        HEProfiler.dispose();
    }
    
    public Main(BenchmarkResult bmResult, int threadId) {
        super(bmResult, threadId);
    }

    public static void main(String[] args) throws Exception {
        runSimple( Main.class, args );
    }
    
}
