/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */

package spec.benchmarks.serial.data;

import java.io.Serializable;

/**
 * $Id: TestReadResolve.java,v 1.1 2005/09/02 21:12:10 csuconic Exp $
 *
 * @author <a href="mailto:tclebert.suconic@jboss.com">Clebert Suconic</a>
 */
public class TestReadResolve implements Serializable {
    
    public static final TestReadResolve[] testReadResolveField = new TestReadResolve[]{new TestReadResolve(0,"zero"), new TestReadResolve(0,"zero")};
    
    public int order;
    public transient String name;
    
    public TestReadResolve(int order, String name) {
        this.order=order;
        this.name=name;
    }
    
    public Object readResolve() {
        return testReadResolveField[order];
    }
    
    
    public static TestReadResolve createTestInstance() {
        return testReadResolveField[1];
    }
    
    public boolean equals(Object obj) {
        TestReadResolve objRef = (TestReadResolve)obj;
        return order==objRef.order && name.equals(objRef.name);
    }
    
    public String toString() {
        return "TestReadResolve order=" + order + " name=" + name;
    }
    
}

