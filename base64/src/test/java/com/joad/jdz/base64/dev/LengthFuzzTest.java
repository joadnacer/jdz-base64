package com.joad.jdz.base64.dev;

import static org.junit.Assert.*;

import java.util.Random;
import org.junit.Test;
 
public class LengthFuzzTest {
    @Test
    public void lengthFuzz() {
        // for (int i = 0; i < 1000; i++) {
        for (int i = 0; i < 100; i++) {
            byte[] randomData = new byte[i];

            //System.out.println(i);
            new Random().nextBytes(randomData);

            // try{
            byte[] randomDataEnc = java.util.Base64.getEncoder().encode(randomData);
            byte[] randomDataEncUrl = java.util.Base64.getUrlEncoder().encode(randomData);
            byte[] randomDataEncMime = java.util.Base64.getMimeEncoder().encode(randomData);

            String javaResEnc = new String(randomDataEnc);
            String javaResUrlEnc = new String(randomDataEncUrl);
            String javaResMimeEnc = new String(randomDataEncMime);
            
            String javaResDec = new String(java.util.Base64.getDecoder().decode(randomDataEnc));
            String javaResUrlDec = new String(java.util.Base64.getUrlDecoder().decode(randomDataEncUrl));
            String javaResMimeDec = new String(java.util.Base64.getMimeDecoder().decode(randomDataEncMime));
            
            String jdzResScalarEnc = new String(com.joad.jdz.base64.scalar.Base64Scalar.getEncoder().encode(randomData));
            String jdzResScalarUrlEnc = new String(com.joad.jdz.base64.scalar.Base64Scalar.getUrlEncoder().encode(randomData));
            String jdzResScalarMimeEnc = new String(com.joad.jdz.base64.scalar.Base64Scalar.getMimeEncoder().encode(randomData));

            String jdzResVectorEnc = new String(com.joad.jdz.base64.vector.Base64Vector.getEncoder().encode(randomData));
            String jdzResVectorUrlEnc = new String(com.joad.jdz.base64.vector.Base64Vector.getUrlEncoder().encode(randomData));
            String jdzResVectorMimeEnc = new String(com.joad.jdz.base64.vector.Base64Vector.getMimeEncoder().encode(randomData));
            
            String jdzResScalarDec = new String(com.joad.jdz.base64.scalar.Base64Scalar.getDecoder().decode(randomDataEnc));
            String jdzResScalarUrlDec = new String(com.joad.jdz.base64.scalar.Base64Scalar.getUrlDecoder().decode(randomDataEncUrl));
            String jdzResScalarMimeDec = new String(com.joad.jdz.base64.scalar.Base64Scalar.getMimeDecoder().decode(randomDataEncMime));
            String jdzResScalarFastMimeDec = new String(com.joad.jdz.base64.scalar.Base64Scalar.getMimeDecoder().decodeFast(randomDataEncMime));

            String jdzResVectorDec = new String(com.joad.jdz.base64.vector.Base64Vector.getDecoder().decode(randomDataEnc));
            String jdzResVectorUrlDec = new String(com.joad.jdz.base64.vector.Base64Vector.getUrlDecoder().decode(randomDataEncUrl));
            String jdzResVectorMimeDec = new String(com.joad.jdz.base64.vector.Base64Vector.getMimeDecoder().decode(randomDataEncMime));
            String jdzResVectorFastMimeDec = new String(com.joad.jdz.base64.vector.Base64Vector.getMimeDecoder().decodeFast(randomDataEncMime));

            assertEquals("scalarEnc", javaResEnc, jdzResScalarEnc);
            assertEquals("scalarUrlEnc", javaResUrlEnc, jdzResScalarUrlEnc);
            assertEquals("scalarMimeEnc", javaResMimeEnc, jdzResScalarMimeEnc);

            assertEquals("vectorEnc", javaResEnc, jdzResVectorEnc);
            assertEquals("vectorUrlEnc", javaResUrlEnc, jdzResVectorUrlEnc);
            assertEquals("vectorMimeEnc", javaResMimeEnc, jdzResVectorMimeEnc);

            assertEquals("scalarDec", javaResDec, jdzResScalarDec);
            assertEquals("scalarUrlDec", javaResUrlDec, jdzResScalarUrlDec);
            assertEquals("scalarMimeDec", javaResMimeDec, jdzResScalarMimeDec);
            assertEquals("scalarFastMimeDec", javaResMimeDec, jdzResScalarFastMimeDec);

            assertEquals("vectorDec", javaResDec, jdzResVectorDec);
            assertEquals("vectorUrlDec", javaResUrlDec, jdzResVectorUrlDec);
            assertEquals("vectorMimeDec", javaResMimeDec, jdzResVectorMimeDec);
            assertEquals("vectorFastMimeDec", javaResMimeDec, jdzResVectorFastMimeDec);
            // } catch (Exception e) {e.printStackTrace(); System.exit(0);}
        }
    }
}
