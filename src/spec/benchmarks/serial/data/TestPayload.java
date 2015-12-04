/* * JBoss, Home of Professional Open Source
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package spec.benchmarks.serial.data;

import java.io.Serializable;
import java.util.Random;

/**
 * *
 *
 * @author Tom Elrod
 */
public class TestPayload implements Serializable {
    private String name = null;
    
    private int id = -1;
    
    private TestPayload child = null;
    
    public static TestPayload createTestInstance() {
        TestPayload testObject = new TestPayload("foobar", new Random()
        .nextInt());
        TestPayload child = new TestPayload("child", new Random().nextInt());
        testObject.setChild(child);
        return testObject;
    }
    
    public String toString() {
        return "TestPayLoad(" + name + "," + id + "," + child + ")";
    }
    
    /**
     * Jbossserialization requires a default constructor. * We could accept
     * private constructors
     */
    public TestPayload() {
    }
    
    public TestPayload(String name, int id) {
        this.name = name;
        this.id = id;
    }
    
    public void setChild(TestPayload child) {
        this.child = child;
    }
    
    public TestPayload getChild() {
        return child;
    }
    
    public String getName() {
        return name;
    }
    
    public int getId() {
        return id;
    }
    
    public String getIdentity() {
        return name + id;
    }
    
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof TestPayload) {
            TestPayload tp = (TestPayload) obj;
            if (child == null) {
                if (tp.getChild() != null) {
                    return false;
                } else {
                    return getIdentity().equals(tp.getIdentity());
                }
            } else {
                if (tp.getChild() == null) {
                    return false;
                } else {
                    if (getChild().equals(tp.getChild())) {
                        return getIdentity().equals(tp.getIdentity());
                    } else {
                        return false;
                    }
                }
            }
        } else {
            return false;
        }
    }
}