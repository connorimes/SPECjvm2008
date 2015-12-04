/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * Copyright (c) 2006 Sun Microsystems, Inc. All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 */

package spec.benchmarks.compiler;

import com.sun.tools.javac.main.Main;
import com.sun.tools.javac.util.Context;

public class Compiler {    
    protected String[] args;    
    Context context;	   
	Main main = new Main("javac");
	SpecFileManager fileManager;
	public boolean skipVerify;
    
    public Compiler(String[] args) {
    	this.args = args;        
    }    
    
    public void compile(int compiles) {      
    	long checkSum = 0;
        for (int i = compiles - 1; i >=0; i--) {     
        	context = new Context();
        	SpecFileManager.preRegister(context, this);   
        	main = new Main("javac");        	
            int r = main.compile(args, context);
            if (r != 0) {
                spec.harness.Context.getOut().println("ERROR: compiler exit code: "
                		+ Integer.toString(r));
                break;
            }
            if (skipVerify) {
            	return;
            }
            if (i == 0) {
            	checkSum = fileManager.getChecksum();
            	spec.harness.Context.getOut().println("Total checksum:" + checkSum);
            } else if (i == compiles - 1) {
            	checkSum = fileManager.getChecksum();
            } else {
            	if (checkSum != fileManager.getChecksum()) {
            		spec.harness.Context.getOut().println("Total checksum on " 
            				+ i + " loop (" + fileManager.getChecksum() + ") differs from " 
            				+ "total checksum gooten on " 
            				+ (compiles - 1) + " loop (" + checkSum + ").");
            	}
            }
        }
    }        
}

