/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 */
package spec.harness.analyzer;

import java.util.Random;

/**
 * This Analyzer is an example of an Analyzer that is based on a callback  
 * mechanism available in the SPECjvm2008 analyzer functionality.
 * 
 * The harness will call the method execute at a regular time interval, 
 * but it will not do anything in this case, just run the empty version of it.
 *
 * Instead the Analyzer is listening on call backs from some other system and
 * adds the info to the iteration result.
 *
 * This Analyzer type is good to use when you want to log events, 
 * for example listening to the garbage collector or code generator.
 * 
 * In this example data will be gathered from a simulated outer source which 
 * started as a thread in this class. So it is a contrived example.
 * 
 * It will also report results as summaries of the events gathered 
 * throughout the iteration. This is available in the result raw file afterwards.
 */
public class CallbackAnalyzerExample extends AnalyzerBase {
    
    private static String name = "A random number";
    private static String unit = "number";

    private TheNumberGenerator externalInputSimulator = null;
    private int sum = 0;
    private int max = 0;
    
    public void execute(long time) { 
      // Deliberately empty
    }
    
    public void setup() {
        TheNumberGenerator.register(this);
    }
    
    public void startMeasurementInterval() {
        // No need to do anything.
    }

    public void endMeasurementInterval() {
        // No need to do anything.
    }

    public void tearDown() {
        TheNumberGenerator.deregister(this);
        
        // Report the max and sum 
        report(new NumberResult(max, "Max value", "number"));
        report(new NumberResult(sum, "Sum value", "number"));
    }

    public void callbackMethod(int value) {
        long time = System.currentTimeMillis();
        this.sum += value;
        this.max = (value > max ? value : max);
        report(new NumberInfo(time, value));

    }
    
    public static class NumberInfo extends TYInfo {
        
        public NumberInfo(long time, long value) {
            super(time, value);
        }
        
        public String getName() {
            return CallbackAnalyzerExample.name;
        }
        
        public String getUnit() {
            return CallbackAnalyzerExample.unit;
        }
    }
    
   public static class NumberResult extends AnalyzerResult {
        
        String resName;
        String resUnit;
        
        public NumberResult(double result, String name, String unit) {
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

   /**
    * This is a number generator which will calculate somewhat random numbers.
    * It is used as a simulator of an external source to feed back numbers every now and then.
    * It will do this using callbacks.
    */
    public static class TheNumberGenerator extends Thread {

        private static TheNumberGenerator theOne;
        
        private Random random = new Random();
        private CallbackAnalyzerExample listener = null;

        static {
            theOne = new TheNumberGenerator();
            theOne.setDaemon(true);
            theOne.start();
        }

        private TheNumberGenerator() { }

        private synchronized void setListener(CallbackAnalyzerExample cae) {
            listener = cae;
        }
        
        public static void register(CallbackAnalyzerExample cae) {
            theOne.setListener(cae);
        }

        public static void deregister(CallbackAnalyzerExample cae) {
            theOne.setListener(null);
        }

        public void run() {
            try {
                System.out.println("TheNumberGenerator starting.");
                int r1 = random.nextInt(50);
                boolean up = true;
                while (true) {
                    int r2 = random.nextInt(50);
                    if (r2 > 45) {
                        up = !up;
                    }
                    if (up) {
                        r1 += 2;
                    } else {
                        r1 -= 2;
                    }
                    synchronized (this) {
                        if (listener != null) {
                            listener.callbackMethod(r1);
                        }
                    }
                    sleep(40 * r2 + 1); // Sleep up to 2 seconds
                }
            } catch(InterruptedException ie) {
                System.out.println("TheNumberGenerator shutting down.");
            }
        }
    }
}
