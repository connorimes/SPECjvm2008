/*
 * Copyright (c) 2008 Standard Performance Evaluation Corporation (SPEC)
 *               All rights reserved.
 *
 * Used 'Inner Classes' to minimize temptations of JVM exploiting low hanging
 * fruits. 'Inner classes' are defined in appendix D of the 'Java Programming
 * Language' by Ken Arnold. - We moved the class declaration, unchanged, of
 * Hash_Table to within the class declaration of Compressor. - We moved the
 * class declarations, unchanged, of De_Stack and Suffix_Table to within the
 * class declaration of Decompressor. - pre-computed trivial htab(i) to minimize
 * millions of trivial calls - Don McCauley (IBM), Kaivalya 4/16/98
 *
 * @(#)Compress.java 1.7 06/17/98 // Don McCauley/kmd - IBM 02/26/98 // getbyte
 * and getcode fixed -- kaivalya & Don compress.c - File compression ala IEEE
 * Computer, June 1984.
 *
 * Authors: Spencer W. Thomas (decvax!harpo!utah-cs!utah-gr!thomas) Jim McKie
 * (decvax!mcvax!jim) Steve Davies (decvax!vax135!petsd!peora!srd) Ken Turkowski
 * (decvax!decwrl!turtlevax!ken) James A. Woods (decvax!ihnp4!ames!jaw) Joe
 * Orost (decvax!vax135!petsd!joe)
 *
 * Algorithm from "A Technique for High Performance Data Compression", Terry A.
 * Welch, IEEE Computer Vol 17, No 6 (June 1984), pp 8-19.
 *
 * Algorithm: Modified Lempel-Ziv method (LZW). Basically finds common
 * substrings and replaces them with a variable size code. This is
 * deterministic, and can be done on the fly. Thus, the decompression procedure
 * needs no input table, but tracks the way the table was built.
 *
 * This source code is provided as is, without any express or implied warranty.
 */

package spec.benchmarks.compress;

import java.util.zip.CRC32;

final class Compress {
    final static int COMPRESS = 0;
    final static int UNCOMPRESS = 1;
    final static int BITS = 16; /* always set to 16 for SPEC95 */
    final static int INIT_BITS = 9; /* initial number of bits/code */
    final static int HSIZE = 69001; /* 95% occupancy */
    final static int SUFFIX_TAB_SZ = 65536; /* 2**BITS */
    final static int STACK_SZ = 8000; /* decompression stack size */
    final static byte magic_header[] = { (byte) 037, (byte) 0235 }; /* 1F 9D */
    
    /* Defines for third byte of header */
    final static int BIT_MASK = 0x1f;
    final static int BLOCK_MASK = 0x80;
    
        /*
         * Masks 0x40 and 0x20 are free. I think 0x20 should mean that there is a
         * fourth header byte (for expansion).
         */
    
        /*
         * the next two codes should not be changed lightly, as they must not lie
         * within the contiguous general code space.
         */
    final static int FIRST = 257; /* first free entry */
    final static int CLEAR = 256; /* table clear output code */
    
    final static byte lmask[] = { (byte) 0xff, (byte) 0xfe, (byte) 0xfc,
    (byte) 0xf8, (byte) 0xf0, (byte) 0xe0,
    (byte) 0xc0, (byte) 0x80, (byte) 0x00 };
    
    final static byte rmask[] = { (byte) 0x00, (byte) 0x01, (byte) 0x03,
    (byte) 0x07, (byte) 0x0f, (byte) 0x1f,
    (byte) 0x3f, (byte) 0x7f,	(byte) 0xff };
    
    public static OutputBuffer performAction(byte[] src, int srcLength,
            int action, byte[] dst) {
        
        InputBuffer in = new InputBuffer(srcLength, src);
        OutputBuffer out = new OutputBuffer(dst);
        
        if (action == COMPRESS) {
            new Compressor(in, out).compress();
        } else {
            new Decompressor(in, out).decompress();
        }
        return out;
    }
}

final class InputBuffer {
    private int cnt;
    private int current;
    private byte[] buffer;
    
