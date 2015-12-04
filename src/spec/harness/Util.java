/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 */
package spec.harness;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;


public class Util {   
    /**
     * Get the value of the property passed in.
     * 1. Get the value of the benchmark specific property, that is <propname>.<bmname>
     * 2. Get the value of the base property.
     * 3. Use the default value.
     *
     * A time can be passed in as a long in millis, or with a unit on it, like '60s'.
     * Possible units are
     *   ms, millis seconds
     *   s, seconds
     *   m, minutes
     *   h, hours
     *
     * @return The value of the property, or what is in default.
     * @throws NumberFormatException
     */
    public static long getTimeProperty(String propname, String bmName)
    throws NumberFormatException {
        String value = getProperty(propname, bmName);
        return getTimeAsMillis(value);
    }
    
    /**
     * A time can be passed in as a long in millis, or with a unit on it, like '60s'.
     * Possible units are
     *   ms, millis seconds
     *   s, seconds
     *   m, minutes
     *   h, hours
     *
     * @return The value of the property, or what is in default.
     * @throws NumberFormatException
     */
    public static long getTimeAsMillis(String value)
    throws NumberFormatException {
        if (value.indexOf("ms") != -1) {
            return Long.parseLong(value.substring(0, value.indexOf("ms")));
        } else if (value.indexOf("s") != -1) {
            return 1000 * Long
                    .parseLong(value.substring(0, value.indexOf("s")));
        } else if (value.indexOf("m") != -1) {
            return 1000 * 60 * Long.parseLong(value.substring(0, value
                    .indexOf("m")));
        } else if (value.indexOf("h") != -1) {
            return 1000 * 60 * 60 * Long.parseLong(value.substring(0, value
                    .indexOf("h")));
        } else {
            return 1000 * Long.parseLong(value);
        }
    }
    
    /**
     * A time can be passed in as a long in millis, or with a unit on it, like '60s'.
     * Possible units are
     *   ms, millis seconds
     *   s, seconds
     *   m, minutes
     *   h, hours
     *
     * @return The value of the property, or what is in default.
     * @throws NumberFormatException
     */
    public static long getTimeAsSeconds(String value) {
        return getTimeAsMillis(value) / 1000;
    }
    
    /**
     * Get the calculated int value of the property passed in. 1. Get the value
     * of the benchmark specific property, that is <propname>.<bmname> 2. Get
     * the value of the base property. 3. Use the default value.
     *
     * A hard ware thread property can be passed in as fixed or relativ.
     * Examples: #hwt, same as number of hard ware threads on the machine
     * (processors available to java) 2x#hwt, 2 times the number of hardware
     * threads 4, will return 4.
     *
     * @return Number of hardware threads calculated based on value.
     * @throws NumberFormatException
     */
    public static int getHwtCalcProperty(String propname, String bmName)
    throws NumberFormatException {
        String value = getProperty(propname, bmName);
        int hwtFactor = 1;
        double ret = 1;
        if (value.indexOf("x") != -1) {
            ret = Double.parseDouble(value.substring(0,value.indexOf("x")));
            value = value.substring(1+value.indexOf("x"), value.length());
        }
        if (value.indexOf("#hwt") != -1) {
            int tmp = Util.getIntProperty(Constants.BENCHMARK_THREADS_HW_OVERRIDE_PROP, bmName);
            hwtFactor = (tmp == -1 ? Runtime.getRuntime().availableProcessors() : tmp);
            value = value.substring(0, value.indexOf("#hwt"));
        }
        if (value.length() > 0) {
            ret = Integer.parseInt(value);
        }
        return (int) Math.round(ret * hwtFactor);
    }
    
    /**
     * Get the value of the property passed in as a boolean.
     * 1. Get the value of the benchmark specific property, that is <propname>.<bmname>
     * 2. Get the value of the base property.
     * 3. Use the default value.
     * @return The value of the property, or what is in default.
     */
    public static boolean getBoolProperty(String propname, String bmName) {
        String value = getProperty(propname, bmName);
        return value == null ? false : (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("1"));
    }
    
