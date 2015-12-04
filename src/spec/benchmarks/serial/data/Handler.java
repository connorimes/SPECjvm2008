/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */

package spec.benchmarks.serial.data;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.io.Serializable;

/**
 * $Id: TestHandler.java,v 1.1 2005/08/26 19:47:38 csuconic Exp $
 *
 * @author <a href="mailto:tclebert.suconic@jboss.com">Clebert Suconic</a>
 */
public class Handler implements InvocationHandler, Serializable {
    private int factor;
    Object proxied;
    public Handler(int factor, Object proxied) {
        this.factor=factor;
        this.proxied = proxied;
    }
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object retVal = method.invoke(proxied,args);
        
        Integer newRetVal = Integer.valueOf(((Integer)retVal).intValue() * factor);
        
        return newRetVal;
    }
}

