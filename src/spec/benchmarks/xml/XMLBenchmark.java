/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 */
package spec.benchmarks.xml;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import spec.harness.Context;
import spec.harness.SpecJVMBenchmarkBase;
import spec.harness.results.BenchmarkResult;
import spec.io.FileCache;

public abstract class XMLBenchmark extends SpecJVMBenchmarkBase {
    
    protected DocumentBuilderFactory documentBuilderFactory;
    protected SAXParserFactory SAXfactory;
    protected ReportingHandler callbackReporter = new ReportingHandler();
    protected DocumentBuilder builder;
    protected SAXParser saxParser;
    protected XMLReader xmlParser;
    
    public XMLBenchmark(BenchmarkResult bmResult, int threadId) {
        super(bmResult, threadId);
        SAXfactory = SAXParserFactory.newInstance();
        SAXfactory.setNamespaceAware(true);
        documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        try {
            builder = documentBuilderFactory.newDocumentBuilder();
            builder.setErrorHandler(callbackReporter);
            builder.setEntityResolver(callbackReporter);
            saxParser = SAXfactory.newSAXParser();
            xmlParser = saxParser.getXMLReader();
            xmlParser.setErrorHandler(callbackReporter);
            xmlParser.setEntityResolver(callbackReporter);
        } catch (ParserConfigurationException e) {
            e.printStackTrace(Context.getOut());
        } catch (SAXException e) {
            e.printStackTrace(Context.getOut());
        }
    }
    
    public Source createDomSource(FileCache.CachedFile cachedInput)
    throws SAXException, IOException {
        return new DOMSource(builder.parse(cachedInput.asNewInputSource()));
    }
    
    public Source createSaxSource(FileCache.CachedFile cachedInput) throws IOException {
        return new SAXSource(xmlParser, cachedInput.asNewInputSource());
    }
    
    public static String getFullName(Class cls, String dirName, String name) {
        String suffix = File.separator + name;        
        return dirName == null ? Context.getResourceDirFor(cls) + suffix
                : dirName + suffix;
    }  
   
    public static FileCache.CachedFile getCachedFile(Class cls, String dirName, String name) {
        FileCache.CachedFile result = Context.getFileCache().new CachedFile(getFullName(cls,
                dirName,
                name));
        try {
            result.cache();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    public static void remove(File file) {
        if (!file.isDirectory()) {
            file.delete();
        } else {
            File[] files = file.listFiles();
            for (int i = 0; i < files.length; i ++) {
                remove(files[i]);
            }
            file.delete();
        }
    }
}