    public InputBuffer(int c, byte[] b) {
        cnt = c;
        buffer = b;
    }
    
    public int readByte() {
        return cnt-- > 0 ? (buffer[current++] & 0x00FF) : -1;
    }
    
    public int readBytes(byte[] buf, int n) {
        if (cnt <= 0)
            return -1;
        int num = Math.min(n, cnt);
        for (int i = 0; i < num; i++) {
            buf[i] = buffer[current++];
            cnt--;
        }
        return num;
    }
}

final class OutputBuffer {
    private int cnt;
    private byte[] buffer;
    
    public OutputBuffer(byte[] b) {
        buffer = b;
    }
    
    public int getLength() {
        return cnt;
    }
    
    public long getCRC() {
        CRC32 crc32 = new CRC32();
        crc32.update(buffer, 0, cnt);
        return crc32.getValue();
    }
    
    public void writeByte(byte c) {
        buffer[cnt++] = c;
    }
    
    public void writebytes(byte[] buf, int n) {
        for (int i = 0; i < n; i++) {
            buffer[cnt++] = buf[i];
        }
    }
}



final class CodeTable {
    private short tab[];
    
    public CodeTable() {
        tab = new short[Compress.HSIZE];
    }
    
    public int of(int i) {
        return (int) tab[i] << 16 >>> 16;
    }
    
    public void set(int i, int v) {
        tab[i] = (short) v;
    }
    
    public void clear(int size) {
        for (int code = 0; code < size; code++) {
            tab[code] = 0;
        }
    }
}


class CompBase {
    protected int bitsNumber; /* number of bits/code */
    
    protected int maxBits; /* user settable max # bits/code */
    
    protected int maxCode; /* maximum code, given n_bits */
    
    protected int maxMaxCode; /* should NEVER generate this code */
    
    protected int offset;
    
    protected int blockCompress;
    
    protected int freeEntry; /* first unused entry */
    
    protected int clearFlag;
    
    protected InputBuffer input;
    
    protected OutputBuffer output;
    
    protected byte buf[];
    
    public CompBase(InputBuffer in, OutputBuffer out) {
        input = in;
        output = out;
        maxBits = Compress.BITS;
        blockCompress = Compress.BLOCK_MASK;
        buf = new byte[Compress.BITS];
    }
    
    public int getMaxCode() {
        return ((1 << (bitsNumber)) - 1);
    }
}


/*
 * compress (Originally: stdin to stdout -- Changed by SPEC to: memory to
 * memory)
 *
 * Algorithm: use open addressing double hashing (no chaining) on the prefix
 * code / next character combination. We do a variant of Knuth's algorithm D
 * (vol. 3, sec. 6.4) along with G. Knott's relatively-prime secondary probe.
 * Here, the modular division first probe is gives way to a faster exclusive-or
 * manipulation. Also do block compression with an adaptive reset, whereby the
 * code table is cleared when the compression ratio decreases, but after the
 * table fills. The variable-length output codes are re-sized at this point, and
 * a special CLEAR code is generated for the decompressor. Late addition:
 * construct the table according to file size for noticeable speed improvement
 * on small files. Please direct questions about this implementation to
 * ames!jaw.
 */

final class Compressor extends CompBase {
    private final static int CHECK_GAP = 10000; /* ratio check interval */
    
    private int ratio;
    
    private int checkpoint;
    
    private int inCount; /* length of input */
    
    private int outCount; /* # of codes output */
    
    private int bytesOut; /* length of compressed output */
    
    private HashTable htab;
    
    private CodeTable codetab;
    
    public Compressor(InputBuffer in, OutputBuffer out) {
        super(in, out);
        if (maxBits < Compress.INIT_BITS) {
            maxBits = Compress.INIT_BITS;
        }
        if (maxBits > Compress.BITS) {
            maxBits = Compress.BITS;
        }
        maxMaxCode = 1 << maxBits;
        bitsNumber = Compress.INIT_BITS;
        maxCode = getMaxCode();
        
        offset = 0;
        bytesOut = 3; /* includes 3-byte header mojo */
        outCount = 0;
        clearFlag = 0;
        ratio = 0;
        inCount = 1;
        checkpoint = CHECK_GAP;
        freeEntry = ((blockCompress != 0) ? Compress.FIRST : 256);
        
        htab = new HashTable(); // dm/kmd 4/10/98
        codetab = new CodeTable();
        
        output.writeByte(Compress.magic_header[0]);
        output.writeByte(Compress.magic_header[1]);
        output.writeByte((byte) (maxBits | blockCompress));
    }
    
