/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 */

package spec.harness.analyzer;

import java.util.HashMap;

import spec.harness.Constants;
import spec.harness.Context;
import spec.harness.Util;

public class PowerAnalyzer extends AnalyzerBase {
    
    private HashMap<String, String> powerResults = null;

    private static String baseName = "Power Consumption";
    private static PowerMeter powerMeter = null;
    private static boolean verbose = false; 
    private static boolean dummy = false; 
    private static String powerMeterHost;
    private static int powerMeterPort; 
    
    private boolean doMeasure = false;

    public static void setupAnalyzerClass() {

        powerMeterHost = Util.getProperty(Constants.ANALYZER_POWER_HOST, null);
        powerMeterPort = Util.getIntProperty(Constants.ANALYZER_POWER_PORT, null); 
        verbose = Util.getBoolProperty(Constants.ANALYZER_POWER_VERBOSE, null);
        dummy = Util.getBoolProperty(Constants.ANALYZER_POWER_DUMMY, null);
        
        if (powerMeterHost == null) {
            powerMeterHost = "127.0.0.1";
        }
        
        if (powerMeterPort == -1) {
            powerMeterPort = 8888;
        }

        if (verbose) {
            Context.getOut().println("Connecting to power analyzer " + powerMeterHost + ":" + powerMeterPort);
        }
        
        if (dummy) {
            AnalyzerBase.addViolationToSuiteResult("Dummy power analyzer used. Data will be invalid!");
            return;
        }

        powerMeter = new PowerMeter(powerMeterHost, powerMeterPort);
        if (verbose) {
            Context.getOut().println("Power analyzer type is " + powerMeter.meterType());
            Context.getOut().println("Power analyzer ptd version is " + powerMeter.meterPtdVersion());
            Context.getOut().println("Power analyzer ptd host OS is " + powerMeter.meterPtdHostOs());
        }
        if (!powerMeter.meterCompliant()) {
            AnalyzerBase.addViolationToSuiteResult("Warning: power analyzer not SPEC compliant!");
        }
        if (!powerMeter.meterPtdMode().equals("power")) {
            AnalyzerBase.addViolationToSuiteResult("Error: power analyzer ptd not in power mode, data will be invalid!");
        }
    }

    public static void tearDownAnalyzerClass() {
        if (verbose) {
            Context.getOut().println("Tearing down power analyzer class.");
        }
        if (dummy) {
            return;
        }
		powerMeter.endSocConnection();
    }
    
    public PowerAnalyzer() {
    	; // 
    }
    
    public void setup() {

        // Only measure in the timed runs.
        // This excludes check and startup runs,
        // which are to short for getting good power results.
        doMeasure = isTimedRun();
    }

    public void startMeasurementInterval() {
        if (!doMeasure) {
            return;
        }
        if (verbose) {
            Context.getOut().println("Starting power measurement interval.");
        }
        if (dummy) {
            return;
        }
        powerMeter.go();
    }

    public void endMeasurementInterval() {
        if (!doMeasure) {
            return;
        }
        if (verbose) {
            Context.getOut().println("Ending power measurement interval.");
        }
        if (dummy) {
            return;
        }
        powerMeter.stop();
        powerResults = powerMeter.reportRun("test");
    }
    
