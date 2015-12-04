/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 */
package spec.reporter;

import java.util.ArrayList;

import spec.harness.Constants;

public class BenchmarkRecord {
    static class IterationRecord {
        long runTime;
        String iterName;
        double operations;
        double score;
        String expectedRunTime;
        ArrayList<String> errors;
        
        public IterationRecord(String iterName,
                String expectedRunTime,
                String startTime, String endTime, String operations) {
            this.iterName = iterName;
            runTime = Long.parseLong(endTime) - Long.parseLong(startTime);
            this.operations = Double.parseDouble(operations);
            this.score = ((double) Double.parseDouble(operations)) * 60000 / (double)runTime;
            this.expectedRunTime = expectedRunTime;
        }
        
        final boolean isValidIteration() {
            return errors == null;
        }
        
        final void addError(String message) {
            if (errors == null) {
                errors = new ArrayList<String>();
            }
            errors.add(message);
            
        }
        
    }
    
    String name;
    String iterationsInfo;
    ArrayList<IterationRecord> iterRecords = new ArrayList<IterationRecord>();
    double maxScore = Double.MIN_VALUE;
    String[] configuration = new String[Utils.BM_CONFIGURATION_ENAMES.length];
    boolean isSubgroupMember;
    
    public BenchmarkRecord(String name, int numberBmThreads) {
        this.name = name;
        isSubgroupMember = name.indexOf(".") >= 0;
    }
    
    public void startHandling(String info) {
        iterationsInfo = info;
    }
    
    public IterationRecord addIterationRecord(String iter, String expectedRunTime,
            String startTime, String endTime, String operations) {
        
        String key = Constants.WARMUP_RESULT_ENAME.equals(iterationsInfo)
        ? "warmup" : "iteration " + iter;
        
        IterationRecord record = new IterationRecord(key, expectedRunTime,
                startTime, endTime, operations);
        
        if (!key.equals("warmup")) {
            maxScore = Math.max(maxScore, record.score);
        }
        iterRecords.add(record);
        return record;
    }
    
    public void printAllRecordedInfo() {
        for (int i = 0; i < iterRecords.size(); i ++) {
            IterationRecord record = (IterationRecord)iterRecords.get(i);
            System.out.println(record.iterName + " " + record.runTime + " " + record.operations);
        }
    }
    
    public boolean isValidRun() {
        boolean result = true;
        for (int i = 0; i < iterRecords.size(); i ++) {
            result = result & ((IterationRecord)iterRecords.get(i)).isValidIteration();
        }
        return result;
    }
}
