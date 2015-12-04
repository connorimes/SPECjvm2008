/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * Copyright (c) 1997,1998 Sun Microsystems, Inc. All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 */

package spec.benchmarks.helloworld;

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
        HelloWorld.main();
    }
    
    public static void main(String [] args) {
        runBenchmark();
    }
    
    public void harnessMain() {
        runBenchmark();
    }
    
    public Main(BenchmarkResult bmResult, int threadId) {
        super(bmResult, threadId);
    }
}