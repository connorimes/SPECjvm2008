/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 */

package spec.benchmarks.scimark.fft;

import spec.benchmarks.scimark.utils.kernel;
import spec.harness.SpecJVMBenchmarkBase;
import spec.harness.results.BenchmarkResult;

public class Main extends SpecJVMBenchmarkBase {
    
    /**
     * Run this in multi mode, next to each other.
     */
    public static String testType() {
        return MULTI;
    }
    
    static void runBenchmark() {
        //return new FFT().inst_main(args);
        // Loop a few times, to create some more work in each ops.
        for (int i = kernel.FFT_LOOPS; i > 0; i --) {
            FFT.main(i);
        }
    }
    
    public static void Main() {
        runBenchmark();
    }
    
    public void harnessMain() {
        runBenchmark();
    }
    
    public Main(BenchmarkResult bmResult, int threadId) {
        super(bmResult, threadId);
    }
    
    public static void setupBenchmark() {
        kernel.init();
    }
    
    public static void main(String[] args) throws Exception {
        runSimple( Main.class, args );
    }
}
