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

public class TempAnalyzer extends AnalyzerBase {

    private HashMap<String, String> tempResults = null;
    private static String baseName = "Temperature";
    private static TempSensor tempSensor = null;
    private static boolean verbose = false;
    private static boolean dummy = false;
    private static String tempSensorHost;
    private static int tempSensorPort;
    private boolean doMeasure = false;

    public static void setupAnalyzerClass() {

        tempSensorHost = Util.getProperty(Constants.ANALYZER_TEMP_HOST, null);
        tempSensorPort = Util.getIntProperty(Constants.ANALYZER_TEMP_PORT, null);
        verbose = Util.getBoolProperty(Constants.ANALYZER_TEMP_VERBOSE, null);
        dummy = Util.getBoolProperty(Constants.ANALYZER_TEMP_DUMMY, null);

        if (tempSensorHost == null) {
            tempSensorHost = "127.0.0.1";
        }

        if (tempSensorPort == -1) {
            tempSensorPort = 8889;
        }

        if (verbose) {
            Context.getOut().println("Connecting to temp sensor " + tempSensorHost + ":" + tempSensorPort);
        }

        if (!dummy) {
            tempSensor = new TempSensor(tempSensorHost, tempSensorPort);
            if (verbose) {
                Context.getOut().println("Temperature sensor type is " + tempSensor.sensorType());
                Context.getOut().println("Temperature sensor ptd version is " + tempSensor.sensorPtdVersion());
                Context.getOut().println("Temperature sensor ptd host OS is " + tempSensor.sensorPtdHostOs());
            }
            if (!tempSensor.sensorCompliant()) {
                AnalyzerBase.addViolationToSuiteResult("Warning: temperature sensor not SPEC compliant!");
            }
            if (!tempSensor.sensorPtdMode().equals("temperature")) {
                AnalyzerBase.addViolationToSuiteResult("Error: temp sensor ptd not in temperature mode, data will be invalid!");
            }

            tempSensor.go(); // start it now to measure entire benchmark
        } else {
            AnalyzerBase.addViolationToSuiteResult("Dummy temp sensor used. Data will be invalid!");
        }
    }

    public static void tearDownAnalyzerClass() {
        if (verbose) {
            Context.getOut().println("Tearing down temperature sensor class.");
        }
        if (dummy) {
            return;
        }
        tempSensor.stop();
        tempSensor.endSocConnection();
    }

    public TempAnalyzer() {
        ; // 
    }

    public void setup() {

        // Only measure in the timed runs.
        // This excludes startup runs, which are to short for
        // getting good power results.
        doMeasure = isTimedRun();
    }

    public void startMeasurementInterval() {
        if (!doMeasure) {
            return;
        }
        if (verbose) {
            Context.getOut().println("Starting temp measurement interval.");
        }
        if (dummy) {
            return;
        }
        // tempSensor.go();
    }

    public void endMeasurementInterval() {
        if (!doMeasure) {
            return;
        }
        if (verbose) {
            Context.getOut().println("Ending temperature measurement interval.");
        }
        if (dummy) {
            return;
        }
        tempResults = tempSensor.reportRun("test");
        // temperature sensors are too slow, so we let it run for the full run
        // of the benchmark suite, which also lets it capture the temperature during 
        // the other periods
        // tempSensor.stop();
    }

    public void tearDown() {
        if (!doMeasure) {
            return;
        }
        if (verbose) {
            Context.getOut().println("Tearing down TempAnalyzer.");
        }
        if (dummy) {
            report(new TemperatureResult(3.1415D, baseName + " Avg temp", "C"));
            return;
        }

        report(new TemperatureResult(Double.parseDouble(tempResults.get("Avg Temp")), baseName + " Avg temp", "C"));
        report(new TemperatureResult(Double.parseDouble(tempResults.get("Min Temp")), baseName + " Min temp", "C"));
        report(new TemperatureResult(Double.parseDouble(tempResults.get("Max Temp")), baseName + " Max temp", "C"));
        report(new TemperatureResult(Double.parseDouble(tempResults.get("Temp Samples")), baseName + " temp samples", ""));
        report(new TemperatureResult(Double.parseDouble(tempResults.get("Temp Errors")), baseName + " temp errors", ""));

        // validity checking - dependent on run rules
        Double tempErrorPercent = 100.0 * Double.parseDouble(tempResults.get("Temp Errors"))
                / Double.parseDouble(tempResults.get("Temp Samples"));
        if (tempErrorPercent >= 2.0) {
            addError("Warning: Temperature error rate = " + tempErrorPercent
                    + "% >= 2%; temperature measurement not valid.");
        }
        if (Double.parseDouble(tempResults.get("Min Temp")) < 20.0) {
            addError("Warning: Minimum temperature less than 20 C; run will not be SPEC compliant!");
        }
    }

    public void execute(long time) {
        ; // Intentially do nothing.
    }

    public static class TemperatureResult extends AnalyzerResult {

        String resName;
        String resUnit;

        public TemperatureResult(double result, String name, String unit) {
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
