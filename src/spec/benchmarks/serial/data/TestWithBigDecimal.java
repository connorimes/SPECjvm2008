/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */

package spec.benchmarks.serial.data;

import java.math.BigDecimal;
import java.util.Random;
import java.util.Comparator;

/**
 * $Id: TestWithBigDecimal.java,v 1.2 2005/09/30 23:24:15 csuconic Exp $
 *
 * @author <a href="mailto:tclebert.suconic@jboss.com">Clebert Suconic</a>
 */
public class TestWithBigDecimal implements java.io.Serializable, Comparator {
    
    BigDecimal value;
    BigDecimal value2 = null;
    
    
    public static TestWithBigDecimal createTestInstance() {
        TestWithBigDecimal x = new TestWithBigDecimal();
        
        x.value = new BigDecimal(new Random().nextDouble());
        
        return x;
    }
    
    
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        final TestWithBigDecimal that = (TestWithBigDecimal) o;
        
        if (value != null ? !value.equals(that.value) : that.value != null) return false;
        
        return true;
    }
    
    public int hashCode() {
        return (value != null ? value.hashCode() : 0);
    }
    
    public int compare(Object o1, Object o2) {
        TestWithBigDecimal r1 = (TestWithBigDecimal) o1;
        TestWithBigDecimal r2 = (TestWithBigDecimal) o2;
        
        return r1.value.compareTo(r2.value);
    }
    
}

