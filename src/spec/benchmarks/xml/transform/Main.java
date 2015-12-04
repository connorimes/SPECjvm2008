/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 */
package spec.benchmarks.xml.transform;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import org.xml.sax.SAXException;

import spec.benchmarks.xml.XMLBenchmark;
import spec.harness.Constants;
import spec.harness.Context;
import spec.harness.Launch;
import spec.harness.Util;
import spec.harness.results.BenchmarkResult;
import spec.io.FileCache;
import java.util.Properties;


public class Main extends XMLBenchmark {
    private static final int LONG_VALIDATION_MODE = 0;
    private static final int SHORT_VALIDATION_MODE = 1;
    private static final int SINGLE_LOOP_MODE = 0;
    private static final int MULTIPLE_LOOP_MODE = 1;
    private static final int INPUT_PAIR = 10;
    private static final String CONTROL_FILE_NAME = "transformations.properties";
    static String OUT_DIR = "xml_out";
    private static final String[] XML_NAMES = {
        "chess-fo/Kasparov-Karpov.xml",
        "jenitennison/index.xml",
        "jenitennison/text.xml",        
        "nitf/nitf-fishing.xml",
        "shared/REC-xml-19980210.xml",
        "recipes/recipes.xml",
        "dsd/article.xml",
        "renderx/chess/Kasparov-Karpov.xml",
        "renderx/examples/balance/balance_sheet.xml",
        "renderx/examples/meeting/meeting_minutes.xml"
    };
    
    private static final String[] XSL_NAMES = {
        "chess-fo/chess.xsl",
        "jenitennison/page.xsl",
        "jenitennison/markup.xsl",        
        "nitf/nitf-stylized.xsl",
        "spec-html/xmlspec.xsl",
        "recipes/recipes.xsl",
        "dsd/article2html.xsl",
        "renderx/chess/chess.xsl",
        "renderx/examples/balance/balance_sheet.xsl",
        "renderx/examples/meeting/meeting_minutes.xsl",
    };
    /*
     Loops number is inversely proportional to geo_mean(output_size, input_size).
     file name										input 	output 	geo_mean 		loops
     "chess-fo/Kasparov-Karpov.xml"					3003	377586	33673.29443		5
     "jenitennison/index.xml"						5462	14134	8786.347819		18
     "jenitennison/text.xml"						5154	4912	5031.545289		31
     "sp.xsl/index.xml"								3654	5099	4316.450625		37
     "nitf/nitf-fishing.xml"						5955	3582	4618.528987		34
     "shared/REC-xml-19980210.xml"					159519	157265	158387.9905		1
     "recipes/recipes.xml"							17178	14725	15904.27773		10
     "dsd/article.xml"								13401	12831	13112.90323		12
     "renderx/chess/Kasparov-Karpov.xml",			2951	385700	33737.23018		5
     "renderx/examples/balance/balance_sheet.xml",	4994	44433	14896.25463		11
     "renderx/examples/meeting/meeting_minutes.xml"	5908	8063	6901.898579		23
     
     chess-fo/Kasparov-Karpov.xml and renderx/chess/Kasparov-Karpov.xml are from the same application
     area. Because of this they should do 5 loops total instead of 10 loops total.
     Because of this loops number for these files are differs from table above (2, 3) instead of (0, 5).           
     */
    private static final int[] loops = {
        2,
        18,
        31,        
        34,
        1,
        10,
        12,
        3,
        11,
        23
    };
    
    private static FileCache.CachedFile[] xmlInput;
    private static FileCache.CachedFile[] xslInput;
    private static int validationMode = LONG_VALIDATION_MODE;
    private static int loopMode = SINGLE_LOOP_MODE;
    private static Properties longValidationProperties;
    private static Properties[] shortValidationProperties;
    private static String validationFileName;
    private static Transformer[][] allTransformers;
    
    private static void setValidationMode(int mode) {
        validationMode = mode;
    }
    
    private static int getValidationMode() {
        return validationMode;
    }
    
    private static void setLoopMode(int mode) {
        loopMode = mode;
    }
    
    private static int getLoopMode() {
        return loopMode;
    }
    
    public static String testType() {
        return MULTI;
    }
    
