/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 */
package spec.reporter;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Iterator;
import java.util.Formatter;

import spec.harness.Constants;
import spec.harness.results.TestResult;

public class PlainWriter extends Writer {

    public PlainWriter(String outputFileName) {
        super(outputFileName);
        setType(Utils.PLAIN_WRITER_TYPE);
    }  

    private void handleIterRecord(BenchmarkRecord records) {
        BenchmarkRecord.IterationRecord iterRecord;
        Formatter formatter = new Formatter(stream);
        for (int i = 0; i < records.iterRecords.size(); i++) {
            iterRecord = records.iterRecords.get(i);
            stream.println();
            formatter.format("%1$-30s%2$-15s%3$-15s%4$-15s", records.name, iterRecord.iterName, iterRecord.expectedRunTime, iterRecord.runTime);            
            if (iterRecord.isValidIteration()) {
                formatter.format("%1$-15.2f%2$-15.2f", iterRecord.operations, iterRecord.score);
            } else {
                stream.print(iterRecord.errors);
            }
        }
        stream.println();
    }   

    public void handleBenchmarkRecord(BenchmarkRecord record) {
        if (!Constants.CHECK_BNAME.equals(record.name)) {
            handleIterRecord(record);
        } else {
            if (record.isValidRun()) {
                stream.println("PASSED");
            } else {
                if (record.iterRecords != null && record.iterRecords.size() > 0) {
                    stream.println("FAILED");
                }
            }
            stream.println();
            Formatter formatter = new Formatter(stream);
            formatter.format("%1$-30s%2$-15s%3$-15s%4$-15s%5$-15s%6$-15s", "Benchmark", "Iteration", "Expected(ms)", "Actual(ms)", "Operations", Constants.WORKLOAD_METRIC);
            stream.println();
            stream.println("---------------------------------------------------------------------------------------------------");
        }
    }

    public void insertRecord(String prop, String value) {
        Formatter formatter = new Formatter(stream);
        formatter.format("%1$-30s%2$-40s", prop, value);
        stream.println();
    }

    public void putHat(String runStatus,
            String compositeScore, String workloadName, String category, String hwVendor,
            String jvmVendor, String hwModel, String jvmName,
            String submitter, String testDate, String jvmVersion) {
        stream.println("================================");
        stream.println(workloadName);
        stream.println("================================");
        stream.println(hwVendor + " " + hwModel);
        stream.println(jvmVendor + " " + jvmName);
        stream.println("Tested by: " + submitter + " Test date: " + testDate);
        stream.println(runStatus);
    }

    public void putSummaryTable(String scoreString, TreeMap<String, Double> scores) {
        stream.println();
        stream.println("================================");
        
        Iterator<String> iteratorBenchmarks = scores.keySet().iterator();
        while (iteratorBenchmarks.hasNext()) {
            String key = iteratorBenchmarks.next();
            insertRecord(key, Utils.formatScore(scores.get(key)));                
        }

        stream.println(scoreString);
        stream.println("================================");
        stream.println();
        stream.println();
    }

    public void startDetailsTable() {
        stream.println();
        stream.println("Details of Runs");
        stream.println("---------------");
    }   

    public void insertRecords(ArrayList violations) {
        if (violations != null) {
            stream.println();
            for (int i = 0; i < violations.size(); i++) {
                stream.println(TestResult.correctContent(((String) violations.get(i)), false));
            }
        }
        stream.print("");
    }    
    	
	public String getCopyRightSign() {
		return "(C)";
	}
}







