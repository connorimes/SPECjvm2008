/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 */
package spec.reporter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import spec.harness.Constants;
import spec.harness.results.TestResult;

public class ReportGenerator {	
    private ArrayList<Writer> writer = new ArrayList<Writer>();
    private Element root;    
    private BenchmarkGroupRecords records = new BenchmarkGroupRecords();
    private double compositeScore;
    private String metric;
    TreeMap<String, Double> subGroupScores;
    private ArrayList<String> violations;
    private ArrayList<String> configs;     
    private String subFileName = null;
    private String summaryFileName = null;
    protected static String reporterDir = null;
    
    protected static String specNonCompliance = System.getProperty("spec.jvm2008.result.noncompliant");
    protected static String specNonComplianceReason = System.getProperty("spec.jvm2008.result.noncompliant.reason");
    protected static String specNonComplianceRemedy = System.getProperty("spec.jvm2008.result.noncompliant.remedy");
    protected static String specLogo = System.getProperty("spec.jvm2008.result.logo");
        
    public ReportGenerator(String ifn, boolean doHtml, boolean doTxt, boolean doSub, boolean doSummary) {    	
        String inputFileName = ifn;
        reporterDir = new File(inputFileName).getAbsoluteFile().getParent();
        Utils.REPORTER_DIR = reporterDir;
        Utils.IMAGES_DIR = reporterDir + "/images";
        File images = new File(Utils.IMAGES_DIR);
        if (!images.exists()) {
            new File(Utils.IMAGES_DIR).mkdir();
        }
        FileInputStream rawFileIS = null;
        root = null;
        int index = inputFileName.lastIndexOf(".");
        String prefix = inputFileName.substring(0, index);
        
        if (doSub || doSummary || doHtml || doTxt) {
        	try {
        		String resDir = new File(prefix + "tmp").getParentFile().getCanonicalPath();
        		System.out.println("Generating reports in:\n" + resDir);
        	} catch(IOException ioe) { }
        }
        
        if (doSub) {
            subFileName = prefix + ".sub";
        }
        if (doSummary) {
            summaryFileName = prefix + ".summary";
        }
        try { 
            if (doHtml) {
                // System.out.println("Generating report " + prefix + ".html");
                writer.add(Writer.getInstance(Utils.HTML_WRITER_TYPE, prefix + ".html"));
            }
            if (doTxt) {
                // System.out.println("Generating report " + prefix + ".txt");
                writer.add(Writer.getInstance(Utils.PLAIN_WRITER_TYPE, prefix + ".txt"));
            }
            rawFileIS = new FileInputStream(inputFileName);
            root = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(rawFileIS).getDocumentElement();
            handleBenchmarksResults(getChildByName(Constants.BM_RESULTS_ENAME));
        } catch (Exception e) {
            try {
                rawFileIS.close();
            } catch (IOException ioe) { }
            System.out.println("ERROR: Reporter cannot be initialized (" + e + ")");            
            System.exit(-1);
        } finally {
            try {
                rawFileIS.close();
            } catch (IOException e) { }
        }
    }   
    
    private void handleBenchmarksResults(Node node) {
        Node current = node.getFirstChild();
        do {
            if (isNotTextNode(current)) {
                handleBenchmarkResult(current);
            }
        } while ((current = current.getNextSibling()) != null);        
        compositeScore = records.computeCompositeScore();        
        if (!(specNonCompliance == null || specNonCompliance.equals("no"))) {
            compositeScore = 0;
        }        
    }
    
    private String getValue(NamedNodeMap nodeMap, String name) {
        Node node = nodeMap.getNamedItem(name);
        return node == null ? null : node.getNodeValue();
    }
    
    private Node getChildByName(String name) {
        return root.getElementsByTagName(name).item(0);
    }  
    
    private void collectBmConfiguration(BenchmarkRecord record,
            NamedNodeMap attrs) {
        for (int i = 0; i < Utils.BM_CONFIGURATION_ENAMES.length; i ++) {
            record.configuration[i] = getValue(attrs,
                    Utils.BM_CONFIGURATION_ENAMES[i]);
        }
        
    }
    
