package spec.harness;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Properties;

import spec.harness.results.TestResult;

public class CommandLineParser {
    private static void usage(PrintStream ps) {
        ps.println("Usage: spec.harness.Launch <configuration>");
        ps.println("  -h,   --help                   Show this help.");
        ps.println("        --version                Print version and exit.");
        ps.println("  -sv,  --showversion            Print version and continue.");
        ps.println("        --base                   Run the base compliant run of SPECjvm2008 ");
        ps.println("                                 (default, unless jvm args are specified)");
        ps.println("        --peak                   Run the peak compliant run of SPECjvm2008");
        ps.println("        --lagom                  Run the Lagom benchmark suite, a version");
        ps.println("                                 of SPECjvm2008 that uses a fixed workload.");
        ps.println("  -pf,  --propfile <string>      Use this properties file.");
        ps.println("  -i,   --iterations <int>       How many iterations to run. ");
        ps.println("                                 'inf' means an infinite number.");
        ps.println("  -mi,  --miniter <int>          Minimum number of iterations.");
        ps.println("  -ma,  --maxiter <int>          Maximum number of iterations.");
        ps.println("  -it,  --iterationTime <time>   How long one iteration should be.");
        ps.println("                                 The time is specified as an integer,");
        ps.println("                                 and assumed to be in seconds, or an integer");
        ps.println("                                 with unit, for example 4m (4 minutes).");
        ps.println("                                 Units available are ms, s, m and h.");
        ps.println("                                 If iteration time is too short, based on the");
        ps.println("                                 warmup result, it will be adjusted to handle");
        ps.println("                                 at least 5 operations.");
        ps.println("  -fit, --forceIterationTime <time> As iteration time, but not adjusting time.");
        ps.println("  -wt,  --warmuptime <time>      How long warmup should be.");
        ps.println("                                 Time format is the same as in iteration time.");
        ps.println("  -ops, --operations <int>       Hom many operations each iteration will ");
        ps.println("                                 consist of. It will then be a fixed workload");
        ps.println("                                 and iteration time is ignored.");
        ps.println("  -bt,  --benchmarkThreads <int> How many benchmark threads to use.");
        ps.println("  -ja,  --jvmArgs \"options\"      JVM arguments used for startup subtests.");
        ps.println("  -jl,  --jvmLauncher \"path\"     JVM launcher used for startup subtests.");
        ps.println("  -r,   --reporter <file name>   Invokes the reporter with given file(s).");
        ps.println("                                 The benchmarks will not be run.");
        ps.println("  -v,   --verbose                Print verbose info.");
        ps.println("  -pja, --parseJvmArgs           Parse jvm arguments info for command line,");
        ps.println("                                 including heap settings (uses JMXBean info)."); 
        ps.println("  -coe, --contintueOnError       Continue to run suite, even if one test fails.");
        ps.println("  -ict, --ignoreCheckTest        Do not run check benchmark.");
        ps.println("  -ikv, --ignoreKitValidation    Do not run checksum validition of");
        ps.println("                                 benchmark kit."); 
        ps.println("  -crf, --createRawFile <bool>   Whether to generate a raw file.");
        ps.println("  -ctf, --createTextFile <bool>  Whether to generate text report.");
        ps.println("  -chf, --createHtmlFile <bool>  Whether to generate html report.");
        ps.println("                                 If raw is disabled, so is text and html.");
        ps.println("  -xd,  --xmlDir \"path\"          To set path to xml input files");
        ps.println("  <benchmark>                    Name of benchmark(s) to run. Benchmarks");
        ps.println("                                 with sub-benchmarks is also possible to ");
        ps.println("                                 specify. By default all submission");
        ps.println("                                 benchmarks will be selected.");
        ps.println("");
        ps.println("Benchmarks: " + Constants.VALID_BENCHMARKS_SPEC);
    }
    
