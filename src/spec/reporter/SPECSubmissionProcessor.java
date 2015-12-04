package spec.reporter;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import spec.harness.Constants;
import spec.harness.StopBenchmarkException;

public class SPECSubmissionProcessor {

    private static String inFileNameAbsolute = null;
    private static String reportRootDir = null;
    private static String reportRootName = null;
    private static String resultDirBase = null;
    private static String resultDirPeak = null;
    private static String mergedSubFileName = null;
    private static String baseRawFileName = null;
    private static String peakRawFileName = null;

    private static String specNonCompliance = null;
    private static String specNonComplianceReason = null;
    private static String specNonComplianceRemedy = null;
    private static String specReportPreparedDate = null;

    private static LinkedList<String> errors = new LinkedList<String>();

    // Properties that are expected (allowed) to differ between a base and a peak run.
    private static String [] modeBasedProps = {
            Constants.REPORTER_STATUS_PROPS,
            Constants.REPORTER_SCORE_PROPS,
            Constants.REPORTER_METRIC_PROPS,
            Constants.REPORTER_WORKLOAD_NAME_PROPS,
            Constants.REPORTER_WORKLOAD_MODE_PROPS,
            Constants.REPORTER_RUN_DATE,
            Constants.REPORTER_JVM_COMMAND_LINE,
            Constants.REPORTER_JVM_COMMAND_LINE_INITIAL_HEAP_SIZE,
            Constants.REPORTER_JVM_COMMAND_LINE_MAX_HEAP_SIZE,
            Constants.REPORTER_JVM_STARTUP_COMMAND_LINE,
            Constants.REPORTER_JVM_OTHER_TUNING,
            Constants.REPORTER_OS_TUNING,
            Constants.REPORTER_SW_OTHER_TUNING,
    };

    // Properties that is expected to be the same in a base and a peak run.
    private static String [] commonProps = {
            Constants.REPORTER_RUN_TESTER,
            Constants.REPORTER_RUN_SUBMITTER,
            Constants.REPORTER_RUN_SUBMITTER_URL,
            Constants.REPORTER_RUN_LOCATION,
            Constants.REPORTER_RUN_LICENSE,
            Constants.REPORTER_JVM_NAME,
            Constants.REPORTER_JVM_VERSION,
            Constants.REPORTER_JVM_VENDOR,
            Constants.REPORTER_JVM_VENDOR_URL,
            Constants.REPORTER_JVM_JAVA_SPECIFICATION,
            Constants.REPORTER_JVM_ADDRESS_BITS,
            Constants.REPORTER_JVM_AVAILABLE_DATE,
            Constants.REPORTER_JVM_STARTUP_LAUNCHER,
            Constants.REPORTER_JVM_APP_CLASS_PATH,
            Constants.REPORTER_JVM_BOOT_CLASS_PATH,
            Constants.REPORTER_OS_NAME,
            Constants.REPORTER_OS_ADDRESS_BITS,
            Constants.REPORTER_OS_AVAILABLE_DATE,
            Constants.REPORTER_SW_FILESYSTEM,
            Constants.REPORTER_SW_OTHER_NAME,
            Constants.REPORTER_SW_OTHER_AVAILABLE,
            Constants.REPORTER_HW_VENDOR,
            Constants.REPORTER_HW_VENDOR_URL,
            Constants.REPORTER_HW_MODEL,
            Constants.REPORTER_HW_AVAILABLE,
            Constants.REPORTER_HW_CPU_VENDOR,
            Constants.REPORTER_HW_CPU_VENDOR_URL,
            Constants.REPORTER_HW_CPU_NAME,
            Constants.REPORTER_HW_CPU_SPEED,
            Constants.REPORTER_HW_LOGICAL_CPUS,
            Constants.REPORTER_HW_NUMBER_OF_CHIPS,
            Constants.REPORTER_HW_NUMBER_OF_CORES,
            Constants.REPORTER_HW_NUMBER_OF_CORES_PER_CHIP,
            Constants.REPORTER_HW_THREADING_ENABLED,
            Constants.REPORTER_HW_THREADS_PER_CORE,
            Constants.REPORTER_HW_ADDRESS_BITS,
            Constants.REPORTER_HW_CPU_CACHE_L1,
            Constants.REPORTER_HW_CPU_CACHE_L2,
            Constants.REPORTER_HW_CPU_CACHE_OTHER,
            Constants.REPORTER_HW_MEMORY_SIZE,
            Constants.REPORTER_HW_MEMORY_DETAILS,
            Constants.REPORTER_HW_DETAILS_OTHER,
            Constants.REPORTER_BENCHMARK_VERSION
    };

    
    private static void unzipFile(String zipFileName, String rawFileName) throws StopBenchmarkException {

        try {
            ZipFile zipFile = new ZipFile(zipFileName);
            Enumeration entries = zipFile.entries();

            if (!entries.hasMoreElements()) {
                throw new StopBenchmarkException("Submitted file '" + zipFileName + "' is empty.");
            }

            ZipEntry entry = (ZipEntry) entries.nextElement();

            if (entry.isDirectory()) {
                throw new StopBenchmarkException("Zip file contains directory structure. Not allowed.");
            }

            if (entries.hasMoreElements()) {
                throw new StopBenchmarkException("Submitted file '" + zipFileName + "' contains more than one file. Not allowed.");
            }

            System.err.println("Extracting file: " + entry.getName());
            
            InputStream in = zipFile.getInputStream(entry);
            OutputStream out = new BufferedOutputStream(new FileOutputStream(rawFileName));
            byte[] buf = new byte[1024];
            for (int n = in.read(buf); n > 0; n = in.read(buf)) {
                out.write(buf, 0, n);
            }

            in.close();
            out.close();
            zipFile.close();
        } catch (IOException ioe) {
            System.err.println("Unhandled exception:");
            ioe.printStackTrace();
            throw new StopBenchmarkException("Failed extracting zipfile '" + zipFileName + "'");
        }
    }  
    
