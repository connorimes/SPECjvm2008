/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */

package spec.benchmarks.serial.data;

import java.io.Serializable;

/**
 * $Id: TestClassReferenceTest.java,v 1.1 2005/08/26 19:47:38 csuconic Exp $
 *
 * @author <a href="mailto:tclebert.suconic@jboss.com">Clebert Suconic</a>
 */
public class TestClassReferenceTest implements Serializable {
    
    Class refClass = String.class;
    public static TestClassReferenceTest createTestInstance() {
        return new TestClassReferenceTest();
    }
    
    public boolean equals(Object obj) {
        return refClass == ((TestClassReferenceTest)obj).refClass;
    }
}

