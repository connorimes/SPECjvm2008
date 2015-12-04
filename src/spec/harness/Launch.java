/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 */
package spec.harness;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;

import spec.harness.analyzer.AnalyzersRunner;
import spec.harness.results.BenchmarkResult;
import spec.harness.results.SuiteResult;
import spec.harness.results.TestResult;
import spec.reporter.Reporter;
import spec.validity.Digests;

public class Launch {
    
    public static int currentNumberBmThreads;
    public static boolean verbose = false;
    static boolean parseDefaultArgs = false;
    static String workLoad = Constants.WORKLOAD_NAME_NOT_SET;
    static String specjvmHomeDir = System.getProperty(Constants.HOMEDIR_PROP, ".");    
    
    final static int defaultMode = 1;
    final static int startupMode = 2;
    final static int scimarkMode = 3;
    
    static {
    	Configuration.setupProperties();
    }

    public static void runBenchmarkSuite(Properties commandLineProps) {
        
        // Read run properties
        String pf = commandLineProps.getProperty(Constants.PROPFILE_PROP);
        try {
        	Configuration.userProperties = Configuration.readProperties(pf);
        } catch (Exception e) {
            System.out.println("Error reading properites file '" + pf + "'");
            System.out.println(e.getMessage());
            return;
        }
        
        // Add command line props (which overrides property files props)
        Configuration.userProperties.putAll(commandLineProps);
        Configuration.userProperties.put(Constants.HOMEDIR_PROP, specjvmHomeDir);
        verbose = Util.getBoolProperty(Constants.VERBOSE_PROP, null);

        // Setup Context ++
        setupContext();
        SuiteResult sResult = Context.getSuiteResult();

        // Get version
        String version = "n/a";
        try {
            Properties bp = new Properties();
            bp.load(new FileInputStream(specjvmHomeDir + "/version.txt"));
            String versionNumber = bp.getProperty("Kit_version", "-");
            String buildDate = bp.getProperty("Build_date", "-");
            String benchName = bp.getProperty("Benchmark", "-");
            version = benchName + " " + versionNumber + " (" + buildDate +")";
        } catch (Exception e) { }
        sResult.setKitVersion(version);
        
        // Check properties
        try {
            Configuration.checkSetup(sResult);
        } catch(StopBenchmarkException e) {
            // System.err.println(e.getMessage());
            e.printStackTrace();
            return;
        }
        
        
        ProgramRunner.setPrintProgress(Util.getBoolProperty(Constants.PRINT_PROGRESS_PROP, null));
        
        String benchmarks = Util.getProperty(Constants.BENCHMARKS_PROP, null);
        String propfile = Util.getProperty(Constants.PROPFILE_PROP, null);
        boolean continueOnError = Util.getBoolProperty(Constants.CONTINUE_ON_ERROR_PROP, null);
        boolean doCheckTest = Util.getBoolProperty(Constants.INITIAL_CHECK_PROP, null);
        boolean doCheckSumCheck = Util.getBoolProperty(Constants.INITIAL_CHECKSUM_CHECK_PROP, null);
        
        Configuration.setupReportInfo(sResult, Configuration.userProperties);
        sResult.setWorkloadName(Util.getProperty(Constants.NAME_OF_RUN_PROP, null));
        
        Context.getOut().println();
        Context.getOut().println(Util.getProperty(Constants.NAME_OF_RUN_PROP, null));
        Context.getOut().println("  Properties file:   " + (propfile != null ? propfile : "none"));
        if (Util.getProperty(Constants.PROPFILE_ADDITIONAL_PROP, null) != null) {
            Context.getOut().println("  Properties file 2: " + Util.getProperty(Constants.PROPFILE_ADDITIONAL_PROP, null));
        }
        if (AnalyzersRunner.getNoAnalyzer() > 0) {
            Context.getOut().println("  Analyzers:         " + AnalyzersRunner.getAnalyzerNames());
        }
        if (Context.getSpecBasePath().equals(".") == false) {
            Context.getOut().println("  Base dir:          " + Context.getSpecBasePath());
        }
        Context.getOut().println("  Benchmarks:        " + benchmarks);
                
        Context.getOut().println();
        if (!sResult.isCompliant()) {
            Context.getOut().println("  WARNING: Run will not be compliant.");
            Iterator<String> iter = sResult.getViolations().iterator();
            while (iter.hasNext()) {
                Context.getOut().println("  " + iter.next());
            }
            Context.getOut().println();
        }
        
        if (verbose) {
            Context.getOut().println("User defined properties:");
            Util.printProperties(Configuration.userProperties);
            Context.getOut().println();
            
            Context.getOut().println("Default properties:");
            Util.printProperties(Configuration.defaultProperties);
            Context.getOut().println();
            
            Context.getOut().println("Requirements:");
            Util.printReqs(Configuration.propsRequirements);
            Context.getOut().println();
        }
        
        // Make sure the benchmarks exists (find typos early)
        boolean cont = true;
        for (StringTokenizer st = new StringTokenizer(benchmarks); st.hasMoreElements(); ) {
            String bm = st.nextToken().trim();
            if (!Util.isBenchmark(bm)) {
                Context.getOut().println("Unknown benchmark: " + bm);
                cont = false;
            }
        }
        if (!cont) {
            Context.getOut().println("Aborting.");
            Context.getOut().println();
            return;
        }
        
        // Check jar files and resources, for checksum and sign.
        // Do it here, so the user have had some output before
        if (doCheckSumCheck) {
            String jarCheckResult = checkBenchmarkKit();
            if (jarCheckResult != null) {
                sResult.addViolation(jarCheckResult);
                Context.getOut().println("\n  Kit signature and checksum validation failed.");
                Context.getOut().println("  " + jarCheckResult);
                Context.getOut().println("  WARNING: Run will not be compliant.");
            } else {
                Context.getOut().println("passed.");
            }
        }
        
        // Open file for raw results
        String resultFileName = null;        
        if (Util.getBoolProperty(Constants.CREATE_XML_REPORT_PROP, null)) {
            try {
                resultFileName = openResultFile(Configuration.userProperties);                
            } catch (IOException ioe) {
                Context.getOut().println("Aborting.");
                Context.getOut().println();
                return;
            }
        }
        
        if (Util.getBoolProperty(Constants.CREATE_XML_REPORT_PROP, null)) {
            sResult.headerToXml(Context.getXmlResultFile(), 0);
        }
        
        // Run JVM check benchmark
        BenchmarkResult check = null;
        if (doCheckTest) {
            check = runOneBenchmark("check", TestResult.ITER, 1, defaultMode);
            sResult.addBenchmarkResults(check);
        }
        
        // Run benchmarks
        if (check == null || check.isValid()) {
            BenchmarkResult  res;
            StringTokenizer bmt = new StringTokenizer(benchmarks);
            while (bmt.hasMoreTokens()) {
                String benchmark = bmt.nextToken();
                int runType = Util.getIntProperty(Constants.TYPE_OF_RUN_PROP, null);
                int fixedOpsNumber = (runType == TestResult.TIMED ? -1 : Util.getIntProperty(Constants.FIXED_OPERATIONS_PROP, benchmark));
                
                if (BenchmarkThread.createValidityCheckFiles) {
                    runOneBenchmark(benchmark, TestResult.ITER, 1, defaultMode);
                    continue;
                }
                
                if(benchmark.startsWith(Constants.SCIMARK_BNAME_PREFIX)){
                    if(benchmark.endsWith(Constants.SCIMARK_BNAME_LARGE_POSTFIX)){
                    	Configuration.setupProp(Constants.SCIMARK_SIZE_PROP, "LARGE", Constants.WHATEVER_REQ, "LARGE", Constants.WHATEVER_REQ);
                    } else if(benchmark.endsWith(Constants.SCIMARK_BNAME_SMALL_POSTFIX)){
                    	Configuration.setupProp(Constants.SCIMARK_SIZE_PROP, "SMALL", Constants.WHATEVER_REQ, "SMALL", Constants.WHATEVER_REQ);
                    } else{
                    	Configuration.setupProp(Constants.SCIMARK_SIZE_PROP, "DEFAULT", Constants.WHATEVER_REQ, "DEFAULT", Constants.WHATEVER_REQ);
                    }
                    //benchmark = benchmark.substring(0,benchmark.lastIndexOf("."));
                    res = runOneBenchmark(benchmark, runType, fixedOpsNumber, scimarkMode);
                } else if( benchmark.startsWith(Constants.STARTUP_BNAME_PREFIX)){
                    res = runOneBenchmark(benchmark, TestResult.ITER, 1, startupMode);
                } else{
                    res = runOneBenchmark(benchmark, runType, fixedOpsNumber, defaultMode);
                }
                
                sResult.addBenchmarkResults(res);
                
                if (!res.isValid() && !continueOnError) {
                    Context.getOut().println("Benchmark " + benchmark + " failed. Aborting run.");
                    Context.getOut().println();
                	break;
                }
            }
        } else {
            Context.getOut().println("No benchmarks will be run, since initial check test failed.");
            KnownIssues.printKnownIssueJavacVersion();
        }

        // Tear down analyzers.
        AnalyzersRunner.tearDownAnalyzerClasses();
        
        // Handle results
        if (Util.getBoolProperty(Constants.CREATE_XML_REPORT_PROP, null)) {
            sResult.footerToXml(Context.getXmlResultFile(), 0);
            closeResultFile(resultFileName);            
        }
        
        // Create fancy report (optional)
        if (Util.getBoolProperty(Constants.CREATE_XML_REPORT_PROP, null)
            && (Util.getBoolProperty(Constants.CREATE_HTML_REPORT_PROP, null)
            || Util.getBoolProperty(Constants.CREATE_TXT_REPORT_PROP, null))){
            try {
                String [] rArgs = {
                        resultFileName,
                        "-sub", "true",
                        "-summary", "true",
                        "-txt", Util.getProperty(Constants.CREATE_TXT_REPORT_PROP, null),
                        "-html", Util.getProperty(Constants.CREATE_TXT_REPORT_PROP, null)
                };
                String result = createReport(rArgs);
                Context.getOut().println("");
                Context.getOut().println(result);

            } catch (OutOfMemoryError oome) {
                Context.getOut().println("Error while creating report. Out of Memory.");
            } catch (Throwable t) {
                Context.getOut().println("Error while creating report: " + t.getMessage());
                t.printStackTrace(Context.getOut());
            }
        }
        
        // Print known issues that are hit at end of run as well to expose it more.
        KnownIssues.printKnownIssuesInfo();
        
        if (Util.getBoolProperty(Constants.HARNESS_SPIN_AT_END_PROP, null)) {
            Context.getOut().println("Harness will sleep here.");
            // This is useful, if you want to attach to the process and look at what state it ended in.
            while (Util.sleep(1000));
        }
    }
    
