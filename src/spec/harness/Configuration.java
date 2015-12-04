package spec.harness;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import java.util.StringTokenizer;

import spec.benchmarks.startup.StartupBenchmarkOutput;
import spec.harness.analyzer.AnalyzersRunner;
import spec.harness.results.SuiteResult;
import spec.harness.results.TestResult;

public class Configuration {
	
    // package private, to be accessed via Util
    static Properties userProperties;
    static Properties invalidProperties;
    static Properties additionalConfigurationProperties;
    static Properties defaultProperties;
    static Properties defaultPropertiesSPECbase;
    static Properties defaultPropertiesSPECpeak;
    static Properties defaultPropertiesLagom;
    static HashMap<String, String> propsRequirements;
    static HashMap<String, String> propsRequirementsSPECbase;
    static HashMap<String, String> propsRequirementsSPECpeak;
    static HashMap<String, String> propsRequirementsLagom;

    static void setupProp(String name, String defValueSPECbase, String requirementSPECbase, String defValueSPECpeak, String requirementSPECpeak, String defValueLagom, String requirementLagom) {
        defaultPropertiesSPECbase.put(name, (defValueSPECbase == null ? "null" : defValueSPECbase));
        propsRequirementsSPECbase.put(name, requirementSPECbase);
        defaultPropertiesSPECpeak.put(name, (defValueSPECpeak == null ? "null" : defValueSPECpeak));
        propsRequirementsSPECpeak.put(name, requirementSPECpeak);
        defaultPropertiesLagom.put(name, (defValueLagom == null ? "null" : defValueLagom));
        propsRequirementsLagom.put(name, requirementLagom);
    }
    
    static void setupProp(String name, String defValueSPEC, String requirementSPEC, String defValueLagom, String requirementLagom) {
        setupProp(name, defValueSPEC, requirementSPEC, defValueSPEC, requirementSPEC, defValueLagom, requirementLagom);
    }
    
    static void setupProp(String name, String defValue, String requirement) {
        setupProp(name, defValue, requirement, defValue, requirement);
    }
    
    static String getFixedOperationsProp(String bname) {
        return Constants.FIXED_OPERATIONS_PROP + "." + bname;
    }
    
