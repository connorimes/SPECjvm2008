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
 * $Id: TestSimple.java,v 1.1 2005/11/03 20:48:25 csuconic Exp $
 * @author Clebert Suconic
 */
public class TestSimple implements Serializable {
    public TestSimple() {
        Random random = new Random();
        this.x = random.nextInt();
    }
    
    private int x;
    
    
    public static TestSimple createTestInstance() {
        TestSimple myTest = new TestSimple();
        return myTest;
    }
    
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        final TestSimple that = (TestSimple) o;
        
        if (x != that.x) return false;
        
        return true;
    }
    
    public int hashCode() {
        return x;
    }
    
    public String toString() {
        return "TestSimple:" + x;
    }
}

