package com.joad.jdz.base64.vector;

import static org.junit.Assert.*;

import java.util.Random;
import org.junit.Test;

public class RFC2045VectorEncodeTest {
    /*
     * RFC2045 test vectors
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
    private static final byte[] randomDataZeroMod    = new byte[1000];
    private static final byte[] randomDataOneMod     = new byte[1001];
    private static final byte[] randomDataTwoMod     = new byte[1002];
    private static final byte[] randomDataThreeMod   = new byte[1003];
    private static final byte[] randomDataSeventySix = new byte[55];

    static {
        new Random().nextBytes(randomDataZeroMod);
        new Random().nextBytes(randomDataOneMod);
        new Random().nextBytes(randomDataTwoMod);
        new Random().nextBytes(randomDataThreeMod);
        new Random().nextBytes(randomDataSeventySix);
    }

    @Test
    public void RFC2045EncodeEmptyVector() {
        String jdzRes = new String(com.joad.jdz.base64.vector.Base64Vector.getMimeEncoder().encode(emptyVector));

       assertEquals("", jdzRes);
    }
    
    @Test
    public void RFC2045EncodefVector() {
        String jdzRes = new String(com.joad.jdz.base64.vector.Base64Vector.getMimeEncoder().encode(fVector));

        assertEquals("Zg==", jdzRes);
    }

    @Test
    public void RFC2045EncodefoVector() {
        String jdzRes = new String(com.joad.jdz.base64.vector.Base64Vector.getMimeEncoder().encode(foVector));

        assertEquals("Zm8=", jdzRes);
    }

    @Test
    public void RFC2045EncodefooVector() {
        String jdzRes = new String(com.joad.jdz.base64.vector.Base64Vector.getMimeEncoder().encode(fooVector));

        assertEquals("Zm9v", jdzRes);
    }

    @Test
    public void RFC2045EncodefoobVector() {
        String jdzRes = new String(com.joad.jdz.base64.vector.Base64Vector.getMimeEncoder().encode(foobVector));

        assertEquals("Zm9vYg==", jdzRes);
    }

    @Test
    public void RFC2045EncodefoobaVector() {
        String jdzRes = new String(com.joad.jdz.base64.vector.Base64Vector.getMimeEncoder().encode(foobaVector));

        assertEquals("Zm9vYmE=", jdzRes);
    }

    @Test
    public void RFC2045EncodefoobarVector() {
        String jdzRes = new String(com.joad.jdz.base64.vector.Base64Vector.getMimeEncoder().encode(foobarVector));

        assertEquals("Zm9vYmFy", jdzRes);
    }
    @Test
    public void RFC2045EncodeZeroMod() {
        String javaRes = new String(java.util.Base64.getMimeEncoder().encode(randomDataZeroMod));
        String joadRes = new String(com.joad.jdz.base64.vector.Base64Vector.getMimeEncoder().encode(randomDataZeroMod));

        assertEquals(javaRes, joadRes);
    }

    @Test
    public void RFC2045EncodeOneMod() {
        String javaRes = new String(java.util.Base64.getMimeEncoder().encode(randomDataOneMod));
        String joadRes = new String(com.joad.jdz.base64.vector.Base64Vector.getMimeEncoder().encode(randomDataOneMod));

        assertEquals(javaRes, joadRes);
    }

    @Test
    public void RFC2045EncodeTwoMod() {
        String javaRes = new String(java.util.Base64.getMimeEncoder().encode(randomDataTwoMod));
        String joadRes = new String(com.joad.jdz.base64.vector.Base64Vector.getMimeEncoder().encode(randomDataTwoMod));

        assertEquals(javaRes, joadRes);
    }

    @Test
    public void RFC2045EncodeThreeMod() {
        String javaRes = new String(java.util.Base64.getMimeEncoder().encode(randomDataThreeMod));
        String joadRes = new String(com.joad.jdz.base64.vector.Base64Vector.getMimeEncoder().encode(randomDataThreeMod));

        assertEquals(javaRes, joadRes);
    }

    @Test
    public void RFC2045EncodeSeventySix() {
        String javaRes = new String(java.util.Base64.getMimeEncoder().encode(randomDataSeventySix));
        String joadRes = new String(com.joad.jdz.base64.vector.Base64Vector.getMimeEncoder().encode(randomDataSeventySix));

        assertEquals(javaRes, joadRes);
    }
}