	static void setupProperties() {
		invalidProperties = new Properties();
		additionalConfigurationProperties = new Properties();
		defaultPropertiesSPECbase = new Properties();
		defaultPropertiesSPECpeak= new Properties();
		defaultPropertiesLagom = new Properties();
		defaultProperties = defaultPropertiesSPECbase;
		propsRequirementsSPECbase = new HashMap<String, String>();
		propsRequirementsSPECpeak = new HashMap<String, String>();
		propsRequirementsLagom = new HashMap<String, String>();
		propsRequirements = propsRequirementsSPECbase;
        
        // Run configuration
        setupProp(Constants.NAME_OF_RUN_PROP, Constants.WORKLOAD_NAME_SPEC_BASE, Constants.MATCH_REQ, Constants.WORKLOAD_NAME_SPEC_PEAK, Constants.MATCH_REQ, Constants.WORKLOAD_NAME_LAGOM, Constants.MATCH_REQ);
        setupProp(Constants.TYPE_OF_RUN_PROP, "" + TestResult.TIMED, Constants.MATCH_REQ, "" + TestResult.ITER, Constants.MATCH_REQ);
        setupProp(Constants.BENCHMARK_THREADS_PROP, Constants.HWT_VALUE, Constants.WHATEVER_REQ);
        setupProp(Constants.FIXED_OPERATIONS_PROP, null, Constants.NOT_SET_REQ, "-1", Constants.WHATEVER_REQ);
        setupProp(Constants.RUNTIME_PROP, "240s", Constants.MATCH_REQ, "240s", Constants.TIME_AT_LEAST_REQ, "0s", Constants.MATCH_REQ);
        setupProp(Constants.FORCED_RUNTIME_PROP, null, Constants.NOT_SET_REQ, null, Constants.NOT_SET_REQ);
        setupProp(Constants.WARMUPTIME_PROP, "120s", Constants.MATCH_REQ, "120s", Constants.WHATEVER_REQ, "0s", Constants.MATCH_REQ);
        setupProp(Constants.ITERATIONS_MINIMUM_PROP, "1", Constants.MATCH_REQ, "1", Constants.MATCH_REQ, "1", Constants.WHATEVER_REQ);
        setupProp(Constants.ITERATIONS_MAXIMUM_PROP, "1", Constants.MATCH_REQ, "1", Constants.MATCH_REQ, "1", Constants.WHATEVER_REQ);
        setupProp(Constants.ITERATIONS_MINIMUM_PROP + ".check", "1", Constants.MATCH_REQ, "1", Constants.MATCH_REQ);
        setupProp(Constants.ITERATIONS_MAXIMUM_PROP + ".check", "1", Constants.MATCH_REQ, "1", Constants.MATCH_REQ);
        setupProp(Constants.ITER_DO_SYSTEMGC_PROP, "false", Constants.MATCH_REQ, "false", Constants.MATCH_REQ);
        setupProp(Constants.ITER_DELAYTIME_PROP, "0s", Constants.TIME_MAX_5s_REQ, "0s", Constants.TIME_MAX_5s_REQ);
        setupProp(Constants.BM_DO_SYSTEMGC_PROP, "false", Constants.MATCH_REQ, "false", Constants.MATCH_REQ);
        setupProp(Constants.BM_DELAYTIME_PROP, "0s", Constants.TIME_MAX_5s_REQ, "0s", Constants.TIME_MAX_5s_REQ);
        setupProp(Constants.STARTUP_JVM_OPTIONS_PROP, null, Constants.NOT_SET_REQ, null, Constants.WHATEVER_REQ, null, Constants.WHATEVER_REQ);
        setupProp(Constants.STARTUP_LAUNCHER, null, Constants.SPECIAL_REQ, null, Constants.SPECIAL_REQ);
        
        // Benchmark specific run configuration for Lagom workload
        setupProp(getFixedOperationsProp(Constants.COMPILER_COMPILER_BNAME), null, Constants.NOT_SET_REQ, "20", Constants.MATCH_REQ);
        setupProp(getFixedOperationsProp(Constants.COMPILER_SUNFLOW_BNAME), null, Constants.NOT_SET_REQ, "20", Constants.MATCH_REQ);
        setupProp(getFixedOperationsProp(Constants.COMPRESS_BNAME), null, Constants.NOT_SET_REQ, "50", Constants.MATCH_REQ);
        setupProp(getFixedOperationsProp(Constants.CRYPTO_AES_BNAME), null, Constants.NOT_SET_REQ, "20", Constants.MATCH_REQ);
        setupProp(getFixedOperationsProp(Constants.CRYPTO_RSA_BNAME), null, Constants.NOT_SET_REQ, "150", Constants.MATCH_REQ);
        setupProp(getFixedOperationsProp(Constants.CRYPTO_SIGNVERIFY_BNAME), null, Constants.NOT_SET_REQ, "125", Constants.MATCH_REQ);
        setupProp(getFixedOperationsProp(Constants.DERBY_BNAME), null, Constants.NOT_SET_REQ, "30", Constants.MATCH_REQ);
        setupProp(getFixedOperationsProp(Constants.MPEGAUDIO_BNAME), null, Constants.NOT_SET_REQ, "50", Constants.MATCH_REQ);
        setupProp(getFixedOperationsProp(Constants.SCIMARK_LARGE_FFT_BNAME), null, Constants.NOT_SET_REQ, "10", Constants.MATCH_REQ);
        setupProp(getFixedOperationsProp(Constants.SCIMARK_LARGE_LU_BNAME), null, Constants.NOT_SET_REQ, "4", Constants.MATCH_REQ);
        setupProp(getFixedOperationsProp(Constants.SCIMARK_MONTE_CARLO_BNAME), null, Constants.NOT_SET_REQ, "900", Constants.MATCH_REQ);
        setupProp(getFixedOperationsProp(Constants.SCIMARK_LARGE_SOR_BNAME), null, Constants.NOT_SET_REQ, "15", Constants.MATCH_REQ);
        setupProp(getFixedOperationsProp(Constants.SCIMARK_LARGE_SPARSE_BNAME), null, Constants.NOT_SET_REQ, "10", Constants.MATCH_REQ);
        setupProp(getFixedOperationsProp(Constants.SCIMARK_SMALL_FFT_BNAME), null, Constants.NOT_SET_REQ, "100", Constants.MATCH_REQ);
        setupProp(getFixedOperationsProp(Constants.SCIMARK_SMALL_LU_BNAME), null, Constants.NOT_SET_REQ, "125", Constants.MATCH_REQ);
        setupProp(getFixedOperationsProp(Constants.SCIMARK_SMALL_SOR_BNAME), null, Constants.NOT_SET_REQ, "75", Constants.MATCH_REQ);
        setupProp(getFixedOperationsProp(Constants.SCIMARK_SMALL_SPARSE_BNAME), null, Constants.NOT_SET_REQ, "25", Constants.MATCH_REQ);
        setupProp(getFixedOperationsProp(Constants.SERIAL_BNAME), null, Constants.NOT_SET_REQ, "25", Constants.MATCH_REQ);
        setupProp(getFixedOperationsProp(Constants.SUNFLOW_BNAME), null, Constants.NOT_SET_REQ, "30", Constants.MATCH_REQ);
        setupProp(getFixedOperationsProp(Constants.XML_TRANSFORM_BNAME), null, Constants.NOT_SET_REQ, "7", Constants.MATCH_REQ);
        setupProp(getFixedOperationsProp(Constants.XML_VALIDATION_BNAME), null, Constants.NOT_SET_REQ, "40", Constants.MATCH_REQ);
        
        
        // Harness configuration
        setupProp(Constants.VERIFY_PROP, Constants.TRUE_VALUE, Constants.MATCH_REQ);
        setupProp(Constants.VALIDITY_CHECKSUM_PROP, Constants.TRUE_VALUE, Constants.MATCH_REQ);
        setupProp(Constants.VALID_SETUP_PROP, null, Constants.NOT_SET_REQ);
        setupProp(Constants.CREATE_XML_REPORT_PROP, Constants.TRUE_VALUE, Constants.MATCH_REQ);
        setupProp(Constants.CREATE_TXT_REPORT_PROP, Constants.TRUE_VALUE, Constants.WHATEVER_REQ);
        setupProp(Constants.CREATE_HTML_REPORT_PROP, Constants.TRUE_VALUE, Constants.WHATEVER_REQ);
        setupProp(Constants.GEN_VALIDITY_FILE_PROP, Constants.FALSE_VALUE, Constants.MATCH_REQ);
        setupProp(Constants.HARNESS_SPIN_AT_END_PROP, Constants.FALSE_VALUE, Constants.MATCH_REQ);
        setupProp(Constants.VERBOSE_PROP, Constants.FALSE_VALUE, Constants.WHATEVER_REQ);
        setupProp(Constants.PRINT_PROGRESS_PROP, Constants.FALSE_VALUE, Constants.WHATEVER_REQ);
        
        // Environment configuration
        setupProp(Constants.HOMEDIR_PROP, ".", Constants.WHATEVER_REQ, ".", Constants.WHATEVER_REQ);
        setupProp(Constants.RESULT_DIR_PROP, "./results", Constants.WHATEVER_REQ, "./results", Constants.WHATEVER_REQ);
        setupProp(Constants.PROPFILE_PROP, null, Constants.WHATEVER_REQ, null, Constants.WHATEVER_REQ);
        setupProp(Constants.PROPFILE_ADDITIONAL_PROP, null, Constants.WHATEVER_REQ, null, Constants.WHATEVER_REQ);
        setupProp(Constants.BENCHMARK_THREADS_HW_OVERRIDE_PROP, null, Constants.WHATEVER_REQ, null, Constants.NOT_SET_REQ);
        
        // Analyzer configuration
        setupProp(Constants.ANALYZER_NAMES_PROP, "", Constants.WHATEVER_REQ);
        setupProp(Constants.ANALYZER_FREQUENCY_PROP, "1s", Constants.WHATEVER_REQ);
        setupProp(Constants.ANALYZER_POWER_HOST, "127.0.0.1", Constants.WHATEVER_REQ);
        setupProp(Constants.ANALYZER_POWER_PORT, "8888", Constants.WHATEVER_REQ);
        setupProp(Constants.ANALYZER_POWER_VERBOSE, "false", Constants.WHATEVER_REQ);
        setupProp(Constants.ANALYZER_POWER_DUMMY, "false", Constants.WHATEVER_REQ);
		setupProp(Constants.ANALYZER_TEMP_HOST, "127.0.0.1", Constants.WHATEVER_REQ);
		setupProp(Constants.ANALYZER_TEMP_PORT, "8889", Constants.WHATEVER_REQ);
		setupProp(Constants.ANALYZER_TEMP_VERBOSE, "false", Constants.WHATEVER_REQ);
		setupProp(Constants.ANALYZER_TEMP_DUMMY, "false", Constants.WHATEVER_REQ);
		setupProp(Constants.ANALYZER_METER_VERBOSE, "false", Constants.WHATEVER_REQ);
		setupProp(Constants.ANALYZER_SENSOR_VERBOSE, "false", Constants.WHATEVER_REQ);
        
        
        // Benchmark configuration
        setupProp(Constants.BENCHMARKS_PROP, Constants.VALID_BENCHMARKS_SPEC, Constants.MATCH_REQ, Constants.VALID_BENCHMARKS_LAGOM, Constants.MATCH_REQ);
        setupProp(Constants.CONTINUE_ON_ERROR_PROP, Constants.FALSE_VALUE, Constants.WHATEVER_REQ);
        setupProp(Constants.INITIAL_CHECK_PROP, Constants.TRUE_VALUE, Constants.MATCH_REQ);
        setupProp(Constants.INITIAL_CHECKSUM_CHECK_PROP, Constants.TRUE_VALUE, Constants.MATCH_REQ);
        setupProp(Constants.BENCHMARK_THREADS_PROP + "." + Constants.CHECK_BNAME, "1", Constants.MATCH_REQ);
        setupProp(Constants.BENCHMARK_THREADS_PROP + "." + Constants.SUNFLOW_BNAME, "0.5x" + Constants.HWT_VALUE, Constants.WHATEVER_REQ);        
        setupProp(Constants.SCIMARK_SIZE_PROP, "DEFAULT", Constants.MATCH_REQ);
        setupProp(Constants.SUNFLOW_THREADS, "4", Constants.MATCH_REQ);
        setupProp(Constants.XML_TRANSFORM_OUT_DIR_PROP, null, Constants.NOT_SET_REQ);
        setupProp(Constants.XML_TRANSFORM_LEAVE_OUT_DIR_PROP, null, Constants.NOT_SET_REQ);
        setupProp(Constants.XML_VALIDATION_INPUT_DIR_PROP, null, Constants.SPECIAL_REQ);
        setupProp(Constants.STARTUP_RUN_ALL_SCIMARK, null, Constants.NOT_SET_REQ);
        setupProp(Constants.STARTUP_RUN_ALL_CRYPTO, null, Constants.NOT_SET_REQ);
        setupProp(Constants.DEBUG_DERBY, null, Constants.WHATEVER_REQ);
        setupProp(Constants.DEBUG_STARTUP, null, Constants.WHATEVER_REQ);
        setupProp(Constants.DEBUG_VALIDATION, null, Constants.WHATEVER_REQ);       

	}
	
