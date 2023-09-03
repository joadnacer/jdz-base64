package com.joad.jdz.base64.vector;

import static org.junit.Assert.*;

import java.util.Random;
import org.junit.Test;
    
public class RFC2045FastVectorDecodeTest {
    /*
     * RFC2045Fast test vectors
     */
    private static final byte[] emptyVector  = "".getBytes();
    private static final byte[] fVector      = "Zg==".getBytes();
    private static final byte[] foVector     = "Zm8=".getBytes();
    private static final byte[] fooVector    = "Zm9v".getBytes();
    private static final byte[] foobVector   = "Zm9vYg==".getBytes();
    private static final byte[] foobaVector  = "Zm9vYmE=".getBytes();
    private static final byte[] foobarVector = "Zm9vYmFy".getBytes();

    /*
     * Random data fuzz
     */
    private static final byte[] randomDataZeroMod  = new byte[1000];
    private static final byte[] randomDataOneMod   = new byte[1001];
    private static final byte[] randomDataTwoMod   = new byte[1002];
    private static final byte[] randomDataThreeMod = new byte[1003];
    private static final byte[] randomDataSixtyOne = new byte[61];

    static {
        new Random().nextBytes(randomDataZeroMod);
        new Random().nextBytes(randomDataOneMod);
        new Random().nextBytes(randomDataTwoMod);
        new Random().nextBytes(randomDataThreeMod);
        new Random().nextBytes(randomDataSixtyOne);
    }

    private static final byte[] base64RandomDataZeroMod  = java.util.Base64.getMimeEncoder().encode(randomDataZeroMod);
    private static final byte[] base64RandomDataOneMod   = java.util.Base64.getMimeEncoder().encode(randomDataOneMod);
    private static final byte[] base64RandomDataTwoMod   = java.util.Base64.getMimeEncoder().encode(randomDataTwoMod);
    private static final byte[] base64RandomDataThreeMod = java.util.Base64.getMimeEncoder().encode(randomDataThreeMod);
    private static final byte[] base64RandomDataSixtyOne = java.util.Base64.getMimeEncoder().encode(randomDataSixtyOne);

    @Test
    public void RFC2045FastDecodeEmptyVector() {
        String jdzRes = new String(com.joad.jdz.base64.vector.Base64Vector.getMimeDecoder().decodeFast(emptyVector));

        assertEquals("", jdzRes);
    }
    
    @Test
    public void RFC2045FastDecodefVector() {
        String jdzRes = new String(com.joad.jdz.base64.vector.Base64Vector.getMimeDecoder().decodeFast(fVector));

        assertEquals("f", jdzRes);
    }

    @Test
    public void RFC2045FastDecodefoVector() {
        String jdzRes = new String(com.joad.jdz.base64.vector.Base64Vector.getMimeDecoder().decodeFast(foVector));

        assertEquals("fo", jdzRes);
    }

    @Test
    public void RFC2045FastDecodefooVector() {
        String jdzRes = new String(com.joad.jdz.base64.vector.Base64Vector.getMimeDecoder().decodeFast(fooVector));

        assertEquals("foo", jdzRes);
    }

    @Test
    public void RFC2045FastDecodefoobVector() {
        String jdzRes = new String(com.joad.jdz.base64.vector.Base64Vector.getMimeDecoder().decodeFast(foobVector));

        assertEquals("foob", jdzRes);
    }

    @Test
    public void RFC2045FastDecodefoobaVector() {
        String jdzRes = new String(com.joad.jdz.base64.vector.Base64Vector.getMimeDecoder().decodeFast(foobaVector));

        assertEquals("fooba", jdzRes);
    }

    @Test
    public void RFC2045FastDecodefoobarVector() {
        String jdzRes = new String(com.joad.jdz.base64.vector.Base64Vector.getMimeDecoder().decodeFast(foobarVector));

        assertEquals("foobar", jdzRes);
    }

    @Test
    public void RFC2045FastDecodeZeroMod() {
        String javaRes = new String(java.util.Base64.getMimeDecoder().decode(base64RandomDataZeroMod));
        String jdzRes = new String(com.joad.jdz.base64.vector.Base64Vector.getMimeDecoder().decodeFast(base64RandomDataZeroMod));

        assertEquals(javaRes, jdzRes);
    }

    @Test
    public void RFC2045FastDecodeOneMod() {
        String javaRes = new String(java.util.Base64.getMimeDecoder().decode(base64RandomDataOneMod));
        String jdzRes = new String(com.joad.jdz.base64.vector.Base64Vector.getMimeDecoder().decodeFast(base64RandomDataOneMod));

        assertEquals(javaRes, jdzRes);
    }

    @Test
    public void RFC2045FastDecodeTwoMod() {
        String javaRes = new String(java.util.Base64.getMimeDecoder().decode(base64RandomDataTwoMod));
        String jdzRes = new String(com.joad.jdz.base64.vector.Base64Vector.getMimeDecoder().decodeFast(base64RandomDataTwoMod));

        assertEquals(javaRes, jdzRes);
    }

    @Test
    public void RFC2045FastDecodeThreeMod() {
        String javaRes = new String(java.util.Base64.getMimeDecoder().decode(base64RandomDataThreeMod));
        String jdzRes = new String(com.joad.jdz.base64.vector.Base64Vector.getMimeDecoder().decodeFast(base64RandomDataThreeMod));

        assertEquals(javaRes, jdzRes);
    }
    
    @Test
    public void RFC2045DecodeSixtyOne() {
        String javaRes = new String(java.util.Base64.getMimeDecoder().decode(base64RandomDataSixtyOne));
        String jdzRes = new String(com.joad.jdz.base64.vector.Base64Vector.getMimeDecoder().decodeFast(base64RandomDataSixtyOne));

        assertEquals(javaRes, jdzRes);
    }
}