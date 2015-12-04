/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 */
package spec.reporter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.TreeMap;

public abstract class Writer {
	
    protected PrintStream stream;
    protected FileOutputStream fos;
    String type;
    
    protected Writer(String outputFileName) {
        try {
            fos = new FileOutputStream(outputFileName);
            stream = new PrintStream(fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    public void closeStream() {
        stream.close();
    }
    
    public void startTable(String header, boolean needHeader) {};

    public void startTable(String header, boolean needHeader, 
    		               boolean showBorder, boolean onlyTableStart) {};

    public abstract void startDetailsTable();
    
    public void endTable(boolean endTbody) {};
    
    public abstract void insertRecord(String prop, String value);
    
    public abstract void insertRecords(ArrayList violations);
    
    public abstract void putSummaryTable(String scoreString, TreeMap<String, Double> scores);
    
    public abstract void putHat(String runStatus,
            String compositeScore, String workloadName,
            String category, String hwVendor, String jvmVendor, 
            String hwModel, String jvmName, String submitter,
            String testDate, String jvmVersion) ;
    
    public void startRecordAndData() {}
    
    public void endRecordAndData() {}
    
    public void endAndStartRecordAndData() {}
    
    public void endAndStartData() {};
    
    public void endTable() {};
    
    public abstract void handleBenchmarkRecord(BenchmarkRecord record);
    
    public void startReport() {};
    
    public void endReport() {};
    
    public abstract String getCopyRightSign();
    
    public static Writer getInstance(String desc, String outputFileName) {
        if (Utils.HTML_WRITER_TYPE.equals(desc)) {
            return (Writer) new HTMLWriter(outputFileName);
        } else if (Utils.PLAIN_WRITER_TYPE.equals(desc)) {
            return (Writer) new PlainWriter(outputFileName);
        } else {
            throw new RuntimeException("Cannot instantiate writer of type '" + desc + "'");
        }
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String aType) {
        type = aType;
    }
}