    public void compress() {
        int fcode;
        int i = 0;
        int c;
        int disp;
        int hshift = 0;
        
        int ent = input.readByte();
        
        for (fcode = htab.hsize(); fcode < 65536; fcode *= 2) {
            hshift++;
        }
        hshift = 8 - hshift; /* set hash code range bound */
        
        int hsizeReg = htab.hsize();
        htab.clear(); /* clear hash table */
        
        next_byte: while ((c = input.readByte()) != -1) {
            inCount++;
            fcode = (((int) c << maxBits) + ent);
            i = ((c << hshift) ^ ent); /* xor hashing */
            int temphtab = htab.of(i);
            if (temphtab == fcode) {
                ent = codetab.of(i);
                continue next_byte;
            }
            if (temphtab >= 0) { /* non-empty slot dm kmd 4/15 */
                disp = hsizeReg - i; /* secondary hash (after G. Knott) */
                if (i == 0) {
                    disp = 1;
                }
                do {
                    if ((i -= disp) < 0) {
                        i += hsizeReg;
                    }
                    temphtab = htab.of(i);
                    if (temphtab == fcode) {
                        ent = codetab.of(i);
                        continue next_byte;
                    }
                } while (temphtab > 0);
            }
            
            output(ent);
            outCount++;
            ent = c;
            if (freeEntry < maxMaxCode) {
                codetab.set(i, freeEntry++); /* code -> hashtable */
                htab.set(i, fcode);
            } else if (inCount >= checkpoint && blockCompress != 0) {
                clBlock();
            }
        }
                /*
                 * Put out the final code.
                 */
        output(ent);
        outCount++;
        output(-1);
        
        return;
    }
    
        /*
         * Output the given code. Inputs: code: A n_bits-bit integer. If == -1, then
         * EOF. This assumes that n_bits = < (long)wordsize - 1. Outputs: Outputs
         * code to the file. Assumptions: Chars are 8 bits long. Algorithm: Maintain
         * a BITS character long buffer (so that 8 codes will fit in it exactly).
         */
    
    private void output(int code) {
        int rOff = offset, bits = bitsNumber;
        int bp = 0;
        
        if (code >= 0) {
                        /*
                         * Get to the first byte.
                         */
            bp += rOff >> 3;
            rOff &= 7;
                        /*
                         * Since code is always >= 8 bits, only need to mask the first hunk
                         * on the left.
                         */
            buf[bp] = (byte) ((buf[bp] & Compress.rmask[rOff]) | (code << rOff)
            & Compress.lmask[rOff]);
            bp++;
            bits -= 8 - rOff;
            code >>= 8 - rOff;
            /* Get any 8 bit parts in the middle ( <=1 for up to 16 bits). */
            if (bits >= 8) {
                buf[bp++] = (byte) code;
                code >>= 8;
                bits -= 8;
            }
            /* Last bits. */
            if (bits != 0) {
                buf[bp] = (byte) code;
            }
            offset += bitsNumber;
            if (offset == (bitsNumber << 3)) {
                bp = 0;
                bits = bitsNumber;
                bytesOut += bits;
                do {
                    output.writeByte(buf[bp++]);
                } while (--bits != 0);
                offset = 0;
            }
            
                        /*
                         * If the next entry is going to be too big for the code size, then
                         * increase it, if possible.
                         */
            if (freeEntry > maxCode || clearFlag > 0) {
                                /*
                                 * Write the whole buffer, because the input side won't discover
                                 * the size increase until after it has read it.
                                 */
                if (offset > 0) {
                    output.writebytes(buf, bitsNumber);
                    bytesOut += bitsNumber;
                }
                offset = 0;
                
                if (clearFlag != 0) {
                    bitsNumber = Compress.INIT_BITS;
                    maxCode = getMaxCode();
                    clearFlag = 0;
                } else {
                    bitsNumber++;
                    if (bitsNumber == maxBits) {
                        maxCode = maxMaxCode;
                    } else {
                        maxCode = getMaxCode();
                    }
                }
            }
        } else {
                        /*
                         * At EOF, write the rest of the buffer.
                         */
            if (offset > 0) {
                output.writebytes(buf, ((offset + 7) / 8));
            }
            bytesOut += (offset + 7) / 8;
            offset = 0;
        }
    }
    
