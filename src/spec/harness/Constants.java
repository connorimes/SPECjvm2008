/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 */

package spec.harness;

public class Constants {
    /** Specifies which benchmarks to run. */
    public final static String BENCHMARKS_PROP = "specjvm.benchmarks";
    
    /** Specifies which jvm options to use in startup benchmark. */
    public final static String STARTUP_JVM_OPTIONS_PROP = "specjvm.startup.jvm_options";
    
    /** Specifies if setup is ok for a valid run or not. */
    public final static String VALID_SETUP_PROP = "specjvm.valid.setup";
    
    /** Property that points to where SPECjvm home is. */
    public final static String HOMEDIR_PROP = "specjvm.home.dir";
    
    /** Property that points to where SPECjvm results are stored. */
    public final static String RESULT_DIR_PROP = "specjvm.result.dir";
    
    /** Property that hold the prop file name. */
    public final static String PROPFILE_PROP = "specjvm.propfile";

    /** Property that hold the additional prop file name (reporter info). */
    public final static String PROPFILE_ADDITIONAL_PROP = "specjvm.additional.properties.file";

    /** Property that the run time for an iteration is specified in */
    public final static String RUNTIME_PROP = "specjvm.iteration.time";
    
    /** Property that whether the run time for an iteration is forced is specified in */
    public final static String FORCED_RUNTIME_PROP = "specjvm.iteration.time.forced";
    
    /** Property that the run time for a warmup is specified in */
    public final static String WARMUPTIME_PROP = "specjvm.benchmark.warmup.time";
    
    /** Property that the mnimal amount of iteration is specified in. */
    public final static String ITERATIONS_MINIMUM_PROP = "specjvm.miniter";
    
    /** Property that the max amount of iteration is specified in. */
    public final static String ITERATIONS_MAXIMUM_PROP = "specjvm.maxiter";
    
    /** Property that the min amount of iteration is specified in. */
    public final static String FIXED_OPERATIONS_PROP = "specjvm.fixed.operations";
    
    /** Property that specifies if a system gc should be performed between iterations. */
    public final static String ITER_DO_SYSTEMGC_PROP = "specjvm.iteration.systemgc";
    
    /** Property that specifies how long, if any, delay should exist between iterations. */
    public final static String ITER_DELAYTIME_PROP = "specjvm.iteration.delay";
    
    /** Property that specifies if a system gc should be done between benchmarks. */
    public final static String BM_DO_SYSTEMGC_PROP = "specjvm.benchmark.systemgc";
    
    /** Property that specifies how long, if any, delay should exist between benchmarks. */
    public final static String BM_DELAYTIME_PROP = "specjvm.benchmark.delay";
    
    /** Property that specifies how long, if any, delay should exist between benchmarks. */
    public final static String VALIDITY_CHECKSUM_PROP = "specjvm.validity.checksum";
    
    /** Property that specifies which analyzers to run. */
    public final static String ANALYZER_NAMES_PROP = "specjvm.benchmark.analyzer.names";
    
    /** Property that specifies how often analyzers should be invoked. */
    public final static String ANALYZER_FREQUENCY_PROP = "specjvm.benchmark.analyzer.frequency";
    
    /** Property that is used to specify if the the harness shall continue to run the full suite on errors. */
    public final static String CONTINUE_ON_ERROR_PROP = "specjvm.continue.on.error";

    /** Property that is used to specify if the initial check should be run. */
    public final static String INITIAL_CHECK_PROP = "specjvm.run.initial.check";

    /** Property that is used to specify if the initial check sum check should be run. */
    public final static String INITIAL_CHECKSUM_CHECK_PROP = "specjvm.run.checksum.validation";

    /** Property that tells the harness whether to verify the result. */
    public final static String VERIFY_PROP = "specjvm.verify";
    
    /** Property that tells the harness to print for each finished benchmark loop. */
    public final static String PRINT_PROGRESS_PROP = "specjvm.print.progress";
    
