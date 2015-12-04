/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */

package spec.benchmarks.serial.data;

import java.io.Serializable;

/**
 * $Id: Child.java,v 1.1 2005/07/26 01:30:56 csuconic Exp $
 * @author Clebert Suconic
 */
public class Child implements Serializable {
    private int b=0;
    
    private TestParent parent;
    
    
    
    /**
     * @return Returns the b.
     */
    public int getB() {
        return b;
    }
    /**
     * @param b The b to set.
     */
    public void setB(int b) {
        this.b = b;
    }
    public String toString() {
        return "Child UniqueID=" + System.identityHashCode(this) + " and a=" + b + " parent UID=" + (parent==null?0:System.identityHashCode(parent));
    }
    
    public TestParent getParent() {
        return parent;
    }
    public void setParent(TestParent parent) {
        this.parent = parent;
    }
    
}