    public void tearDown() {
        if (!doMeasure) {
            return;
        }
        if (verbose) {
            Context.getOut().println("Tearing down PowerAnalyzer.");
        }
        if (dummy) {
            report(new PowerConsumtionResult(3.14D, baseName + " Avg watts", "W"));
            report(new PowerConsumtionResult(1.21D, baseName + " Min watts", "W"));
            //addError("Warning: Power Analyzer actually run in dummy mode.");
            //addError("Warning: Power Analyzer still run in dummy mode.");
            return;
        }
		report(new PowerConsumtionResult(Double.parseDouble(powerResults.get("Avg Watts")), baseName + " Avg watts", "W"));
		report(new PowerConsumtionResult(Double.parseDouble(powerResults.get("Min Watts")), baseName + " Min watts", "W"));
		report(new PowerConsumtionResult(Double.parseDouble(powerResults.get("Max Watts")), baseName + " Max watts", "W"));
		report(new PowerConsumtionResult(Double.parseDouble(powerResults.get("Watts Samples")), baseName + " watts samples", ""));
		report(new PowerConsumtionResult(Double.parseDouble(powerResults.get("Watts Errors")), baseName + " watts errors", ""));
	
		report(new PowerConsumtionResult(Double.parseDouble(powerResults.get("Avg Volts")), baseName + " Avg volts", "V"));
		report(new PowerConsumtionResult(Double.parseDouble(powerResults.get("Min Volts")), baseName + " Min volts", "V"));
		report(new PowerConsumtionResult(Double.parseDouble(powerResults.get("Max Volts")), baseName + " Max volts", "V"));
		report(new PowerConsumtionResult(Double.parseDouble(powerResults.get("Volts Samples")), baseName + " volts samples", ""));
		report(new PowerConsumtionResult(Double.parseDouble(powerResults.get("Volts Errors")), baseName + " volts errors", ""));

		report(new PowerConsumtionResult(Double.parseDouble(powerResults.get("Avg Amps")), baseName + " Avg amps", "A"));
		report(new PowerConsumtionResult(Double.parseDouble(powerResults.get("Min Amps")), baseName + " Min amps", "A"));
		report(new PowerConsumtionResult(Double.parseDouble(powerResults.get("Max Amps")), baseName + " Max amps", "A"));
		report(new PowerConsumtionResult(Double.parseDouble(powerResults.get("Amps Samples")), baseName + " amps samples", ""));
		report(new PowerConsumtionResult(Double.parseDouble(powerResults.get("Amps Errors")), baseName + " amps errors", ""));

		report(new PowerConsumtionResult(Double.parseDouble(powerResults.get("Avg PF")), baseName + " Avg PF", ""));
		report(new PowerConsumtionResult(Double.parseDouble(powerResults.get("Min PF")), baseName + " Min PF", ""));
		report(new PowerConsumtionResult(Double.parseDouble(powerResults.get("Max PF")), baseName + " Max PF", ""));
		report(new PowerConsumtionResult(Double.parseDouble(powerResults.get("PF Samples")), baseName + " PF samples", ""));
		report(new PowerConsumtionResult(Double.parseDouble(powerResults.get("PF Errors")), baseName + " PF errors", ""));

		// validity checks for analyzer output - dependent on run rules
		Double wattErrorPercent = 100.0 * Double.parseDouble(powerResults.get("Watts Errors"))/Double.parseDouble(powerResults.get("Watts Samples")); 
		if (wattErrorPercent >= 1.0) {
			addError("Warning: Power Analyzer Watt error rate = " + wattErrorPercent + "% >= 1%; power measurement not valid.");
		}
		Double voltErrorPercent = 100.0 * Double.parseDouble(powerResults.get("Volts Errors"))/Double.parseDouble(powerResults.get("Volts Samples")); 
		if (voltErrorPercent >= 2.0) {
			addError("Warning: Power Analyzer Volt error rate = " + voltErrorPercent + "% >= 2%; power measurement not valid.");
		}
		Double ampErrorPercent = 100.0 * Double.parseDouble(powerResults.get("Amps Errors"))/Double.parseDouble(powerResults.get("Amps Samples")); 
		if (ampErrorPercent >= 2.0) {
			addError("Warning: Power Analyzer Amp error rate = " + ampErrorPercent + "% >= 2%; power measurement not valid.");
		}
		Double pfErrorPercent = 100.0 * Double.parseDouble(powerResults.get("PF Errors"))/Double.parseDouble(powerResults.get("PF Samples")); 
		if (pfErrorPercent >= 2.0) {
			addError("Warning: Power Analyzer PF error rate = " + pfErrorPercent + "% >= 2%; power measurement not valid.");
		}
    }

    public void execute(long time) {
        ; // Intentionally do nothing.
    }

    public static class PowerConsumtionResult extends AnalyzerResult {
        
        String resName;
        String resUnit;
        
        public PowerConsumtionResult(double result, String name, String unit) {
            super(result);
            this.resName = name;
            this.resUnit = unit;
        }
        
        public String getName() {
            return resName;
        }
        
        public String getUnit() {
            return resUnit;
        }
    }
}