    /** Property that tells the harness to print verbose info. */
    public final static String VERBOSE_PROP = "specjvm.print.verbose";
    
    /** Property that tells the harness to generate a file from the output. */
    public final static String GEN_VALIDITY_FILE_PROP = "specjvm.generateValidityCheckFiles";
    
    /** Property that is used to specify if the xml report should be generated. */
    public final static String CREATE_XML_REPORT_PROP = "specjvm.create.xml.report";
    
    /** Property that is used to specify if the xml report should be generated. */
    public final static String CREATE_TXT_REPORT_PROP = "specjvm.create.txt.report";
    
    /** Property that is used to specify if the html report should be generated. */
    public final static String CREATE_HTML_REPORT_PROP = "specjvm.create.html.report";
    
    /** Property that is used to specify the harness to spin and not exit - useful for memleak debugging*/
    public final static String HARNESS_SPIN_AT_END_PROP = "specjvm.spin.at.end";
    
    /** Property that is used to specify run type, iterative or time based or... */
    public final static String TYPE_OF_RUN_PROP = "specjvm.run.type";
    
    /** Property that is used to specify run name, SPEC or Lagom or ... */
    public final static String NAME_OF_RUN_PROP = "specjvm.run.name";
    
    /** Property that specifies how many threads to use. */
    public final static String BENCHMARK_THREADS_PROP = "specjvm.benchmark.threads";
    
    /** Property that specifies how many hardware threads jvm07 should think it uses. */
    public final static String BENCHMARK_THREADS_HW_OVERRIDE_PROP = "specjvm.hardware.threads.override";
    
    /** Property that specifies how large scimark input to use. */
    public final static String SCIMARK_SIZE_PROP = "specjvm.scimark.size";
    
    /** Property that specifies internal threads in sunflow benchmark. */
    public final static String SUNFLOW_THREADS = "specjvm.benchmark.sunflow.threads.per.instance";
        
    /** Property that specifies the directory with xml.validation input files. A relative path can be specified */
    public final static String XML_VALIDATION_INPUT_DIR_PROP = "specjvm.benchmark.xml.validation.input.dir";
    
    /** Property that specifies the directory with xml.transform output files at full verification. */
    public final static String XML_TRANSFORM_OUT_DIR_PROP = "specjvm.benchmark.xml.transform.out.dir";
    
    /** Property that specifies whether the xml.transform output directory should be removed after benchmark execution. */
    public final static String XML_TRANSFORM_LEAVE_OUT_DIR_PROP = "specjvm.benchmark.xml.transform.leave.out.dir";
    
    /** Properties that defines how to run startup benchmarks on scimark and crypto */
    public final static String STARTUP_RUN_ALL_SCIMARK = "specjvm.benchmark.startup.scimark.all";
    public final static String STARTUP_RUN_ALL_CRYPTO = "specjvm.benchmark.startup.crypto.all";
    public final static String STARTUP_LAUNCHER = "specjvm.benchmark.startup.launcher";
    
    /** Properties that enables debugging (of specific classes). */
    public final static String DEBUG_STARTUP = "specjvm.benchmarks.startup.debug";
    public final static String DEBUG_DERBY = "specjvm.benchmarks.derby.debug";
    public final static String DEBUG_VALIDATION = "specjvm.io.validation.debug";
    
    public final static String MATCH_REQ = "MATCH";
    public final static String TIME_MAX_5s_REQ = "TIME_MAX_5s";
    public final static String WHATEVER_REQ = "WHATEVER";
    public final static String AT_LEAST_1_MARK_IF_HIGHER = "AT_LEAST_1_MARK_IF_HIGHER";
    public final static String TIME_AT_LEAST_REQ = "TIME_AT_LEAST";
    public final static String SPECIAL_REQ = "SPECIAL";
    public final static String NOT_SET_REQ = "NOT_SET";
    