    protected static String openResultFile(Properties userProperties) throws IOException, FileNotFoundException {
        File resultDir = new File(Context.getResultDirNameBase());
        if (resultDir.exists()) {
            if (!resultDir.isDirectory()) {
                throw new IOException("Result dir " + resultDir.getAbsolutePath() + " is a file and already exists.");
            }
        } else {
            if (!resultDir.mkdir()) {
                throw new IOException("Failed to create dir " + resultDir.getAbsolutePath());
            }
        }

        String xmlFileName = Util.getNextRawFileInDir(resultDir);
        PrintStream xmlPs = new PrintStream(new FileOutputStream(xmlFileName));
        Context.setXmlResultFile(xmlPs);
        return xmlFileName;
    }   

    
    protected static void closeResultFile(String resultFile) {
        try {
			Context.getOut().println("Results are stored in: \n" + new File(resultFile).getCanonicalPath());
		} catch (IOException e) { }
        Context.closeXmlResultFile();
    }
    
    protected static void closeSubmissionFile(String submissionFile) {
        Context.getOut().println("Results are stored in: \n" + submissionFile + "\n");
    }
    
    public static String createReport(String xmlFileName) throws Exception {
        return createReport(new String[]{xmlFileName});
    }
    
    public static String createReport(String [] reporterArgs) throws Exception {
        return Reporter.main2(reporterArgs);
    }
    
