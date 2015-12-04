/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * Copyright (c) 2006 Sun Microsystems, Inc. All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 */

package spec.benchmarks.compiler;

import java.io.File;

import spec.harness.Context;
import spec.harness.SpecJVMBenchmarkBase;
import spec.harness.results.BenchmarkResult;

import spec.benchmarks.compiler.Compiler;
import spec.benchmarks.compiler.Util;

public class MainBase extends SpecJVMBenchmarkBase {
    protected static File resDir = null; 
    protected static File tmpDir = null; 
    protected static File srcsFile = null; 
    protected Compiler compiler;  
    protected int loops;
    
    public MainBase(BenchmarkResult bmResult, int threadId, int compiles) {
        super(bmResult, threadId);   
        this.loops = compiles;        
    }
    
    public static String testType() {
        return MULTI;
    }   
        
    public static void preSetupBenchmark(Class cl) {
    	SpecFileManager.reset();
    	resDir = new File(Context.getResourceDirFor(cl));
    	tmpDir = Util.getTmpDir(resDir, false);
    	if (tmpDir != null) {
    	   Util.recursiveRemoveDir(tmpDir.getParentFile());
    	}    
    	tmpDir = Util.getTmpDir(resDir, true);   	
    }    
    
    public static void tearDownBenchmark() {    	
        Util.recursiveRemoveDir(tmpDir.getParentFile());
        SpecFileManager.reset();       
    }
    
    public void harnessMain() {    	
        compiler.compile(loops);
    } 
    
    public void harnessMain(boolean skipVerify) {
    	compiler.skipVerify = skipVerify;
        compiler.compile(loops);
    } 
}
