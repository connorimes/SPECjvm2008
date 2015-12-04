/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 * Copyright (c) 2006 Standard Performance Evaluation Corporation (SPEC) 
 *               All rights reserved. 
 * Copyright (c) 2006 Dell Inc. All rights reserved. 
 *               2006/11/14 gnd: first version
 *
 * This source code is provided as is, without any express or implied warranty.
 */
package spec.harness.analyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

import spec.harness.Constants;
import spec.harness.Context;
import spec.harness.Util;
import spec.harness.StopBenchmarkException;

public class PowerMeter {

    //private final static boolean verbose = Boolean.parseBoolean(System.getProperty("spec.harness.analyzer.PowerMeter.verbose", "false"));
	private final static boolean verbose = Util.getBoolProperty(Constants.ANALYZER_METER_VERBOSE, null);
	static String[] AnalyzerCommands = {"Watts","Volts","Amps","PF"};
	static String[] identifyParts = {"Error","0","0","0","0","0","0","0","0","Error","Error","Error"};
   
    private Socket meterSocket;
    private PrintWriter outputToMeter;
    private BufferedReader inputFromMeter;

    public PowerMeter(String host, int port) {
        
        if (verbose) {
            Context.getOut().println("\nChecking for ptd power connection...\n");
        }
        
        try {
            meterSocket = new Socket(host, port);
        } catch (UnknownHostException unknown) {
            throw new StopBenchmarkException("Could not connect to power ptd.", unknown);
        } catch (IOException er) {
            throw new StopBenchmarkException("Error connecting to power ptd.", er);
        }

        if (verbose) {
            Context.getOut().println("Connection established...");
        }
        
        try {
            outputToMeter = new PrintWriter(meterSocket.getOutputStream(), true);
            inputFromMeter = new BufferedReader(new InputStreamReader(meterSocket.getInputStream()));
        } catch (IOException er) {
            throw new StopBenchmarkException("Error connection to meter server.", er);
        }
        
        if (verbose) {
            Context.getOut().println("Reader/writer set up\n");
        }
        
        outputToMeter.println("Identify");
        try {
            String meterResponse = inputFromMeter.readLine();
			String[] meterResponseParts;
            if (verbose) {
                Context.getOut().println("Meter response: " + meterResponse);
            }
			meterResponseParts = meterResponse.split(",");
			if (meterResponseParts.length == 12) 
			{
				identifyParts = meterResponseParts;
				 
				if (verbose) 
				{
					Context.getOut().println("Power analyzer type: " + meterResponseParts[0]);
					Context.getOut().println("Power analyzer SPEC compliant: " + meterResponseParts[8]);
					Context.getOut().println("PTD version: " + meterResponseParts[9]);
					Context.getOut().println("PTD host OS: " + meterResponseParts[10]);
					Context.getOut().println("PTD mode: " + meterResponseParts[11]);
				}
			} 
			else 
			{ // error in meter response
				Context.getOut().println("Warning! Unexpected input from power meter: " + meterResponse);
				Context.getOut().println("Expected 12 tokens, separated with ','.");
			}

        } 
		catch (IOException er) 
		{
            throw new StopBenchmarkException(er.getMessage(), er);
        }

        if (verbose) {
            Context.getOut().println("Meter connection successful!\n");
        }
    }

    private int writeMeter(String mString) {
        outputToMeter.println(mString);
        return (1);
    }

    private String readMeter() {
        String meterResponse = null;
        try {
            meterResponse = inputFromMeter.readLine();
        } catch (IOException er) {
            throw new StopBenchmarkException(er.getMessage(), er);
        }
        
        if (verbose) {
            Context.getOut().println("Meter response: " + meterResponse);
        }
 
        return (meterResponse);
    }

    public void endSocConnection() {
        try {
            outputToMeter.close();
            meterSocket.close();
        } catch (IOException e) {
            throw new StopBenchmarkException("Error closing the power analyzer connection", e);
        }
    }

    public void startRun(int samples, int rate_ms, int rampup_ms) {
        String msg;

        msg = String.format("Timed,%d,%d,%d", samples, rate_ms, rampup_ms);
        writeMeter(msg);
        if (verbose) {
            Context.getOut().println("Meter responds:" + readMeter());
        }
    }

    public void go() {
        writeMeter("Go,0,0");
        readMeter();
    }

    public void stop() {
        writeMeter("Stop");
        readMeter();
    }

    public HashMap<String, String> reportRun(String propPrefix) {

 
	HashMap<String, String> results = new HashMap<String, String>();
	String msg;
	String[] msg_parts;
		
	for (int i=0; i<4; i++) 
	{	
		writeMeter(AnalyzerCommands[i]);
		msg = readMeter();
		if (verbose) 
		{
			Context.getOut().println(msg);
		}
		msg_parts = msg.split(",");
		if (msg_parts.length == 7) 
		{
			results.put("Avg " + AnalyzerCommands[i],  msg_parts[1]);
			results.put("Min " + AnalyzerCommands[i], msg_parts[2]);
			results.put("Max " + AnalyzerCommands[i], msg_parts[3]);
			results.put(AnalyzerCommands[i] + " Samples", msg_parts[4]);
			results.put(AnalyzerCommands[i] + " Errors", msg_parts[5]);
		} 
		else 
		{ // error in meter response
			Context.getOut().println("Warning! Unexpected input from power meter: " + msg);
			Context.getOut().println("Expected 7 tokens, separated with ','.");
			// fill results with bogus values
			results.put("Avg " + AnalyzerCommands[i],  "-1.0");
			results.put("Min " + AnalyzerCommands[i], "-1.0");
			results.put("Max " + AnalyzerCommands[i], "-1.0");
			// put 1 sample and 1 error (rather than 0 and 0) so we don't get divide by 0 exception
			//   when calculating error percentage
			results.put(AnalyzerCommands[i] + " Samples", "1");
			results.put(AnalyzerCommands[i] + " Errors", "1");

		}

	}

          
        return results;
    }

	
	public String meterType() 
	{
		return (identifyParts[0]);
	}

	public Boolean meterCompliant() 
	{
		return (identifyParts[8].equals("1"));
	}

	public String meterPtdVersion() 
	{
		if(identifyParts[9].substring(0,8).equals("version="))
		{
			return (identifyParts[9].substring(8));
		}
		return("Error");
	}
	
	public String meterPtdHostOs() 
	{
		if(identifyParts[10].substring(0,3).equals("OS="))
		{
			return (identifyParts[10].substring(3));
		}
		return("Error");
	}
	
	public String meterPtdMode() 
	{
		if(identifyParts[11].substring(0,5).equals("mode="))
		{
			return (identifyParts[11].substring(5));
		}
		return("Error");
	}
}
