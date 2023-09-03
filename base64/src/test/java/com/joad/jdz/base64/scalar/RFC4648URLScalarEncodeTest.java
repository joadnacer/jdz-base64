package com.joad.jdz.base64.scalar;

import static org.junit.Assert.*;

import java.util.Random;
import org.junit.Test;

public class RFC4648URLScalarEncodeTest { 
    /*
     * RFC4648 test vectors
     */
    private static final byte[] emptyVector  = "".getBytes();
    private static final byte[] fVector      = "f".getBytes();
    private static final byte[] foVector     = "fo".getBytes();
    private static final byte[] fooVector    = "foo".getBytes();
    private static final byte[] foobVector   = "foob".getBytes();
    private static final byte[] foobaVector  = "fooba".getBytes();
    private static final byte[] foobarVector = "foobar".getBytes();

    /*
     * Random data fuzz
     */
    private static final byte[] randomDataZeroMod  = new byte[1000];
    private static final byte[] randomDataOneMod   = new byte[1001];
    private static final byte[] randomDataTwoMod   = new byte[1002];
    private static final byte[] randomDataThreeMod = new byte[1003];

    static {
        new Random().nextBytes(randomDataZeroMod);
        new Random().nextBytes(randomDataOneMod);
        new Random().nextBytes(randomDataTwoMod);
        new Random().nextBytes(randomDataThreeMod);
    }
    
    @Test
    public void RFC4648URLEncodeEmptyVector() {
        String jdzRes = new String(com.joad.jdz.base64.scalar.Base64Scalar.getUrlEncoder().encode(emptyVector));

        assertEquals("", jdzRes);
    }
    
    @Test
    public void RFC4648URLEncodefVector() {
        String jdzRes = new String(com.joad.jdz.base64.scalar.Base64Scalar.getUrlEncoder().encode(fVector));

        assertEquals("Zg==", jdzRes);
    }

    @Test
    public void RFC4648URLEncodefoVector() {
        String jdzRes = new String(com.joad.jdz.base64.scalar.Base64Scalar.getUrlEncoder().encode(foVector));

        assertEquals("Zm8=", jdzRes);
    }

    @Test
    public void RFC4648URLEncodefooVector() {
        String jdzRes = new String(com.joad.jdz.base64.scalar.Base64Scalar.getUrlEncoder().encode(fooVector));

        assertEquals("Zm9v", jdzRes);
    }

    @Test
    public void RFC4648URLEncodefoobVector() {
        String jdzRes = new String(com.joad.jdz.base64.scalar.Base64Scalar.getUrlEncoder().encode(foobVector));

        assertEquals("Zm9vYg==", jdzRes);
    }

    @Test
    public void RFC4648URLEncodefoobaVector() {
        String jdzRes = new String(com.joad.jdz.base64.scalar.Base64Scalar.getUrlEncoder().encode(foobaVector));

        assertEquals("Zm9vYmE=", jdzRes);
    }

    @Test
    public void RFC4648URLEncodefoobarVector() {
        String jdzRes = new String(com.joad.jdz.base64.scalar.Base64Scalar.getUrlEncoder().encode(foobarVector));

        assertEquals("Zm9vYmFy", jdzRes);
    }
    @Test
    public void RFC4648URLEncodeZeroMod() {
        String javaRes = new String(java.util.Base64.getUrlEncoder().encode(randomDataZeroMod));
        String joadRes = new String(com.joad.jdz.base64.scalar.Base64Scalar.getUrlEncoder().encode(randomDataZeroMod));

        assertEquals(javaRes, joadRes);
    }

    @Test
    public void RFC4648URLEncodeOneMod() {
        String javaRes = new String(java.util.Base64.getUrlEncoder().encode(randomDataOneMod));
        String joadRes = new String(com.joad.jdz.base64.scalar.Base64Scalar.getUrlEncoder().encode(randomDataOneMod));

        assertEquals(javaRes, joadRes);
    }

    @Test
    public void RFC4648URLEncodeTwoMod() {
        String javaRes = new String(java.util.Base64.getUrlEncoder().encode(randomDataTwoMod));
        String joadRes = new String(com.joad.jdz.base64.scalar.Base64Scalar.getUrlEncoder().encode(randomDataTwoMod));

        assertEquals(javaRes, joadRes);
    }

    @Test
    public void RFC4648URLEncodeThreeMod() {
        String javaRes = new String(java.util.Base64.getUrlEncoder().encode(randomDataThreeMod));
        String joadRes = new String(com.joad.jdz.base64.scalar.Base64Scalar.getUrlEncoder().encode(randomDataThreeMod));

        assertEquals(javaRes, joadRes);
    }
}