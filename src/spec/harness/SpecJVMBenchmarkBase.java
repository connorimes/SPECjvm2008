/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 */
package spec.harness;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import spec.harness.results.BenchmarkResult;
import spec.harness.results.TestResult;

public abstract class SpecJVMBenchmarkBase extends BenchmarkThread implements SpecJVMBenchmark {
    
    /**
     * This method is called before anything in the benchmark is started.
     * Inherit this, if you want to do any one time setup things, which then
     * is done for the complete benchmark outside the measurement period.
     *
     * @see spec.harness.BenchmarkThread
     */
    public static void setupBenchmark() {
        // Setup the benchmark.
        // Do nothing by default.
        //System.out.println("Base set up bm");
    }
    
    /**
     * This method is called before each iteration of the benchmark is started.
     * Inherit this, if you want to do any one time setup things, which then
     * is done before each iteration outside the measurement period.
     *
     * @see spec.harness.BenchmarkThread
     */
    public static void setupIteration() {
        // Setup the Iteation.
        // Do nothing by default.
        //System.out.println("Base set up iter");
    }
    
    protected SpecJVMBenchmarkBase(BenchmarkResult bmResult, int threadId) {
        super(bmResult, threadId);
    }
    
    /**
     * This method is called after each iteration in the benchmark is run.
     * Inherit this, if you want to do any one time tear down things,
     * which then is done outside the measurement period.
     *
     * @see spec.harness.BenchmarkThread
     */
    public static void tearDownIteration() {
        // Tear down the iteration.
        // Do nothing by default.
        //System.out.println("Base tear down iter");
    }
    
    /**
     * This method is called after everything in the benchmark is run.
     * Inherit this, if you want to do any one time tear down things,
     * which then is done outside the measurement period.
     *
     * @see spec.harness.BenchmarkThread
     */
    public static void tearDownBenchmark() {
        // Tear down the benchmark.
        // Do nothing by default.
        //System.out.println("Base tear down bm");
    }
    
    /**
     * Method that returns the run type of the bencmark, for example SINGLE or MULTI.
     */
    public static String testType() {
        return INVALID;
    }
    
    /**
     * Help method that executes a benchmark one iteration.
     * To be called from main() from benchmark Main classes.
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws IllegalArgumentException
     */
    @SuppressWarnings(value={"unchecked"})
    public static void runSimple(Class benchmarkClass, String [] args) {
        
        // Minimal initiate the launcher and context.
        if (!Launch.setupSimple(args)) {
            return;
        }
        
        try {
            BenchmarkResult bmResult = new BenchmarkResult();
            bmResult.setNames(benchmarkClass.getName());
            bmResult.setRunMode(TestResult.ITER);
            bmResult.setNumberOfLoops(1);
            
            // These args are used only by the 'startup' benchmark
            String[] bmArgs = new String[1];
            bmArgs[0]= Util.getProperty(Constants.BENCHMARKS_PROP, null);
            bmResult.setArgs(bmArgs);
            
            Class[] cArgs = { BenchmarkResult.class, int.class };
            Object[] inArgs = { bmResult, Integer.valueOf(1)};
            Constructor c = benchmarkClass.getConstructor(cArgs);
            
            Method setupBenchmarkMethod =
                    benchmarkClass.getMethod( "setupBenchmark", new Class[]{});
            Method setupIterationMethod =
                    benchmarkClass.getMethod( "setupIteration", new Class[]{});
            Method tearDownBenchmarkMethod =
                    benchmarkClass.getMethod( "tearDownBenchmark", new Class[]{});
            Method tearDownIterationMethod =
                    benchmarkClass.getMethod( "tearDownIteration", new Class[]{});
            
            // Setup for benchmark and iteration (if any).
            setupBenchmarkMethod.invoke( null, new Object[]{});
            setupIterationMethod.invoke( null, new Object[]{});
            
            // Run the benchmark.
            SpecJVMBenchmarkBase benchmark = (SpecJVMBenchmarkBase) c.newInstance(inArgs);
            benchmark.harnessMain();
            
            // tear down things for the iteration and benchmark (if any).
            tearDownIterationMethod.invoke( null, new Object[]{});
            tearDownBenchmarkMethod.invoke( null, new Object[]{});
            
        } catch (ClassCastException e) {
            System.err.println("Class " + benchmarkClass.getName() + " does not seem to inherit SpecJVMBenchmarkBase.");
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
