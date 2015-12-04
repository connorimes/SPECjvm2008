/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */

package spec.benchmarks.serial.data;


import java.util.Random;
import java.io.Serializable;

/**
 * $Id: TestProxy.java,v 1.2 2005/09/07 19:50:58 csuconic Exp $
 *
 * @author <a href="mailto:tclebert.suconic@jboss.com">Clebert Suconic</a>
 */
public class TestProxy implements Serializable {
    
    int a;
    int b;
    InterfaceForProxy proxy;
    
    public TestProxy() {
        a = new Random().nextInt();
        b = new Random().nextInt();
        proxy = ProxiedClass.createMyProxy(a,b);
    }
    
    
    public boolean equals(Object obj) {
        return proxy.doSomething()==((TestProxy)obj).proxy.doSomething();
    }
    
    public static TestProxy createTestInstance() {
        return new TestProxy();
    }
    
    
    public InterfaceForProxy getProxy() {
        return proxy;
    }
    
    
}

