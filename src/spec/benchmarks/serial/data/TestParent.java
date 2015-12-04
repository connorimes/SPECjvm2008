/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */

package spec.benchmarks.serial.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Random;


/**
 * $Id: TestParent.java,v 1.4 2005/10/24 22:10:46 csuconic Exp $
 * @author Clebert Suconic
 */
public class TestParent implements Serializable {
    public TestParent(int x) {
        Random random = new Random();
        this.b=random.nextDouble()*1000;
        for (int i=0;i<6;i++) {
            value[i] = "value" + i;
        }
        bval = new Random().nextBoolean();
    }
    
    public TestParent(int a, double b, String nullValue, Child child,
            String[] value, HashMap map, Long longObjectValue, boolean bval,
            byte byteValue, short shortValue, int intValue,
            Integer intValueClass, long longValue,
            Long longValueClass,
            float floatValue,
            double doubleValue,
            char charValue,
            java.util.Date date,
            java.sql.Date sqlDate) {
        /*this.a=0;
        private double b=0;
         
        String nullValue;
         
        private Child child;
         
        public String[] value = new String[6];
         
        public HashMap map = new HashMap();
         
        Long longObjectValue = new Long(10);
        boolean bval;
        byte byteValue=33;
        short shortValue=34;
        int intValue = 35;
        Integer intValueClass= new Integer(55);
        long longValue = 56;
        Long longValueClass = new Long(57);
        float floatValue = 35.34f;
        double doubleValue = 37;
        char charValue = 'a';
        java.util.Date date = new java.util.Date();
        java.sql.Date sqldate = new java.sql.Date(System.currentTimeMillis()); */
    }
    
    
    
    
    private int a=0;
    private double b=0;
    
    String nullValue;
    
    private Child child;
    
    public String[] value = new String[6];
    
    public HashMap<String, String> map = new HashMap<String, String>();
    
    Long longObjectValue = Long.valueOf(10);
    boolean bval;
    byte byteValue=33;
    short shortValue=34;
    int intValue = 35;
    Integer intValueClass= Integer.valueOf(55);
    long longValue = 56;
    Long longValueClass = Long.valueOf(57);
    float floatValue = 35.34f;
    double doubleValue = 37;
    char charValue = 'a';
    java.util.Date date = new java.util.Date();
    java.sql.Date sqldate = new java.sql.Date(System.currentTimeMillis());
    
    public boolean equals(Object obj) {
        TestParent toCompare = (TestParent)obj;
        
        boolean result = true;
        
        for (int i=0;i<value.length;i++) {
            result = value[i].equals(toCompare.value[i]);
            if (!result) break;
        }
        
        if (!result) {
            return false;
        }
        
        if (map.size()!=toCompare.map.size()) return false;
        
        if (b!=toCompare.b) return false;
        
        
        if (byteValue!=toCompare.byteValue) return false;
        if (shortValue!=toCompare.shortValue) return false;
        if (intValue!=toCompare.intValue) return false;
        if (!intValueClass.equals(toCompare.intValueClass)) return false;
        if (longValue!=toCompare.longValue) return false;
        if (!longValueClass.equals(toCompare.longValueClass)) return false;
        if (floatValue!=toCompare.floatValue) return false;
        if (doubleValue!=toCompare.doubleValue) return false;
        if (charValue!=toCompare.charValue) return false;
        if (!(date.equals(toCompare.date))) return false;
        if (!(sqldate.equals(toCompare.sqldate))) return false;
        if (bval!=toCompare.bval) return false;
        
        return a==toCompare.a;
        
    }
    
    
    /**
     * @return Returns the child.
     */
    public Child getChild() {
        return child;
    }
    /**
     * @param child The child to set.
     */
    public void setChild(Child child) {
        this.child = child;
    }
    /**
     * @return Returns the a.
     */
    public int getA() {
        return a;
    }
    /**
     * @param a The a to set.
     */
    public void setA(int a) {
        this.a = a;
    }
    
    public static TestParent createTestInstance() {
        TestParent myTest = new TestParent(0);
        myTest.setA(33);
        
        for (int i=0;i<100;i++) {
            myTest.map.put("key" + i,"value" + i);
            
        }
        
        Child child = new Child();
        myTest.setChild(child);
        child.setParent(myTest);
        return myTest;
    }
    
    public String toString() {
        
        String value = "byteValue=" + byteValue +
                "\nshortValue=" + shortValue +
                "\nintValue=" + intValue+
                "\nintValueClass=" + intValueClass+
                "\nlongValue=" + longValue+
                "\nlongValueClass=" + longValueClass+
                "\nfloatValue=" + floatValue+
                "\ndoubleValue=" + doubleValue+
                "\ncharValue=" + charValue +
                "\ndate=" + date +
                "\nsqldate=" + sqldate;
        
        return "TestParent " + value + "Parent UniqueID=" + System.identityHashCode(this) + "NullValue=" + this.nullValue +" and a=" + a + " b=" + b + " and " + child + " uniqueIdForArray=" + System.identityHashCode(value) + " value=" + value + " mapSize=" + map.size() + " identity=" + System.identityHashCode(map) + ")";
    }
}

