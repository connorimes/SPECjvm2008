/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 */
package spec.harness.analyzer;

public abstract class TYInfo {
    
    protected long time = 0;
    protected long value = 0;
    
    /**
     * Constructor, create with time and value.
     */
    public TYInfo(long time, long value) {
        this.time = time;
        this.value = value;
    }
    
    /**
     * @return Name/description of what is monitored.
     */
    public abstract String getName();
    
    /**
     * @return Unit of what is monitored.
     */
    public abstract String getUnit();
    
    /**
     * @return Time for when it is monitored.
     */
    public long getTime() {
        return time;
    }
    
    /**
     * @return Value of what is monitored.
     */
    public long getValue() {
        return value;
    }
}
