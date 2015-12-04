/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * Copyright (c) 1997,1998 Sun Microsystems, Inc. All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 */

package spec.harness;

import java.io.File;
import java.io.PrintStream;

import spec.harness.results.SuiteResult;
import spec.io.FileCache;

/**
 * This class is used to define the context for the execution of the benchmark.
 * This container class has the parameters which can hold the benchmark run
 * specific parameters like the run time, speed, files opened, number of
 * cached_files, SpecBasePath, console window for displaying the trace
 */

public final class Context {
    
    /**
     * SpecBasePath is used to define the base directory of the specJava
     *
     * @see java.lang.String
     */
    private static String specBasePath = ".";
    
    private static String specResultDirBase = specBasePath + "/results";
    private static String specResultDirCurrent = specBasePath + "/results";
    
    /**
     * verify flag is used to turn on/off the verification process of benchmark
     * runs.
     */
    private static boolean verify = true;
    
    /**
     * A print stream to store the xml results to.
     */
    private static PrintStream xmlResultFile = null;
    
    
    /**
     * A file cache for files that will repetedly be read, but never changed.
     * Used to remove IO-tasks from where it should be a factor.
     */
    private static FileCache fileCache = new FileCache();
    
    /**
     * out is the PrintStream into which the trace messages will be written.
     * This is assigned to the System.output stream by default
     */
    private static ThreadLocal<Object> tlOut = new ThreadLocal<Object>() {
        protected synchronized Object initialValue() {
            return System.out;
        }
    };
    
    private static SuiteResult suiteResult = null;
    
    public static SuiteResult getSuiteResult() {
        return suiteResult;
    }
    
    public static void setSuiteResult(SuiteResult sr) {
        suiteResult = sr;
    }
    
    public static PrintStream getOut() {
        return (PrintStream) tlOut.get();
    }
    
    public static void setOut(PrintStream os) {
        tlOut.set(os);
    }
    
    /**
     * This function set the SpecBasePath to the string value passed with some
     * data stripped.
     *
     * @param basepath The URL of the file.
     */
    public static void setSpecBasePath(String basepath) {
        specBasePath = basepath;
    }
    
    /**
     * returns the specbase path
     */
    public static String getSpecBasePath() {
        return specBasePath;
    }
    
    /** Root folder for results */
    public static void setResultDirNameBase(String resultDirNameBase) {
        Context.specResultDirBase = resultDirNameBase;
    }
    
    /** Root folder for results */
    public static String getResultDirNameBase() {
        return Context.specResultDirBase;
    }
    
    /** Folder for results in this run */
    public static void setResultDirNameCurrent(String resultDirNameCurrent) {
        Context.specResultDirCurrent = resultDirNameCurrent;
    }
    
    /** Folder for results in this run */
    public static String getResultDirNameCurrent() {
        return Context.specResultDirCurrent;
    }
    
    /**
     * Returns the canonical path for the resource directory
     * for this benchmark
     *
     * @return  benchmark specific resource directory
     */
    public static String getResourceDirFor( Class benchmarkMainClass ) {
        String prefix = "spec.benchmarks.";
        String benchmark;
        File   resdir;
        String respath;
        
        benchmark = benchmarkMainClass.getPackage().getName();
        if ( benchmark.startsWith(prefix) ) {
            benchmark = benchmark.substring(prefix.length());
        }
        resdir = new File(Context.getSpecBasePath()
        + File.separator + "resources"
                + File.separator + benchmark );
        try {
            respath = resdir.getCanonicalPath();
        } catch (Exception e) {
            // clearly this error needs to be handled better
            // currently this requires every benchmark to have a
            // resource directory, even if it is empty
            e.printStackTrace();
            respath = ".";
        }
        return respath;
    }
    
    /**
     * Set verify flag
     */
    public static void setVerify(boolean value) {
        verify = value;
    }
    
    /**
     * Get verify flag
     */
    public static boolean getVerify() {
        return verify;
    }
    
    public static FileCache getFileCache() {
        return fileCache;
    }
    
    public static String staticToString() {
        return "Context: base=" + specBasePath;
    }
    
    public static PrintStream getXmlResultFile() {
        return xmlResultFile;
    }
       
    public static void setXmlResultFile(PrintStream xmlResultFile) {
        Context.xmlResultFile = xmlResultFile;
    }
    
    public static void closeXmlResultFile() {
        Context.xmlResultFile.close();
        Context.xmlResultFile = null;
    }
 }
