/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */

package spec.benchmarks.serial.data;

import java.util.ArrayList;
import java.util.Random;

/**
 * $Id: TestArrayList.java,v 1.2 2005/10/21 16:06:21 csuconic Exp $
 *
 * @author <a href="mailto:tclebert.suconic@jboss.com">Clebert Suconic</a>
 */
public class TestArrayList implements java.io.Serializable {
    
    int x;
    ArrayList<TestDomainObject> list = new ArrayList<TestDomainObject>();
    
    public TestArrayList() {
        x = new Random().nextInt();
        for (int i=0;i<10;i++) {
            list.add(new TestDomainObject(false));
        }
    }
    
    public static TestArrayList createTestInstance() {
        return new TestArrayList();
    }
    
    public int hashCode() {
        return x;
    }
    
    public boolean equals(Object obj) {
        if (x!=((TestArrayList)obj).x) {
            return false;
        } else {
            return list.equals(((TestArrayList)obj).list);
        }
    }
    
    
}