    private static String getResultFile(String workLoadLine, String resultDir) {
        String resultFile = null;
        if (workLoadLine != null && workLoadLine.indexOf("Base") != -1) {
            resultDirBase = reportRootDir + "/" + reportRootName + ".base";
            (new File(resultDirBase)).mkdir();
            resultFile = resultDirBase + "/" + "SPECjvm2008.base.raw";
            baseRawFileName = resultFile;
        } else if (workLoadLine != null && workLoadLine.indexOf("Peak") != -1) {
            resultDirPeak = reportRootDir + "/" + reportRootName + ".peak";
            (new File(resultDirPeak)).mkdir();
            resultFile = resultDirPeak + "/" + "SPECjvm2008.peak.raw";
            peakRawFileName = resultFile;
        } else {
            throw new UnsupportedOperationException("Unknown workload: " + workLoadLine);
        }
        return resultFile;
    }

    private static void splitMergedRawFile(String inFileName) throws IOException {

        // Check content, base and/or peak
        File inFile = new File(inFileName);
        inFileNameAbsolute = inFile.getAbsolutePath();
        reportRootDir = new File(inFileNameAbsolute).getParent();
        int ePos = Math.max(inFileNameAbsolute.indexOf(".raw"), inFileNameAbsolute.indexOf(".xml"));
        reportRootName = inFileNameAbsolute.substring(reportRootDir.length(), ePos);
        if (reportRootName.charAt(0) == '/' || reportRootName.charAt(0) == '\\' ) {
            reportRootName = reportRootName.substring(1);
        }
        BufferedReader rawFileStream = new BufferedReader(new InputStreamReader(new FileInputStream(inFile)));
        String line = rawFileStream.readLine();
        String headerLine = line;
        String [] workLoadLines = new String[2];
        int i = 0;

        String stag1 = "<spec.jvm2008.result.noncompliant>";
        String etag1 = "</spec.jvm2008.result.noncompliant>";
        String stag2 = "<spec.jvm2008.result.noncompliant.reason>";
        String etag2 = "</spec.jvm2008.result.noncompliant.reason>";
        String stag3 = "<spec.jvm2008.result.noncompliant.remedy>";
        String etag3 = "</spec.jvm2008.result.noncompliant.remedy>";
        String stag4 = "<spec.jvm2008.result.date>";
        String etag4 = "</spec.jvm2008.result.date>";
        
        while (line != null) {
            
            // Example: <workload>SPECjvm2008 Peak</workload>
            if (line.indexOf("<workload>") != -1) {
                if (i > 1) {
                    System.out.println("Error: Finds more than 2 workloads in raw file.");
                    System.exit(-1);
                }
                workLoadLines[i++] = line;
            }
            
            // Example <spec.jvm2008.result.noncompliant>no</spec.jvm2008.result.noncompliant>
            if (line.indexOf(stag1) != -1) {
                specNonCompliance = line.substring(line.indexOf(stag1) + stag1.length(), line.indexOf(etag1));
                if (specNonCompliance == "no") {
                    specNonCompliance = null;
                }
            }

            // Example <spec.jvm2008.result.noncompliant.reason></spec.jvm2008.result.noncompliant.reason>
            if (line.indexOf(stag2) != -1) {
                specNonComplianceReason = line.substring(line.indexOf(stag2) + stag2.length(), line.indexOf(etag2));
            }

            // Example <spec.jvm2008.result.noncompliant.remedy></spec.jvm2008.result.noncompliant.remedy>
            if (line.indexOf(stag3) != -1) {
                specNonComplianceRemedy = line.substring(line.indexOf(stag3) + stag3.length(), line.indexOf(etag3));
            }

            // Example <spec.jvm2008.result.date>Sat March 15 14:53:04 CET 2008</spec.jvm2008.result.date>
            if (line.indexOf(stag4) != -1) {
                specReportPreparedDate = line.substring(line.indexOf(stag4) + stag4.length(), line.indexOf(etag4));
            }

            line = rawFileStream.readLine();
        }
        rawFileStream.close();

        // Open file to produce first split raw file
        String resultDir = inFile.getParent();
        String resultFileName = getResultFile(workLoadLines[0], resultDir);
        System.out.println("Creating file " + resultFileName);
        File resultFile = new File(resultFileName);
        PrintStream resultFileStream = new PrintStream(new FileOutputStream(resultFile));

        // Start second pass in infile, this time to echo to result files
        inFile = new File(inFileName);
        rawFileStream = new BufferedReader(new InputStreamReader(new FileInputStream(inFile)));
        line = rawFileStream.readLine();

        // Find first result
        while (line != null && line.indexOf("<specjvm-result>") == -1) {
            line = rawFileStream.readLine();
        }
        
        // Print first result
        resultFileStream.println(headerLine);
        while (line != null && line.indexOf("</specjvm-result>") == -1) {
            resultFileStream.println(line);
            line = rawFileStream.readLine();
        }
        resultFileStream.println(line);
        resultFileStream.close();
        
        // Take care of second result (if there is any)
        if (workLoadLines[1] != null) {
            
            // Open file to produce first split raw file
            resultFileName = getResultFile(workLoadLines[1], resultDir);
            resultFile = new File(resultFileName);
            System.out.println("Creating file " + resultFileName);
            resultFile.createNewFile();
            resultFileStream = new PrintStream(new FileOutputStream(resultFile));

            // Find second result
            while (line != null && line.indexOf("<specjvm-result>") == -1) {
                line = rawFileStream.readLine();
            }

            // Print second result
            resultFileStream.println(headerLine);
            while (line != null && line.indexOf("</specjvm-result>") == -1) {
                resultFileStream.println(line);
                line = rawFileStream.readLine();
            }
            resultFileStream.println(line);
            resultFileStream.close();
        }
        rawFileStream.close();
    }
    