    public static void setupBenchmark() {
        String tmpName = Util.getProperty(Constants.XML_TRANSFORM_OUT_DIR_PROP, null);
        OUT_DIR = tmpName != null ? tmpName : OUT_DIR;
        File file = new File(OUT_DIR);
        validationFileName = getFullName(Main.class, null, CONTROL_FILE_NAME);
        xmlInput = new FileCache.CachedFile[INPUT_PAIR];
        xslInput = new FileCache.CachedFile[INPUT_PAIR];
        for (int i = 0; i < INPUT_PAIR; i ++) {
            xmlInput[i] = getCachedFile(Main.class, null, XML_NAMES[i]);
            xslInput[i] = getCachedFile(Main.class, null, XSL_NAMES[i]);
        }
        longValidationProperties = new Properties();
        try {
            if (!file.exists()) {
                file.mkdir();
            }
            longValidationProperties.load(new FileInputStream(validationFileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        setupTransformers();
        
        setValidationMode(LONG_VALIDATION_MODE);
        setLoopMode(SINGLE_LOOP_MODE);
        Main main = new Main(new BenchmarkResult(), 1);
        main.harnessMain();
        int threads = Launch.currentNumberBmThreads;
        shortValidationProperties = new Properties[Launch.currentNumberBmThreads];
        Properties outProperties = main.getOutProperties();
        for (int i = 0; i < threads; i ++) {
            shortValidationProperties[i] = (Properties)outProperties.clone();
        }
        setValidationMode(SHORT_VALIDATION_MODE);
        setLoopMode(MULTIPLE_LOOP_MODE);
    }
    
    public static void tearDownBenchmark() {
        if (!ExtOutputStream.wasFullVerificationError
                && !Util.getBoolProperty(Constants.XML_TRANSFORM_LEAVE_OUT_DIR_PROP, null)) {
            remove(new File(OUT_DIR));
        }
    }
    
    
    private static void setupTransformers() {
        allTransformers = new Transformer[Launch.currentNumberBmThreads][INPUT_PAIR];
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            for (int i = 0; i < INPUT_PAIR; i ++) {
                Templates precompiledTemplates =
                        transformerFactory.newTemplates(xslInput[i].asNewStreamSource());
                for (int j = 0; j < Launch.currentNumberBmThreads; j ++) {
                    allTransformers[j][i] = precompiledTemplates.newTransformer();
                }
            }
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private Transformer[] precompiledTransformers;
    private BaseOutputStream outputStream;
    private StreamResult streamResult;
    
    public Main(BenchmarkResult bmResult, int threadId) {
        super(bmResult, threadId);
        if (getValidationMode() == LONG_VALIDATION_MODE) {
            setOutputStream(new ExtOutputStream());
            setValidationProperties(longValidationProperties);
        } else {
            setOutputStream(new BaseOutputStream());
            setValidationProperties(shortValidationProperties[threadId - 1]);
        }
        
        precompiledTransformers = allTransformers[threadId - 1];
    }
    
    public void harnessMain() {
        try {
            for (int i = 0; i < 3; i++) {
                executeWorkload();
            }
        } catch (Exception e) {
            e.printStackTrace(Context.getOut());
        }
    }
    
    public void setOutputStream(BaseOutputStream stream) {
        outputStream = stream;
        streamResult = new StreamResult(outputStream);
    }
    
    public static void main(String[] args) throws Exception {
        runSimple( Main.class, args );
    }
    
    private void setValidationProperties(Properties props) {
        outputStream.setValidationProperties(props);
    }
    
    private Properties getOutProperties() {
        if (outputStream instanceof ExtOutputStream) {
            return ((ExtOutputStream)outputStream).getOutProperties();
        }
        return null;
    }
    
    private void executeWorkload() throws
            TransformerException, ParserConfigurationException, SAXException, IOException {
        for (int i = 0; i < INPUT_PAIR; i ++) {
            String propertyNamePrefix = XML_NAMES[i] + ".";
            int loops = (getLoopMode() == SINGLE_LOOP_MODE) ? 1 : Main.loops[i];
            Transformer transformer = precompiledTransformers[i];
            for (int j = loops - 1; j >= 0; j--) {
                transform(transformer, createSaxSource(xmlInput[i]), propertyNamePrefix + "SAX", j);
                transform(transformer, createDomSource(xmlInput[i]), propertyNamePrefix + "DOM", j);
                transform(transformer, xmlInput[i].asNewStreamSource(), propertyNamePrefix + "Stream", j);
            }
        }
    }
    
    private void transform(Transformer transformer, Source source, String descr, int loop) throws
            TransformerException, ParserConfigurationException, SAXException, IOException {
        transformer.reset();
        outputStream.setCurrentProp(descr);
        transformer.transform(source, streamResult);
        outputStream.checkResult(loop);
    }
}
