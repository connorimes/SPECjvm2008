/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 */
package spec.harness;

/**
 * This class mainly handles the the runtime exceptions caused during the
 * execution of the benchmarks. This is useful to have smooth exit from the
 * program, even the program produces unexpected problems.
 */
public class StopBenchmarkException extends RuntimeException {
    
    public StopBenchmarkException(String s) {
        super(s);
    }
    
    public StopBenchmarkException(String s, Throwable t) {
        super(s, t);
    }
}
