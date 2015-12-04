/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */

package spec.benchmarks.serial.data;

import java.io.Serializable;
import java.util.Random;

/**
 * $Id: TestArrayDouble.java,v 1.1 2005/10/24 05:03:27 csuconic Exp $
 *
 * @author <a href="mailto:tclebert.suconic@jboss.com">Clebert Suconic</a>
 */
public class TestArrayDouble implements Serializable {
    
    public static final int ARRAY_SIZE=50;
    int x;
    
    TestDomainObject[][] arrayDouble;
    
    public TestArrayDouble() {
        x = new Random().nextInt();
        arrayDouble = new TestDomainObject[10][];
        for (int i=0;i<10;i++) {
            arrayDouble[i] = new TestDomainObject[10];
            for (int j=0;j<10;j++) {
                arrayDouble[i][j] = new TestDomainObject(false);
            }
        }
    }
    
    public static TestArrayDouble createTestInstance() {
        return new TestArrayDouble();
    }
    
    
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        final TestArrayDouble testArray = (TestArrayDouble) o;
        
        if (x != testArray.x) return false;
        
        for (int i=0;i<arrayDouble.length;i++) {
            for (int j=0;j<arrayDouble[i].length;j++) {
                if (!arrayDouble[i][j].equals(testArray.arrayDouble[i][j])) return false;
            }
        }
        
        return true;
    }
    
    public int hashCode() {
        return x;
    }
    
}

