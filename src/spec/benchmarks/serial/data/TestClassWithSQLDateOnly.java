/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */

package spec.benchmarks.serial.data;

import java.io.Serializable;

/**
 * $Id: TestClassWithSQLDateOnly.java,v 1.1 2005/07/26 01:30:56 csuconic Exp $
 * @author Clebert Suconic
 */
public class TestClassWithSQLDateOnly implements Serializable {
    public TestClassWithSQLDateOnly() {
    }
    
    public java.sql.Date newDate = new java.sql.Date(System.currentTimeMillis());
    
    public String toString() {
        return "ClassWithSQLDateOnl:" + newDate.toString();
    }
    
    public static TestClassWithSQLDateOnly createTestInstance() {
        TestClassWithSQLDateOnly testObject = new TestClassWithSQLDateOnly();
        return testObject;
    }
    
    public boolean equals(Object obj) {
        return newDate.equals(((TestClassWithSQLDateOnly)obj).newDate);
    }
    
}

