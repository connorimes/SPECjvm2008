/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 */
package spec.harness.analyzer;

public abstract class AnalyzerResult {
    
    protected double result = 0;
    
    /**
     * Constructor, create with time and value.
     */
    public AnalyzerResult(double result) {
        this.result = result;
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
     * @return Result of what is monitored.
     */
    public final double getResult() {
        return result;
    }
    
    /** 
     * Descriptive result including name and unit.
     */
    public String getDescription() {
        return getName() + ": " + getResult() + " " + getUnit();
    }
}
