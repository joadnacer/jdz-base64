package com.joad.jdz.base64.scalar;

import com.joad.jdz.base64.Base64Decoder;
import com.joad.jdz.base64.Base64Encoder;

public class Base64Scalar {
    private static final Base64Encoder RFC4648_ENCODER = new RFC4648ScalarEncoder();

    private static final Base64Encoder RFC4648URL_ENCODER = new RFC4648URLScalarEncoder();

    private static final Base64Encoder RFC2045_ENCODER = new RFC2045ScalarEncoder();

    private static final Base64Decoder RFC4648_DECODER = new RFC4648ScalarDecoder();

    private static final Base64Decoder RFC4648URL_DECODER = new RFC4648URLScalarDecoder();

    private static final Base64Decoder RFC2045_DECODER = new RFC2045ScalarDecoder();

    /**
     * Encoder for the RFC4648 Base64 spec
     * 
     * @return An RFC4648 Base64 Encoder
     */
    public static Base64Encoder getEncoder() {
        return RFC4648_ENCODER;
    }
    
    /**
     * Encoder for the RFC4648URL Base64 spec
     * 
     * @return An RFC4648 Base64 Encoder
     */
    public static Base64Encoder getUrlEncoder() {
        return RFC4648URL_ENCODER;
    }
    
    /**
     * Encoder for the RFC2045 Base64 spec
     * 
     * @return An RFC42045 Base64 encoder
     */
    public static Base64Encoder getMimeEncoder() {
        return RFC2045_ENCODER;
    }

    /**
     * Decoder for the RFC4648 Base64 spec
     * 
     * @return An RFC4648 Base64 decoder
     */
    public static Base64Decoder getDecoder() {
        return RFC4648_DECODER;
    }

    /**
     * Decoder for the RFC4648 URL Base64 spec
     * 
     * @return An RFC4648 URL Base64 decoder
     */
    public static Base64Decoder getUrlDecoder() {
        return RFC4648URL_DECODER;
    }
    
    /**
     * Decoder for the RFC2045 Base64 spec
     * 
     * @return An RFC2045 Base64 fast encoder
     */
    public static Base64Decoder getMimeDecoder() {
        return RFC2045_DECODER;
    }
}