    /**
     * Get the value of the property passed in as a int.
     * 1. Get the value of the benchmark specific property, that is <propname>.<bmname>
     * 2. Get the value of the base property.
     * 3. Use the default value.
     * @return The value of the property, or what is in default, or -1 if nothing is set.
     */
    public static int getIntProperty(String propname, String bmName) {
        String value = getProperty(propname, bmName);
        return value == null ? -1 : Integer.parseInt(value);
    }
    
    /**
     * Get the value of the property passed in as a long.
     * 1. Get the value of the benchmark specific property, that is <propname>.<bmname>
     * 2. Get the value of the base property.
     * 3. Use the default value.
     * @return The value of the property, or what is in default.
     */
    public static long getLongProperty(String propname, String bmName) {
        String value = getProperty(propname, bmName);
        return value == null ? -1 : Long.parseLong(value);
    }
    
    private static String getPropertyHelper(Properties props, String propname, String bmName) {
        String value = null;
        if (value == null && bmName != null) {
            value = props.getProperty(propname + "." + bmName);
            if (value != null && value.equals("null")) {
                value = null;
            }
        }
        if (value == null) {
            value = props.getProperty(propname);
            if (value != null && value.equals("null")) {
                value = null;
            }
        }
        return value;
    }
    
    /**
     * Get the value of the property passed in.
     * 1. Get the value of the benchmark specific property, that is <propname>.<bmname>
     * 2. Get the value of the base property.
     * 3. Use the default value.
     * @param propname Name of the property (base name).
     * @param bmName Name of the benchmark that gets the property.
     * @return The value of the property, or null if all options are unset.
     */
    public static String getProperty(String propname, String bmName) {
        
        String value = null;
        
        // Always use default properties for check
        // Check is not like the other children...
        if (bmName != null && bmName.equals("check")) {
            if (value == null && Configuration.defaultProperties != null) {
                value = getPropertyHelper(Configuration.defaultProperties, propname, bmName);
            }
        }
        
        if (value == null && Configuration.userProperties != null) {
            value = getPropertyHelper(Configuration.userProperties, propname, bmName);
        }
        
        if (value == null && Configuration.defaultProperties != null) {
            value = getPropertyHelper(Configuration.defaultProperties, propname, bmName);
        }
        return value;
    }
    
    static String getDefaultProperty(String propname, String bmName) {
        return getPropertyHelper(Configuration.defaultProperties, propname, bmName);
    }

    static String getUserProperty(String propname, String bmName) {
        return getPropertyHelper(Configuration.userProperties, propname, bmName);
    }

    public static void printProperties(Properties props) {
        printProperties(Context.getOut(), props);
    }
    
    public static void printProperties(PrintStream ps, Properties props) {
        if (props != null) {
            Enumeration e = props.keys();
            while (e.hasMoreElements()) {
                String key = (String) e.nextElement();
                String value = (String) props.getProperty(key);
                ps.println("  " + key + "=" + value);
            }
        }
    }
    
    public static void printReqs(HashMap<String,String> reqs) {
        printProperties(Context.getOut(), reqs);
    }
    
    public static void printProperties(PrintStream ps, HashMap<String,String> reqs) {
        if (reqs != null) {
            Iterator<String> i = reqs.keySet().iterator();
            while (i.hasNext()) {
                String key = i.next();
                String value = reqs.get(key);
                ps.println("  " + key + "=" + value);
            }
        }
    }
    
