/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * Copyright (c) 1997,1998 Sun Microsystems, Inc. All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 */

package spec.io;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;

import spec.harness.Constants;
import spec.harness.Context;
import spec.harness.Util;
import spec.harness.results.LoopResult;

/**
 * This class implements a special from of OutputStream that is used by to
 * validity check the output from the benchmarks. Data is written here when
 * spec.harness.Context.out.println() is called.
 */
public class ValidityCheckOutputStream extends OutputStream {
    
    private final static boolean DEBUG = Util.getBoolProperty(Constants.DEBUG_VALIDATION, null);
    
    /** Name of benchmark */
    String benchName;
    
    /**
     * Output stream used in collect the data.
     */
    java.io.ByteArrayOutputStream ostream = new java.io.ByteArrayOutputStream(4096);
    
    /**
     * Number of validation errors found
     */
    int errors = 0;
    
    /**
     * Creates a new PrintStream.
     *
     * @param benchName Name of benchmark.
     */
    public ValidityCheckOutputStream(String benchName) {
        this.benchName = benchName;
    }
    
    /**
     * Writes a byte.
     *
     * @param b the byte.
     * @exception IOException If an I/O error has occurred.
     */
    public void write(int b) throws java.io.IOException {       
        ostream.write(b);       
        flush();
    }
    
    /**
     * Validity check the output and write any errors into the results property
     * file
     *
     * @param lResults the bean that contains config info and will contain the errors.
     * @return boolean, whether it is valid or not.
     */
    public boolean validityCheck(LoopResult lResults) {
        try {
            validityCheck2(lResults);
        } catch (IOException x) {
            String msg = "Error in validityCheck. " + x.getClass().getName() + ": " + x.getMessage();
            lResults.addError(msg);
            errors++;
        }
        if (errors != 0) {
            // Context.out.println("#############  NOT VALID  #############");
            return false;
        } else {
            return true;
        }
    }
    
    /**
     * Validity check the output and write any errors into the results property file
     *
     * @param lResults the bean that contains config info and will contain the errors.
     */
    private void validityCheck2(LoopResult lResults) throws IOException {
    	
        String fileName = getValidityFileName(benchName);
        
        if (! new File(fileName).exists()) {
            throw new IOException("Can't find validation file " + fileName
                    + ". Verify that the run is done from or the property "
                    + Constants.HOMEDIR_PROP + " point to the base dir for SPECjvm2008.");
        }
        BufferedReader in1 = new BufferedReader(new InputStreamReader(Context.getFileCache().getStream(fileName)));
        BufferedReader in2 = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(ostream.toByteArray())));
        
        for (int ln = 1; errors < 50; ln++) {
            
            String l1 = in1.readLine();
            String l2 = in2.readLine();
            
            if (l1 == null && l2 == null) {
                // The output is ok.
                break;
            }
            
            if (DEBUG) {
                System.err.println("l1='" + l1 + "'");
                System.err.println("l2='" + l2 + "'");
            }
            
            if (l1 != null && l2 != null && l1.equals(l2)) {
                continue;
            }
            
            handleValidationFailure(lResults, ln, l1, l2);
            errors++;
            break;
        }
        in1.close();
        in2.close();
    }

	private void handleValidationFailure(LoopResult lResults, int failedLineNo, String el1, String el2) throws IOException {
		
		int rows = 5;
		String validationFileName = getValidityFileName(benchName);
		String errorLogFileName = Context.getResultDirNameCurrent() 
				+ "/" + benchName 
				+ ".bt" + lResults.getBmThreadId() 
				+ ".i" + lResults.getIteration()
				+ ".op" + (lResults.getLoopCnt() + 1)
				+ ".error.log";

        // Create summary of failure
		StringBuilder report = new StringBuilder("Validation failure on line " + failedLineNo);
		if (el1 == null) {
		    report.append(", more output than expected.\n");
		} else if (el2 == null) {
		    report.append(", expected more output.\n");
		} else /* l1 != l2 */{
		    report.append(".\n");
		}
		
		report.append("    Expected output: \n");
		BufferedReader in1 = new BufferedReader(new InputStreamReader(Context.getFileCache().getStream(validationFileName)));
        String l1 = in1.readLine();
		for (int ln = 1; l1 != null; ln++) {

        	String lns = (ln < 10 ? "00" + ln : ln < 100 ? "0" + ln : "" + ln);

    		if (ln > failedLineNo - 2 && ln < failedLineNo + rows) {
    		    report.append("    [" + lns + "] " + l1 + "\n");
    		}
    		if (ln == failedLineNo + rows) {
    			report.append("    [" + lns + "] ...\n\n");
    		}
    		l1 = in1.readLine();
		}
		in1.close();
        
        report.append("    Received output: \n");
        BufferedReader in2 = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(ostream.toByteArray())));
        PrintStream ps = new PrintStream(new FileOutputStream(errorLogFileName));
        String l2 = in2.readLine();
        for (int ln = 1; l2 != null; ln++) {

        	ps.println(l2);

        	String lns = (ln < 10 ? "00" + ln : ln < 100 ? "0" + ln : "" + ln);
        	
        	if (ln > failedLineNo - 2 && ln < failedLineNo + rows) {
    		    report.append("    [" + lns + "] " + l2 + "\n");
    		}
    		if (ln == failedLineNo + rows) {
    			report.append("    [" + lns + "] ...\n\n");
    		}
    		l2 = in2.readLine();
        }
        in2.close();

        report.append("Complete output: " + (new File(errorLogFileName).getCanonicalPath()) + "\n");
        report.append("Validation file: " + (new File(validationFileName).getCanonicalPath()) + "\n\n");
        
		lResults.addError(report.toString());
	}
    
    
    
    public static String getValidityFileName(String bmName) {
        return Context.getSpecBasePath() + "/resources/" + bmName + "/"
                + "validity." + bmName +  ".dat";
    }
    
    /**
     * Create the valitity check file. This is only used in the devlopment cycle.
     */
    public void createValidityFile() {
        try {
            String fullName = getValidityFileName(benchName);
            Context.getOut().println("Opening " + fullName);
            java.io.FileOutputStream out = new java.io.FileOutputStream(fullName);
            out.write(ostream.toByteArray());
            out.close();
        } catch (java.io.IOException x) {
            Context.getOut().println("Error creating validity check file: " + x);
        }
    }
}
