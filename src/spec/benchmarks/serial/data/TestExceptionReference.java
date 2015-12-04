/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */

package spec.benchmarks.serial.data;

import java.io.IOException;
import java.util.Random;

/**
 * $Id: TestExceptionReference.java,v 1.2 2005/08/31 19:02:07 csuconic Exp $
 *
 * @author <a href="mailto:tclebert.suconic@jboss.com">Clebert Suconic</a>
 */
public class TestExceptionReference implements java.io.Serializable {
    IOException exception;
    
    public TestExceptionReference() {
        exception = new IOException(""+new Random().nextInt());
    }
    
    public static TestExceptionReference createTestInstance() {
        return new TestExceptionReference();
    }
    
    public boolean equals(Object obj) {
        TestExceptionReference compared = (TestExceptionReference)obj;
        
        return exception.getMessage().equals(compared.exception.getMessage());
    }
    
    public String toString() {
        return "TestExceptionReference( message= " + exception.getMessage() + ")";
    }
    
}

