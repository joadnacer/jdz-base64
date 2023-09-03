package com.joad.jdz.base64;

import java.nio.charset.StandardCharsets;

public abstract class Base64Decoder {

    /**
     * Returns a newly allocated byte[] representing base64 decoded source.
     * 
     * @param src the data to be decoded
     * 
     * @return the decoded data
     */
    public byte[] decode(byte[] src) {
        int pad = getPadLength(src);
        int dl = getDecodedLength(src, pad);

        int sl = src.length - pad;

        byte[] dst = new byte[dl];

        decode(src, dst, sl, dl);

        return dst;
    }

    /**
     * Decodes base64 data from src to dst.
     * 
     * @param src the data to be decoded
     * @param dst the byte array to write to
     * 
     * @return the decoded data
     */
    public int decode(byte[] src, byte[] dst) {
        int pad = getPadLength(src);
        int dl = getDecodedLength(src, pad);

        int sl = src.length - pad;

        return decode(src, dst, sl, dl);
    }

    /**
     * Returns a newly allocated byte[] representing base64 decoded source.
     * Assumes that input is valid.
     * 
     * @param src the data to be decoded
     * 
     * @return the decoded data
     */
    public byte[] decodeFast(byte[] src) {
        int pad = getPadLength(src);
        int dl = getDecodedLengthFast(src, pad);

        int sl = src.length - pad;

        byte[] dst = new byte[dl];

        decodeFast(src, dst, sl, dl);

        return dst;
    }

    /**
     * Decodes base64 data from src to dst.
     * Assumes that input is valid.
     * 
     * @param src the data to be decoded
     * @param dst the byte array to write to
     * 
     * @return the decoded data
     */
    public int decodeFast(byte[] src, byte[] dst) {
        int pad = getPadLength(src);
        int dl = getDecodedLengthFast(src, pad);

        int sl = src.length - pad;

        return decodeFast(src, dst, sl, dl);
    }

    /**
     * Calculates decoded length for src
     * 
     * @param src the data to be decoded
     * 
     * @return the decoded length
     */
    public int getDecodedLength(byte[] src) {
        int pad = getPadLength(src);
        
        return getDecodedLength(src, pad);
    }

    protected abstract int getDecodedLength(byte[] src, int pad);
    
    /**
     * Calculates decoded length for src
     * Assumes that input is valid - works for standard and url encoding
     * 
     * @param src the data to be decoded
     * 
     * @return the decoded length
     */
    public int getDecodedLengthFast(byte[] src) {
        int pad = getPadLength(src);
        
        return getDecodedLengthFast(src, pad);
    }

    protected abstract int getDecodedLengthFast(byte[] src, int pad);

    /**
     * Returns a newly allocated byte[] representing base64 decoded source.
     * 
     * @param src the data to be decoded
     * 
     * @return the decoded data
     */
    public byte[] decode(String src) {
        return decode(src.getBytes(StandardCharsets.ISO_8859_1));
    }

    /**
     * Decodes base64 data from src to dst.
     * 
     * @param src the data to be decoded
     * @param dst the byte array to write to
     * 
     * @return the decoded data
     */
    public int decode(String src, byte[] dst) {
        return decode(src.getBytes(StandardCharsets.ISO_8859_1), dst);
    }

    private int getPadLength(byte[] src) {
        int sl = src.length;

        return sl > 0 && src[sl - 1] == '=' ? (src[sl - 2] == '=' ? 2 : 1) : 0;
    }

    protected abstract int decode(byte[] src, byte[] dst, int sl, int dl);

    protected abstract int decodeFast(byte[] src, byte[] dst, int sl, int dl);
}