    public static boolean sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            return false;
        }
        return true;
    }
    
    public static boolean isScimarkAndNotMonteCarlo(String name) {
    	return name != null && name.startsWith(Constants.SCIMARK_BNAME_PREFIX) 
    	    && !name.equals(Constants.SCIMARK_MONTE_CARLO_BNAME); 
    }    
    
    public static String getNextRawFileInDir() throws IOException {
        File resultDir = new File(Util.getProperty(Constants.RESULT_DIR_PROP, null));
        return getNextRawFileInDir(resultDir);
    }
    
    private static boolean createDir(File dir) {
        
        // If root is reached without being a dir, give up
        if (dir == null) {
            return false;
        }
        
        // If directory exists, we are done
        if (dir.exists()) {
            return true;
        }
        
        // If parent exists (now)...
        if (createDir(dir.getParentFile())) {
            // Create this and return result
            return dir.mkdir();
        } else {
            // Parent cannot be created.
            return false;
        }
    }
    
    public static String getNextRawFileInDir(File resultDir) throws IOException {
        File newResultDir = null;
        String localName = null;
        for (int i = 1; newResultDir == null; i++) {
            String is = (i < 10 ? "00" + i : i < 100 ? "0" + i : "" + i);
            localName = "SPECjvm2008." + is;
            newResultDir = new File(resultDir.getAbsolutePath() + "/" + localName);
            if (newResultDir.exists()) {
                newResultDir = null;
            }
        }
        
        if (!createDir(newResultDir)) {
            throw new IOException("Failed to create dir " + newResultDir.getAbsolutePath());
        }
        
        Context.setResultDirNameCurrent(newResultDir + "");
        
        return newResultDir + "/" + localName + ".raw";
    }

    public static boolean isBenchmark(String s) {
        if (Constants.CHECK_BNAME.equals(s)) {
            return false;
        } else if(s.startsWith(Constants.STARTUP_BNAME_PREFIX)) {
            int index=s.indexOf(".");
            if (index == -1) {
                return false;
            }
            String startupBenchmark = s.substring(index+1,s.length());
            if (startupBenchmark.startsWith(Constants.SCIMARK_BNAME_PREFIX)) {
                return new File(Launch.specjvmHomeDir + "/resources/" + startupBenchmark + "/validity." + startupBenchmark + ".dat").exists();
            }
            return isBenchmark(startupBenchmark);
        } else if(Util.isScimarkAndNotMonteCarlo(s)) {
            int index=s.lastIndexOf(".");
            if (index == -1) {
                return false;
            }
            String scimarkBenchmark = s.substring(0,index);
            return new File(Launch.specjvmHomeDir + "/resources/" + scimarkBenchmark + "/validity." + scimarkBenchmark + ".dat").exists();
        } else
            return new File(Launch.specjvmHomeDir + "/resources/" + s + "/validity." + s + ".dat").exists();
    }
    
    static String [] parseDefaultJvmArgs() {
        // Get command line details:
        String allArgs = Util.getProperty(Constants.REPORTER_JVM_COMMAND_LINE, "n/a");
        String msArg = Util.getProperty(Constants.REPORTER_JVM_COMMAND_LINE_INITIAL_HEAP_SIZE, "n/a");
        String mxArg = Util.getProperty(Constants.REPORTER_JVM_COMMAND_LINE_MAX_HEAP_SIZE, "n/a");
        
        if (Launch.parseDefaultArgs && (allArgs == null || allArgs.equals("n/a"))) {
            RuntimeMXBean rtMXBean = ManagementFactory.getRuntimeMXBean();
            List<String> args = rtMXBean.getInputArguments();
            StringBuilder sb = new StringBuilder();
            for (Iterator<String> iter = args.iterator(); iter.hasNext(); ) {
                String arg = iter.next();
                if (arg.indexOf("bootclasspath") != -1 
                        || arg.indexOf("sun.java.launcher") != -1
                        || arg.indexOf("-showversion") != -1
                        || arg.indexOf("java.home") != -1) {
                    continue;
                }

                if (sb.length() > 0) {
                    sb.append(" ");
                }
                sb.append(arg);
                if (arg.indexOf("-Xms") != -1 || arg.indexOf("-ms") != -1) {
                    msArg = getSizeFromHeapArg(arg);
                }
                if (arg.indexOf("-Xmx") != -1 || arg.indexOf("-mx") != -1) {
                    mxArg = getSizeFromHeapArg(arg);
                }
            }
            allArgs = sb.toString();
        }
        String [] ret = new String[3];
        ret[0] = allArgs;
        ret[1] = msArg;
        ret[2] = mxArg;
        return ret; 
	}

	private static String getSizeFromHeapArg(String arg) {
        String msArg = null;
        if (arg == null) {
            return msArg;
        }
        for (int i = 1; i < arg.length(); i++) {
            if (Character.isDigit(arg.charAt(i))) {
                msArg = arg.substring(i);   
                break;
            }
        }
        return msArg;
    }
}
