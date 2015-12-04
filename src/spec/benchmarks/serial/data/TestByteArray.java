/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */

package spec.benchmarks.serial.data;

import java.util.Random;

/**
 * $Id: TestByteArray.java,v 1.1 2005/09/13 14:53:19 csuconic Exp $
 *
 * @author <a href="mailto:tclebert.suconic@jboss.com">Clebert Suconic</a>
 */
public class TestByteArray implements java.io.Serializable {
    
    byte[] bytes;
    
    public TestByteArray() {
        bytes = new byte[10];
        Random random = new Random();
        random.nextBytes(bytes);
    }
    
    public static TestByteArray createTestInstance() {
        return new TestByteArray();
    }
    
    public boolean equals(Object obj) {
        TestByteArray ref = (TestByteArray) obj;
        
        for (int i=0;i<bytes.length;i++) {
            if (bytes[i]!=ref.bytes[i]) return false;
        }
        
        return true;
    }
    
}

