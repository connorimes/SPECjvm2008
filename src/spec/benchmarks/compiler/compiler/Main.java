/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * Copyright (c) 2006 Sun Microsystems, Inc. All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 */

package spec.benchmarks.compiler.compiler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import spec.harness.StopBenchmarkException;
import spec.harness.results.BenchmarkResult;

import spec.benchmarks.compiler.Compiler;
import spec.benchmarks.compiler.MainBase;
import spec.benchmarks.compiler.Util;

public class Main extends spec.benchmarks.compiler.MainBase {  
    
    public Main(BenchmarkResult bmResult, int threadId) {
        super(bmResult, threadId, 1);        
        String[] args = new String[] {  
                "-nowarn",        
                "-proc:none",                
                "@" + srcsFile.getPath()
                }; 

        compiler = new Compiler(args);       
    }   
    
    public static void setupBenchmark() {    	
    	MainBase.preSetupBenchmark(Main.class);
        srcsFile = getSrcFile(resDir, tmpDir);  
        new Main(new BenchmarkResult(), 1).harnessMain(true);       
    }
   
    public static File getSrcFile(File resDir, File tmpDir) {
        File srcsFile = Util.getSrcFile(tmpDir);        
        File srcDir = Util.getSrcDir(resDir, "compiler.compiler");      
        File compilerZip = Util.getZipFile(srcDir);        
        try {        	
            ArrayList<String> srcs = new ArrayList<String>();
            FileWriter fw = new FileWriter(srcsFile);
            Util.unzip(compilerZip.getPath(), tmpDir.getPath(), srcs);            
            
            for (String src: srcs) {
                if (src.endsWith(".java") && src.contains("/src/share/classes/")) {                	
                    // JCTree.java has an error at 1615 that we shall avoid for now
                    if (src.endsWith("version-template.java")) {
                        String version = src.replace("-template", "");
                         File templateFile = new File(src);
                         File versionFile = new File(version);
                         templateFile.renameTo(versionFile);
                         src = version;
                    }
                    fw.write(src);
                    fw.write(Util.linesep());
                }
           }
           fw.close();
        } catch (IOException ioe) {
        	ioe.printStackTrace();
            throw new StopBenchmarkException("ERROR: could not create: " + srcsFile.getPath());          
        }
      
      return srcsFile;
    }        
    
    public static void main(String[] args) throws Exception {
        runSimple( Main.class, args );
    }
}