    public static void checkSetup(SuiteResult sResult) {
        
        String benchmarks = Util.getProperty(Constants.BENCHMARKS_PROP, null);
        if (benchmarks == null) {
            throw new StopBenchmarkException("No benchmarks specified, property is null.");
        }
        
        if (Launch.workLoad.equals(Constants.WORKLOAD_NAME_NOT_SET)) {
            // Check if this is a base or a peak run (lagom would not end up here)
        	String [] allJvmArgs = Util.parseDefaultJvmArgs();
            String jvmOptionsStartup = Util.getProperty(Constants.STARTUP_JVM_OPTIONS_PROP, null);
            if (// If JVM arguments are used, it is a peak run.
            		(allJvmArgs[0] == null || allJvmArgs[0].equals("n/a")
            				|| allJvmArgs[0].equals("") || allJvmArgs[0].equals("-showversion"))
            	// If JVM arguments are used, it is a peak run.
                    && (jvmOptionsStartup == null || jvmOptionsStartup.equals("n/a")
                            || jvmOptionsStartup.equals("") || jvmOptionsStartup.equals("-showversion"))        	    
                // If iterations are more than one, it is a peak run.
            		&& (Util.getUserProperty(Constants.ITERATIONS_MINIMUM_PROP, null) == null 
            				|| Util.getUserProperty(Constants.ITERATIONS_MINIMUM_PROP, null)
            				.equals(Util.getDefaultProperty(Constants.ITERATIONS_MINIMUM_PROP, null)))
            		&& (Util.getUserProperty(Constants.ITERATIONS_MAXIMUM_PROP, null) == null 
            				|| Util.getUserProperty(Constants.ITERATIONS_MAXIMUM_PROP, null)
            				.equals(Util.getDefaultProperty(Constants.ITERATIONS_MAXIMUM_PROP, null)))
        	   // If warmup time or run time time is changed, it is a peak run.
            		&& (Util.getUserProperty(Constants.WARMUPTIME_PROP, null) == null 
            				|| Util.getUserProperty(Constants.WARMUPTIME_PROP, null)
            				.equals(Util.getDefaultProperty(Constants.WARMUPTIME_PROP, null)))
            		&& (Util.getUserProperty(Constants.RUNTIME_PROP, null) == null 
            				|| Util.getUserProperty(Constants.RUNTIME_PROP, null)
            				.equals(Util.getDefaultProperty(Constants.RUNTIME_PROP, null)))) {
            	Configuration.defaultProperties = Configuration.defaultPropertiesSPECbase;
            	Configuration.propsRequirements = Configuration.propsRequirementsSPECbase;
                Launch.workLoad = Constants.WORKLOAD_NAME_SPEC_BASE;
            } else {
                defaultProperties = defaultPropertiesSPECpeak;
                propsRequirements = propsRequirementsSPECpeak;
                Launch.workLoad = Constants.WORKLOAD_NAME_SPEC_PEAK;
            }
        }
        
        
        // Go through all properties in default and validate what you get in default.
        for (Enumeration dk = defaultProperties.keys(); dk.hasMoreElements(); ) {
            String key = (String) dk.nextElement();
            validateProperty(sResult, key, Util.getProperty(key, null), Util.getDefaultProperty(key, null), propsRequirements.get(key), key);
            // Go through all properties in default with benchmark addon and validate what you get in default.
            for (StringTokenizer st = new StringTokenizer(benchmarks); st.hasMoreElements(); ) {
                String bm = st.nextToken().trim();
                validateProperty(sResult, key + "." + bm, Util.getProperty(key, bm), Util.getDefaultProperty(key, bm), propsRequirements.get(key), key);
            }
        }
        
        // Don't accept unknown properties.
        // Go through all properties in jvm and make sure they are in default.
        for (Enumeration sk = userProperties.keys(); sk.hasMoreElements();) {
            String key = (String) sk.nextElement();
            if (key.startsWith("spec.jvm2008.report")) {
                continue;
            }

            boolean found = defaultProperties.containsKey(key);
            for (StringTokenizer st = new StringTokenizer(Constants.VALID_BENCHMARKS_SPEC);
                 !found && st.hasMoreElements();) {
                String bm = st.nextToken().trim();
                if (key.endsWith("." + bm)) {
                    String shortKey = key.substring(0, key.length() - 1 - bm.length());
                    if (defaultProperties.containsKey(shortKey)) {
                        found = true;
                    }
                }
            }

            if (!found) {
                addViolation(sResult, key, "Property " + key + " unknown.");
            }
        }

        // Check that analyzer classes are sound (and initiate classes).
        try {
            AnalyzersRunner.setupAnalyzerClasses(Util.getProperty(Constants.ANALYZER_NAMES_PROP, null));
        } catch(Throwable t) {
            throw new StopBenchmarkException("Error setting up analyzers: " + t.getClass().getName() + ": " + t.getMessage(), t);
        }
    }
    
