/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * Copyright (c) 1997,1998 Sun Microsystems, Inc. All rights reserved.
 *
 * This source code is provided as is, without any express or implied warranty.
 */
package spec.benchmarks.mpegaudio;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.zip.CRC32;

import spec.harness.Context;
import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.BitstreamException;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.DecoderException;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;

public class Harness {
    static final int TRACKS_NUMBER = 6;
    
    static final int FRAMES_LIMIT = 8000;
    
    long[] result = new long[TRACKS_NUMBER];
    
    private String getName(int index) {
        return Main.prefix + "track" + index + ".mp3";
    }
    
    public void run(int id) {
        try {
            for (int i = id; i < id + TRACKS_NUMBER; i++) {
                int ind = i % TRACKS_NUMBER;
                result[ind] = decode(getName(ind));
            }
        } catch (Exception e) {
            e.printStackTrace(Context.getOut());
        }
    }
    
    private void updateCRC32(CRC32 crc32, short[] buffer) {
        int length = buffer.length;
        byte[] b = new byte[length * 2];
        for (int i = 0; i < length; i++) {
            short value = buffer[i];
            b[i] = (byte) buffer[i];
            b[i + length] = (byte) ((value & 0xff00) >> 8);
        }
        
        crc32.update(b, 0, b.length);
    }
    
    public long decode(final String name) throws BitstreamException,
            DecoderException, FileNotFoundException {
        Bitstream stream = new Bitstream(new FileInputStream(name));
        Decoder decoder = new Decoder();
        Header h;
        CRC32 crc = new CRC32();
        int decodedFrames = 0;
        while (decodedFrames < FRAMES_LIMIT && (h = stream.readFrame()) != null) {
            decodedFrames++;
            updateCRC32(crc, ((SampleBuffer) decoder.decodeFrame(h, stream))
            .getBuffer());
            stream.closeFrame();
        }
        stream.close();
        return crc.getValue();
    }
    
    public void inst_main(int id) {
        run(id);
        for (int i = 0; i < TRACKS_NUMBER; i++) {
            Context.getOut().println("track" + i + ": " + result[i]);
        }
    }
}