    private static BenchmarkResult runOneBenchmark(String bm, int runMode, int fixedLoops, int mode) {
        BenchmarkResult bmResult = new BenchmarkResult();
        String args[] = null;
        //args = ((mode == startupMode)) ? new String[]{bm.substring(bm.indexOf(".") + 1)} :((mode == scimarkMode)) ? new String[]{bm.substring(0,bm.lastIndexOf("."))} : new String[0];
        if(mode == startupMode){
            args = new String[]{bm.substring(bm.indexOf(".")+1)};
        } else if(mode == scimarkMode){
            args = new String[]{bm.substring(0,bm.lastIndexOf("."))};
        } else {
            args = new String[0];
        }
        //System.out.println("args[0]: " + args[0] );
        bmResult.setNames(bm);
        bmResult.setArgs(args);
        bmResult.setRunMode(runMode);
        bmResult.setNumberOfLoops(fixedLoops);
        
        bmResult.setIterationTime(
                Util.getTimeProperty(Constants.RUNTIME_PROP, bm),
                Util.getBoolProperty(Constants.FORCED_RUNTIME_PROP, bm));
        
        int miniter = Util.getIntProperty(Constants.ITERATIONS_MINIMUM_PROP, bm);
        bmResult.setMinIter(miniter);
        
        int maxiter = Util.getIntProperty(Constants.ITERATIONS_MAXIMUM_PROP, bm);
        if (maxiter != -1 && maxiter < miniter) {
            // -1 means forever...
            maxiter = miniter;
        }
        bmResult.setMaxIter(maxiter);
        
        currentNumberBmThreads = Util.getHwtCalcProperty(Constants.BENCHMARK_THREADS_PROP, bm);
        bmResult.setNumberBmThreads((mode == startupMode) ? 1 : currentNumberBmThreads);
        bmResult.setWarmupTime(Util.getTimeProperty(Constants.WARMUPTIME_PROP, bm));
        bmResult.setIterationForcegc(Util.getBoolProperty(Constants.ITER_DO_SYSTEMGC_PROP, bm));
        bmResult.setIterationDelay(Util.getTimeProperty(Constants.ITER_DELAYTIME_PROP, bm));
        bmResult.setBenchmarkForcegc(Util.getBoolProperty(Constants.BM_DO_SYSTEMGC_PROP, bm));
        bmResult.setBenchmarkDelay(Util.getTimeProperty(Constants.BM_DELAYTIME_PROP, bm));
        bmResult.setChecksum(Util.getBoolProperty(Constants.VALIDITY_CHECKSUM_PROP, bm));
        bmResult.setAnalyzers(Util.getProperty(Constants.ANALYZER_NAMES_PROP, bm));
        bmResult.setAnalyzerFreq(Util.getTimeProperty(Constants.ANALYZER_FREQUENCY_PROP, bm));
        
        if (runMode == TestResult.ITER) {
            bmResult.setIterationTime(-1);
            bmResult.setNumberOfLoops(fixedLoops);
            bmResult.setWarmupTime(-1);
        }
        
        ProgramRunner runner = new ProgramRunner(bmResult, mode);
        runner.start();
        try {
            runner.join();
        } catch (InterruptedException e) {
            // Context.getOut().println(e.getClass().getName() + ": " + e.getMessage());
            e.printStackTrace(Context.getOut());
            bmResult.addError("BenchmarkRunner failed to join: " + e.getMessage());
        }
        return bmResult;
    }
    
    
    /**
     * Check benchmark kit, jars (signing and checksum) and resources (checksum).
     * @return Null if everything is ok, otherwise a String with the error message.
     */
    private static String checkBenchmarkKit() {
        
        try {
            Class.forName("spec.validity.Digests");
        } catch (ClassNotFoundException e) {
            return "Digests.java missing. Checksum test will not be run.";
        }
        
        // Validate that jar files have not been altered
        try {
            Digests digests = new Digests(Context.getOut());
            
            Context.getOut().println("  Kit signature and checksum is validated.");
            Context.getOut().println("  This can take several minutes.");
            Context.getOut().println("  Use argument '-ikv' to skip this.");
            Context.getOut().print("  ");
            
            String res = digests.crunch_jars();
            if (res != null) {
                return "Checksum test failed for jar files (" + res + "). Kit may not be changed or rebuild.";
            } 
            res = digests.crunch_resources();
            if (res != null) {
                return "Checksum test failed for resource files (" + res + "). Kit may not be changed or rebuild.";
            }
        } catch (Throwable e) {
            return "Checksum test throw exception, message: " + e.getMessage();
        }
        return null; // All good.
    }
    