    public final static String NULL_VALUE = "null";
    public final static String TRUE_VALUE = "true";
    public final static String FALSE_VALUE = "false";
    public final static String HWT_VALUE = "#hwt";
    
    //Benchmarks names
    public final static String SCIMARK_BNAME_PREFIX = "scimark";    
    public final static String SCIMARK_BNAME_LARGE_POSTFIX = "large";
    public final static String SCIMARK_BNAME_SMALL_POSTFIX = "small";
    public final static String SCIMARK_FFT_BNAME = SCIMARK_BNAME_PREFIX + ".fft";
    public final static String SCIMARK_LU_BNAME = SCIMARK_BNAME_PREFIX + ".lu";
    public final static String SCIMARK_MONTE_CARLO_BNAME = SCIMARK_BNAME_PREFIX + ".monte_carlo";
    public final static String SCIMARK_SOR_BNAME = SCIMARK_BNAME_PREFIX + ".sor";
    public final static String SCIMARK_SPARSE_BNAME = SCIMARK_BNAME_PREFIX + ".sparse";
    public final static String SCIMARK_LARGE_FFT_BNAME = SCIMARK_FFT_BNAME + "." + SCIMARK_BNAME_LARGE_POSTFIX;
    public final static String SCIMARK_LARGE_LU_BNAME = SCIMARK_LU_BNAME + "." + SCIMARK_BNAME_LARGE_POSTFIX;    
    public final static String SCIMARK_LARGE_SOR_BNAME = SCIMARK_SOR_BNAME + "." + SCIMARK_BNAME_LARGE_POSTFIX;
    public final static String SCIMARK_LARGE_SPARSE_BNAME = SCIMARK_SPARSE_BNAME + "." + SCIMARK_BNAME_LARGE_POSTFIX;
    public final static String SCIMARK_SMALL_FFT_BNAME = SCIMARK_FFT_BNAME + "." + SCIMARK_BNAME_SMALL_POSTFIX;
    public final static String SCIMARK_SMALL_LU_BNAME = SCIMARK_LU_BNAME + "." + SCIMARK_BNAME_SMALL_POSTFIX;    
    public final static String SCIMARK_SMALL_SOR_BNAME = SCIMARK_SOR_BNAME + "." + SCIMARK_BNAME_SMALL_POSTFIX;
    public final static String SCIMARK_SMALL_SPARSE_BNAME = SCIMARK_SPARSE_BNAME + "." + SCIMARK_BNAME_SMALL_POSTFIX;
    public final static String CRYPTO_BNAME_PREFIX = "crypto";
    public final static String CRYPTO_AES_BNAME = CRYPTO_BNAME_PREFIX + ".aes";
    public final static String CRYPTO_RSA_BNAME = CRYPTO_BNAME_PREFIX + ".rsa";
    public final static String CRYPTO_SIGNVERIFY_BNAME = CRYPTO_BNAME_PREFIX + ".signverify";
    public final static String SERIAL_BNAME = "serial";
    public final static String JAVAC_BNAME = "javac";
    public final static String CRYPTO_BNAME = "crypto";
    public final static String DERBY_BNAME = "derby";
    public final static String XML_BNAME_PREFIX = "xml";
    public final static String XML_TRANSFORM_BNAME = XML_BNAME_PREFIX + ".transform";
    public final static String XML_VALIDATION_BNAME = XML_BNAME_PREFIX + ".validation";
    public final static String MPEGAUDIO_BNAME = "mpegaudio";
    public final static String COMPILER_BNAME_PREFIX = "compiler";
    public final static String COMPILER_COMPILER_BNAME = COMPILER_BNAME_PREFIX + ".compiler";
    public final static String COMPILER_SUNFLOW_BNAME = COMPILER_BNAME_PREFIX + ".sunflow";    
    public final static String SUNFLOW_BNAME = "sunflow";
    public final static String CHECK_BNAME = "check";
    public final static String COMPRESS_BNAME = "compress";
    