    private static void validateProperty(SuiteResult sResult, String key, String value, String defValue, String req, String baseKey) {
        if (req == null) {
            throw new StopBenchmarkException("Requirement for property " + key + " is not set.");
        }
        // Special case benchmarks string.
        if (key != null && key.equals(Constants.BENCHMARKS_PROP)) {
            if (req.equals(Constants.MATCH_REQ)) {
                if (value == null || !value.equals(defValue)) {
                    addViolation(sResult, baseKey, "Not a compliant sequence of benchmarks for publication.");
                }
            } else {
                throw new StopBenchmarkException("Don't know how to handle requirement " + req + " for property " + key);
            }
        }
        
        if (req.equals(Constants.WHATEVER_REQ)) {
            if ((value == null && defValue != null)
            || (value != null && defValue == null)
            || (value != null && !value.equals(defValue))) {
                addConfigurationDetail(sResult, baseKey, key + "=" + value);
            }
        } else if (req.equals(Constants.NOT_SET_REQ)) {
            // This property may not be set.
            if (value != null) {
                addViolation(sResult, baseKey, "Property " + key + " not allowed in publication run.");
            }
        } else if (req.equals(Constants.MATCH_REQ)) {
            // This property must match.
            if (value == null || !value.equals(defValue)) {
                addViolation(sResult, baseKey, "Property " + key + " must be " + defValue + " for publication.");
            }
        } else if (req.equals(Constants.TIME_MAX_5s_REQ)) {
            // This property must be no more than 5 seconds.
            if (value == null || Util.getTimeAsMillis(value) > 5000) {
                addViolation(sResult, baseKey, "Property " + key + " may not be higher than 5 seconds for publication.");
            } else if (!value.equals(defValue)) {
                addConfigurationDetail(sResult, baseKey, key + "=" + value);
            }
        } else if (req.equals(Constants.AT_LEAST_1_MARK_IF_HIGHER)) {
            // This property must at least 1, comment if it is above.
        	try {
        		int i = Integer.parseInt(value);
        		if (i < 1) {
                    addViolation(sResult, baseKey, "Property " + key + " must be at least 1.");
        		} else if (i > 1) {
                    addConfigurationDetail(sResult, baseKey, key + "=" + value);
        		}
        	} catch(Exception e) {
                addViolation(sResult, baseKey, "Property " + key + " must be set to an integer.");
        	}
            
        } else if (req.equals(Constants.TIME_AT_LEAST_REQ)) {
            // This time property must be at least as much as default.
            if (value == null || Util.getTimeAsMillis(value) < Util.getTimeAsMillis(defValue)) {
                addViolation(sResult, baseKey, "Property " + key + " must be at least " + Util.getTimeAsSeconds(defValue) + " seconds for publication.");
            } else if (!value.equals(defValue)) {
                addConfigurationDetail(sResult, baseKey, key + "=" + value);
            }
        } else if (req.equals(Constants.SPECIAL_REQ)) {
            if (key != null && key.startsWith(Constants.XML_VALIDATION_INPUT_DIR_PROP)) {
                if (value != null && !"resources/xml.validation".equals(value)) {
                    addViolation(sResult, baseKey, "Property " + key + " should be either not set or equal to resources/xml.validation" + " for publication.");
                }
            } else {
                if (Constants.STARTUP_LAUNCHER.equals(key) && value != null && !checkLauncher(value)) {
                    addViolation(sResult, baseKey, "Property " + key + " should point to the same as a current launcher for publication.");
                }
            }
            
        } else {
            throw new StopBenchmarkException("Don't know how to handle requirement: " + req);
        }
    }
    
