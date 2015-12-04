package spec.benchmarks.serial;
/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 */


import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.BitSet;

public class Utils {
    public static final String[] classNames = new String[] {
        "TestArray", "TestArrayList",
        "TestByteArray", "TestClassReferenceTest",
        "TestClassWithSQLDateOnly", "TestDomainObject",
        "TestExceptionReference", "TestExternalizable", "TestHugeData",
        "TestParent", "TestPayload", "TestProxy", "TestReadResolve",
        "TestSimple", "TestWithBigDecimal", "TestWithFinalField" };
        
    public static int classesNumber = classNames.length;
    
    public static final String dataPackagePrefix = "spec.benchmarks.serial.data.";
    
    public static final int singleLoop = 1000;
    
    
    public static Object getInstance(int index) throws Exception {
        String fullName = Utils.dataPackagePrefix + classNames[index];
        Method method = Class.forName(fullName).getMethod("createTestInstance",
                (Class []) null);
        return method.invoke(Class.forName(fullName), (Object []) null);
    }
    
    public static BitSet createBitSet() {
        BitSet result = new BitSet();
        result.set(0, Utils.classesNumber);
        return result;
    }
    
    
    public static void printResult(PrintStream stream, BitSet results) {
        for (int i = 0; i < classesNumber; i ++) {
            stream.println(Utils.dataPackagePrefix + Utils.classNames[i] + ":"
                    + (results.get(i) ? "PASSED" : "FAILED"));
        }
    }
    
    public static Object[] createInstances() throws Exception {
        Object[] result = new Object[classesNumber]; 
        for (int i = 0; i < classesNumber; i ++) {        	
            result[i] = Utils.getInstance(i);
        }
        return result;
    }
}