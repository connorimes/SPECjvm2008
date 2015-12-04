/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * Copyright (c) 1997,1998 Sun Microsystems, Inc. All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 */

package spec.benchmarks.compress;

import spec.harness.SpecJVMBenchmarkBase;
import spec.harness.results.BenchmarkResult;

public class Main extends SpecJVMBenchmarkBase {
    public static final Harness HB = new Harness();
    
    public static void main(String[] args) throws Exception {
        runSimple( Main.class, args );
    }
    
    public Main(BenchmarkResult bmResult, int threadId) {
        super(bmResult, threadId);
    }
    
    /**
     * Run this in single mode.
     * No multi threading for this benchmark right now.
     */
    public static String testType() {
        return MULTI;
    }
    
    public long runBenchmark() {
        return HB.inst_main(super.getThreadId());
        
    }
    
    public void harnessMain() {
        runBenchmark();
    }
    
    public void Main() {
        runBenchmark();
    }
    
    public static void setupBenchmark() {
        HB.prepareBuffers();
    }
}
