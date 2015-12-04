/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */

package spec.benchmarks.serial.data;

import java.io.Serializable;
import java.util.Random;
import java.util.Arrays;

/**
 * $Id: TestArray.java,v 1.1 2005/10/21 16:06:21 csuconic Exp $
 *
 * @author <a href="mailto:tclebert.suconic@jboss.com">Clebert Suconic</a>
 */
public class TestArray implements Serializable {
    
    public static final int ARRAY_SIZE=50;
    int x;
    
    TestDomainObject[] arrayTest;
    
    public TestArray() {
        x = new Random().nextInt();
        arrayTest  = new TestDomainObject[ARRAY_SIZE];
        
        for (int i=0;i<ARRAY_SIZE;i++) {
            arrayTest[i] = new TestDomainObject(false);
        }
    }
    
    public static TestArray createTestInstance() {
        return new TestArray();
    }
    
    
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        final TestArray testArray = (TestArray) o;
        
        if (x != testArray.x) return false;
        if (!Arrays.equals(arrayTest, testArray.arrayTest)) return false;
        
        return true;
    }
    
    public int hashCode() {
        return x;
    }
    
}