    static Properties readProperties(String name) throws FileNotFoundException, IOException {
        
        Properties props = new Properties();
        
        if (name != null) {
            
            String fullFileName = null;
            {
                String potFullFileName;
            	potFullFileName = name;
            	if (new File(potFullFileName).exists()) {
            		fullFileName = potFullFileName;
                } else {
                    potFullFileName = Launch.specjvmHomeDir + "/props/" + name;
                    if (new File(potFullFileName).exists()) {
                        fullFileName = potFullFileName;
                	} 
                }
            }
            
            FileInputStream fisFullFileName = null;
            
            try {
                fisFullFileName = new FileInputStream(fullFileName);
                props.load(fisFullFileName);
                props.setProperty(Constants.PROPFILE_PROP, fullFileName);
                props.setProperty(Constants.HOMEDIR_PROP, Launch.specjvmHomeDir);
            } catch (FileNotFoundException fnfe) {
                throw new FileNotFoundException("Can't find properties file '" + name + "'");
            } finally {
                fisFullFileName.close();
            }
            
            String nextFileName = props.getProperty("specjvm.additional.properties.file");
            if (nextFileName != null) {
                String nextFileNameFull = null;
                String nextPotFullFileName = null; 
                nextPotFullFileName = nextFileName;
                if (new File(nextPotFullFileName).exists()) {
                    nextFileNameFull = nextPotFullFileName;
				} else {
					nextPotFullFileName = Launch.specjvmHomeDir + "/props/" + nextFileName;
					if (new File(nextPotFullFileName).exists()) {
						nextFileNameFull = nextPotFullFileName;
					}
				}
                
                FileInputStream fisFileNameFull = null;
                
                try {
                    fisFileNameFull = new FileInputStream(nextFileNameFull);
                    props.load(fisFileNameFull);
                } catch (FileNotFoundException fnfe) {
                    throw new FileNotFoundException("Can't find additional properties file '" + nextFileName + "'");
                } finally {
                    fisFileNameFull.close();
                }
            }
        }
        return props;
    }
    
