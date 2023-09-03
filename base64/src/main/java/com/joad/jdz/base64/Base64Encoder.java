package com.joad.jdz.base64;

import java.nio.charset.StandardCharsets;

public abstract class Base64Encoder {

    /**
     * Returns a newly allocated byte[] representing base64 encoded source.
     * 
     * @param src the data to be encoded
     * 
     * @return the base64 encoded data
     */
    public byte[] encode(byte[] src) {
        int dl = getEncodedLength(src);

        byte[] dst = new byte[dl];

        encode(src, dst);

        return dst;
    }

    /**
     * Writes base64 encoded data from src to dst.
     * 
     * @param src the data to be encoded
     * @param dst the byte array to write to
     * 
     * @return the length of encoded data
     */
    public abstract int encode(byte[] src, byte[] dst);

    /**
     * Calculates encoded length for src
     * 
     * @param src the data to be encoded
     * 
     * @return the encoded length
     */
    public abstract int getEncodedLength(byte[] src);

    /**
     * Returns a newly allocated byte[] representing base64 encoded source.
     * 
     * @param src the data to be encoded
     * 
     * @return the length of encoded data
     */
    public byte[] encode(String src) {
        return encode(src.getBytes(StandardCharsets.ISO_8859_1));
    }

    /**
     * Writes base64 encoded data from src to dst.
     * 
     * @param src the data to be encoded
     * @param dst the byte array to write to
     * 
     * @return the base64 encoded data
     */

    public int encode(String src, byte[] dst) {
        return encode(src.getBytes(StandardCharsets.ISO_8859_1), dst);
    }
}
