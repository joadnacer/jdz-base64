package com.joad.jdz.base64.scalar;

import static org.junit.Assert.*;

import java.util.Random;
import org.junit.Test;

public class RFC4648ScalarEncodeTest {
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
    public void RFC4648EncodeEmptyVector() {
        String jdzRes = new String(com.joad.jdz.base64.scalar.Base64Scalar.getEncoder().encode(emptyVector));

       assertEquals("", jdzRes);
    }
    
    @Test
    public void RFC4648EncodefVector() {
        String jdzRes = new String(com.joad.jdz.base64.scalar.Base64Scalar.getEncoder().encode(fVector));

        assertEquals("Zg==", jdzRes);
    }

    @Test
    public void RFC4648EncodefoVector() {
        String jdzRes = new String(com.joad.jdz.base64.scalar.Base64Scalar.getEncoder().encode(foVector));

        assertEquals("Zm8=", jdzRes);
    }

    @Test
    public void RFC4648EncodefooVector() {
        String jdzRes = new String(com.joad.jdz.base64.scalar.Base64Scalar.getEncoder().encode(fooVector));

        assertEquals("Zm9v", jdzRes);
    }

    @Test
    public void RFC4648EncodefoobVector() {
        String jdzRes = new String(com.joad.jdz.base64.scalar.Base64Scalar.getEncoder().encode(foobVector));

        assertEquals("Zm9vYg==", jdzRes);
    }

    @Test
    public void RFC4648EncodefoobaVector() {
        String jdzRes = new String(com.joad.jdz.base64.scalar.Base64Scalar.getEncoder().encode(foobaVector));

        assertEquals("Zm9vYmE=", jdzRes);
    }

    @Test
    public void RFC4648EncodefoobarVector() {
        String jdzRes = new String(com.joad.jdz.base64.scalar.Base64Scalar.getEncoder().encode(foobarVector));

        assertEquals("Zm9vYmFy", jdzRes);
    }
    @Test
    public void RFC4648EncodeZeroMod() {
        String javaRes = new String(java.util.Base64.getEncoder().encode(randomDataZeroMod));
        String joadRes = new String(com.joad.jdz.base64.scalar.Base64Scalar.getEncoder().encode(randomDataZeroMod));

        assertEquals(javaRes, joadRes);
    }

    @Test
    public void RFC4648EncodeOneMod() {
        String javaRes = new String(java.util.Base64.getEncoder().encode(randomDataOneMod));
        String joadRes = new String(com.joad.jdz.base64.scalar.Base64Scalar.getEncoder().encode(randomDataOneMod));

        assertEquals(javaRes, joadRes);
    }

    @Test
    public void RFC4648EncodeTwoMod() {
        String javaRes = new String(java.util.Base64.getEncoder().encode(randomDataTwoMod));
        String joadRes = new String(com.joad.jdz.base64.scalar.Base64Scalar.getEncoder().encode(randomDataTwoMod));

        assertEquals(javaRes, joadRes);
    }

    @Test
    public void RFC4648EncodeThreeMod() {
        String javaRes = new String(java.util.Base64.getEncoder().encode(randomDataThreeMod));
        String joadRes = new String(com.joad.jdz.base64.scalar.Base64Scalar.getEncoder().encode(randomDataThreeMod));

        assertEquals(javaRes, joadRes);
    }
}