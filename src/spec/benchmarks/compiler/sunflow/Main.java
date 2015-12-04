/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * Copyright (c) 2006 Sun Microsystems, Inc. All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 */

package spec.benchmarks.compiler.sunflow;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import spec.harness.results.BenchmarkResult;
import spec.benchmarks.compiler.Compiler;
import spec.benchmarks.compiler.MainBase;
import spec.benchmarks.compiler.Util;

public class Main extends spec.benchmarks.compiler.MainBase {    
    
    public Main(BenchmarkResult bmResult, int threadId) {
        super(bmResult, threadId, 2);        
        String[] args = new String[] {
        		"-proc:none",               
                "@" + srcsFile.getPath()};
        compiler = new Compiler(args);
    }
    
    public static void setupBenchmark() {    	
    	MainBase.preSetupBenchmark(Main.class);
        srcsFile = getSrcFile(resDir, tmpDir);       
        new Main(new BenchmarkResult(), 1).harnessMain(true);
    }
    
    public void harnessMain() {     
        for (int i = 0; i < 3; i++) {
            compiler.compile(loops);
        }
    } 

        
    public static File getSrcFile(File resDir, File tmpDir) {    	
        File srcsFile = Util.getSrcFile(tmpDir);                
        File srcDir = Util.getSrcDir(resDir, "sunflow");
        File srcZip = Util.getZipFile(srcDir);         
        try {
            ArrayList<String> srcs = new ArrayList<String>();
            FileWriter fw = new FileWriter(srcsFile);
            Util.unzip(srcZip.getPath(), tmpDir.getPath(), srcs);
            for (String src: srcs) {
                if (src.endsWith(".java") && ! src.contains("examples")) {
                    // avoid the example *.java files
                    fw.write(src);
                    fw.write(Util.linesep());
                }
            }
            fw.close();
        } catch (IOException ioe) {
            System.out.println("ERROR: could not create: " + srcsFile.getPath());                
            ioe.printStackTrace();
        }        
        return srcsFile;    	
    }
      
    public static void main(String[] args) throws Exception {
        runSimple( Main.class, args );
    }
}
