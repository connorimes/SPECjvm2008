package spec.benchmarks.crypto;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;

import spec.harness.Context;
import spec.harness.StopBenchmarkException;

public class Util {
    
    private final static boolean DEBUG = false;
    
    public final static String TEST_DATA_BASE =
            Context.getSpecBasePath()
            + File.separator
            + "resources"
            + File.separator
            + "crypto"
            + File.separator;
    
    public final static String TEST_DATA_1 =
            TEST_DATA_BASE + "fredmans21.txt";
    
    public final static String TEST_DATA_2 =
            TEST_DATA_BASE + "track3.mp3";
    
    public final static String TEST_DATA_3 =
            TEST_DATA_BASE + "random96.dat";
    
    public final static String TEST_DATA_4 =
            TEST_DATA_BASE + "random1024.dat";
    
    public final static String TEST_DATA_5 =
            TEST_DATA_BASE + "random65536.dat";
    
    public final static String TEST_DATA_6 =
            TEST_DATA_BASE + "random1048576.dat";
    
    /**
     * Will collect the test data from FileCache.
     * @param name The file to collect.
     * @return An byte buffer with the test data.
     */
    public final static byte [] getTestData(String name) {
        
        try {
            int length = Context.getFileCache().getLength(name);
            length -= length % 16; // align length to avoid padding indata...
            byte[] testData = new byte[length];
            ByteArrayInputStream bais = Context.getFileCache().getStream(name);
            
            int counter = 0;
            int bytes_read = 0;
            do {
                bytes_read = bais.read(testData, counter, 16);
                counter += bytes_read;
            } while (counter + 16 <= length);
            
            return testData;
        } catch (IOException ioe) {
            throw new StopBenchmarkException("Error getting input stream for file " + name, ioe);
        }
    }
    
    public static void createRandomTestData(String fileName, int length) throws IOException {
        SecureRandom random = new SecureRandom();
        byte [] testData = new byte[length];
        random.nextBytes(testData);
        
        FileOutputStream fos = new FileOutputStream(new File(fileName));
        fos.write(testData);
        fos.close();
    }
    
    public final static int checkSum(byte [] arr) {
        byte sum = 0;
        byte xor = 0;
        for (int i = 0; i < arr.length; i++) {
            sum += arr[i];
            xor |= arr[i];
        }
        return (xor << 8) + sum;
    }
    
    public final static boolean check(byte [] ba1, byte [] ba2) {
        
        if (ba1 == null || ba2 == null) {
            if (DEBUG) System.out.println("Fail null.");
            return false;
        }
        
        if (ba1.length != ba2.length) {
            if (DEBUG) System.out.println("Fail length.");
            return false;
        }
        
        for (int i = 0; i < ba1.length; i++) {
            if (ba1[i] != ba2[i]) {
                if (DEBUG) System.out.println("Fail ba1[" + i + "]=" + ba1[i] + " ba2[" + i + "]=" + ba2[i]);
                return false;
            }
        }
        return java.util.Arrays.equals(ba1, ba2);
    }
}
