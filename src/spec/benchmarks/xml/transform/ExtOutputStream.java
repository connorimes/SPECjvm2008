package spec.benchmarks.xml.transform;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import org.w3c.tidy.Configuration;
import org.w3c.tidy.Tidy;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.canonical.Canonicalizer;
import spec.benchmarks.xml.XMLBenchmark;
import spec.harness.Context;
import spec.io.FileCache;

class ExtOutputStream extends BaseOutputStream {
    private Properties outProperties;
    private FileOutputStream writer;
    private FileOutputStream diffOutputStream;
    private String currentFileName;
    private String canonicalizedFileName;
    private String tidyFileName;
    private String diffFileName;
    public static boolean wasFullVerificationError = false;
    
    private static byte[] getFileArray(String name) {
        FileCache cache = Context.getFileCache();
        FileCache.CachedFile file = cache.new CachedFile(name);
        try {
            file.cache();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] result = cache.getByteArray(name);
        file.discard();
        return result;
    }
    
    public static boolean isDosNewLine(byte[] b, int index) {
        return b[index] == 10;
    }
    
    public static boolean isUnixNewLine(byte[] b, int index) {
        int incIndex = index + 1;
        return b[index] == 13 && incIndex != b.length && b[incIndex] == 10;
    }
    
    public boolean checkValidity() {
        byte[] b0 = getFileArray(canonicalizedFileName);
        byte[] b1 = getFileArray(XMLBenchmark.getFullName(Main.class, null, propertyName + ".canon"));
        HashMap<Integer, byte[]> hashMap = new HashMap<Integer, byte[]>();
        hashMap.put(Integer.valueOf(0), b0);
        hashMap.put(Integer.valueOf(1), b1);
        int lineCounter = 0;
        int[] lineStart = {0, 0};
        boolean[] fileEnd = {false, false};
        boolean wasErrors = false;
        do {
            lineCounter ++;
            int[] index = {lineStart[0], lineStart[1]};
            boolean[] lineEnd = {false, false};
            boolean wasError = false;
            int[] errorStart = {lineStart[0], lineStart[1]};
            int[] errorEnd = {lineStart[0], lineStart[1]};
            String[] diffs = {"", ""};
            int[] errorEndCorrection = {0, 0};
            do {
                for (int i = 0; i < 2; i ++) {
                    byte[] b = hashMap.get(Integer.valueOf(i));
                    fileEnd[i] = index[i] == b.length;
                    lineEnd[i] = fileEnd[i];
                    if (!fileEnd[i]) {
                        if (isDosNewLine(b, index[i])) {
                            lineEnd[i] = true;
                            errorEndCorrection[i] = -1;
                        } else if (isUnixNewLine(b, index[i])) {
                            lineEnd[i] = true;
                            index[i] ++;
                            errorEndCorrection[i] = -2;
                        }
                    }
                }
                
                if (!wasError && ((lineEnd[0] != lineEnd[1])
                || (!fileEnd[0] && !fileEnd[1] && b0[index[0]] != b1[index[1]]))) {
                    wasError = true;
                    errorStart[0] = Math.max(index[0] - 5, lineStart[0]);
                    errorStart[1] = Math.max(index[1] - 5, lineStart[1]);
                }
                
                for (int i = 0; i < 2; i ++) {
                    if (!lineEnd[i]) {
                        index[i] ++;
                    }
                }
            } while (!(lineEnd[0] && lineEnd[1]));
            
            if (wasError) {
                for (int i = 0; i < 2; i ++) {
                    byte[] b = hashMap.get(Integer.valueOf(i));
                    errorEnd[i] = Math.max(Math.min(index[i], b.length) + errorEndCorrection[i], errorStart[i]);
                    diffs[i] = filter(new String(b, errorStart[i], errorEnd[i] - errorStart[i]));
                }
                wasError = !diffs[0].equals(diffs[1]);
            }
            
            for (int i = 0; i < 2; i ++) {
                int ind = (i + 1) % 2;
                byte[] b = hashMap.get(Integer.valueOf(ind));
                if (fileEnd[i] && !fileEnd[ind]) {
                    wasError = true;
                    lineCounter ++;
                    errorEnd[ind] = b.length;
                    errorStart[ind] = wasError ? errorStart[ind] : index[ind];
                    diffs[ind] = filter(((wasError) ? diffs[ind] : "")
                    + new String(b, index[ind], b.length - index[ind]));
                }
                lineStart[i] = index[i] + 1;
            }
            if (wasError) {
                writeDiffToFile(lineCounter, errorStart[1], diffs);
            }
            
            wasErrors = wasErrors || wasError;
        } while (!fileEnd[0] && !fileEnd[1]);
        return !wasErrors;
    }
    