    private static void version(PrintStream ps) {
        File vf = new File(Launch.specjvmHomeDir + "/version.txt");
        if (vf.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(vf));
                for (String line = br.readLine(); line != null; line = br.readLine()) {
                    ps.println(line);
                }
                br.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            ps.println("Version not available.");
        }
    }
    
    static boolean parseArgs(String[] args, Properties props) {
        
        // if (args.length == 0) {
        // usage(System.out);
        // return false;
        // }
        
        for (int i = 0; i < args.length; i++) {
            // Help...
            if (args[i].equalsIgnoreCase("-h") || args[i].equalsIgnoreCase("--help") || args[i].equalsIgnoreCase("-help") || args[i].equals("/?") || args[i].equalsIgnoreCase("-?")) {
                usage(System.out);
                return false;
            }
            
            // Version...
            if (args[i].equalsIgnoreCase("-vs") || args[i].equalsIgnoreCase("-version") || args[i].equalsIgnoreCase("--version")) {
                version(System.out);
                return false;
            }
            
            // Invoke reporter...
            if (args[i].equalsIgnoreCase("-r") || args[i].equalsIgnoreCase("-reporter") || args[i].equalsIgnoreCase("--reporter")) {
                if (i + 1 < args.length) {
                    try {
                        String[] rArgs = new String[args.length - i - 1];
                        System.arraycopy(args, i + 1, rArgs, 0, rArgs.length);
                        Launch.createReport(rArgs);
                    } catch (Exception e) {
                        System.out.println("Error creating report: " + e.getMessage());
                        e.printStackTrace(System.out);
                    }
                } else {
                    System.out.println("Argument: " + args[i] + " expects a value.");
                }
                return false;
            }
            
            // Showversion...
            if (args[i].equalsIgnoreCase("-sv") || args[i].equalsIgnoreCase("-showversion") || args[i].equalsIgnoreCase("--showversion")) {
                version(System.out);
                
                // verbose
            } else if (args[i].equalsIgnoreCase("-v") || args[i].equalsIgnoreCase("-verbose") || args[i].equalsIgnoreCase("--verbose")) {
                Launch.verbose = true;
                props.setProperty(Constants.VERBOSE_PROP, Constants.TRUE_VALUE);
                props.setProperty(Constants.PRINT_PROGRESS_PROP, Constants.TRUE_VALUE);

                // Parse jvm command line args from RuntimeMXBean
            } else if (args[i].equalsIgnoreCase("-pja") || args[i].equalsIgnoreCase("--parseJvmArgs")) {
                Launch.parseDefaultArgs = true;
                
                // propfile
            } else if (args[i].equalsIgnoreCase("-pf") || args[i].equalsIgnoreCase("--propfile")) {
                if (i + 1 < args.length) {
                    props.setProperty(Constants.PROPFILE_PROP, args[++i]);
                } else {
                    System.out.println("Argument: " + args[i] + " expects a value.");
                }
                // result dir
            } else if (args[i].equalsIgnoreCase("-rd") || args[i].equalsIgnoreCase("--resultdir")) {
                if (i + 1 < args.length) {
                    props.setProperty(Constants.RESULT_DIR_PROP, args[++i]);
                } else {
                    System.out.println("Argument: " + args[i] + " expects a value.");
                }
                // startup jvm options
            }  else if (args[i].equalsIgnoreCase("-ja") || args[i].equalsIgnoreCase("--jvmArgs")) {
                if (i + 1 < args.length) {
                    props.setProperty(Constants.STARTUP_JVM_OPTIONS_PROP, args[++i]);
                } else {
                    System.out.println("Argument: " + args[i] + " expects a value.");
                }
                // startup jvm options
            } else if (args[i].equalsIgnoreCase("-jl") || args[i].equalsIgnoreCase("--jvmLauncher")) {
                if (i + 1 < args.length) {
                    props.setProperty(Constants.STARTUP_LAUNCHER, args[++i]);
                } else {
                    System.out.println("Argument: " + args[i] + " expects a value.");
                }
                // Run category
            } else if (args[i].equalsIgnoreCase("-i") || args[i].equalsIgnoreCase("--iterations")) {
                if (i + 1 < args.length) {
                    String s = args[++i];
                    if (s.equalsIgnoreCase("inf")) {
                        props.put(Constants.ITERATIONS_MINIMUM_PROP, "-1");
                        props.put(Constants.ITERATIONS_MAXIMUM_PROP, "-1");
                    } else {
                        try {
                            Integer.parseInt(s);
                            props.put(Constants.ITERATIONS_MINIMUM_PROP, s);
                            props.put(Constants.ITERATIONS_MAXIMUM_PROP, s);
                        } catch (NumberFormatException nfe) {
                            System.out.println("Error parsing " + args[i - 1] + ". Expects an integer: " + args[i]);
                        }
                    }
                } else {
                    System.out.println("Argument: " + args[i] + " expects a value.");
                }
                
                // set hardware threads override to say how many benchmark
                // threads to use
            } else if (args[i].equalsIgnoreCase("-bt") || args[i].equalsIgnoreCase("--benchmarkThreads")) {
                if (i + 1 < args.length) {
                    String s = args[++i];
                    try {
                        Integer.parseInt(s);
                        props.put(Constants.BENCHMARK_THREADS_PROP, s);
                    } catch (NumberFormatException nfe) {
                        System.out.println("Error parsing " + args[i - 1] + ". Expects an integer, not: " + args[i]);
                        return false;
                    }
                } else {
                    System.out.println("Argument: " + args[i] + " expects a value.");
                    return false;
                }
                
                // warmup time
            } else if (args[i].equalsIgnoreCase("-wt") || args[i].equalsIgnoreCase("--warmuptime")) {
                if (i + 1 < args.length) {
                    String s = args[++i];
                    try {
                        Util.getTimeAsMillis(s);
                        props.put(Constants.WARMUPTIME_PROP, s);
                    } catch (NumberFormatException nfe) {
                        System.out.println("Error parsing " + args[i - 1] + ". Expects a time string, not: " + args[i]);
                        return false;
                    }
                } else {
                    System.out.println("Argument: " + args[i] + " expects a value.");
                    return false;
                }

                // Number of fixed operationds to make
            } else if (args[i].equalsIgnoreCase("-ops") || args[i].equalsIgnoreCase("--operations")) {
                if (i + 1 < args.length) {
                    String s = args[++i];
                    try {
                        Integer.parseInt(s);
                        props.put(Constants.FIXED_OPERATIONS_PROP, s);
                        props.put(Constants.TYPE_OF_RUN_PROP, "" + TestResult.ITER);
                    } catch (NumberFormatException nfe) {
                        System.out.println("Error parsing " + args[i - 1] + ". Expects an integer, not: " + args[i]);
                        return false;
                    }
                } else {
                    System.out.println("Argument: " + args[i] + " expects a value.");
                    return false;
                }
                

            } else if (args[i].equalsIgnoreCase("-v") || args[i].equalsIgnoreCase("--verbose")) {
                if (i + 1 < args.length) {
                    String s = args[++i];
                    try {
                        Boolean.parseBoolean(s);
                        props.put(Constants.VERBOSE_PROP, s);
                    } catch (Exception e) {
                        System.out.println("Error parsing " + args[i - 1] + ". Expects a boolean value, not: " + args[i]);
                        return false;
                    }
                } else {
                    System.out.println("Argument: " + args[i] + " expects a value.");
                    return false;
                }
                
            } else if (args[i].equalsIgnoreCase("-it") || args[i].equalsIgnoreCase("--iterationTime")) {
                if (i + 1 < args.length) {
                    String s = args[++i];
                    try {
                        Util.getTimeAsMillis(s);
                        props.put(Constants.RUNTIME_PROP, s);
                    } catch (NumberFormatException nfe) {
                        System.out.println("Error parsing " + args[i - 1] + ". Expects a time string, not: " + args[i]);
                        return false;
                    }
                } else {
                    System.out.println("Argument: " + args[i] + " expects a value.");
                    return false;
                }
                
                // forced time of iteration
            } else if (args[i].equalsIgnoreCase("-fit") || args[i].equalsIgnoreCase("--forceIterationTime")) {
                if (i + 1 < args.length) {
                    String s = args[++i];
                    try {
                        Util.getTimeAsMillis(s);
                        props.put(Constants.RUNTIME_PROP, s);
                        props.put(Constants.FORCED_RUNTIME_PROP, Constants.TRUE_VALUE);
                    } catch (NumberFormatException nfe) {
                        System.out.println("Error parsing " + args[i - 1] + ". Expects a time string, not: " + args[i]);
                        return false;
                    }
                } else {
                    System.out.println("Argument: " + args[i] + " expects a value.");
                    return false;
                }
                
                // miniter
            } else if (args[i].equalsIgnoreCase("-mi") || args[i].equalsIgnoreCase("--minIter")) {
                if (i + 1 < args.length) {
                    String s = args[++i];
                    try {
                        Integer.parseInt(s);
                        props.put(Constants.ITERATIONS_MINIMUM_PROP, s);
                    } catch (NumberFormatException nfe) {
                        System.out.println("Error parsing " + args[i - 1] + ". Expects an integer: " + args[i]);
                    }
                } else {
                    System.out.println("Argument: " + args[i] + " expects a value.");
                }
                
                // maxiter
            } else if (args[i].equalsIgnoreCase("-ma") || args[i].equalsIgnoreCase("--maxIter")) {
                if (i + 1 < args.length) {
                    String s = args[++i];
                    try {
                        Integer.parseInt(s);
                        props.put(Constants.ITERATIONS_MAXIMUM_PROP, s);
                    } catch (NumberFormatException nfe) {
                        System.out.println("Error parsing " + args[i - 1] + ". Expects an integer: " + args[i]);
                    }
                } else {
                    System.out.println();
                    System.out.println("Argument: " + args[i] + " expects a value.");
                }
                
            } else if (args[i].equalsIgnoreCase("-ss") || args[i].equalsIgnoreCase("--scimarkSize")) {
                if (i + 1 < args.length) {
                    props.put(Constants.SCIMARK_SIZE_PROP, args[++i]);
                } else {
                    System.out.println();
                    System.out.println("Argument: " + args[i] + " expects a value.");
                }
                
            } else if (args[i].equalsIgnoreCase("-xd") || args[i].equalsIgnoreCase("--xmlDir")) {
                if (i + 1 < args.length) {
                    props.put(Constants.XML_VALIDATION_INPUT_DIR_PROP, args[++i]);
                } else {
                    System.out.println();
                    System.out.println("Argument: " + args[i] + " expects a value.");
                }
                
            } else if (args[i].equalsIgnoreCase("-xod") || args[i].equalsIgnoreCase("--xmlOutDir")) {
                if (i + 1 < args.length) {
                    props.put(Constants.XML_TRANSFORM_OUT_DIR_PROP, args[++i]);
                } else {
                    System.out.println();
                    System.out.println("Argument: " + args[i] + " expects a value.");
                }
                
            } else if (args[i].equalsIgnoreCase("-xlod") || args[i].equalsIgnoreCase("--xmlLeaveOutDir")) {
                if (i + 1 < args.length) {
                    props.put(Constants.XML_TRANSFORM_LEAVE_OUT_DIR_PROP, args[++i]);
                } else {
                    System.out.println();
                    System.out.println("Argument: " + args[i] + " expects a value.");
                }
                
            } else if (args[i].equalsIgnoreCase("-coe") || args[i].equalsIgnoreCase("--contintueOnError")) {
                props.put(Constants.CONTINUE_ON_ERROR_PROP, "true");

            } else if (args[i].equalsIgnoreCase("-ict") || args[i].equalsIgnoreCase("--ignoreCheckTest")) {
                props.put(Constants.INITIAL_CHECK_PROP, "false");
                
            } else if (args[i].equalsIgnoreCase("-ikv") || args[i].equalsIgnoreCase("-icsv") || args[i].equalsIgnoreCase("--ignoreCheckSumValidation")) {
                props.put(Constants.INITIAL_CHECKSUM_CHECK_PROP, "false");

            } else if (args[i].equalsIgnoreCase("-crf") || args[i].equalsIgnoreCase("--createRawFile")) {
                if (i + 1 < args.length) {
                    props.put(Constants.CREATE_XML_REPORT_PROP, args[++i]);
                } else {
                    System.out.println();
                    System.out.println("Argument: " + args[i] + " expects a value.");
                }
                
            } else if (args[i].equalsIgnoreCase("-ctf") || args[i].equalsIgnoreCase("--createTextFile")) {
                if (i + 1 < args.length) {
                    props.put(Constants.CREATE_TXT_REPORT_PROP, args[++i]);
                } else {
                    System.out.println();
                    System.out.println("Argument: " + args[i] + " expects a value.");
                }
                
            } else if (args[i].equalsIgnoreCase("-chf") || args[i].equalsIgnoreCase("--createHtmlFile")) {
                if (i + 1 < args.length) {
                    props.put(Constants.CREATE_HTML_REPORT_PROP, args[++i]);
                } else {
                    System.out.println();
                    System.out.println("Argument: " + args[i] + " expects a value.");
                }
                
                // Lagom workload
            } else if (args[i].equalsIgnoreCase("-lagom") || args[i].equalsIgnoreCase("--lagom")) {
                Configuration.defaultProperties = Configuration.defaultPropertiesLagom;
                Configuration.propsRequirements = Configuration.propsRequirementsLagom;
                Launch.workLoad = Constants.WORKLOAD_NAME_LAGOM;

                // Base workload
            } else if (args[i].equalsIgnoreCase("-base") || args[i].equalsIgnoreCase("--base")) {
            	Configuration.defaultProperties = Configuration.defaultPropertiesSPECbase;
            	Configuration.propsRequirements = Configuration.propsRequirementsSPECbase;
            	Launch.workLoad = Constants.WORKLOAD_NAME_SPEC_BASE;

                // Peak workload
            } else if (args[i].equalsIgnoreCase("-peak") || args[i].equalsIgnoreCase("--peak")) {
            	Configuration.defaultProperties = Configuration.defaultPropertiesSPECpeak;
            	Configuration.propsRequirements = Configuration.propsRequirementsSPECpeak;
            	Launch.workLoad = Constants.WORKLOAD_NAME_SPEC_PEAK;

                // arbitrary property
            } else if (args[i].length() > 2 && args[i].indexOf('=') > 0 && args[i].startsWith("-D")) {
                int ei = args[i].indexOf('=');
                String key = args[i].substring(2, ei);
                String value = args[i].substring(ei + 1, args[i].length());
                props.put(key, value);
                
                // Special case specjvm home dir
                if (key.equals(Constants.HOMEDIR_PROP)) {
                	Launch.specjvmHomeDir = value;
                }
                
                // all benchmarks
            } else if (args[i].equalsIgnoreCase("all")) {
                props.put(Constants.BENCHMARKS_PROP, Constants.VALID_BENCHMARKS_SPEC);

                // shortcut all compiler benchmarks
            } else if (args[i].equalsIgnoreCase(Constants.COMPILER_BNAME_PREFIX)) {
                String bms = props.getProperty(Constants.BENCHMARKS_PROP);
                bms = (bms == null ? Constants.BM_SHORTCUT_COMPILER : bms + " " + Constants.BM_SHORTCUT_COMPILER);
                props.put(Constants.BENCHMARKS_PROP, bms);

                // shortcut all crypto benchmarks
            } else if (args[i].equalsIgnoreCase(Constants.CRYPTO_BNAME_PREFIX)) {
                String bms = props.getProperty(Constants.BENCHMARKS_PROP);
                bms = (bms == null ? Constants.BM_SHORTCUT_CRYPTO : bms + " " + Constants.BM_SHORTCUT_CRYPTO);
                props.put(Constants.BENCHMARKS_PROP, bms);

                // shortcut all scimark benchmarks
            } else if (args[i].equalsIgnoreCase(Constants.SCIMARK_BNAME_PREFIX)) {
                String bms = props.getProperty(Constants.BENCHMARKS_PROP);
                bms = (bms == null ? Constants.BM_SHORTCUT_SCIMARK_ALL : bms + " " + Constants.BM_SHORTCUT_SCIMARK_ALL);
                props.put(Constants.BENCHMARKS_PROP, bms);
                
                // shortcut all scimark.small benchmarks
            } else if (args[i].equalsIgnoreCase(Constants.SCIMARK_SMALL_GNAME)) {
                String bms = props.getProperty(Constants.BENCHMARKS_PROP);
                bms = (bms == null ? Constants.BM_SHORTCUT_SCIMARK_SMALL : bms + " " + Constants.BM_SHORTCUT_SCIMARK_SMALL);
                props.put(Constants.BENCHMARKS_PROP, bms);

                // shortcut all scimark.large benchmarks
            } else if (args[i].equalsIgnoreCase(Constants.SCIMARK_LARGE_GNAME)) {
                String bms = props.getProperty(Constants.BENCHMARKS_PROP);
                bms = (bms == null ? Constants.BM_SHORTCUT_SCIMARK_LARGE : bms + " " + Constants.BM_SHORTCUT_SCIMARK_LARGE);
                props.put(Constants.BENCHMARKS_PROP, bms);

                // shortcut all startup benchmarks
            } else if (args[i].equalsIgnoreCase(Constants.STARTUP_BNAME_PREFIX)) {
                String bms = props.getProperty(Constants.BENCHMARKS_PROP);
                bms = (bms == null ? Constants.VALID_BENCHMARKS_STARTUP : bms + " " + Constants.VALID_BENCHMARKS_STARTUP);
                props.put(Constants.BENCHMARKS_PROP, bms);

            } else if (args[i].equalsIgnoreCase("throughput")) {
                String bms = props.getProperty(Constants.BENCHMARKS_PROP);
                bms = (bms == null ? Constants.VALID_BENCHMARKS_THROUGHPUT : bms + " " + Constants.VALID_BENCHMARKS_THROUGHPUT);
                props.put(Constants.BENCHMARKS_PROP, bms);

                // shortcut all xml benchmarks
            } else if (args[i].equalsIgnoreCase(Constants.XML_BNAME_PREFIX)) {
                String bms = props.getProperty(Constants.BENCHMARKS_PROP);
                bms = (bms == null ? Constants.BM_SHORTCUT_XML : bms + " " + Constants.BM_SHORTCUT_XML);
                props.put(Constants.BENCHMARKS_PROP, bms);

                // benchmarks
            } else if (Util.isBenchmark(args[i])) {
                String bms = props.getProperty(Constants.BENCHMARKS_PROP);
                bms = (bms == null ? args[i] : bms + " " + args[i]);
                props.put(Constants.BENCHMARKS_PROP, bms);
                
                // Giving up
            } else {
                System.out.println("Unknown argument or benchmark: " + args[i] + "\n");
                usage(System.out);
                System.out.println("\nUnknown argument or benchmark: " + args[i] + "\n");
                return false;
            }
        }
        
        return true;
    }
    
}