    public final static String STARTUP_BNAME_PREFIX = "startup";
    public final static String STARTUP_HELLOWORLD_BNAME = "startup.helloworld";
    public final static String STARTUP_COMPILER_COMPILER_BNAME = STARTUP_BNAME_PREFIX + "." + COMPILER_COMPILER_BNAME;    
    public final static String STARTUP_COMPILER_SUNFLOW_BNAME = STARTUP_BNAME_PREFIX + "." + COMPILER_SUNFLOW_BNAME;
    public final static String STARTUP_COMPRESS_BNAME = STARTUP_BNAME_PREFIX + "." + COMPRESS_BNAME;
    public final static String STARTUP_CRYPTO_AES_BNAME = STARTUP_BNAME_PREFIX + "." + CRYPTO_AES_BNAME;
    public final static String STARTUP_CRYPTO_RSA_BNAME = STARTUP_BNAME_PREFIX + "." + CRYPTO_RSA_BNAME;
    public final static String STARTUP_CRYPTO_SIGNVERFY_BNAME = STARTUP_BNAME_PREFIX + "." + CRYPTO_SIGNVERIFY_BNAME;
    public final static String STARTUP_MPEGAUDIO_BNAME = STARTUP_BNAME_PREFIX + "." + MPEGAUDIO_BNAME;
    public final static String STARTUP_SCIMARK_FFT_BNAME = STARTUP_BNAME_PREFIX + "." + SCIMARK_FFT_BNAME;
    public final static String STARTUP_SCIMARK_LU_BNAME = STARTUP_BNAME_PREFIX + "." + SCIMARK_LU_BNAME;
    public final static String STARTUP_SCIMARK_MONTE_CARLO_BNAME = STARTUP_BNAME_PREFIX + "." + SCIMARK_MONTE_CARLO_BNAME;
    public final static String STARTUP_SCIMARK_SOR_BNAME = STARTUP_BNAME_PREFIX + "." + SCIMARK_SOR_BNAME;
    public final static String STARTUP_SCIMARK_SPARSE_BNAME = STARTUP_BNAME_PREFIX + "." + SCIMARK_SPARSE_BNAME;
    public final static String STARTUP_SERIAL_BNAME = STARTUP_BNAME_PREFIX + "." + SERIAL_BNAME;
    public final static String STARTUP_SUNFLOW_BNAME = STARTUP_BNAME_PREFIX + "." + SUNFLOW_BNAME;
    public final static String STARTUP_XML_TRANSFORM_BNAME = STARTUP_BNAME_PREFIX + "." + XML_TRANSFORM_BNAME;
    public final static String STARTUP_XML_VALIDATION_NAME = STARTUP_BNAME_PREFIX + "." + XML_VALIDATION_BNAME;
    
    public final static String WORKLOAD_NAME_NOT_SET = "not set";
    public final static String WORKLOAD_NAME_LAGOM = "SPECjvm2008 Lagom Workload";
    public final static String WORKLOAD_NAME_SPEC_COMMON = "SPECjvm2008";
    public final static String WORKLOAD_NAME_SPEC_BASE = "SPECjvm2008 Base";
    public final static String WORKLOAD_NAME_SPEC_PEAK = "SPECjvm2008 Peak";
    public final static String WORKLOAD_METRIC = "ops/m";
    
    public final static String ANALYZER_POWER_HOST = "spec.harness.analyzer.PowerAnalyzer.host";
    public final static String ANALYZER_POWER_PORT = "spec.harness.analyzer.PowerAnalyzer.port"; 
    public final static String ANALYZER_POWER_VERBOSE = "spec.harness.analyzer.PowerAnalyzer.verbose";
    public final static String ANALYZER_POWER_DUMMY = "spec.harness.analyzer.PowerAnalyzer.useDummy";

