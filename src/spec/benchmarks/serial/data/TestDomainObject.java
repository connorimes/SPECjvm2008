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
 * $Id: TestDomainObject.java,v 1.4 2005/10/28 17:26:00 csuconic Exp $
 *
 * Reference for this class:
 * http://www.jboss.com/index.html?module=bb&op=viewtopic&t=71200
 *
 * From user irvingd on that forum
 *
 * @author <a href="mailto:tclebert.suconic@jboss.com">Clebert Suconic</a>
 */
public class TestDomainObject implements Serializable {
    static Random random = new Random();
    
    public static TestDomainObject createTestInstance() {
        TestDomainObject testObject = new TestDomainObject(true);
        return testObject;
    }
    
    private static final int CHILD_COUNT = 10;
    
    private TestDomainObject[] children;
    private long aLong = random.nextLong();
    private String aString = "test"  + random.nextDouble();
    private int anInt= random.nextInt();
    private int id =random.nextInt();
    private Long objLong = Long.valueOf(random.nextLong());
    private Boolean newBoolean = Boolean.valueOf(true);
    
    TestDomainObject() {
        this(true);
        System.out.println("shouldn't be called");
    }
    
    TestDomainObject(boolean hasChildren) {
        this.id = random.nextInt();
        
        if (hasChildren) {
            children = new TestDomainObject[CHILD_COUNT];
            for (int i=0; i<CHILD_COUNT; ++i) {
                children[i] = new TestDomainObject(false);
            }
        }
    }
    
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        final TestDomainObject that = (TestDomainObject) o;
        
        if (aLong != that.aLong) return false;
        if (anInt != that.anInt) return false;
        if (id != that.id) return false;
        if (aString != null ? !aString.equals(that.aString) : that.aString != null) return false;
        
        return true;
    }
    
    public int hashCode() {
        int result;
        result = (int) (aLong ^ (aLong >>> 32));
        result = 29 * result + (aString != null ? aString.hashCode() : 0);
        result = 29 * result + anInt;
        result = 29 * result + id;
        return result;
    }
}

