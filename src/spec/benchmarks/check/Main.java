/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * Copyright (c) 1997,1998 Sun Microsystems, Inc. All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 *
 * Complain about Java errors that ought to be caught as
 * exceptions by the JVM. *NOT* an exhaustive conformance
 * test. This is just intended to catch some common errors
 * and omissions for the convenience of the benchmarker in
 * avoiding some run rule mistakes. This needs to be
 * expanded to test more.
 *
 * The timing of this "benchmark" is ignored in metric
 * calculation. It is here only in order to pass or
 * fail output verification.
 */

package spec.benchmarks.check;

import spec.harness.KnownIssues;
import spec.harness.SpecJVMBenchmarkBase;
import spec.harness.results.BenchmarkResult;
import com.sun.tools.javac.main.JavaCompiler;

public class Main extends SpecJVMBenchmarkBase {
    
    public Main(BenchmarkResult bmResult, int threadId) {
        super(bmResult, threadId);
    }
    
    /**
     * Run this functional test.
     */
    public static String testType() {
        return FUNCTIONAL;
    }
    
    public long runBenchmark() {
        boolean caughtIndex = false;
        boolean gotToFinally = false;
        
        try {
            int[] a = new int[10];
            for (int i = 0; i <= 10; i++)
                a[i] = i;
            spec.harness.Context.getOut().println("Error: array bounds not checked");
        } catch (ArrayIndexOutOfBoundsException e) {
            caughtIndex = true;
        } finally {
            gotToFinally = true;
        }
        
        if (!caughtIndex) {
            spec.harness.Context.getOut().println("1st bounds test error:\tindex exception not received");
        }
        if (!gotToFinally) {
            spec.harness.Context.getOut().println("1st bounds test error:\tfinally clause not executed");
        }
        if (caughtIndex && gotToFinally) {
            spec.harness.Context.getOut().println("1st bounds test:\tOK");
        }
        
        checkSubclassing();
        
        
        // LoopBounds mule = new LoopBounds();
        LoopBounds.run();
        
        if (LoopBounds.gotError) {
            spec.harness.Context.getOut().println("2nd bounds test:\tfailed");
        } else {
            spec.harness.Context.getOut().println("2nd bounds test:\tOK");
        }
        
        if(!checkCompilerVersion()) {
            spec.harness.Context.getOut().println("Compiler version test:\tfailed");
            KnownIssues.isKnownIssueJavacVersion = true;
        }
        
        PepTest horse = new PepTest();
        horse.instanceMain();
        
        if (horse.gotError) {
            spec.harness.Context.getOut().println("PepTest failed");
        }

        return 0;
    }
    
    private static void checkSubclassing() {
        Super sup = new Super(3);
        Sub sub = new Sub(3);
        spec.harness.Context.getOut().println(sup.getName() + ": " + sup.toString());
        spec.harness.Context.getOut().println(sub.getName() + ": " + sub.toString());
        spec.harness.Context.getOut().println("Super: prot=" + sup.getProtected() + ", priv=" + sup.getPrivate());
        spec.harness.Context.getOut().println("Sub:  prot=" + sub.getProtected() + ", priv=" + sub.getPrivate());
    }
    
    private boolean checkCompilerVersion(){
    	return "1.7.0-opensource".equals(JavaCompiler.version());
    }
    
    public void harnessMain() {
        runBenchmark();
    }    
}