	public final static String ANALYZER_TEMP_HOST = "spec.harness.analyzer.TempAnalyzer.host";
	public final static String ANALYZER_TEMP_PORT = "spec.harness.analyzer.TempAnalyzer.port"; 
	public final static String ANALYZER_TEMP_VERBOSE = "spec.harness.analyzer.TempAnalyzer.verbose";
	public final static String ANALYZER_TEMP_DUMMY = "spec.harness.analyzer.TempAnalyzer.useDummy";

	public final static String ANALYZER_METER_VERBOSE = "spec.harness.analyzer.PowerMeter.verbose";
	public final static String ANALYZER_SENSOR_VERBOSE = "spec.harness.analyzer.TempSensor.verbose";
	
	public final static String SCIMARK_LARGE_GNAME = SCIMARK_BNAME_PREFIX + "." + SCIMARK_BNAME_LARGE_POSTFIX;
    public final static String SCIMARK_SMALL_GNAME = SCIMARK_BNAME_PREFIX + "." + SCIMARK_BNAME_SMALL_POSTFIX;    

    // Shortcuts for benchmark groups
    public final static String BM_SHORTCUT_COMPILER = 
        COMPILER_COMPILER_BNAME + " " + COMPILER_SUNFLOW_BNAME;
    public final static String BM_SHORTCUT_CRYPTO = 
        CRYPTO_AES_BNAME + " " + CRYPTO_RSA_BNAME + " " + CRYPTO_SIGNVERIFY_BNAME;
    public final static String BM_SHORTCUT_SCIMARK_SMALL = ""
        + SCIMARK_SMALL_FFT_BNAME + " " + SCIMARK_SMALL_LU_BNAME + " "
        + SCIMARK_SMALL_SOR_BNAME + " " + SCIMARK_SMALL_SPARSE_BNAME + " "
        + SCIMARK_MONTE_CARLO_BNAME;
    public final static String BM_SHORTCUT_SCIMARK_LARGE = ""
        + SCIMARK_LARGE_FFT_BNAME + " " + SCIMARK_LARGE_LU_BNAME + " "  
        + SCIMARK_LARGE_SOR_BNAME + " " + SCIMARK_LARGE_SPARSE_BNAME + " "
        + SCIMARK_MONTE_CARLO_BNAME;
    public final static String BM_SHORTCUT_SCIMARK_ALL = ""
        + SCIMARK_LARGE_FFT_BNAME + " " + SCIMARK_LARGE_LU_BNAME + " "  
        + SCIMARK_LARGE_SOR_BNAME + " " + SCIMARK_LARGE_SPARSE_BNAME + " "
        + SCIMARK_SMALL_FFT_BNAME + " " + SCIMARK_SMALL_LU_BNAME + " "
        + SCIMARK_SMALL_SOR_BNAME + " " + SCIMARK_SMALL_SPARSE_BNAME + " "
        + SCIMARK_MONTE_CARLO_BNAME;
    public final static String BM_SHORTCUT_XML = 
        XML_TRANSFORM_BNAME + " " + XML_VALIDATION_BNAME;
    
    
    /** List of benchmarks for valid submission. Order matters. "Alla ska med!" */
    public static final String VALID_BENCHMARKS_THROUGHPUT =
            COMPILER_COMPILER_BNAME + " " + COMPILER_SUNFLOW_BNAME + " "
            + COMPRESS_BNAME + " "
            + CRYPTO_AES_BNAME + " " + CRYPTO_RSA_BNAME + " " + CRYPTO_SIGNVERIFY_BNAME + " "
            + DERBY_BNAME + " "
            + MPEGAUDIO_BNAME + " "
            + SCIMARK_LARGE_FFT_BNAME + " " + SCIMARK_LARGE_LU_BNAME + " "  
            + SCIMARK_LARGE_SOR_BNAME + " " + SCIMARK_LARGE_SPARSE_BNAME + " "
            + SCIMARK_SMALL_FFT_BNAME + " " + SCIMARK_SMALL_LU_BNAME + " "
            + SCIMARK_SMALL_SOR_BNAME + " " + SCIMARK_SMALL_SPARSE_BNAME + " "
            + SCIMARK_MONTE_CARLO_BNAME + " "
            + SERIAL_BNAME + " "
            + SUNFLOW_BNAME + " "
            + XML_TRANSFORM_BNAME + " " + XML_VALIDATION_BNAME;
    