    static void addViolation(SuiteResult sResult, String key, String message) {
        if (Configuration.invalidProperties.get(key) == null) {
        	Configuration.invalidProperties.put(key, message);
            sResult.addViolation(message);
        }
    }
    
    static void addConfigurationDetail(SuiteResult sResult, String key, String message) {
        if (Configuration.additionalConfigurationProperties.get(key) == null) {
        	Configuration.additionalConfigurationProperties.put(key, message);
            sResult.addConfiguration(message);
        }
    }
    
    private static boolean checkLauncher(String launcher) {
        Runtime run = Runtime.getRuntime();
        String launcherLog = "";
        try {
            Process p = run.exec(launcher
                    + " -classpath " + System.getProperty("java.class.path")
                    + " spec.harness.VMVersionTest");
            StartupBenchmarkOutput out = new StartupBenchmarkOutput(p
                    .getInputStream(), true);
            out.start();
            p.waitFor();
            launcherLog = out.log;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        return launcherLog.equals(VMVersionTest.getVersionInfo());
    }

    
    static void setupReportInfo(SuiteResult sr, Properties specjvmProps) {
        
        if (Launch.verbose) {
            Context.getOut().println("Report values: ");
        }

        sr.addRunInfo(Constants.REPORTER_RUN_DATE, null, new Date().toString());
        sr.addRunInfo(Constants.REPORTER_RUN_TESTER, specjvmProps, System.getProperty("user.name", "n/a"));
        sr.addRunInfo(Constants.REPORTER_RUN_SUBMITTER, specjvmProps, "n/a");
        sr.addRunInfo(Constants.REPORTER_RUN_SUBMITTER_URL,specjvmProps, "n/a");
        sr.addRunInfo(Constants.REPORTER_RUN_LOCATION, specjvmProps, "n/a");
        sr.addRunInfo(Constants.REPORTER_RUN_LICENSE, specjvmProps, "n/a");
        
        sr.addJvmInfo(Constants.REPORTER_JVM_NAME, specjvmProps, System.getProperty("java.vm.name", "n/a"));
        String jvv = System.getProperty("java.vm.version", "");
        String jvi = System.getProperty("java.vm.info", "");
        sr.addJvmInfo(Constants.REPORTER_JVM_VERSION, specjvmProps,  jvv + ((jvv.length() > 0 && jvi.length() > 0) ? " " : "") + jvi);
        sr.addJvmInfo(Constants.REPORTER_JVM_VENDOR, specjvmProps, System.getProperty("java.vm.vendor", System.getProperty("java.vendor", "n/a")));
        sr.addJvmInfo(Constants.REPORTER_JVM_VENDOR_URL, specjvmProps, System.getProperty("java.vm.vendor.url", System.getProperty("java.vendor.url", "n/a")));
        sr.addJvmInfo(Constants.REPORTER_JVM_JAVA_SPECIFICATION, specjvmProps, System.getProperty("java.specification.version", "n/a"));
        sr.addJvmInfo(Constants.REPORTER_JVM_ADDRESS_BITS,specjvmProps, System.getProperty("sun.arch.data.model", "n/a"));
        sr.addJvmInfo(Constants.REPORTER_JVM_AVAILABLE_DATE, specjvmProps, "n/a");
        
        String [] jvmArgs = Util.parseDefaultJvmArgs();
        
        sr.addJvmInfo(Constants.REPORTER_JVM_COMMAND_LINE, specjvmProps, jvmArgs[0]);
        sr.addJvmInfo(Constants.REPORTER_JVM_COMMAND_LINE_INITIAL_HEAP_SIZE, specjvmProps, jvmArgs[1]);
        sr.addJvmInfo(Constants.REPORTER_JVM_COMMAND_LINE_MAX_HEAP_SIZE, specjvmProps, jvmArgs[2]);
        String scl = Util.getProperty(Constants.STARTUP_JVM_OPTIONS_PROP, null);
        sr.addJvmInfo(Constants.REPORTER_JVM_STARTUP_COMMAND_LINE, specjvmProps, scl != null ? scl : " ");
        String sl = Util.getProperty(Constants.STARTUP_LAUNCHER, null);
        sr.addJvmInfo(Constants.REPORTER_JVM_STARTUP_LAUNCHER, specjvmProps, sl != null ? sl : "default");
        sr.addJvmInfo(Constants.REPORTER_JVM_OTHER_TUNING, specjvmProps, " ");
        sr.addJvmInfo(Constants.REPORTER_JVM_APP_CLASS_PATH, null, System.getProperty("java.class.path", "n/a"));
        sr.addJvmInfo(Constants.REPORTER_JVM_BOOT_CLASS_PATH, null, System.getProperty("sun.boot.class.path", "n/a"));
        
        sr.addSwInfo(Constants.REPORTER_OS_NAME, specjvmProps, System.getProperty("os.name", "n/a"));
        sr.addSwInfo(Constants.REPORTER_OS_ADDRESS_BITS, specjvmProps, "n/a");
        sr.addSwInfo(Constants.REPORTER_OS_AVAILABLE_DATE, specjvmProps, "n/a");
        sr.addSwInfo(Constants.REPORTER_OS_TUNING, specjvmProps, " ");
        sr.addSwInfo(Constants.REPORTER_SW_FILESYSTEM, specjvmProps, "n/a");
        sr.addSwInfo(Constants.REPORTER_SW_OTHER_NAME, specjvmProps, " ");
        sr.addSwInfo(Constants.REPORTER_SW_OTHER_TUNING, specjvmProps, " ");
        sr.addSwInfo(Constants.REPORTER_SW_OTHER_AVAILABLE, specjvmProps, " ");
        
        sr.addHwInfo(Constants.REPORTER_HW_VENDOR, specjvmProps, "n/a");
        sr.addHwInfo(Constants.REPORTER_HW_VENDOR_URL, specjvmProps, "n/a");
        sr.addHwInfo(Constants.REPORTER_HW_MODEL, specjvmProps, "n/a");
        sr.addHwInfo(Constants.REPORTER_HW_AVAILABLE, specjvmProps, "n/a");
        sr.addHwInfo(Constants.REPORTER_HW_CPU_VENDOR, specjvmProps, "n/a");
        sr.addHwInfo(Constants.REPORTER_HW_CPU_VENDOR_URL, specjvmProps, "n/a");
        sr.addHwInfo(Constants.REPORTER_HW_CPU_NAME, specjvmProps, "n/a");
        sr.addHwInfo(Constants.REPORTER_HW_CPU_SPEED, specjvmProps, "n/a");
        sr.addHwInfo(Constants.REPORTER_HW_LOGICAL_CPUS, specjvmProps, "n/a");
        sr.addHwInfo(Constants.REPORTER_HW_NUMBER_OF_CHIPS, specjvmProps, "n/a");
        sr.addHwInfo(Constants.REPORTER_HW_NUMBER_OF_CORES, specjvmProps, "n/a");
        sr.addHwInfo(Constants.REPORTER_HW_NUMBER_OF_CORES_PER_CHIP, specjvmProps, "n/a");
        sr.addHwInfo(Constants.REPORTER_HW_THREADING_ENABLED, specjvmProps, "n/a");
        sr.addHwInfo(Constants.REPORTER_HW_THREADS_PER_CORE, specjvmProps, "n/a");
        sr.addHwInfo(Constants.REPORTER_HW_ADDRESS_BITS, specjvmProps, "n/a");
        sr.addHwInfo(Constants.REPORTER_HW_CPU_CACHE_L1, specjvmProps, "n/a");
        sr.addHwInfo(Constants.REPORTER_HW_CPU_CACHE_L2, specjvmProps, "n/a");
        sr.addHwInfo(Constants.REPORTER_HW_CPU_CACHE_OTHER, specjvmProps, "n/a");
        sr.addHwInfo(Constants.REPORTER_HW_MEMORY_SIZE, specjvmProps, "n/a");
        sr.addHwInfo(Constants.REPORTER_HW_MEMORY_DETAILS, specjvmProps, "n/a");
        sr.addHwInfo(Constants.REPORTER_HW_DETAILS_OTHER, specjvmProps, "n/a");
        
    }

}
