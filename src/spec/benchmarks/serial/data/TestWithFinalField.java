/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */

package spec.benchmarks.serial.data;

import java.util.Random;
import java.io.Serializable;

/**
 * $Id: TestWithFinalField.java,v 1.1 2005/09/01 17:12:12 csuconic Exp $
 *
 * @author <a href="mailto:tclebert.suconic@jboss.com">Clebert Suconic</a>
 */
public class TestWithFinalField implements Serializable {
    
    protected final int value;
    
    public TestWithFinalField() {
        value = new Random().nextInt();
    }
    
    public static TestWithFinalField createTestInstance() {
        return new TestWithFinalField();
    }
    
    
    public boolean equals(Object obj) {
        return value==((TestWithFinalField)obj).value;
    }
    
}

