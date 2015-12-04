/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 * All rights reserved.
 *
 * Copyright (c) 1997,1998 Sun Microsystems, Inc. All rights reserved.
 *
 * Modified by Kaivalya M. Dixit & Don McCauley (IBM) to read input files This
 * source code is provided as is, without any express or implied warranty.
 */

package spec.benchmarks.compress;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.CRC32;
import spec.harness.Context;
import spec.harness.Launch;

public final class Harness {
    public static final String[] FILES_NAMES = new String[] {
        "resources/compress/input/202.tar",
        "resources/compress/input/205.tar",
        "resources/compress/input/208.tar",
        "resources/compress/input/209.tar",
        "resources/compress/input/210.tar",
        "resources/compress/input/211.tar",
        "resources/compress/input/213x.tar",
        "resources/compress/input/228.tar",
        "resources/compress/input/239.tar",
        "resources/compress/input/misc.tar"};
    
    public static final int FILES_NUMBER = FILES_NAMES.length;
    public static final int LOOP_COUNT = 2;
    public static Source[] SOURCES;
    public static byte[][] COMPRESS_BUFFERS;
    public static byte[][] DECOMPRESS_BUFFERS;
    public static Compress CB;
    
    public void runCompress(int btid) {
        spec.harness.Context.getOut().println("Loop count = " + LOOP_COUNT);
        for (int i = 0; i < LOOP_COUNT; i++) {
            for (int j = 0; j < FILES_NUMBER; j++) {
                Source source = SOURCES[j];
                OutputBuffer comprBuffer, decomprBufer;
                comprBuffer = CB.performAction(source.getBuffer(),
                        source.getLength(),
                        CB.COMPRESS,
                        COMPRESS_BUFFERS[btid - 1]);
                decomprBufer = CB.performAction(COMPRESS_BUFFERS[btid - 1],
                        comprBuffer.getLength(),
                        CB.UNCOMPRESS,
                        DECOMPRESS_BUFFERS[btid - 1]);
                Context.getOut().print(source.getLength() + " " + source.getCRC() + " ");
                Context.getOut().print(comprBuffer.getLength() + comprBuffer.getCRC() + " ");
                Context.getOut().println(decomprBufer.getLength() + " " + decomprBufer.getCRC());
            }
        }
    }
    
    public long inst_main(int btid) {
        long startTime = System.currentTimeMillis();
        runCompress(btid);
        return System.currentTimeMillis() - startTime;
    }
    
    static void prepareBuffers() {
        CB = new Compress();
        SOURCES = new Source[FILES_NUMBER];
        for (int i = 0; i < FILES_NUMBER; i ++) {
            SOURCES[i] = new Source(Context.getSpecBasePath() + "/" + FILES_NAMES[i]);
        }
        DECOMPRESS_BUFFERS = new byte[Launch.currentNumberBmThreads][Source.MAX_LENGTH];
        COMPRESS_BUFFERS = new byte[Launch.currentNumberBmThreads][Source.MAX_LENGTH];
    }
    
    static class Source {
        private byte[] buffer;
        private long crc;
        private int length;
        static int MAX_LENGTH;
        
        public Source(String fileName) {
            buffer = fillBuffer(fileName);
            length = buffer.length;
            MAX_LENGTH = Math.max(length, MAX_LENGTH);
            CRC32 crc32 = new CRC32();
            crc32.update(buffer, 0, length);
            crc = crc32.getValue();
        }
        
        long getCRC() {
            return crc;
        }
        
        int getLength() {
            return length;
        }
        
        byte[] getBuffer() {
            return buffer;
        }
        
        private static byte[] fillBuffer(String fileName) {
            try {
                FileInputStream sif = new FileInputStream(fileName);
                int length = (int) new File(fileName).length();
                int counter = 0;
                
                // Only allocate size of input file rather than MAX - kmd
                // If compressed file is larger than input file this allocation
                // will fail and out of bound exception will occur
                // In real lie, compress will no do any compression as no
                // space is saved.-- kaivalya
                byte[] result = new byte[length];
                
                int bytes_read;
                while ((bytes_read = sif.read(result, counter,
                        (length - counter))) > 0) {
                    counter += bytes_read;
                }
                
                sif.close(); // release resources
                
                if (counter != length) {
                    spec.harness.Context.getOut().println(
                            "ERROR reading test input file");
                }
                return result;
            } catch (IOException e) {
                e.printStackTrace(Context.getOut());
            }
            
            return null;
        }
    }
}

