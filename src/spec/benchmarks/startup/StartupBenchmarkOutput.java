/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 */
package spec.benchmarks.startup;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import spec.harness.Constants;
import spec.harness.Util;

/**
 *
 * @author dagastine
 */
public class StartupBenchmarkOutput extends Thread {
    InputStream input;
    public String log = "";
    boolean printOut;
    public void run() {
        boolean debug = Util.getBoolProperty(Constants.DEBUG_STARTUP, null);
        String line;
        byte[] buf = new byte[80];
        int num;
        boolean validRun = false;
        BufferedInputStream in = new BufferedInputStream(input);
        //InputStreamReader in= new InputStreamReader(input);
        try {
            while((num=in.read(buf))>0) {
                line=new String(buf,0,num);
                if (printOut) {
                    log += line;
                }
                
                if (debug) {
                    System.err.print(line);
                }
                if (line.contains("Valid run!")) {
                    if (debug) {
                        System.out.println("Found valid run string");
                    }
                    validRun = true;
                    //break;
                }
            }
            if (validRun){
                Main.setStartupTestValidity(validRun);
            }
        } catch(IOException ioe) {
            System.out.println("IOERROR: Error reading StartupBenchmark subtest output!");
            ioe.printStackTrace();
        }
    }
    /** Creates a new instance of StartupBenchmarkOutput */
    public StartupBenchmarkOutput(InputStream input) {
        this.input = input;
    }
    
    /** Creates a new instance of StartupBenchmarkOutput */
    public StartupBenchmarkOutput(InputStream input, boolean printOut) {
        this.input = input;
        this.printOut = printOut;
    }
    
}
