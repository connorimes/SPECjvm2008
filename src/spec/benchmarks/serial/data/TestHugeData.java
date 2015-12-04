/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */

package spec.benchmarks.serial.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 * $Id: TestHugeData.java,v 1.2 2005/10/24 22:10:46 csuconic Exp $
 *
 * @author <a href="mailto:tclebert.suconic@jboss.com">Clebert Suconic</a>
 */
public class TestHugeData implements Serializable {
    
    static Random random = new Random();
    
    public static TestHugeData createTestInstance() {
        return createTestInstance(10);
    }
    
    private static TestHugeData createTestInstance(int deep) {
        TestHugeData hugeData = new TestHugeData();
        for (int i=0;i<5;i++) {
            hugeData.data.add("sdflkjsdfkljsdfkljsdflkjsdlfkjsdklfjskldfjslkdfjslkdfjslkdfj;alskdfj a;sldkfj a;skldjf a;klsdfj ;alksdfj a;lksdfj ;asklf" + random.nextDouble());
        }
        if (deep>0) {
            hugeData.subData = createTestInstance(deep-1);
        }
        return hugeData;
    }
    
    ArrayList<String> data = new ArrayList<String>();
    
    public TestHugeData() {
        Random rnd = new Random();
        index = rnd.nextInt();
    }
    
    
    int index;
    TestHugeData subData;
    
    
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        final TestHugeData that = (TestHugeData) o;
        
        if (index != that.index) return false;
        
        return true;
    }
    
    public int hashCode() {
        return index;
    }
    
}

