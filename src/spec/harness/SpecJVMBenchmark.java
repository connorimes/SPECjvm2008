/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 */
package spec.harness;

public interface SpecJVMBenchmark {
    
    /**
     * Run type that tells the famework to only kickoff one instance of the benchmark.
     * This is expected to use, if the benchmark is a single threaded workload.
     * This is expected to use, if the benchmark handles multi threaded balancing itself.
     */
    public static String SINGLE = "single";
    
    /**
     * Run type that tells the famework to kickoff multiple benchmark instances.
     * This is expected to use, if the benchmark can and should be run with multiple
     * instances next to each other.
     */
    public static String MULTI = "multi";
    
    /**
     * Run type that tells the framework this is a functional test.
     * It will be run once with one single thread and report pass or fail - no score.
     * This is expected to use if it is a functional test to check it follow the spec,
     * nothing else.
     */
    public static String FUNCTIONAL = "functional";
    
    /**
     * Run type that tells the framework this is a stress test.
     * It will kick off multiple instances and run for the complete run time.
     * It will report PASS or FAILED.
     * This is expected to use if it is a functional test that stress the system
     * and the requirement is that it runs for a run time of time, without issues.
     */
    public static String STRESS = "stress";
    
    /**
     * Run type that tells the framework this isn't set.
     */
    public static String INVALID = "invalid";
    
}
