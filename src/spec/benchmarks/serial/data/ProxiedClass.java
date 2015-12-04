/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 *
 * Used by SPEC in the SPECjvm2008 project on permission by the author Clebert Suconi.
 */

package spec.benchmarks.serial.data;

import java.io.Serializable;
import java.lang.reflect.Proxy;

/**
 * $Id: ProxiedClass.java,v 1.2 2005/09/02 21:12:10 csuconic Exp $
 *
 * @author <a href="mailto:tclebert.suconic@jboss.com">Clebert Suconic</a>
 */
public class ProxiedClass implements InterfaceForProxy, Serializable {
    private int value;
    
    public ProxiedClass(int value) {
        this.value=value;
    }
    /** It always return value.
     *  {@link spec.benchmarks.serial.data.TestHandler} will multiply this
     *  by a factor and we will use that value to test
     *  if the value is being used or not.
     */
    public int doSomething() {
        return value;
    }
    
    
    public static InterfaceForProxy createMyProxy(int a, int b) {
        ProxiedClass proxyClass = new ProxiedClass(a);
        
        Class proxyClazz = Proxy.getProxyClass(Thread.currentThread().getContextClassLoader(),new Class[]{InterfaceForProxy.class});
        
        InterfaceForProxy proxy = (InterfaceForProxy) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),new Class[]{InterfaceForProxy.class},new Handler(b,proxyClass));
        return proxy;
    }
    
}