    private static void createMergedSubFile() throws IOException {
        
        String baseSubFileName = reportRootDir + "/" + reportRootName + ".base/SPECjvm2008.base.sub";
        File baseSubFile = new File(baseSubFileName);
        if (!baseSubFile.exists()) {
            baseSubFile = null;
        }

        String peakSubFileName = reportRootDir + "/" + reportRootName + ".peak/SPECjvm2008.peak.sub";
        File peakSubFile = new File(peakSubFileName);
        if (!peakSubFile.exists()) {
            peakSubFile = null;
        }

        // Create summary prop file
        mergedSubFileName = reportRootDir + "/" + reportRootName + ".sub";
        PrintStream resultFileStream = new PrintStream(new FileOutputStream(mergedSubFileName));
        
        // Read properties from base and peak run.
        Properties peak = new Properties();
        Properties base = new Properties();

        if (baseSubFile != null) {
            base.load(new FileInputStream(baseSubFileName));            
        } else {
            errors.add("Missing base submission property file.");
        }

        if (peakSubFile != null) {
            peak.load(new FileInputStream(peakSubFileName));
        }

        resultFileStream.println("spec.jvm2008.result.noncompliant" + "=" + (specNonCompliance == null ? "no" : specNonCompliance));
        resultFileStream.println("spec.jvm2008.result.noncompliant.reason" + "=" + (specNonComplianceReason == null ? "" : specNonComplianceReason));
        resultFileStream.println("spec.jvm2008.result.noncompliant.remedy" + "=" + (specNonComplianceRemedy == null ? "" : specNonComplianceRemedy));
        resultFileStream.println("spec.jvm2008.result.prepared.date" + "=" + (specReportPreparedDate == null ? "" : specReportPreparedDate));

        for (int i = 0; i < modeBasedProps.length; i++) {
            String key = modeBasedProps[i];
            String baseValue = base.getProperty(key);
            String peakValue = peak.getProperty(key);
            resultFileStream.println(key + ".base=" + (baseValue != null ? baseValue : ""));
            resultFileStream.println(key + ".peak=" + (peakValue != null ? peakValue : ""));
        }

        for (int i = 0; i < commonProps.length; i++) {
            String key = commonProps[i];
            String baseValue = base.getProperty(key);
            String peakValue = peak.getProperty(key);
            String value = baseValue;

            if (baseValue == null) {
                errors.add("Base missing value for " + key);
                value = peakValue;
            }
            
            if (baseValue != null && peakValue != null && !baseValue.equals(peakValue)) {
                errors.add("Base and peak has different values for " + key + "  base=" + baseValue + "  peak=" + peakValue);
            }

            resultFileStream.println(key + "=" + (value != null ? value : ""));
        }

        Iterator<String> iter = errors.iterator();
        for (int i = 1; iter.hasNext(); ) {
            resultFileStream.println("spec.jvm2008.result.error." + i + "=" + iter.next());
        }

        resultFileStream.close();
    }

