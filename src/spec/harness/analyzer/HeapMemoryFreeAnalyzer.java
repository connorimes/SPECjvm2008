/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 */
package spec.harness.analyzer;

import spec.harness.Constants;

public class HeapMemoryFreeAnalyzer extends AnalyzerBase {
    
    private static String name = Constants.FREE_MEMORY_ENAME;
    private static String unit = "bytes";
    
    public void execute(long time) {
        report(new HeapMemoryFree(time, Runtime.getRuntime().freeMemory()));
    }
    
    public static class HeapMemoryFree extends TYInfo {
        
        public HeapMemoryFree(long time, long value) {
            super(time, value);
        }
        
        public String getName() {
            return name;
        }
        
        public String getUnit() {
            return unit;
        }
    }
}