    private void handleBenchmarkResult(Node node) {
        NamedNodeMap attrs = node.getAttributes();        
        String bmName = getValue(attrs, Constants.NAME_ENAME);
        String numberBmThreads = getValue(attrs, Constants.NUM_BM_THREADS_ENAME);
        
        BenchmarkRecord record = new BenchmarkRecord(bmName,
                Integer.parseInt(numberBmThreads));
        
        collectBmConfiguration(record, attrs);
        BenchmarkChart chart = new BenchmarkChart(bmName);
        Utils.dbgPrint(bmName + numberBmThreads);
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node current = children.item(i);
            if (isNotTextNode(current)) {
                Utils.dbgPrint("\t?:" + current.getNodeName());
                record.startHandling(current.getNodeName());
                NodeList children2 = current.getChildNodes();
                for (int j = 0; j < children2.getLength(); j++) {
                    Node current2 = children2.item(j);
                    if (isNotTextNode(current2)) {
                        handleIterationResult(record, chart, current2);
                    }
                }
            }
        }
        
        records.addNewBenchmarkRecord(record);        
        Utils.createBmResultGraph(record);
        chart.buildJPEG();        
    }   
    
    private void generateHat(Writer w, String runStatus, String score, String workloadName) {        
        
        w.putHat(runStatus,
                 score,
                 workloadName,
                 "",
                 getProperty(Constants.REPORTER_HW_VENDOR),
                 getProperty(Constants.REPORTER_JVM_VENDOR),
                 getProperty(Constants.REPORTER_HW_MODEL),
                 getProperty(Constants.REPORTER_JVM_NAME),
                 getProperty(Constants.REPORTER_RUN_SUBMITTER),
                 getProperty(Constants.REPORTER_RUN_DATE),
                 getProperty(Constants.REPORTER_JVM_VERSION));
    }
    
    private void generateSummaryTable(Writer w, String scoreString) {
        w.putSummaryTable(scoreString, records.scores);
    }
    
    String [] runInfoTableFields = {
    	    Constants.REPORTER_RUN_SUBMITTER,
    	    Constants.REPORTER_RUN_SUBMITTER_URL,
    	    Constants.REPORTER_RUN_LICENSE,
    	    Constants.REPORTER_RUN_TESTER,
    	    Constants.REPORTER_RUN_LOCATION,
    	    Constants.REPORTER_RUN_DATE
    };

    String [] jvmInfoTableFields = {
    	    Constants.REPORTER_JVM_VENDOR,
    	    Constants.REPORTER_JVM_VENDOR_URL,
    	    Constants.REPORTER_JVM_NAME,
    	    Constants.REPORTER_JVM_VERSION,
    	    Constants.REPORTER_JVM_AVAILABLE_DATE,
    	    Constants.REPORTER_JVM_JAVA_SPECIFICATION,
    	    Constants.REPORTER_JVM_ADDRESS_BITS,
    	    Constants.REPORTER_JVM_COMMAND_LINE_INITIAL_HEAP_SIZE,
    	    Constants.REPORTER_JVM_COMMAND_LINE_MAX_HEAP_SIZE,
    	    Constants.REPORTER_JVM_COMMAND_LINE,
            Constants.REPORTER_JVM_STARTUP_COMMAND_LINE,
            Constants.REPORTER_JVM_STARTUP_LAUNCHER,
    	    Constants.REPORTER_JVM_OTHER_TUNING,
    	    Constants.REPORTER_JVM_APP_CLASS_PATH,
    	    Constants.REPORTER_JVM_BOOT_CLASS_PATH
    };

    String [] swInfoTableFields = {
    	    Constants.REPORTER_OS_NAME,
    	    Constants.REPORTER_OS_AVAILABLE_DATE,
    	    Constants.REPORTER_OS_ADDRESS_BITS,
    	    Constants.REPORTER_OS_TUNING,
    	    Constants.REPORTER_SW_FILESYSTEM,
    	    Constants.REPORTER_SW_OTHER_NAME,
    	    Constants.REPORTER_SW_OTHER_TUNING,
    	    Constants.REPORTER_SW_OTHER_AVAILABLE
    };

    String [] hwInfoTableFields = {
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
    	    Constants.REPORTER_HW_THREADS_PER_CORE,
    	    Constants.REPORTER_HW_THREADING_ENABLED,
    	    Constants.REPORTER_HW_ADDRESS_BITS,
    	    Constants.REPORTER_HW_CPU_CACHE_L1,
    	    Constants.REPORTER_HW_CPU_CACHE_L2,
    	    Constants.REPORTER_HW_CPU_CACHE_OTHER,
    	    Constants.REPORTER_HW_MEMORY_SIZE,
    	    Constants.REPORTER_HW_MEMORY_DETAILS,
    	    Constants.REPORTER_HW_DETAILS_OTHER
    };
    
    private void generateConfigTable(Writer w) {
        w.startTable("", false);
        w.startRecordAndData();        
        generateInfoTable(getChildByName(Constants.RUN_INFO_ENAME), w, runInfoTableFields);
        w.endAndStartData();        
        generateInfoTable(getChildByName(Constants.SW_INFO_ENAME), w, swInfoTableFields);
        w.endAndStartRecordAndData();        
        generateInfoTable(getChildByName(Constants.JVM_INFO_ENAME), w, jvmInfoTableFields);
        w.endAndStartData();        
        generateInfoTable(getChildByName(Constants.HW_INFO_ENAME), w, hwInfoTableFields);        
        w.endAndStartRecordAndData();
        generateViolationsTable(w);
        insertSuiteConfiguration(w);
        w.endRecordAndData();
        w.endTable(true);
    }   
    
    private ArrayList<String> findProperties(String ename, String subelementName) {
        NodeList currentNode = root.getElementsByTagName(ename);
        if (currentNode == null) {
            return null;
        }
        Node violation = currentNode.item(0);
        if (violation == null) {
            return null;
        }
        NodeList children = violation.getChildNodes();        
        ArrayList<String> result = new ArrayList<String>();
        for (int i = 0; i < children.getLength(); i++) {
            Node current = children.item(i);            
            if (subelementName.equals(current.getNodeName())) {
                result.add(current.getTextContent());
            }
        }        
        return result;
    }
    
    private void generateViolationsTable(Writer w) {
        w.startTable(Constants.VIOLATIONS_ENAME, true);
        if (violations != null && violations.size() != 0) {
            w.insertRecords(violations);
        }
        w.endTable(true);
    }
    
    private void generateDetailsTable(Writer w) {
        w.startDetailsTable();
        BenchmarkGroupRecords.BenchmarkResultsIterator iter = 
        	   records.new BenchmarkResultsIterator();
        while (iter.next() != null) {
        	w.handleBenchmarkRecord(iter.getCurrentRecord());
        }
        
        w.endTable(true);
    }
    
    public void generateCopyRightStatement(Writer w) {
        GregorianCalendar g = new GregorianCalendar();
        int year = g.get(Calendar.YEAR);
        ArrayList<String> al = new ArrayList<String>();
        al.add("SPECjvm2008 Version: [" + getProperty(Constants.KIT_VERSION_ENAME) + "]");
        if (year > 2008) {
            al.add("Copyright " + w.getCopyRightSign() + " 2008-" + year + " SPEC. All rights reserved");
        } else {
            al.add("Copyright " + w.getCopyRightSign() + " 2008 SPEC. All rights reserved");
        }
        w.startTable("", false, false, true);
        w.insertRecords(al);
        w.endTable();
    }
        
    public String generateReport() {

    	violations = findProperties(Constants.VIOLATIONS_ENAME, Constants.VIOLATION_ENAME);    	
        configs = findProperties(Constants.CONFIGS_ENAME, Constants.CONFIG_ENAME);
        String workloadName = getProperty(Constants.WORKLOAD_ENAME);
        String workloadMode = workloadName;
        String score = Utils.formatScore(compositeScore);
        String runStatus = Utils.getRunStatus(isValidRun(), isCompliantRun());
        String scoreString;
        boolean isComp = isCompliantRun();
        boolean isValid = isValidRun();

        if (!(specNonCompliance == null || specNonCompliance.equals("no"))) {
        	isComp = false;
            // System.out.println("specNOncom: '" + specNonCompliance + "'");
            if (specNonCompliance.equalsIgnoreCase("na")) {
                runStatus = "Not Available";
            } else if (specNonCompliance.equalsIgnoreCase("nc")) {
                runStatus = "Not Compliant";
            } else if (specNonCompliance.equalsIgnoreCase("cd")) {
                runStatus = "Code Defect";
                isValid = false;
            } else {
            	runStatus = specNonCompliance;
            }
            score = "";
            metric = "";
            scoreString = runStatus;
		} else {

			// Standard use case.
			if (isValid) {
				if (Constants.WORKLOAD_NAME_LAGOM.equals(workloadMode)) {
					if (isComp) {
						metric = Constants.WORKLOAD_METRIC;
						scoreString = "Composite result: ";
					} else {
						metric = Constants.WORKLOAD_METRIC;
						scoreString = "Noncompliant composite result: ";
					}
				} else {
					if (isComp) {
						metric = workloadName + " " + Constants.WORKLOAD_METRIC;
						scoreString = "Composite result: ";
					} else {
						metric = Constants.WORKLOAD_METRIC;
						scoreString = "Noncompliant composite result: ";
					}
				}
			} else {
				metric = "";
				scoreString = "Composite result: ";
			}
        }

        
        scoreString += score + " " + metric;
        
        createSummary(scoreString, workloadName, workloadMode);                
        createSubFile(runStatus, score, metric, workloadName, workloadMode);
        
        for (int i = 0; i < writer.size(); i++) {        	
            writer.get(i).startReport();            
            generateHat(writer.get(i), runStatus, scoreString, workloadName);
            if (!(specNonCompliance == null || specNonCompliance.equals("no"))) {
                writer.get(i).startTable("", false);
                writer.get(i).startRecordAndData();
                writer.get(i).startTable("SPEC non-compliance", true);
                ArrayList<String> ncs = new ArrayList<String>(2);
                if (specNonComplianceReason != null) {
                    ncs.add("Reason: " + specNonComplianceReason);
                }
                if (specNonComplianceRemedy != null) {
                    ncs.add("Remedy: " + specNonComplianceRemedy);
                }
                writer.get(i).insertRecords(ncs);
                writer.get(i).endTable();
                writer.get(i).endRecordAndData();
                writer.get(i).endTable();
            } else {
                Utils.generateMainChart(compositeScore, records.scores);
                generateSummaryTable(writer.get(i), scoreString);
            }
                    
            generateConfigTable(writer.get(i));
            generateDetailsTable(writer.get(i));
            generateCopyRightStatement(writer.get(i));
            writer.get(i).endReport();
            writer.get(i).closeStream();
        }
        
        return scoreString;
    }
        
    private void generateInfoTable(Node base, Writer w, String [] fields) {    	
    	// Put all info in list l1
    	LinkedList<Pair<String, String>> l1 = new LinkedList<Pair<String, String>>();
    	for (Node current = base.getFirstChild();
    		current != null;
    		current = current.getNextSibling()) {
			Pair<String, String> p = new Pair<String, String>(current.getNodeName(), current.getTextContent());
    		if (isNotTextNode(current)) {
    			l1.add(p);
    		} 
    	}
    	
    	// Sort the info in requested order into list l2
    	LinkedList<Pair<String, String>> l2 = new LinkedList<Pair<String, String>>();
    	for (int i = 0; i < fields.length; i++) {
    		for (Iterator<Pair<String, String>> iter = l1.iterator(); iter.hasNext(); ) {
    			Pair<String, String> p = iter.next();
    			if (p.fst != null && p.fst.equals(fields[i])) {
        			l2.add(p);
        			iter.remove();
        			break;
    			}
    		}
    	}
    	
    	// Add any (nonxpected) additional info
    	l2.addAll(l1);
    	
    	// Write info
        w.startTable(base.getNodeName(), true);
        for (Iterator<Pair<String, String>> iter = l2.iterator(); iter.hasNext(); ) {
			Pair<String, String> p = iter.next();
			w.insertRecord(Utils.getDescription(p.fst), insertBRTag(TestResult.correctContent(p.snd, false), w));
        }
        w.endTable(true);        
    }
    
    private String insertBRTag(String text, Writer w) {
        if (text.length() < 50) {
            return text;
        }
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (int i = 0; i < text.length(); i ++) {
            char ch = text.charAt(i);
            sb.append(ch);
            count ++;
            if ((count > 40 && (ch == ';' || ch == ':'))) {
                if (w.getType().equals(Utils.HTML_WRITER_TYPE)) {
                    sb.append("<BR>");
                }
                count = 0;
            }
        }
        return sb.toString();
    }    
    
    private void handleIterationResult(BenchmarkRecord record,
            BenchmarkChart chart, Node node) {
        Utils.dbgPrint("\t\t:" + node.getNodeName());
        NamedNodeMap attrs = node.getAttributes();
        
        String startTime = getValue(attrs, Constants.START_TIME_ENAME);
        String endTime = getValue(attrs, Constants.END_TIME_ENAME);
        
        // Null check for startTime, endTime, and Operation added
        // to handle invalid startup benchmark result.
        
        if(startTime == null)startTime = "0";
        if(endTime == null)endTime = "0";
        chart.setStartTime(Long.parseLong(startTime));
        
        String operations = getValue(attrs, Constants.OPERATIONS_ENAME);
        if(operations == null) operations = "0";
        
        BenchmarkRecord.IterationRecord iterRecord
                = record.addIterationRecord(getValue(attrs, Constants.ITERATION_ENAME),
                getValue(attrs, Constants.EXPECTED_DURATION_ENAME),
                startTime, endTime, operations);
        if (checkName(Constants.ITERATIONS_ENAME, node.getParentNode())) {
            chart.addMarker(Long.parseLong(startTime),
                    Utils.df.format(iterRecord.operations) + " ops");
        }
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node currrent = children.item(i);
            if (checkName(Constants.LOOPS_ENAME, currrent)) {
                handleLoopsNode(iterRecord, chart, currrent);
            } else if (checkName(Constants.ANALYZERS_ENAME, currrent)) {
                handleAnalyzersNode(chart, currrent);
            }
        }
    }
    
    private boolean checkName(String name, Node node) {
        return name.equals(node.getNodeName());
    }
    
    private boolean isNotTextNode(Node node) {
        return node.getNodeType() != Node.TEXT_NODE;
    }
    
    private void handleLoopsNode(BenchmarkRecord.IterationRecord record,
            BenchmarkChart chart, Node node) {
        Utils.dbgPrint("\t\t\t:" + node.getNodeName());
        NodeList children = node.getChildNodes();
        Utils.dbgPrint("HandleLoopsNode");
        for (int i = 0; i < children.getLength(); i++) {
            Node current = children.item(i);
            if (checkName(Constants.LOOP_RESULT_ENAME, current)) {
                NamedNodeMap attrs = current.getAttributes();
                chart.addTimeInfo("bmThread" + getValue(attrs, Constants.BM_THREAD_ID_ENAME),
                        Long.parseLong(getValue(attrs, Constants.START_TIME_ENAME)),
                        Long.parseLong(getValue(attrs, Constants.END_TIME_ENAME)));                                
                checkErrors(record, current);
            }
        }
    }
    
    private void checkErrors(BenchmarkRecord.IterationRecord iterRecord, Node loopsNode) {
        NodeList children = loopsNode.getChildNodes();
        for (int i = 0; i < children.getLength(); i ++) {
            Node current = children.item(i);
            if (Constants.ERRORS_ENAME.equals(current.getNodeName())) {
                NodeList errors = current.getChildNodes();
                for (int j = 0; j < errors.getLength(); j ++) {
                    if (Constants.ERROR_ENAME.equals(errors.item(j).getNodeName())) {
                        iterRecord.addError(errors.item(j).getTextContent());
                    }
                }
            }
        }
    }
    
    private void createSummary(String scoreString, String workloadName, String workloadMode) {
        if (summaryFileName == null) {
            return;
        }
        
        // System.out.println("Generating report " + summaryFileName);
        
    	FileOutputStream fos = null;
    	try {
     	    fos = new FileOutputStream(summaryFileName);
     	    PrintStream ps = new PrintStream(fos);
     	    ps.println("Report:      " + workloadName + (workloadName.equals(workloadMode) ? "" : (", " + workloadMode)) + " summary");
     	    ps.println("Result:      " + scoreString);
     	    ps.println("HW Model:    " + getProperty(Constants.REPORTER_HW_VENDOR) + ", " + getProperty(Constants.REPORTER_HW_MODEL));
     	    ps.println("HW CPU:      " + getProperty(Constants.REPORTER_HW_CPU_VENDOR) + ", " + getProperty(Constants.REPORTER_HW_CPU_NAME));
     	    ps.println("OS:          " + getProperty(Constants.REPORTER_OS_NAME));
     	    ps.println("JVM:         " + getProperty(Constants.REPORTER_JVM_VENDOR) + ", " + getProperty(Constants.REPORTER_JVM_NAME) + ", " + getProperty(Constants.REPORTER_JVM_VERSION));
            ps.println("JVM tuning:  " + getProperty(Constants.REPORTER_JVM_COMMAND_LINE));
            ps.println("JVM startup: " + getProperty(Constants.REPORTER_JVM_STARTUP_COMMAND_LINE));
     	    ps.println("Tester:      " + getProperty(Constants.REPORTER_RUN_TESTER) + ", " + getProperty(Constants.REPORTER_RUN_SUBMITTER));
     	    ps.println("Run date:    " + getProperty(Constants.REPORTER_RUN_DATE));
    	} catch (IOException e) {
    		e.printStackTrace();
    	} finally {
    		try {
        	    fos.close();
    		} catch(IOException e) {}
    	}
    }
    
    private void printConstantProp(PrintStream ps, String prop) {
	    ps.println(prop + "=" + getProperty(prop));
    }

    private void printConstantProp(PrintStream ps, String prop, String value) {
        ps.println(prop + "=" + value);
    }

    private void createSubFile(String runStatus, String compositeScore, String metric, String workloadName,
            String workloadMode) {

        if (subFileName == null) {
            return;
        }
        
        // System.out.println("Generating report " + subFileName);
        
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(subFileName);
            PrintStream ps = new PrintStream(fos);
            ps.println(Constants.REPORTER_STATUS_PROPS + "=" + runStatus);
            ps.println(Constants.REPORTER_SCORE_PROPS + "=" + compositeScore);
            ps.println(Constants.REPORTER_METRIC_PROPS + "=" + metric);
            ps.println(Constants.REPORTER_WORKLOAD_NAME_PROPS + "=" + workloadName);
            ps.println(Constants.REPORTER_WORKLOAD_MODE_PROPS + "=" + workloadMode);

            printConstantProp(ps, Constants.REPORTER_RUN_DATE);
            printConstantProp(ps, Constants.REPORTER_RUN_TESTER);
            printConstantProp(ps, Constants.REPORTER_RUN_SUBMITTER);
            printConstantProp(ps, Constants.REPORTER_RUN_SUBMITTER_URL);
            printConstantProp(ps, Constants.REPORTER_RUN_LOCATION);
            printConstantProp(ps, Constants.REPORTER_RUN_LICENSE);
            printConstantProp(ps, Constants.REPORTER_JVM_NAME);
            printConstantProp(ps, Constants.REPORTER_JVM_VERSION);
            printConstantProp(ps, Constants.REPORTER_JVM_VENDOR);
            printConstantProp(ps, Constants.REPORTER_JVM_VENDOR_URL);
            printConstantProp(ps, Constants.REPORTER_JVM_JAVA_SPECIFICATION);
            printConstantProp(ps, Constants.REPORTER_JVM_ADDRESS_BITS);
            printConstantProp(ps, Constants.REPORTER_JVM_AVAILABLE_DATE);
            printConstantProp(ps, Constants.REPORTER_JVM_COMMAND_LINE);
            printConstantProp(ps, Constants.REPORTER_JVM_COMMAND_LINE_INITIAL_HEAP_SIZE);
            printConstantProp(ps, Constants.REPORTER_JVM_COMMAND_LINE_MAX_HEAP_SIZE);
            printConstantProp(ps, Constants.REPORTER_JVM_STARTUP_LAUNCHER);
            printConstantProp(ps, Constants.REPORTER_JVM_STARTUP_COMMAND_LINE);
            printConstantProp(ps, Constants.REPORTER_JVM_OTHER_TUNING);
            printConstantProp(ps, Constants.REPORTER_JVM_APP_CLASS_PATH);
            printConstantProp(ps, Constants.REPORTER_JVM_BOOT_CLASS_PATH);
            printConstantProp(ps, Constants.REPORTER_OS_NAME);
            printConstantProp(ps, Constants.REPORTER_OS_ADDRESS_BITS);
            printConstantProp(ps, Constants.REPORTER_OS_AVAILABLE_DATE);
            printConstantProp(ps, Constants.REPORTER_OS_TUNING);
            printConstantProp(ps, Constants.REPORTER_SW_FILESYSTEM);
            printConstantProp(ps, Constants.REPORTER_SW_OTHER_NAME);
            printConstantProp(ps, Constants.REPORTER_SW_OTHER_TUNING);
            printConstantProp(ps, Constants.REPORTER_SW_OTHER_AVAILABLE);
            printConstantProp(ps, Constants.REPORTER_HW_VENDOR);
            printConstantProp(ps, Constants.REPORTER_HW_VENDOR_URL);
            printConstantProp(ps, Constants.REPORTER_HW_MODEL);
            printConstantProp(ps, Constants.REPORTER_HW_AVAILABLE);
            printConstantProp(ps, Constants.REPORTER_HW_CPU_VENDOR);
            printConstantProp(ps, Constants.REPORTER_HW_CPU_VENDOR_URL);
            printConstantProp(ps, Constants.REPORTER_HW_CPU_NAME);
            printConstantProp(ps, Constants.REPORTER_HW_CPU_SPEED);
            printConstantProp(ps, Constants.REPORTER_HW_LOGICAL_CPUS);
            printConstantProp(ps, Constants.REPORTER_HW_NUMBER_OF_CHIPS);
            printConstantProp(ps, Constants.REPORTER_HW_NUMBER_OF_CORES);
            printConstantProp(ps, Constants.REPORTER_HW_NUMBER_OF_CORES_PER_CHIP);
            printConstantProp(ps, Constants.REPORTER_HW_THREADING_ENABLED);
            printConstantProp(ps, Constants.REPORTER_HW_THREADS_PER_CORE);
            printConstantProp(ps, Constants.REPORTER_HW_ADDRESS_BITS);
            printConstantProp(ps, Constants.REPORTER_HW_CPU_CACHE_L1);
            printConstantProp(ps, Constants.REPORTER_HW_CPU_CACHE_L2);
            printConstantProp(ps, Constants.REPORTER_HW_CPU_CACHE_OTHER);
            printConstantProp(ps, Constants.REPORTER_HW_MEMORY_SIZE);
            printConstantProp(ps, Constants.REPORTER_HW_MEMORY_DETAILS);
            printConstantProp(ps, Constants.REPORTER_HW_DETAILS_OTHER);
            printConstantProp(ps, Constants.REPORTER_BENCHMARK_VERSION, getProperty(Constants.KIT_VERSION_ENAME));
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void handleAnalyzersNode(BenchmarkChart chart, Node node) {
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node current = children.item(i);
            if (checkName(Constants.TYINFO_ENAME, current)) {
                NamedNodeMap attrs = current.getAttributes();
                String name = getValue(attrs, Constants.NAME_ENAME);
                long time = Long.parseLong(getValue(attrs, Constants.TIME_ENAME));
                long value = Long.parseLong(getValue(attrs,Constants.VALUE_ENAME));
                String units = getValue(attrs, Constants.UNIT_ENAME);
                if (Constants.HEAP_SIZE_ENAME.equals(name)) {
                    chart.addTotalHeapInfo(time, value);
                } else if (Constants.FREE_MEMORY_ENAME.equals(name)) {
                    chart.addFreeHeapInfo(time, value);
                } else {
                    chart.addAnalyzerReport(name, units, time, value);
                }
            }
        }
    }
    
    private String getProperty(String prop) {
        return root.getElementsByTagName(prop).item(0).getTextContent();
    }
    
    private boolean isValidRun() {
        return records.allBenchmarksValid;
    }
    
    private boolean isCompliantRun() {
        return violations == null || violations.size() == 0;
    }
    
    private void insertSuiteConfiguration(Writer w) {
        w.startTable("suite configuration", true);
        w.insertRecords(configs);
        w.endTable(true);
    }    
    
    public static String main2(String[] args) throws Exception {

        String rawFile = null;
        boolean doSub = true;
        boolean doHtml = true;
        boolean doSummary = true;
        boolean doTxt = true;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equalsIgnoreCase("-html")) {
                try {
                    doHtml = Boolean.parseBoolean(args[++i]);
                } catch (Exception e){
                    System.out.println("Error: Argument " + args[i-1] + " expects a boolean value.");
                }
            } else if (args[i].equalsIgnoreCase("-txt")) {
                try {
                    doTxt = Boolean.parseBoolean(args[++i]);
                } catch (Exception e){
                    System.out.println("Error: Argument " + args[i-1] + " expects a boolean value.");
                }
            } else if (args[i].equalsIgnoreCase("-summary")) {
                try {
                    doSummary = Boolean.parseBoolean(args[++i]);
                } catch (Exception e){
                    System.out.println("Error: Argument " + args[i-1] + " expects a boolean value.");
                }
            } else if (args[i].equalsIgnoreCase("-sub")) {
                try {
                    doSub = Boolean.parseBoolean(args[++i]);
                } catch (Exception e){
                    System.out.println("Error: Argument " + args[i-1] + " expects a boolean value.");
                }
            } else {
                if ((new File(args[i]).exists())) {
                    rawFile = args[i];
                } else {
                    System.out.println("Error: Cannot parse argument " + args[i] + ". Not a file.");
                }
            }
        }
        
        if (rawFile == null) {
            System.out.println("java spec.reporter.Reporter <raw file>");
            System.out.println("java spec.reporter.Reporter <raw file> [-html <bool>] [-txt <bool>] [-summary <bool>] [-sub <bool>]");
            return null;
        }

        if (!(new File(rawFile).exists())) {
            System.out.println("Cannot find file '" + rawFile + "'");
            return null;
        }
        
        ReportGenerator reporter = new ReportGenerator(rawFile, doHtml, doTxt, doSub, doSummary);
        return reporter.generateReport();
    }
    
    public static void main(String[] args) throws Exception {
        main2(args);
    }

    // Helper class.
    class Pair<A, B> {
        
        public A fst;
        public B snd;

        public Pair(A first, B second) {
            this.fst = first;
            this.snd = second;
        }
    }
}