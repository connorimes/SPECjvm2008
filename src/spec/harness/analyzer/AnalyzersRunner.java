 
package spec.harness.analyzer;

import java.lang.reflect.Method;
import java.util.StringTokenizer;

import spec.harness.Context;
import spec.harness.Launch;
import spec.harness.ProgramRunner;
import spec.harness.StopBenchmarkException;
import spec.harness.results.IterationResult;

public class AnalyzersRunner extends Thread {
    
    private static String analyzerNames;
    private static Class [] analyzerClasses;
    private static boolean debug = Launch.verbose;

    private static AnalyzersRunner analyzerRunner;

    private AnalyzerBase [] analyzers;
    private IterationResult itResult;
    private long analyzerFrequenzy;
    private boolean doDidDone;
    
    private static Object token = new Object();
    
    public AnalyzersRunner(IterationResult itResult, long afreq) {
        super("Analyzer Runner");
        this.itResult = itResult;
        this.analyzerFrequenzy = afreq;
        this.doDidDone = false;
        
        internalSetupAnalyzers();
    }
    
    public void run() {
        
        // Let program runner know that analyzers are started.
        synchronized (token) {
            token.notify();
        }
        
        boolean cont = true;
        while (cont && !isDone()) {
            try {
                cont = executeAnalyzers();
                sleep(analyzerFrequenzy);
            } catch (InterruptedException e) {
                cont = false;
                if (debug) {
                    Context.getOut().println("[Analyzers] Interrupted to be closed down.");
                }
            }
        }
        // Always do a final check, when all is done...
        if (cont) {
            cont = executeAnalyzers();
        }
    }
    
    private synchronized boolean isDone() {
        return doDidDone;
    }
    
    private synchronized void setDone() {
        this.doDidDone = true;
    }
    
    public static int getNoAnalyzer() {
        return analyzerClasses == null ? 0 : analyzerClasses.length;
    }
    
    public static String getAnalyzerNames() {
        return analyzerNames;
    }
    
    private boolean executeAnalyzers() {
        
        if (debug) {
            Context.getOut().println("[Analyzers] Executing analyzers.");
        }
    
        long currtime = System.currentTimeMillis();
        boolean check = true;
        try {
            for (int i = 0; i < analyzers.length; i++) {
                analyzers[i].execute(currtime);
            }
        } catch(Throwable t) {
            t.printStackTrace();
            check = false;
        }
        return check;
    }
    
    // Can not have paramterized arrays
    @SuppressWarnings(value={"unchecked"})
    public static void setupAnalyzerClasses(String analyzers) throws ClassNotFoundException {
        
        if (debug) {
            Context.getOut().println("[Analyzers] Setting up analyzer classes.");
        }

        AnalyzersRunner.analyzerNames = analyzers;
        StringTokenizer st = new StringTokenizer(analyzers == null ? "" : analyzers);
        
        analyzerClasses = new Class[st != null ? st.countTokens() : 0];
        for (int i = 0; i < analyzerClasses.length; i++) {
            String aname = "spec.harness.analyzer." + st.nextToken();
            analyzerClasses[i] = Class.forName(aname);
            try {
                analyzerClasses[i].asSubclass(AnalyzerBase.class);
            } catch (ClassCastException cce) {
                throw new StopBenchmarkException(aname + " is not of type " + AnalyzerBase.class.getName());
            }
            try {
                Method m = analyzerClasses[i].getMethod("setupAnalyzerClass", new Class[0]);
                m.invoke(null, new Object[0]);
            } catch (Exception e) {
                throw new StopBenchmarkException("Error invoking " + analyzerClasses[i].getName() + ".setupAnalyzerClass(). " + e.getClass().getName() + ": " + e.getMessage());
            }
        }
    }
    
    // Can not have paramterized arrays
    @SuppressWarnings(value={"unchecked"})
    public static void tearDownAnalyzerClasses() {
        
        if (debug) {
            Context.getOut().println("[Analyzers] Tearing down analyzer classes.");
        }

        for (int i = 0; i < analyzerClasses.length; i++) {
            try {
                Method m = analyzerClasses[i].getMethod("tearDownAnalyzerClass", new Class[0]);
                m.invoke(null, new Object[0]);
            } catch (Exception e) {
                throw new StopBenchmarkException("Error invoking " + analyzerClasses[i].getName() + ".tearDownAnalyzerClass(). " + e.getClass().getName() + ": " + e.getMessage());
            }
        }
    }
    
