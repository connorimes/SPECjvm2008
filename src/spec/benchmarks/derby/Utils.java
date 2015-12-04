/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 */
package spec.benchmarks.derby;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashMap;

import spec.harness.Constants;
import spec.harness.Context;
import spec.harness.Util;

public class Utils {
    
    static final String specsFileName = Context.getSpecBasePath() + "/resources/derby/specs.bin";
    static final String callsFileName = Context.getSpecBasePath() + "/resources/derby/calls.bin";
    static final String URL = "jdbc:derby:derby_dir/name";
    static BigDecimal[] BASETAXES;   // base tax rate
    static BigDecimal[] DISTAXES;   // distance tax rate
    
    static HashMap BASERATES;
    static HashMap DISRATES;
    
    static final int INIT_THREADS_NUMBED;
    static final int INIT_ARRAYS_SIZE;
    static final boolean MULTI_THREAD_RESTORING;
    
    static {
    	String threadsProps = System.getProperty("derby.init.threads.number");
    	INIT_THREADS_NUMBED = threadsProps == null ? 1 : Integer.parseInt(threadsProps);   	
    	
    	String arraySize = System.getProperty("derby.init.array.size");
    	INIT_ARRAYS_SIZE = arraySize == null ? 50 : Integer.parseInt(arraySize);
    	
    	String useThreads = System.getProperty("derby.use.threads.number");
    	MULTI_THREAD_RESTORING = useThreads == null ? false : Boolean.parseBoolean(useThreads);    	
    }
    
    static final String BACKUP_BASE_DIR = "derby_dir/backup";
    static final String BACKUP_DIR = BACKUP_BASE_DIR + "/name1";	
    
    public static final boolean DEBUG = Util.getBoolProperty(Constants.DEBUG_DERBY, null);
    
    public static String getCreateDurationsTableQuery(String name, int scale) {
        StringBuilder result = new StringBuilder();
        result.append("CREATE TABLE " + name + " ("
                + "ID INTEGER NOT NULL PRIMARY KEY,"
                + "AID INTEGER,");
        for (int i = 0; i < scale; i ++) {
            result.append("DURATION" + (i + 1) + " VARCHAR(17),");
        }
        
        result.append("SPEC CHAR(" + scale * Utils.INFO_LENGTH + ") FOR BIT DATA)");
        return result.toString();
    }
    
    public static String getInsertIntoDurationQuery(int scale, int tableNumber) {
        StringBuilder result = new StringBuilder();
        result.append("INSERT INTO DURATIONS" + tableNumber+ "(ID, AID, ");
        for (int i = 0; i < scale; i ++) {
            result.append("DURATION" + (i + 1) + ", ");
        }
        result.append("SPEC) VALUES (?, ?, ?");
        for (int i = 0; i < scale; i ++) {
            result.append(", ?");
        }
        result.append(")");
        return result.toString();
    }
    
    public static final void initRates() {
        BigDecimal BASERATE = new BigDecimal("0.001312513");
        BigDecimal DISRATE = new BigDecimal("0.008941317");        
        BigDecimal DISTAX = BigDecimal.ONE.divide(new BigDecimal("0.0341"), MathContext.DECIMAL64);
        BASERATES = createRatesMap(300, 10, 15, BASERATE);
        DISRATES = createRatesMap(300, 10, 15, DISRATE);
        BASETAXES = init(BigDecimal.ONE.divide(new BigDecimal("0.0675"), MathContext.DECIMAL64));
        DISTAXES = init(DISTAX);
        
    }
    
    public static BigDecimal[][][] keys ;
    
