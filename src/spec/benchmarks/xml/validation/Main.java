/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 */
package spec.benchmarks.xml.validation;

import java.io.File;
import java.io.IOException;

import java.util.Arrays;
import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

import spec.benchmarks.xml.XMLBenchmark;
import spec.harness.Constants;
import spec.harness.Context;
import spec.harness.Launch;
import spec.harness.Util;
import spec.harness.results.BenchmarkResult;
import spec.io.FileCache;

public class Main extends XMLBenchmark {

    private static final int XSD_NUMBER = 6;
    private static FileCache.CachedFile[] allInstanceBytes;
    private static FileCache.CachedFile[] allSchemaBytes;
    private static Validator[][] allValidators;

    public static String testType() {
        return MULTI;
    }
    private static String[] schemaNames = {
        "validation_input.xsd",
        "periodic_table.xsd",
        "play.xsd",
        "structure.xsd",
        "po.xsd",
        "personal.xsd"
    };
    private static String[] instanceNames = {
        "validation_input.xml",
        "periodicxsd.xml",
        "much_adoxsd.xml",
        "structure.xml",
        "po.xml",
        "personal.xml"
    };
    /*
    Loops numbers are inversely proportional to xml file size
    file name:				file size		loops number
    "validation_input.xml"	621232			1
    "periodicxsd.xml"		116618			5
    "much_adoxsd.xml"		202133			3
    "structure.xml"			11958			52
    "po.xml"					960				647
    "personal.xml"			1482			419
     */
    private static int loops[] = {
        1,
        5,
        3,
        52,
        647,
        419
    ,

           };
    
    public static void setupBenchmark() {
        String dirName = Util.getProperty(Constants.XML_VALIDATION_INPUT_DIR_PROP, null);
        try {
            allInstanceBytes = new FileCache.CachedFile[XSD_NUMBER];
            FileCache cache = Context.getFileCache();
            for (int i = 0; i < XSD_NUMBER; i++) {
                String name = getFullName(Main.class, dirName, instanceNames[i]);
                allInstanceBytes[i] = cache.new CachedFile(name);
                allInstanceBytes[i].cache();
            }
            allSchemaBytes = new FileCache.CachedFile[XSD_NUMBER];
            for (int i = 0; i < XSD_NUMBER; i++) {
                String name = getFullName(Main.class, dirName, schemaNames[i]);
                allSchemaBytes[i] = cache.new CachedFile(name);
                allSchemaBytes[i].cache();
            }

            setupValidators(dirName);
        } catch (IOException e) {
            e.printStackTrace(Context.getOut());
        }
    }

    private static void setupValidators(String dirName) {
        int threads = Launch.currentNumberBmThreads;
        allValidators = new Validator[threads][XSD_NUMBER];
        try {
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            sf.setErrorHandler(null);
            for (int i = 0; i < XSD_NUMBER; i++) {
                String xsdFilename = getFullName(Main.class, dirName, schemaNames[i]);
                File tempURI = new File(xsdFilename);
                Schema precompSchema;
                if (tempURI.isAbsolute()) {
                    precompSchema = sf.newSchema(new StreamSource(allSchemaBytes[i].getStream(),
                            tempURI.toURI().toString()));
                } else {
                    precompSchema = sf.newSchema(new StreamSource(allSchemaBytes[i].getStream(), xsdFilename));
                }
                for (int j = 0; j < threads; j++) {
                    allValidators[j][i] = precompSchema.newValidator();
                }
            }
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private Validator[] schemaBoundValidator;

    public Main(BenchmarkResult bmResult, int threadId) {
        super(bmResult, threadId);
        schemaBoundValidator = allValidators[threadId - 1];
    }

    public void harnessMain() {
        try {
            executeWorkload();
        } catch (Exception e) {
            e.printStackTrace(Context.getOut());
        }
    }

    public static void main(String[] args) throws Exception {
        runSimple(Main.class, args);
    }

    private void executeWorkload() throws
            ParserConfigurationException, IOException, SAXException {
        for (int i = 0; i < XSD_NUMBER; i++) {
            Context.getOut().println("Validating " + instanceNames[i]);
            doValidationTests(loops[i], allInstanceBytes[i], schemaBoundValidator[i]);
        }
    }

    private void doValidationTests(int loops, FileCache.CachedFile file,
            Validator schemaValidator) throws
            ParserConfigurationException, IOException, SAXException {
        for (int i = loops - 1; i >= 0; i--) {
            validateSource(i, createDomSource(file), schemaValidator);
            validateSource(i, createSaxSource(file), schemaValidator);
        }
    }

    private void validateSource(int loop, Source source, Validator schemaValidator) {
        schemaValidator.reset();
        schemaValidator.setErrorHandler(null);
        try {
            schemaValidator.validate(source);
            if (loop == 0) {
                Context.getOut().print("\tas " + source.getClass().getName());
                Context.getOut().println(" succeeded. (correct result)");
            }
        } catch (SAXException e) {
            Context.getOut().print("\tas " + source.getClass().getName());
            Context.getOut().println(" failed. (Incorrect result)" + Arrays.toString(loops));
        //    e.printStackTrace(Context.getOut());
        } catch (IOException e) {
            Context.getOut().println("Unable to validate due to IOException.");
        }
    }
}