    private void writeDiffToFile(int line, int column, String[] diffs) {
        try {
            if (diffOutputStream == null) {
                diffOutputStream = new FileOutputStream(diffFileName);
            }
            String message = "line=" + line + ",column=" + column + "\n"
                    + "\texpected:" + diffs[1] + "\n"
                    + "\treceived:" + diffs[0];
            diffOutputStream.write(message.getBytes());
            
        } catch (IOException e) {
            System.out.println("IOException at diff generation...\n");
            System.out.println(getSendResultsMessage());
            e.printStackTrace();
        }
    }
    
    
    public void canonicalize(String inName, String outName) {
        try {
            FileOutputStream outStream = new FileOutputStream(outName);
            FileInputStream inputStream = new FileInputStream(inName);
            Document doc = new Builder().build(inputStream);
            new Canonicalizer(outStream).write(doc);
            inputStream.close();
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            wasFullVerificationError = true;
            System.out.println("xom canonicalization of " + getCurrentProp() + " crashed.\n");
            System.out.println(getSendResultsMessage());
        }
    }
    
    private void runTidy() {
        try {
            FileInputStream inputStream = new FileInputStream(currentFileName);
            FileOutputStream outStream = new FileOutputStream(tidyFileName);
            Tidy tidy = new Tidy();
            tidy.setQuiet(true);
            tidy.setShowWarnings(false);
            tidy.setDocType("omit");
            tidy.setXmlOut(true);
            tidy.setCharEncoding(Configuration.UTF8);
            tidy.setQuoteNbsp(false);
            tidy.parse(inputStream, outStream);
            inputStream.close();
            outStream.close();
        } catch (IOException e) {
            wasFullVerificationError = true;
            System.out.println("tidy handling of " + getCurrentProp() + " crashed.\n");
            System.out.println(getSendResultsMessage());
        }
    }
    
    
    public void check(String propsValue) {
        if ("xml".equals(propsValue)) {
            canonicalize(currentFileName, canonicalizedFileName);
        } else if ("html".equals(propsValue)) {
            runTidy();
            canonicalize(tidyFileName, canonicalizedFileName);
        }
        try {
            if (!checkValidity()) {
                handleErrorValidation(0);
                wasFullVerificationError = true;
            }
        } catch (Exception e) {
            wasFullVerificationError = true;
            System.out.println("verification of " + getCurrentProp() + " crashed.\n");
            System.out.println(getSendResultsMessage());
        }
    }
    
    public ExtOutputStream() {
        outProperties = new Properties();
    }
    
    public String filter(String s) {                
        return s.replaceAll("html", "HTML").replaceAll("utf", "UTF");
    }
    
    
    public void setCurrentProp(String propertyName) {
        super.setCurrentProp(propertyName);
        try {
            currentFileName = Main.OUT_DIR + "/" + propertyName.replaceAll("/", ".") + ".out";
            canonicalizedFileName = currentFileName + ".canonicalized";
            tidyFileName = currentFileName + ".tidy.xml";
            diffFileName = currentFileName + ".diff";
            writer = new FileOutputStream(currentFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void write(int b) throws IOException {
        super.write(b);
        writer.write((byte)b);
    }
    
    public void write(byte[] b) throws IOException {
        for (int i = 0; i < b.length; i ++) {
            write((byte)b[i]);
        }
    }
    
    
    public void write(byte[] b, int offset, int len) throws IOException {
        for (int i = offset; i < offset + len; i ++) {
            write((byte)b[i]);
        }
    }
    
    public void reset() {
        super.reset();
        try {
            if (diffOutputStream != null) {
                diffOutputStream.flush();
                diffOutputStream.close();
                diffOutputStream = null;
            }
            if (writer != null) {
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void checkResult(int loopNumber) {
        try {
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        check(validiationProperties.getProperty(propertyName));
        outProperties.put(propertyName, "" + getCRC());
        reset();
    }
    
    public void handleSuccessfullValidation() {
        outProperties.put(propertyName, "" + getCRC());
    }
    
    public void handleErrorValidation(int loopNumber) {
        System.out.println("Result of " + propertyName + " transformation differs from canonical one.\n" +
                getSendResultsMessage());
    }
    
    public Properties getOutProperties() {
        return outProperties;
    }
    
    private String getSendResultsMessage() {
        return "(Please send " + currentFileName + " or " + diffFileName +
                " along with the reporter's result.)";
    }
}
