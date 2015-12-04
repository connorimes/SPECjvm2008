/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 */
package spec.benchmarks.helloworld;
import java.io.PrintStream;

public class HelloWorld {
    
    public static void main() {
        HelloWorld hw = new HelloWorld();
        hw.run();
    }
    
    
    
    public void run() {
        PrintStream p = spec.harness.Context.getOut();
        p.println("Hello World!");
        
        
        
        
    }
    
}