    private static void createResultFile(Properties p) throws Exception {

        String testerName = p.getProperty(Constants.REPORTER_RUN_SUBMITTER, "n/a");
        String testerUrl = p.getProperty(Constants.REPORTER_RUN_SUBMITTER_URL, "n/a");
        String jvmCompanyName = p.getProperty(Constants.REPORTER_JVM_VENDOR, "n/a");
        String jvmCompanyUrl = p.getProperty(Constants.REPORTER_JVM_VENDOR_URL, "n/a");
        String jvmName = p.getProperty(Constants.REPORTER_JVM_NAME, "n/a");
        String jvmVersion = p.getProperty(Constants.REPORTER_JVM_VERSION, "n/a");
        String hwCompanyName = p.getProperty(Constants.REPORTER_HW_VENDOR, "n/a");
        String hwCompanyUrl = p.getProperty(Constants.REPORTER_HW_VENDOR, "n/a");
        String hwSystemName = p.getProperty(Constants.REPORTER_HW_MODEL, "n/a");
        String noHWThreads = p.getProperty(Constants.REPORTER_HW_LOGICAL_CPUS, "n/a");
        String noCores = p.getProperty(Constants.REPORTER_HW_NUMBER_OF_CORES, "n/a");
        String noChips = p.getProperty(Constants.REPORTER_HW_NUMBER_OF_CHIPS, "n/a");
        String baseOps = p.getProperty("spec.jvm2008.report.result.score" + ".base", "").trim();
        String peakOps = p.getProperty("spec.jvm2008.report.result.score" + ".peak", "").trim();
        String baseMetric = p.getProperty("spec.jvm2008.report.result.metric" + ".base", "");
        String peakMetric = p.getProperty("spec.jvm2008.report.result.metric" + ".peak", "");
        
        String summaryLink = reportRootDir + "/" + reportRootName + ".html";
        String fdrLinkBaseHtml = ((baseOps == null || baseOps.length() == 0) ? null : "./" + reportRootName + ".base/SPECjvm2008.base.html");
        String fdrLinkBaseTxt = ((baseOps == null || baseOps.length() == 0) ? null : "./" + reportRootName + ".base/SPECjvm2008.base.txt");
        String fdrLinkPeakHtml = ((peakOps == null || peakOps.length() == 0) ? null : "./" + reportRootName + ".peak/SPECjvm2008.peak.html");
        String fdrLinkPeakTxt = ((peakOps == null || peakOps.length() == 0) ? null : "./" + reportRootName + ".peak/SPECjvm2008.peak.txt");


        String summaryHtmlName = summaryLink;
        System.out.println("Creating report " + summaryHtmlName);
        PrintStream summaryHtmlStream = new PrintStream(new FileOutputStream(summaryHtmlName));
        summaryHtmlStream.println("<HTML>");
        summaryHtmlStream.println("  <HEAD>");
        summaryHtmlStream.println("    <META NAME=\"GENERATOR\" CONTENT=\"SPEC Java Reporter\">");
        summaryHtmlStream.println("    <TITLE>SPECjvm2008</TITLE>");
        summaryHtmlStream.println("  </HEAD>");
        summaryHtmlStream.println("  <BODY >");
        summaryHtmlStream.println("    <TABLE WIDTH=100% >");
        summaryHtmlStream.println("      <TR> ");
        summaryHtmlStream.println("        <TD ALIGN=LEFT ROWSPAN=2> <FONT SIZE=+3 COLOR=BLUE>SPECjvm2008 SPEC Summary Report</FONT></TD>");
        summaryHtmlStream.println("      </TR>");
        summaryHtmlStream.println("    </TABLE>");

        summaryHtmlStream.println("    <TABLE WIDTH=100% >");
        summaryHtmlStream.println("        <THEAD>");
        summaryHtmlStream.println("           <COL width = 30%>");
        summaryHtmlStream.println("           <COL width = 70%>");
        summaryHtmlStream.println("        </THEAD>");
        summaryHtmlStream.println("        <TBODY>");
        summaryHtmlStream.println("          <TR>");
        summaryHtmlStream.println("            <TD ALIGN=LEFT>Tester</TD>");
        summaryHtmlStream.println("            <TD ALIGN=LEFT><A HREF=\"" + testerUrl + "\">" + testerName + "</A></TD>");
        summaryHtmlStream.println("          </TR>");
        summaryHtmlStream.println("          <TR>");
        summaryHtmlStream.println("            <TD ALIGN=LEFT>JVM Vendor</TD>");
        summaryHtmlStream.println("            <TD ALIGN=LEFT><A HREF=\"" + jvmCompanyUrl + "\">" + jvmCompanyName + "</A></TD>");
        summaryHtmlStream.println("          </TR>");
        summaryHtmlStream.println("          <TR>");
        summaryHtmlStream.println("            <TD ALIGN=LEFT>JVM</TD>");
        summaryHtmlStream.println("            <TD ALIGN=LEFT>" + jvmName + " " + jvmVersion + "</TD>");
        summaryHtmlStream.println("          </TR>");
        summaryHtmlStream.println("          <TR>");
        summaryHtmlStream.println("            <TD ALIGN=LEFT>Hardware Company</TD>");
        summaryHtmlStream.println("            <TD ALIGN=LEFT><A HREF=\"" + hwCompanyUrl + "\">" + hwCompanyName + "</A></TD>");
        summaryHtmlStream.println("          </TR>");
        summaryHtmlStream.println("          <TR>");
        summaryHtmlStream.println("            <TD ALIGN=LEFT>Hardware Model</TD>");
        summaryHtmlStream.println("            <TD ALIGN=LEFT>" + hwSystemName + "</TD>");
        summaryHtmlStream.println("          </TR>");
        summaryHtmlStream.println("          <TR>");
        summaryHtmlStream.println("            <TD ALIGN=LEFT>Number of chips</TD>");
        summaryHtmlStream.println("            <TD ALIGN=LEFT>" + noChips + "</TD>");
        summaryHtmlStream.println("          </TR>");
        summaryHtmlStream.println("          <TR>");
        summaryHtmlStream.println("            <TD ALIGN=LEFT>Number of cores</TD>");
        summaryHtmlStream.println("            <TD ALIGN=LEFT>" + noCores + "</TD>");
        summaryHtmlStream.println("          </TR>");
        summaryHtmlStream.println("          <TR>");
        summaryHtmlStream.println("            <TD ALIGN=LEFT>Number of logical CPUs</TD>");
        summaryHtmlStream.println("            <TD ALIGN=LEFT>" + noHWThreads + "</TD>");
        summaryHtmlStream.println("          </TR>");
        summaryHtmlStream.println("          <TR>");
        summaryHtmlStream.println("            <TD ALIGN=LEFT>Base result</TD>");
        if (baseOps != null) {
            summaryHtmlStream.println("            <TD ALIGN=LEFT>" + baseOps + " " + baseMetric + "</TD>");
        } else {
            summaryHtmlStream.println("            <TD ALIGN=LEFT>&nbsp;</TD>");
        }
        summaryHtmlStream.println("          </TR>");
        summaryHtmlStream.println("          <TR>");
        summaryHtmlStream.println("            <TD ALIGN=LEFT>Peak result</TD>");
        if (peakOps != null) {
            summaryHtmlStream.println("            <TD ALIGN=LEFT>" + peakOps + " " + peakMetric + "</TD>");
        } else {
            summaryHtmlStream.println("            <TD ALIGN=LEFT>&nbsp;</TD>");
        }
        summaryHtmlStream.println("          </TR>");
        summaryHtmlStream.println("          <TR>");
        summaryHtmlStream.println("            <TD ALIGN=LEFT>Base report</TD>");
        if (fdrLinkBaseHtml != null) {
            summaryHtmlStream.println("            <TD ALIGN=LEFT><A HREF=\"" + fdrLinkBaseHtml + "\">html</A>, <A HREF=\"" + fdrLinkBaseTxt + "\">txt</A></TD>");
        } else {
            summaryHtmlStream.println("            <TD ALIGN=LEFT>&nbsp;</TD>");
        }
        summaryHtmlStream.println("          </TR>");
        summaryHtmlStream.println("          <TR>");
        summaryHtmlStream.println("            <TD ALIGN=LEFT>Peak report</TD>");
        if (fdrLinkPeakHtml != null) {
            summaryHtmlStream.println("            <TD ALIGN=LEFT><A HREF=\"" + fdrLinkPeakHtml + "\">html</A>, <A HREF=\"" + fdrLinkPeakTxt + "\">txt</A></TD>");
        } else {
            summaryHtmlStream.println("            <TD ALIGN=LEFT>&nbsp;</TD>");
        }
        summaryHtmlStream.println("          </TR>");
        summaryHtmlStream.println("       </TBODY>");
        summaryHtmlStream.println("    </TABLE>");

        Iterator<String> i = errors.iterator(); 
        if (i.hasNext()) {
            summaryHtmlStream.println("    <BR><BR>");
            summaryHtmlStream.println("    <TABLE WIDTH=100% >");
            summaryHtmlStream.println("      <TR><TD>Warnings:</TD></TR>");
            while (i.hasNext()) {
                summaryHtmlStream.println("      <TR><TD>" + i.next() + "</TD></TR>");
            }
            summaryHtmlStream.println("    </TABLE>");
        }
        
        summaryHtmlStream.println("  </BODY>");
        summaryHtmlStream.println("</HTML>");
        summaryHtmlStream.close();
    }

