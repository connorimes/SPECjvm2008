/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * Copyright (c) 1997,1998 Sun Microsystems, Inc. All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 */
package spec.benchmarks.mpegaudio;

import spec.harness.Context;
import spec.harness.SpecJVMBenchmarkBase;
import spec.harness.results.BenchmarkResult;

public class Main extends SpecJVMBenchmarkBase {
    
    public static final String prefix = Context.getSpecBasePath() + "/resources/mpegaudio/input/";
    
    /** Run this in multi mode, next to each other. */
    public static String testType() {
        return MULTI;
    }
    
    static long runBenchmark(int btid) {
        long startTime = System.currentTimeMillis();
        try {
            new Harness().inst_main(btid);
        } catch (Exception e) {
            e.printStackTrace(Context.getOut());
        }
        return System.currentTimeMillis() - startTime;
    }
    
    public Main(BenchmarkResult bmResult, int threadId) {
        super(bmResult, threadId);
    }
    
    public static void main(String[] args) {
        runSimple(Main.class, args);
    }
    
    public void harnessMain() {
        runBenchmark(super.getThreadId());
    }
}
