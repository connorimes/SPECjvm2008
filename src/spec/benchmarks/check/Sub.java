/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * Copyright (c) 1997,1998 Sun Microsystems, Inc. All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 * See what happens here where we subclass Super
 */

package spec.benchmarks.check;

public class Sub extends Super{
    
///////////////////////////////////////
//class variable field declarations
///////////////////////////////////////
    
    private static String name = "Sub";
    private static int psi = publicStatic + 7;
    
///////////////////////////////////////
//instance variable field declarations
///////////////////////////////////////
    
    private int priv = 5;
    protected int prot = 11;
    public int pub = 13;
    
///////////////////////////////////////
//constructor declarations
///////////////////////////////////////
    
    public Sub(int black){
        super(black + 77);
        pub += black * 2;
    }
    
///////////////////////////////////////
//class method declarations
///////////////////////////////////////
    
///////////////////////////////////////
//instance method declarations
///////////////////////////////////////
    
    public String getName(){
        return name;
    }
    
    public int getPrivate(){
        return priv + 100;
    }
    
    public int getProtected(){
        return prot + 100;
    }
    
}

