/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */

package spec.benchmarks.serial.data;

import java.io.Externalizable;
import java.io.ObjectOutput;
import java.io.IOException;
import java.io.ObjectInput;
import java.util.Random;

/**
 * $Id: TestExternalizable.java,v 1.2 2005/09/21 20:40:06 csuconic Exp $
 *
 * @author <a href="mailto:tclebert.suconic@jboss.com">Clebert Suconic</a>
 */
public class TestExternalizable implements Externalizable {
    
    
    int test;
    
    public TestExternalizable() {
    }
    
    public TestExternalizable(int test) {
        this.test=test;
    }
    
    public static TestExternalizable createTestInstance() throws Exception {
        int value = new Random().nextInt();
        return new TestExternalizable(value);
    }
    
    
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(test);
    }
    
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        test = in.readInt();
    }
    
    public int hashCode() {
        return test;
    }
    
    public boolean equals(Object obj) {
        return test==((TestExternalizable)obj).test;
    }
    
    public int getTest() {
        return test;
    }
    
    public void setTest(int test) {
        this.test = test;
    }
    
    public String toString() {
        return "TestExternalizable:test=" + test;
    }
}