    public static final String VALID_BENCHMARKS_STARTUP =
            STARTUP_HELLOWORLD_BNAME + " "
            + STARTUP_COMPILER_COMPILER_BNAME + " " 
            + STARTUP_COMPILER_SUNFLOW_BNAME + " "
            + STARTUP_COMPRESS_BNAME + " "
            + STARTUP_CRYPTO_AES_BNAME + " " + STARTUP_CRYPTO_RSA_BNAME + " " + STARTUP_CRYPTO_SIGNVERFY_BNAME + " "
            + STARTUP_MPEGAUDIO_BNAME + " "
            + STARTUP_SCIMARK_FFT_BNAME + " " + STARTUP_SCIMARK_LU_BNAME + " "
            + STARTUP_SCIMARK_MONTE_CARLO_BNAME + " " + STARTUP_SCIMARK_SOR_BNAME + " "
            + STARTUP_SCIMARK_SPARSE_BNAME + " "
            + STARTUP_SERIAL_BNAME + " "
            + STARTUP_SUNFLOW_BNAME + " "
            + STARTUP_XML_TRANSFORM_BNAME + " "
            + STARTUP_XML_VALIDATION_NAME;
    
    public static final String VALID_BENCHMARKS_SPEC = VALID_BENCHMARKS_STARTUP + " " + VALID_BENCHMARKS_THROUGHPUT;    
    public static final String VALID_BENCHMARKS_LAGOM = VALID_BENCHMARKS_THROUGHPUT;
    