    /* table clear for block compress */
    private void clBlock() {
        int rat;
        
        checkpoint = inCount + CHECK_GAP;
        
        if (inCount > 0x007fffff) { /* shift will overflow */
            rat = bytesOut >> 8;
            if (rat == 0) { /* Don't divide by zero */
                rat = 0x7fffffff;
            } else {
                rat = inCount / rat;
            }
        } else {
            rat = (inCount << 8) / bytesOut; /* 8 fractional bits */
        }
        if (rat > ratio) {
            ratio = rat;
        } else {
            ratio = 0;
            htab.clear();
            freeEntry = Compress.FIRST;
            clearFlag = 1;
            output((int) Compress.CLEAR);
        }
    }
    
    static final class HashTable { // moved 4/15/98 dm/kmd
        /*
         * Use protected instead of private to allow access by parent class of inner
         * class. wnb 4/17/98
         */
        
        protected int tab[]; // for dynamic table sizing */
        
        protected int size;
        
        public HashTable() {
            size = Compress.HSIZE;
            tab = new int[size];
        }
        
        public int of(int i) {
            return tab[i];
        }
        
        public void set(int i, int v) {
            tab[i] = v;
        }
        
        public int hsize() {
            return size;
        }
        
        public void clear() {
            for (int i = 0; i < size; i++) {
                tab[i] = -1;
            }
        }
    }
    
}

/*
 * Decompress stdin to stdout. This routine adapts to the codes in the file
 * building the "string" table on-the-fly; requiring no table to be stored in
 * the compressed file. The tables used herein are shared with those of the
 * compress() routine. See the definitions above.
 */

final class Decompressor extends CompBase {
    private int size;
    
    private CodeTable tabPrefix;
    
    private SuffixTable tabSuffix;
    
    private DeStack deStack;
    
    public Decompressor(InputBuffer in, OutputBuffer out) {
        super(in, out);
        
        /* Check the magic number */
        if (((input.readByte() & 0xFF) != (Compress.magic_header[0] & 0xFF))
        || ((input.readByte() & 0xFF) != (Compress.magic_header[1] & 0xFF))) {
            System.err.println("stdin: not in compressed format");
        }
        
        maxBits = input.readByte(); /* set -b from file */
        blockCompress = maxBits & Compress.BLOCK_MASK;
        maxBits &= Compress.BIT_MASK;
        maxMaxCode = 1 << maxBits;
        if (maxBits > Compress.BITS) {
            System.err.println("stdin: compressed with " + maxBits
                    + " bits, can only handle " + Compress.BITS + " bits");
        }
        bitsNumber = Compress.INIT_BITS;
        maxCode = getMaxCode();
        
        offset = 0;
        size = 0;
        clearFlag = 0;
        freeEntry = ((blockCompress != 0) ? Compress.FIRST : 256);
        
        tabPrefix = new CodeTable();
        tabSuffix = new SuffixTable();
        deStack = new DeStack();
        
                /*
                 * As above, initialize the first 256 entries in the table.
                 */
        tabPrefix.clear(256);
        tabSuffix.init(256);
    }
    