    public static void main(String[] args) throws Exception {
        
        if (args.length != 1) {
            System.out.println("Usage: spec.reporter.BasePeakReporter <merged raw file> ");
        }
        
        String inZipFileName = args[0];
        File inFile = new File(inZipFileName);
        if (!inFile.exists()) {
            System.out.println("Cannot find file '" + inZipFileName + "'");
            return;
        }
        
        int dotZipPos = inZipFileName.indexOf(".zip");
        int dotXmlPos = inZipFileName.indexOf(".xml");
        int dotRawPos = inZipFileName.indexOf(".raw");
        if (dotZipPos == -1 && dotXmlPos == -1 && dotRawPos == -1) {
            System.out.println("Expecting a file name *.zip, *.xml or *.raw, but got: '" + inZipFileName + "'");
            return;
        }
        
        String inRawFileName;
        
        if (dotZipPos != -1) {
            // Unzip incomming zip file
            inRawFileName = inZipFileName.substring(0, dotZipPos);
            unzipFile(inZipFileName, inRawFileName);
        } else {
            inRawFileName = inZipFileName;
        }
        
        // Split incoming raw file
        splitMergedRawFile(inRawFileName);
        
        // Update report generator for SPEC
        ReportGenerator.specLogo = System.getProperty("spec.jvm2008.result.logo");
        ReportGenerator.specNonCompliance = System.getProperty("spec.jvm2008.result.noncompliant", specNonCompliance);
        ReportGenerator.specNonComplianceReason = System.getProperty("spec.jvm2008.result.noncompliant.reason", specNonComplianceReason);
        ReportGenerator.specNonComplianceRemedy = System.getProperty("spec.jvm2008.result.noncompliant.remedy", specNonComplianceRemedy);
        
        // generate base report
        if (baseRawFileName != null) {
            String [] rArgs = {baseRawFileName};
            ReportGenerator.main(rArgs);
        }
        
        // generate peak report
        if (peakRawFileName != null) {
            String [] rArgs = {peakRawFileName};
            ReportGenerator.main(rArgs);
        }

        // merge sub files
        createMergedSubFile();
        
        // create summary
        Properties p = new Properties();
        p.load(new FileInputStream(mergedSubFileName));
        createResultFile(p);

    }
}