    public static final String REPORTER_RUN_DATE = "spec.jvm2008.report.run.date";
    public static final String REPORTER_RUN_TESTER = "spec.jvm2008.report.run.tester";
    public static final String REPORTER_RUN_SUBMITTER = "spec.jvm2008.report.run.submitter";
    public static final String REPORTER_RUN_SUBMITTER_URL = "spec.jvm2008.report.run.submitter.url";
    public static final String REPORTER_RUN_LOCATION = "spec.jvm2008.report.run.location";
    public static final String REPORTER_RUN_LICENSE = "spec.jvm2008.report.run.license";
    public static final String REPORTER_JVM_NAME = "spec.jvm2008.report.jvm.name";
    public static final String REPORTER_JVM_VERSION = "spec.jvm2008.report.jvm.version";
    public static final String REPORTER_JVM_VENDOR = "spec.jvm2008.report.jvm.vendor";
    public static final String REPORTER_JVM_VENDOR_URL = "spec.jvm2008.report.jvm.vendor.url";
    public static final String REPORTER_JVM_JAVA_SPECIFICATION = "spec.jvm2008.report.jvm.java.specification";
    public static final String REPORTER_JVM_ADDRESS_BITS = "spec.jvm2008.report.jvm.address.bits";
    public static final String REPORTER_JVM_AVAILABLE_DATE = "spec.jvm2008.report.jvm.available.date";
    public static final String REPORTER_JVM_COMMAND_LINE = "spec.jvm2008.report.jvm.command.line";
    public static final String REPORTER_JVM_COMMAND_LINE_INITIAL_HEAP_SIZE = "spec.jvm2008.report.jvm.command.line.initial.heap.size";
    public static final String REPORTER_JVM_COMMAND_LINE_MAX_HEAP_SIZE = "spec.jvm2008.report.jvm.command.line.max.heap.size";
    public static final String REPORTER_JVM_STARTUP_COMMAND_LINE = "spec.jvm2008.report.jvm.startup.command.line";
    public static final String REPORTER_JVM_STARTUP_LAUNCHER = "spec.jvm2008.report.jvm.startup.launcher";
    public static final String REPORTER_JVM_OTHER_TUNING = "spec.jvm2008.report.jvm.other.tuning";
    public static final String REPORTER_JVM_APP_CLASS_PATH = "spec.jvm2008.report.jvm.app.class.path";
    public static final String REPORTER_JVM_BOOT_CLASS_PATH = "spec.jvm2008.report.jvm.boot.class.path";
    public static final String REPORTER_OS_NAME = "spec.jvm2008.report.os.name";
    public static final String REPORTER_OS_ADDRESS_BITS = "spec.jvm2008.report.os.address.bits";
    public static final String REPORTER_OS_AVAILABLE_DATE = "spec.jvm2008.report.os.available.date";
    public static final String REPORTER_OS_TUNING = "spec.jvm2008.report.os.tuning";
    public static final String REPORTER_SW_FILESYSTEM = "spec.jvm2008.report.sw.filesystem";
    public static final String REPORTER_SW_OTHER_NAME = "spec.jvm2008.report.sw.other.name";
    public static final String REPORTER_SW_OTHER_TUNING = "spec.jvm2008.report.sw.other.tuning";
    public static final String REPORTER_SW_OTHER_AVAILABLE = "spec.jvm2008.report.sw.other.available";
    public static final String REPORTER_HW_VENDOR = "spec.jvm2008.report.hw.vendor";
    public static final String REPORTER_HW_VENDOR_URL = "spec.jvm2008.report.hw.vendor.url";
    public static final String REPORTER_HW_MODEL = "spec.jvm2008.report.hw.model";
    public static final String REPORTER_HW_AVAILABLE = "spec.jvm2008.report.hw.available";
    public static final String REPORTER_HW_CPU_VENDOR = "spec.jvm2008.report.hw.cpu.vendor";
    public static final String REPORTER_HW_CPU_VENDOR_URL = "spec.jvm2008.report.hw.cpu.vendor.url";
    public static final String REPORTER_HW_CPU_NAME = "spec.jvm2008.report.hw.cpu.name";
    public static final String REPORTER_HW_CPU_SPEED = "spec.jvm2008.report.hw.cpu.speed";
    public static final String REPORTER_HW_LOGICAL_CPUS = "spec.jvm2008.report.hw.logical.cpus";
    public static final String REPORTER_HW_NUMBER_OF_CHIPS = "spec.jvm2008.report.hw.number.of.chips";
    public static final String REPORTER_HW_NUMBER_OF_CORES = "spec.jvm2008.report.hw.number.of.cores";
    public static final String REPORTER_HW_NUMBER_OF_CORES_PER_CHIP = "spec.jvm2008.report.hw.number.of.cores.per.chip";
    public static final String REPORTER_HW_THREADING_ENABLED = "spec.jvm2008.report.hw.threading.enabled";
    public static final String REPORTER_HW_THREADS_PER_CORE = "spec.jvm2008.report.hw.threads.per.core";
    public static final String REPORTER_HW_ADDRESS_BITS = "spec.jvm2008.report.hw.address.bits";
    public static final String REPORTER_HW_CPU_CACHE_L1 = "spec.jvm2008.report.hw.cpu.cache.l1";
    public static final String REPORTER_HW_CPU_CACHE_L2 = "spec.jvm2008.report.hw.cpu.cache.l2";
    public static final String REPORTER_HW_CPU_CACHE_OTHER = "spec.jvm2008.report.hw.cpu.cache.other";
    public static final String REPORTER_HW_MEMORY_SIZE = "spec.jvm2008.report.hw.memory.size";
    public static final String REPORTER_HW_MEMORY_DETAILS = "spec.jvm2008.report.hw.memory.details";
    public static final String REPORTER_HW_DETAILS_OTHER = "spec.jvm2008.report.hw.details.other";
    public static final String REPORTER_BENCHMARK_VERSION = "spec.jvm2008.benchmark.version";
    public static final String REPORTER_STATUS_PROPS = "spec.jvm2008.report.result.status";
    public static final String REPORTER_SCORE_PROPS = "spec.jvm2008.report.result.score";
    public static final String REPORTER_METRIC_PROPS = "spec.jvm2008.report.result.metric";
    public static final String REPORTER_WORKLOAD_NAME_PROPS = "spec.jvm2008.report.result.workload.name";
    public static final String REPORTER_WORKLOAD_MODE_PROPS = "spec.jvm2008.report.result.workload.mode";
    