    public static void setupContext() {
        Context.setSpecBasePath(Util.getProperty(Constants.HOMEDIR_PROP, null));
        Context.setResultDirNameBase(Util.getProperty(Constants.RESULT_DIR_PROP, null));
        Context.setVerify(Util.getBoolProperty(Constants.VERIFY_PROP, null));
        if (verbose) {
            System.out.println(Context.staticToString());
        }
        Context.setSuiteResult(new SuiteResult());
    }
        
    /*
     * This method is used by BenchmarkThread.runSimple
     * and duplicates some of the code in runBenchmarkSuite (above)
     * It would be best to refactor things to avoid this duplication.
     */
    public static boolean setupSimple(String [] args) {
        Properties commandLineProps = new Properties();
        boolean cont = CommandLineParser.parseArgs(args, commandLineProps);
        if (cont == false) {
            return false;
        }
        // Read run properties
        try {
            Configuration.userProperties = Configuration.readProperties(commandLineProps.getProperty(Constants.PROPFILE_PROP));
        } catch (Exception e) {
            System.out.println("Error reading run properties: " + e.getMessage());
            return false;
        }
        
        // Add command line props (which overrides property files props)
        Configuration.userProperties.putAll(commandLineProps);
        Configuration.userProperties.put(Constants.HOMEDIR_PROP, specjvmHomeDir);
        Configuration.userProperties.put(Constants.BENCHMARK_THREADS_PROP, "1");
        Launch.currentNumberBmThreads = 1;
        
        // Setup Context ++
        setupContext();
        return true;
    }
    
    public static void main(String [] args) {
        Properties commandLineProps = new Properties();
        boolean cont = CommandLineParser.parseArgs(args, commandLineProps);
        if (cont == false) {
            return;
        }
        runBenchmarkSuite(commandLineProps);
    }

}
