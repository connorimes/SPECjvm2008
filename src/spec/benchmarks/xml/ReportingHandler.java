/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 */
package spec.benchmarks.xml;

import java.io.IOException;

import org.xml.sax.ext.DefaultHandler2;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ReportingHandler extends DefaultHandler2 {
    public void error(SAXParseException e){
        report(e);
        return;
    }
    
    public void fatalError(SAXParseException e){
        report(e);
        return;
    }
    
    public void warning(SAXParseException e){
        report(e);
        return;
    }
    
    public InputSource resolveEntity(String name,
            String publicId,
            String baseURI,
            String systemId)
            throws SAXException, IOException {
        //System.out.println( "Trying to resolve entity " + name + "|"
        //		+ publicId + "|" + systemId + "|" + baseURI );
        return null;
    }
    
    private void report(SAXParseException e){
        System.out.println( e.getSystemId()
        + "(" + e.getLineNumber() + "," + e.getColumnNumber() + ")"
                + ": " + e.toString());
    }
}