    public static HashMap createRatesMap(int dim1, int dim2,
            int dim3, BigDecimal value1) {
        HashMap<BigDecimal, BigDecimal> map = new HashMap<BigDecimal, BigDecimal>();
        keys = new BigDecimal[dim1][dim2][dim3];
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMinimumIntegerDigits(3);
        for (int i = 0; i < dim1; i ++){
            for (int j = 0; j < dim2; j ++) {
                for (int k = 0; k < dim3; k ++) {
                    long res = i * 10000 + j * 100 + k;
                    int scale = 3 + ((i + j + k) % 5);
                    BigDecimal key = new BigDecimal(res + "." + res);
                    keys[i][j][k] = key;
                    map.put(key, value1.setScale(scale, BigDecimal.ROUND_DOWN));
                }
            }
        }
        return map;
    }
    
    public static BigDecimal[][][] initMulti(BigDecimal value) {
        BigDecimal[][][] result = new BigDecimal[300][10][15];
        for (int i = 0; i < 300; i ++) {
            for (int j = 0; j < 10; j ++) {
                for (int k = 0; k < 15; k ++) {
                    result[i][j][k] = value;
                }
            }
        }
        return result;
    }
    
    public static BigDecimal[] init(BigDecimal value) {
        BigDecimal[] result = new BigDecimal[400];
        Arrays.fill(result, 0, 400, value);
        return result;
    }
    
    //SQL statements
    public static final String UPDATE_ACCOUNTS_TABLE = "UPDATE accounts SET sumB = ?, sumD = ?,"
            + "sumT = ? WHERE Aid = " + "?";
    public static final String CREATE_ACCOUNTS_TABLE =
            "CREATE TABLE ACCOUNTS (Aid        INTEGER NOT NULL PRIMARY KEY, "
            + "SUMB        NUMERIC, "
            + "SUMD        NUMERIC, "
            + "SUMT        NUMERIC)";
    
    public static final String getPreparedSelectQuery(int tableNumber) {
        return "SELECT  *  FROM  DURATIONS" + tableNumber + " WHERE AID = ?";
    }
    
    public static final String getSelectQuery(int tableNumber, int accountNumber) {
        return "SELECT *  FROM  durations" + tableNumber + " WHERE Aid = " + accountNumber;
    }
    
    public static final String getUpdateAccountsQuery(BigDecimal[] result, int id) {
        return "UPDATE ACCOUNTS SET SUMB = " + result[0] + ","
                + "SUMD = " + result[1] + ","
                + "SUMT = " + result[2] + " "
                + "WHERE Aid = " + id;
    }
    
    public static BigDecimal[] initResultsArray(BigDecimal[] array) {
        BigDecimal[] result = array == null ? new BigDecimal[3] : array;
        for (int i = 0; i < 3; i ++) {
            result[i] = BigDecimal.ZERO;
        }
        return result;
    }
    
    public static final BigDecimal[] getArray() {
        return new BigDecimal[]{new BigDecimal("0"),
        new BigDecimal("0"),
        new BigDecimal("0")};
    }
    
    public static BigDecimal[] add(BigDecimal[] item1, BigDecimal[] item2) {
        for (int i = 0; i < item1.length; i ++) {
            item1[i] = item1[i].add(item2[i]);
        }
        return item1;
    }
    
    public static void print(String s) {
        if (DEBUG) {
            System.out.println(s);
        }
    }
    
    public static final int BYTES_IN_LONG = 8;
    public static final int CALL_SPEC = 32;
    public static final int INFO_LENGTH = 24;
    
    public static long bytesToLong(byte[] inbytes, int shift) {
        long result = 0;
        for (int i = 0 + shift; i < 8 + shift; i++) {
            result = (result << 8) + (inbytes[i] & 0xff); // [unsigned byte]
        }
        
        return result;
    }
    
    public static int[] bytesToInts(byte[] bytes, int shift, int[] spec) {
        for(int i = 0; i < 4; i ++) {
            spec[i] = 0;
            int index = 4 * i + shift;
            for (int j = 0; j < 4; j ++) {
                spec[i] = (spec[i] << 8) + (bytes[index + j] & 0xff);
            }
        }
        return spec;
    }
    
    public static void  releaseResources() {
        BASERATES = null;
        BASETAXES = null;
        DISRATES = null;
        DISTAXES = null;
        keys = null;
    }
}
