/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 */
package spec.harness.analyzer;

import spec.harness.Constants;

public class HeapMemoryTotalAnalyzer extends AnalyzerBase {
    
    private static String name = Constants.HEAP_SIZE_ENAME;
    private static String unit = "bytes";
    
    public void execute(long time) {
        report(new HeapMemoryTotal(time, Runtime.getRuntime().totalMemory()));
    }
    
    public static class HeapMemoryTotal extends TYInfo {
        
        public HeapMemoryTotal(long time, long value) {
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