    public boolean internalSetupAnalyzers() {
        
        if (debug) {
            Context.getOut().println("[Analyzers] Setting up analyzers.");
        }
        
        boolean ret = true;
        analyzers = new AnalyzerBase[analyzerClasses.length];
        for (int i = 0; i < analyzers.length; i++) {
            try {
                analyzers[i] = (AnalyzerBase) analyzerClasses[i].newInstance();
                analyzers[i].setIterationResult(itResult);
                analyzers[i].setup();
            } catch (InstantiationException e) {
                ret = false;
                Context.getOut().println("Error instantiating analyzer " + analyzerClasses[i].getName() + ": " + e.getMessage());
                e.printStackTrace(Context.getOut());
            } catch (IllegalAccessException e) {
                ret = false;
                Context.getOut().println("Error instantiating analyzer " + analyzerClasses[i].getName() + ": " + e.getMessage());
                e.printStackTrace(Context.getOut());
            } catch (Throwable t) {
                ret = false;
                Context.getOut().println("Error setting up analyzer " + analyzerClasses[i].getName() + ": " + t.getMessage());
                t.printStackTrace(Context.getOut());
            }
        }
        
        return ret;
    }

    public static void invokeStartMeasurementIntervalForAnalyzers() {
        
        if (getNoAnalyzer() == 0) {
            return;
        }
        
        if (debug) {
            Context.getOut().println("[Analyzers] Invoking start measurement interval for analyzers.");
        }
        
        for (int i = 0; i < analyzerRunner.analyzers.length; i++) {
            try {
                analyzerRunner.analyzers[i].startMeasurementInterval();
            } catch (Throwable t) {
                Context.getOut().println("Error invoking start measurement interval on analyzer "
                        + analyzerClasses[i].getName() + ": "
                        + t.getMessage());
                t.printStackTrace(Context.getOut());
            }
        }
    }

    public static void invokeEndMeasurementIntervalForAnalyzers() {

        if (getNoAnalyzer() == 0) {
            return;
        }

        if (debug) {
            Context.getOut().println("[Analyzers] Invoking end measurement interval for analyzers.");
        }
        
        for (int i = 0; i < analyzerRunner.analyzers.length; i++) {
            try {
                analyzerRunner.analyzers[i].endMeasurementInterval();
            } catch (Throwable t) {
                Context.getOut().println("Error invoking end measurement interval on analyzer "
                        + analyzerClasses[i].getName() + ": "
                        + t.getMessage());
                t.printStackTrace(Context.getOut());
            }
        }
    }

    private void internalTearDownAnalyzers() {
        
        if (debug) {
            Context.getOut().println("[Analyzers] Tearing down analyzers.");
        }
        
        for (int i = 0; i < analyzers.length; i++) {
            try {
                analyzers[i].tearDown();
            } catch (Throwable t) {
                Context.getOut().println("Error setting up analyzer "
                        + analyzerClasses[i].getName() + ": "
                        + t.getMessage());
                t.printStackTrace(Context.getOut());
            }
        }
    }

    public static void setupAnalyzers(IterationResult itResult, long frequency) {
        
        if (AnalyzersRunner.getNoAnalyzer() > 0) {
            
            synchronized (token) {
                analyzerRunner = new AnalyzersRunner(itResult, frequency);
                analyzerRunner.start();
                try {
                    token.wait();
                } catch (InterruptedException e) {
                    throw new StopBenchmarkException("Failed to setup analyzers.", e);
                }
            }
        }
    }

    public static void tearDownAnalyzers(IterationResult itResult) {
        if (AnalyzersRunner.getNoAnalyzer() > 0) {
            analyzerRunner.setDone();
            analyzerRunner.interrupt();
            try {
                analyzerRunner.join();
            } catch (InterruptedException e) {
                String msg = "Interrupted when joining analyzer thread: " + e.getMessage();
                itResult.addError(msg);
                itResult.addError(ProgramRunner.getStackTraceAsString(e));
                throw new StopBenchmarkException(msg, e);
            }
            
            analyzerRunner.internalTearDownAnalyzers();
        }
    }
}