    public void decompress() {
        int code, oldcode, incode;
        
        int finchar = oldcode = getCode();
        if (oldcode == -1)  {/* EOF already? */
            return; /* Get out of here */
        }
        output.writeByte((byte) finchar); /* first code must be 8 bits = byte */
        
        while ((code = getCode()) > -1) {
            if ((code == Compress.CLEAR) && (blockCompress != 0)) {
                tabPrefix.clear(256);
                clearFlag = 1;
                freeEntry = Compress.FIRST - 1;
                if ((code = getCode()) == -1) /* O, untimely death! */
                    break;
            }
            incode = code;
                        /*
                         * Special case for KwKwK string.
                         */
            if (code >= freeEntry) {
                deStack.push((byte) finchar);
                code = oldcode;
            }
            
                        /*
                         * Generate output characters in reverse order
                         */
            while (code >= 256) {
                deStack.push(tabSuffix.of(code));
                code = tabPrefix.of(code);
            }
            deStack.push((byte) (finchar = tabSuffix.of(code)));
            
                        /*
                         * And put them out in forward order
                         */
            do {
                output.writeByte(deStack.pop());
            } while (!deStack.isEmpty());
            
                        /*
                         * Generate the new entry.
                         */
            if ((code = freeEntry) < maxMaxCode) {
                tabPrefix.set(code, oldcode);
                tabSuffix.set(code, (byte) finchar);
                freeEntry = code + 1;
            }
                        /*
                         * Remember previous code.
                         */
            oldcode = incode;
        }
    }
    
        /*
         * Read one code from the standard input. If EOF, return -1. Inputs: stdin
         * Outputs: code or -1 is returned.
         */
    
    private int getCode() {
        int code;
        int rOff, bits;
        int bp = 0;
        
        if (clearFlag > 0 || offset >= size || freeEntry > maxCode) {
                        /*
                         * If the next entry will be too big for the current code size, then
                         * we must increase the size. This implies reading a new buffer
                         * full, too.
                         */
            if (freeEntry > maxCode) {
                bitsNumber++;
                if (bitsNumber == maxBits) {
                    maxCode = maxMaxCode; /* won't get any bigger now */
                } else {
                    maxCode = getMaxCode();
                }
            }
            if (clearFlag > 0) {
                bitsNumber = Compress.INIT_BITS;
                maxCode = getMaxCode();
                clearFlag = 0;
            }
            size = input.readBytes(buf, bitsNumber);
            if (size <= 0) {
                return -1; /* end of file */
            }
            offset = 0;
            /* Round size down to integral number of codes */
            size = (size << 3) - (bitsNumber - 1);
        }
        rOff = offset;
        bits = bitsNumber;
                /*
                 * Get to the first byte.
                 */
        bp += rOff >> 3;
        rOff &= 7;
        /* Get first part (low order bits) */
        code = ((buf[bp++] >> rOff) & Compress.rmask[8 - rOff]) & 0xff;
        bits -= 8 - rOff;
        rOff = 8 - rOff; /* now, offset into code word */
        /* Get any 8 bit parts in the middle ( <=1 for up to 16 bits). */
        if (bits >= 8) {
            code |= (buf[bp++] & 0xff) << rOff;
            rOff += 8;
            bits -= 8;
        }
        /* high order bits. */
        //	code |= (buf[bp] & Compress.rmask[bits]) << r_off; // kmd
        // Don McCauley/kmd - IBM 02/26/98
        if (bits > 0) {
            code |= (buf[bp] & Compress.rmask[bits]) << rOff;
        }
        offset += bitsNumber;
        
        return code;
    }
    
    
    static final class DeStack { // moved 4/15/98 dm/kmd
        /*
         * Use protected instead of private to allow access by parent class of inner
         * class. wnb 4/17/98
         */
        
        protected byte tab[];
        
        protected int index;
        
        public DeStack() {
            tab = new byte[Compress.STACK_SZ];
        }
        
        public void push(byte c) {
            tab[index++] = c;
        }
        
        public byte pop() {
            return tab[--index];
        }
        
        public boolean isEmpty() {
            return index == 0;
        }
    }
    
    
    static final class SuffixTable {
        protected byte tab[];
        public SuffixTable() {
            tab = new byte[Compress.SUFFIX_TAB_SZ];
        }
        
        public byte of(int i) {
            return tab[i];
        }
        
        public void set(int i, byte v) {
            tab[i] = v;
        }
        
        public void init(int size) {
            for (int code = 0; code < size; code++) {
                tab[code] = (byte) code;
            }
        }
    }
}


