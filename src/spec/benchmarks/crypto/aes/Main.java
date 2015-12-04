/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 */

package spec.benchmarks.crypto.aes;

import java.security.AlgorithmParameters;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import spec.benchmarks.crypto.Util;
import spec.harness.Context;
import spec.harness.SpecJVMBenchmarkBase;
import spec.harness.StopBenchmarkException;
import spec.harness.results.BenchmarkResult;

public class Main extends SpecJVMBenchmarkBase {
    
    public final static boolean DEBUG = false;
    
    final static int aesKeySize = 128;
    final static int desKeySize = 168;
    final static int level = 12;
    
    static SecretKey aesKey = null;
    static SecretKey desKey = null;
    
    static KeyGenerator aesKeyGen = null;
    static KeyGenerator desKeyGen = null;
    
    AlgorithmParameters algorithmParameters;
    
    public Main(BenchmarkResult bmResult, int threadId) {
        super(bmResult, threadId);
        algorithmParameters = null;
    }
    
    /** Run this in multi mode, next to each other. */
    public static String testType() {
        return MULTI;
    }
    
    private void printMe(String name, byte [] arr) {
        System.out.print("  " + name + ":");
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i]);
        }
        System.out.println();
    }
    
    /**
     * Will encrypt the indata level number of times.
     * @param indata Data to encrypt.
     * @param key Key to use for encryption.
     * @param algorithm Algorithm/Standard to use.
     * @param level Number of times to encrypt.
     * @return The encrypted version of indata.
     */
    private byte[] encrypt(byte [] indata, SecretKey key, String algorithm, int level) {
        
        if (DEBUG) printMe("indata", indata);
        
        byte[] result = indata;
        
        try {
            Cipher c = Cipher.getInstance(algorithm);
            c.init(Cipher.ENCRYPT_MODE, key);
            algorithmParameters = c.getParameters();
            
            for (int i = 0; i < level; i++) {
                byte[] r1 = c.update(result);
                byte[] r2 = c.doFinal();
                
                if (DEBUG) printMe("[" + i + "] r1", r1);
                if (DEBUG) printMe("[" + i + "] r2", r2);
                
                result = new byte[r1.length + r2.length];
                System.arraycopy(r1, 0, result, 0, r1.length);
                System.arraycopy(r2, 0, result, r1.length, r2.length);
            }
        } catch (Exception e) {
            throw new StopBenchmarkException("Exception in encrypt for " + algorithm + ".", e);
        }
        
        if (DEBUG) printMe("result", result);
        return result;
    }
    
    /**
     * Will decrypt the indata level number of times.
     * @param indata Data to decrypt.
     * @param key Key to use for encryption.
     * @param algorithm
     * @param level
     * @return
     */
    private byte[] decrypt(byte[] indata, SecretKey key, String algorithm, int level) {
        
        if (DEBUG) printMe("indata", indata);
        
        byte[] result = indata;
        
        try {
            Cipher c = Cipher.getInstance(algorithm);
            c.init(Cipher.DECRYPT_MODE, key, algorithmParameters);
            
            for (int i = 0; i < level; i++) {
                byte[] r1 = c.update(result);
                byte[] r2 = c.doFinal();
                if (DEBUG) printMe("[" + i + "] r1", r1);
                if (DEBUG) printMe("[" + i + "] r2", r2);
                
                result = new byte[r1.length + r2.length];
                System.arraycopy(r1, 0, result, 0, r1.length);
                System.arraycopy(r2, 0, result, r1.length, r2.length);
            }
            
        } catch (Exception e) {
            throw new StopBenchmarkException("Exception in encrypt for " + algorithm + ".", e);
        }
        
        if (DEBUG) printMe("result", result);
        return result;
    }
    
    public void runEncryptDecrypt(SecretKey key, String algorithm, String inputFile) {
        byte [] indata = Util.getTestData(inputFile);
        byte [] cipher = encrypt(indata, key, algorithm, level);
        byte [] plain = decrypt(cipher, key, algorithm, level);
        boolean match = Util.check(indata, plain);
        Context.getOut().println(algorithm + ":" + " plaincheck="
                + Util.checkSum(plain) + (match ? " PASS" : " FAIL"));
    }
    
    public void harnessMain() {
        runEncryptDecrypt(Main.aesKey, "AES/CBC/NoPadding", Util.TEST_DATA_1);
        runEncryptDecrypt(Main.aesKey, "AES/CBC/PKCS5Padding", Util.TEST_DATA_1);
        runEncryptDecrypt(Main.desKey, "DESede/CBC/NoPadding", Util.TEST_DATA_1);
        runEncryptDecrypt(Main.desKey, "DESede/CBC/PKCS5Padding", Util.TEST_DATA_1);
        runEncryptDecrypt(Main.aesKey, "AES/CBC/NoPadding", Util.TEST_DATA_2);
        runEncryptDecrypt(Main.aesKey, "AES/CBC/PKCS5Padding", Util.TEST_DATA_2);
        runEncryptDecrypt(Main.desKey, "DESede/CBC/NoPadding", Util.TEST_DATA_2);
        runEncryptDecrypt(Main.desKey, "DESede/CBC/PKCS5Padding", Util.TEST_DATA_2);
    }
    
    public static void setupBenchmark() {
        try {
            byte [] seed =  {0x4, 0x7, 0x1, 0x1};
            SecureRandom random = new SecureRandom(seed);
            Context.getFileCache().loadFile(Util.TEST_DATA_1);
            Context.getFileCache().loadFile(Util.TEST_DATA_2);
            aesKeyGen = KeyGenerator.getInstance("AES");
            aesKeyGen.init(aesKeySize, random);
            desKeyGen = KeyGenerator.getInstance("DESede");
            desKeyGen.init(desKeySize, random);
            aesKey = aesKeyGen.generateKey();
            desKey = desKeyGen.generateKey();
        } catch (Exception e) {
            throw new StopBenchmarkException("Error in setup of crypto.aes." + e);
        }
    }
    
    public static void main(String[] args) throws Exception {
        //setupBenchmark();
        //Main m = new Main(new BenchmarkResult(), 0);
        //m.harnessMain();
        runSimple(Main.class, args);
    }
}
