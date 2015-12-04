/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 */
package spec.io;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.xml.transform.stream.StreamSource;

import org.xml.sax.InputSource;

import spec.harness.StopBenchmarkException;

/**
 * This class cache (read only) files in memory, to be able to used
 * in benchmarks to avoid unwanted contention on the file system.
 *
 *  Note!
 *  This class must be used in a thread safe way.
 *  Files should therefore be put in cache by the harness execution.
 *  This is done by loading validation files in cache before first
 *  execution of the benchmark is started.
 *  This is done by loading files in the setupBenchmark method in the
 *  benchmarks.
 */
public class FileCache {
    
    private final static int MAX_FILE_SIZE = 10 * 1024 * 1024;
    private HashMap<String, byte[]> cache = new HashMap<String, byte[]>();
    
    public ByteArrayInputStream getStream(String fileName) throws IOException {
        if (!hasFile(fileName)) {
            loadFile(fileName);
        }
        byte [] arr = getByteArray(fileName);
        return new ByteArrayInputStream(arr);
    }
    
    public void discard(String fileName){
        cache.remove(fileName);
    }
    
    public int getLength(String fileName){
        return getByteArray(fileName).length;
    }
    
    public void clearCache() {
        cache.clear();
    }
    
    private boolean hasFile(String fileName) {
        return cache.get(fileName) != null;
    }
    
    public byte [] getByteArray(String fileName) {
        byte [] fileArray = (byte []) cache.get(fileName);
        if (fileArray == null) {
            throw new StopBenchmarkException("File '" + fileName + "' not in cache.");
        }
        return fileArray;
    }
    
    
    public void loadFile(String fileName) throws IOException {
        byte[] fileArray = (byte[]) cache.get(fileName);
        if (fileArray != null) {
            throw new StopBenchmarkException("Should not load file '" + fileName + "' into cache twice.");
        }
        
        // Get file
        File f = new File(fileName);
        if (!f.exists()) {
            throw new FileNotFoundException("Can't find file: " + fileName);
        }
        
        // Verify size of file
        long length = f.length();
        if (length >= (long) MAX_FILE_SIZE) {
            System.err.println("File length: " + length);
            System.err.println("MAX FILE LENGTH: " + MAX_FILE_SIZE);
            throw new IOException("File is too large to put in cache (length=" + length + ", max=" + MAX_FILE_SIZE + "): " + fileName);
        }
        
        // Create array to store file in
        fileArray = new byte[(int) length];
        
        // Read file
        InputStream is = new FileInputStream(f);
        int offset = 0, numRead = 0;
        while (offset < fileArray.length && (numRead = is.read(fileArray, offset, fileArray.length - offset)) >= 0) {
            offset += numRead;
        }
        is.close();
        
        // Store array
        cache.put(fileName, fileArray);
    }
    
    public class CachedFile {
        
        private String _name;
        
        public CachedFile( String systemId ) {
            _name = systemId;
        }
        
        public String getFilename() {
            return _name;
        }
        
        public ByteArrayInputStream getStream() throws IOException {
            return FileCache.this.getStream(_name);
        }
        
        public void cache() throws IOException {
            FileCache.this.getStream(_name);
        }
        
        public void discard() {
            FileCache.this.discard(_name);
        }
        
        public InputSource asNewInputSource() throws IOException {
            return new InputSource(getStream());
        }
        
        public StreamSource asNewStreamSource() throws IOException {
            StreamSource source = new StreamSource(getStream(),_name);
            return source;
        }
    }
}
