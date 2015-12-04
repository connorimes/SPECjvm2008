/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package spec.benchmarks.serial.data;

import java.io.Serializable;
import java.math.BigInteger;

public class TestBigInteger extends BigInteger implements Serializable{
    BigInteger[] array;
    public TestBigInteger() {
        super("123456216534");
        //this.i = super.;
        System.out.println("AAA");
    }
    public static TestBigInteger createTestInstance() {
        return new TestBigInteger();
    }
    
        /* (non-Javadoc)
         * @see java.math.BigInteger#equals(java.lang.Object)
         */
    public boolean equals(Object obj) {
        // TODO Auto-generated method stub
        return super.equals(obj);
    }
    
}
