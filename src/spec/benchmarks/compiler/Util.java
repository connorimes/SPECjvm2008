/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * Copyright (c) 2006 Sun Microsystems, Inc. All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 */

package spec.benchmarks.compiler;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import spec.harness.StopBenchmarkException;

public class Util {
    public static final int BUFLEN = 2048;
    private static String lineSeparator = null;
    public static final Charset CHARSET = Charset.forName("ASCII");
    public static final CodingErrorAction ACTION = CodingErrorAction.REPORT;
    public static final String REGULAR_FILE_OBJECT_NAME = "RegularFileObject";
	public static final String ZIP_FILE_OBJECT_NAME = "ZipFileObject";
	public static final String ZIP_FILE_INDEX_FILE_OBJECT_NAME = "ZipFileIndexFileObject";
    
    public static String linesep() {
        if (lineSeparator == null) {
            try {
                lineSeparator = System.getProperty("line.separator");
            } catch (RuntimeException re) {
                System.out.println("ERROR: exception getting line.separator property = " + re);
                lineSeparator = "\n";
            }
            
        }
        return lineSeparator;
    }
    
    public static void unzip(String zipfile) {
        unzip(zipfile, "./");
    }
    
    public static void unzip(String zipfile, String dir) {
        unzip(zipfile, dir, null);
    }
    
    public static void unzip(String zipfile, String dir, ArrayList<String> files) {
        byte[] buf = new byte[BUFLEN];
        
        dir = dir.replace('\\', '/'); // canonicalize path
        if (dir.charAt(dir.length()-1) != '/') dir += "/";
        try {
            ZipFile zf = new ZipFile(zipfile);            
            Enumeration zipEnum = zf.entries();            
            ZipEntry item = null;
            File newdir = null;
            String newfile = null;
            InputStream is = null;
            OutputStream os = null;
            int len;             
            // System.out.println("Archive:  " + zipfile);
            while( zipEnum.hasMoreElements() ) {            	
                item = (ZipEntry)zipEnum.nextElement();
                
                if( item.isDirectory() ) {                	
                    newdir = new File(dir + item.getName());                    
                    // System.out.println("   creating: " + newdir.getPath());
                    newdir.mkdir();
                } else {                	
                    newfile = dir + item.getName();
                    // System.out.println("  inflating: " + newfile);
                    if (files != null) files.add(newfile);
                    is = new BufferedInputStream(zf.getInputStream(item));
                    os = new BufferedOutputStream(new FileOutputStream(newfile));
                    while ((len = is.read(buf)) >= 0) os.write(buf, 0, len);
                    is.close();
                    os.close();
                }
            }
            zf.close();
        } catch(Exception e) {
            System.err.println(e);
            e.printStackTrace();
            return;
        }
    }
    
    public static boolean copyFile(String src, String dest) {
        return copyFile(new File(src), new File(dest));
    }   
    
    public static File getTmpDir(File resDir, boolean makeDir) {    	 
        String tmpPath;
        tmpPath = System.getProperty("java.io.tmpdir");
        tmpPath = tmpPath != null ? tmpPath : "";        
                 
        File result = new File(tmpPath + File.separator + "SPECjvm2008"
        		               + File.separator + resDir.getName());        
         if (makeDir && !result.exists() && !result.mkdirs()) {
             System.out.println("ERROR: error creating directory for: " + result.getPath());
         }   	
         return result;
    }   
    
    public static boolean copyFile(File src, File dest) {
        boolean success = false;
        
        try {
            if (src.getCanonicalPath().contentEquals(dest.getCanonicalPath())) {
                System.err.println("cp: same file: " + src + " = " + dest);
            } else {
                FileChannel sourceChannel = new FileInputStream(src).getChannel();
                FileChannel destinationChannel = new FileOutputStream(dest).getChannel();
                sourceChannel.transferTo(0, sourceChannel.size(), destinationChannel);
                sourceChannel.close();
                destinationChannel.close();
                success = true;
            }
        } catch(Exception e) {
            System.err.println(e);
            e.printStackTrace();
        }
        return success;
    }
    
    public static File getZipFile(File srcDir) {    	
        File[] files = srcDir.listFiles();        
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile() && files[i].getName().endsWith(".zip")) {                	
                    return files[i];                    
                }
            }
        }
        
        throw new StopBenchmarkException("ERROR: could not find source zipfile");
        
    }
    
    public static void recursiveRemoveDir(File dir) {
        // System.out.println("DEBUG: recursiveRemoveDir: " + dir.getPath());
    	if (dir == null || !dir.exists()) {
    		return;
    	}
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                recursiveRemoveDir(files[i]);
            } else {
                if (! files[i].delete()) {
                    System.out.println("ERROR: unable to delete file: " + files[i].getPath());
                }
            }
        }
        if (! dir.delete()) {
            System.out.println("ERROR: unable to delete directory: " + dir.getPath());
        }
    }   
    
    public static File getSrcDir(File resDir, String name) {    	
    	return new File(resDir.getParentFile().getParentFile().getPath() 
	               + File.separator + "redistributable_sources" 
                   + File.separator + "packages" 
                   + File.separator + name);
    }
    
    public static File getSrcFile(File tmpDir) {
    	return new File(tmpDir.getPath() + File.separator + "sourcefiles.txt"); 
    }    
    
    static byte[] getBytes(InputStream is) throws IOException {		 
		int available = is.available();
		byte[] tmpBuf = new byte[available];
		int size = 0;
		int currentPointer = 0;
		byte[] result = null;
		while ((size = is.read(tmpBuf)) != -1) {
			result = updateBuffer(result, tmpBuf, currentPointer, size);
			currentPointer += size;			
		}		
		return result;
	}
    
    
    static byte[] getBytes(File f) throws IOException {    	
    	FileInputStream fis = new FileInputStream(f.getAbsolutePath());
    	byte[] result = getBytes(fis);
    	fis.close();
    	return result;
    }
	
	static byte[] updateBuffer(byte[] buf1, byte[] buf2, int currentPointer, int size) {
		if (size <= 0) {
			return buf2;
		}
		if (buf1 == null || buf1.length < currentPointer + size) {
			buf1 = new byte[currentPointer + size];
		}
		
		System.arraycopy(buf2, 0, buf1, currentPointer, size);
		return buf1;
	}
}