    public static final String RUN_INFO_ENAME = "run-info";
    public static final String JVM_INFO_ENAME = "jvm-info";
    public static final String SW_INFO_ENAME = "sw-info";
    public static final String HW_INFO_ENAME = "hw-info";
    public static final String WORKLOAD_ENAME = "workload";
    public static final String KIT_VERSION_ENAME = "suite-build-version";
    public static final String VIOLATIONS_ENAME = "violations";
    public static final String VIOLATION_ENAME = "violation";
    public static final String CONFIGS_ENAME = "configs";
    public static final String CONFIG_ENAME = "config";
    public static final String ERRORS_ENAME = "errors";
    public static final String ERROR_ENAME = "error";
    public static final String LOOPS_ENAME = "loops";
    public static final String ANALYZERS_ENAME = "analyzers";
    public static final String ANALYZER_SUMMARIES_ENAME = "analyzer-results";
    public static final String ITERATIONS_ENAME = "iterations";
    public static final String LOOP_RESULT_ENAME = "loop-result";
    public static final String TYINFO_ENAME = "tyinfo";
    public static final String ITERATION_ENAME = "iteration";
    public static final String NAME_ENAME = "name";
    public static final String UNIT_ENAME = "unit";
    public static final String NUM_BM_THREADS_ENAME = "numberBmThreads";
    public static final String ITERATION_TIME_ENAME = "iterationTime";
    public static final String WARMUP_TIME_ENAME = "warmupTime";
    public static final String MIN_ITER_ENAME = "minIter";
    public static final String MAX_ITER_ENAME = "maxIter";
    public static final String ANALYZER_FREQ_ENAME = "analyzerFreq";
    public static final String BM_DELAY_ENAME = "benchmarkDelay";
    public static final String ITER_DELAY_ENAME = "iterationDelay";
    public static final String START_TIME_ENAME = "startTime";
    public static final String END_TIME_ENAME = "endTime";
    public static final String EXPECTED_DURATION_ENAME = "expectedDuration";
    public static final String OPERATIONS_ENAME = "operations";
    public static final String BM_THREAD_ID_ENAME = "bmThreadId";
    public static final String TIME_ENAME = "time";
    public static final String VALUE_ENAME = "value";
    public static final String HEAP_SIZE_ENAME = "Total Memory in Heap";
    public static final String FREE_MEMORY_ENAME = "Free Memory in Heap";
    public static final String BM_RESULTS_ENAME = "benchmark-results";
    public static final String SPECJVM_RESULT_ENAME = "specjvm-result";
    public static final String BM_RESULT_ENAME = "benchmark-result";
    public static final String WARMUP_RESULT_ENAME = "warmup-result";
    public static final String CATEGORY_ENAME = "category";
    public static final String ARGS_ENAME = "args";
    public static final String FORCED_ITERATION_TIME_ENAME = "forcedIterationTime";
    public static final String NUMBER_OF_LOOPS_ENAME = "numberOfLoops";
    public static final String BENCHMARK_FORCE_GC_ENAME = "benchmarkForcegc";
    public static final String ITERATION_FORCE_GC_ENAME = "iterationForcegc";
    public static final String BM_TYPE_ENAME = "bmType";
    public static final String RUN_MODE_ENAME = "runMode";
    public static final String ITERATION_RESULT_ENAME = "iteration-result";
    public static final String EXPECTED_LOOPS_ENAME = "expectedLoops";
    public static final String RESULT_ENAME = "result";
    public static final String LOOP_CNT_ENAME = "loopCnt";    	
}
