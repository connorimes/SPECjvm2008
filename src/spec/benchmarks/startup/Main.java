/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 */
package spec.benchmarks.startup;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import spec.harness.Constants;
import spec.harness.SpecJVMBenchmarkBase;
import spec.harness.Util;
import spec.harness.results.BenchmarkResult;

public class Main extends SpecJVMBenchmarkBase {
    
    private static boolean RUN_ALL_SCIMARK;
    private static boolean RUN_ALL_CRYPTO;
    
    private static boolean DEBUG;
    
    private static int loops = 1;
    private static boolean startupTestValid;
    public static void main(String[] args) {
        runSimple(Main.class, args);
    }
    
    private String benchmarks = "";
    
    /** Run this in mutli mode, next to each other. */
    public static String testType() {
        return SINGLE;
    }
    
    public static void setupBenchmark() {
        RUN_ALL_SCIMARK = Util.getBoolProperty(Constants.STARTUP_RUN_ALL_SCIMARK, null);
        RUN_ALL_CRYPTO = Util.getBoolProperty(Constants.STARTUP_RUN_ALL_CRYPTO, null);
        DEBUG = Util.getBoolProperty(Constants.DEBUG_STARTUP, null);
    }
    
    public Main(BenchmarkResult bmResult, int threadId) {
        super(bmResult, threadId);
        for (int i = 0; i < bmResult.getArgs().length; i++) {
            benchmarks += bmResult.getArgs()[i] + " ";
        }
    }
    
    public static synchronized boolean startupTestValid(){
        return startupTestValid;
        
    }
    public static synchronized void setStartupTestValidity(boolean result){
        startupTestValid = result;
    }
    
    
    public void harnessMain() {
        
        Properties systemProperties = System.getProperties();
        String classpath = systemProperties.getProperty("java.class.path");
        String javaHome = systemProperties.getProperty("java.home");
        String javaExe = Util.getProperty(Constants.STARTUP_LAUNCHER, null);
        if (javaExe == null) {
            javaExe = javaHome + File.separator + "bin" + File.separator + "java";
        }
        String jvmOptions = Util.getProperty(Constants.STARTUP_JVM_OPTIONS_PROP, null);
        if(jvmOptions == null) {            
            jvmOptions=" ";
        }
              
        int threadsNumber = 1;
        String execString = javaExe + " " + jvmOptions + " -classpath " + classpath
                +  " -D" + Constants.HOMEDIR_PROP + "=" 
                    + System.getProperty(Constants.HOMEDIR_PROP, ".")
                +  " spec.harness.Launch -bt " + threadsNumber
                +  " -ops " + loops + " -crf false -ict -icsv"
                +  " -ss SMALL ";
        String prop = Util.getProperty(Constants.XML_VALIDATION_INPUT_DIR_PROP, null);
        if (prop != null) {
        	execString += " -xd " + prop;
        }        
        
        if (benchmarks.startsWith("scimark") && RUN_ALL_SCIMARK) {
            execString += " " + "scimark.fft.small scimark.lu.small scimark.monte_carlo.small scimark.sor.small scimark.sparse.small";
        } else if(benchmarks.startsWith("scimark")){
            execString += " " + benchmarks.trim() + ".small";
        } else if (benchmarks.startsWith("crypto") && RUN_ALL_CRYPTO) {
            execString += " " + "crypto.aes  crypto.hash  crypto.rsa crypto.signverify";
        } else{
            execString += " " + benchmarks;
        }
        if (DEBUG) {
            System.out.println("Startup Benchmark Execution String: '" + execString + "'");
        }
        Runtime run = Runtime.getRuntime();
        try {
            Process p = run.exec(execString);
            StartupBenchmarkOutput out = new StartupBenchmarkOutput(p
                    .getInputStream());
            
            // Start startup benchmark output thread
            out.start();
            out.join();
            // Wait for process to finish
            p.waitFor();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        if (!Main.startupTestValid()) {
            throw new RuntimeException("INVALID: Error Running Startup Test");
        }
    }
}
