/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 */
package spec.benchmarks.derby;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class CyclicReader  {
    private FileInputStream fis;
    private BufferedInputStream bis;
    private String fileName;
    private BufferedReader bReader;
    private File file;
    private FileReader reader;
    boolean binary;
    
    CyclicReader(String fileName, boolean binary) {
        this.fileName = fileName;
        this.binary = binary;
        init();
    }
    
    private void init() {
        try {
            if (binary) {
                fis = new FileInputStream(fileName);
                bis = new BufferedInputStream(fis);
            } else {
                file = new File(fileName);
                reader = new FileReader(file);
                bReader = new BufferedReader(reader);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    void close() {
        try {
            if (binary) {
                fis.close();
                bis.close();
            } else {
                reader.close();
                bReader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    int read(byte[] bytes, int length)
    throws IOException {
        if (binary) {
            int result = 0;
            result = bis.read(bytes, 0, length);
            if (result < 0) {
                close();
                init();
                result = bis.read(bytes, 0, length);
            }
            return result;
        }
        return -1;
    }
    
    int read(String[] values) throws IOException{
        if (!binary) {            
            for (int i = 0; i < values.length; i ++) {
                String value;
                if (bReader.ready()) {
                    value = bReader.readLine();
                } else {
                    close();
                    init();
                    if (bReader.ready()) {
                        value = bReader.readLine();
                    } else {
                        return -1;
                    }
                }
                values[i] = value;
            }
            return 1;
        }
        return -1;
        
    }
}
