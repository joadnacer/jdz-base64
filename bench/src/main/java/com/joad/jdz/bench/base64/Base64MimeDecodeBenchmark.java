package com.joad.jdz.bench.base64;

import java.io.IOException;
import java.util.Random;
import org.openjdk.jmh.annotations.Benchmark;
import com.joad.jdz.bench.BenchmarkBase;

public class Base64MimeDecodeBenchmark extends BenchmarkBase {
    private static final byte[] randomDataDec = new byte[ARR_LEN];

    static {
        new Random().nextBytes(randomDataDec);
    }

    private static final byte[] randomData = java.util.Base64.getMimeEncoder().encode(randomDataDec);
    
    private static final byte[] dst = new byte[com.joad.jdz.base64.scalar.Base64Scalar.getMimeDecoder().getDecodedLengthFast(randomData)];

    @Benchmark
    public byte[] jdzBase64FastMimeScalarDecode() {
        return com.joad.jdz.base64.scalar.Base64Scalar.getMimeDecoder().decodeFast(randomData);
    }

    @Benchmark
    public byte[] jdzBase64FastMimeScalarDecodeReuse() {
        com.joad.jdz.base64.scalar.Base64Scalar.getMimeDecoder().decodeFast(randomData, dst);

        return dst;
    }

    @Benchmark
    public byte[] jdzBase64FastMimeVectorDecode() {
        return com.joad.jdz.base64.vector.Base64Vector.getMimeDecoder().decodeFast(randomData);
    }

    @Benchmark
    public byte[] jdzBase64FastMimeVectorDecodeReuse() {
        com.joad.jdz.base64.vector.Base64Vector.getMimeDecoder().decodeFast(randomData, dst);

        return dst;
    }

    @Benchmark
    public byte[] jdzBase64MimeScalarDecode() {
        return com.joad.jdz.base64.scalar.Base64Scalar.getMimeDecoder().decode(randomData);
    }

    @Benchmark
    public byte[] jdzBase64MimeScalarDecodeReuse() {
        com.joad.jdz.base64.scalar.Base64Scalar.getMimeDecoder().decode(randomData, dst);

        return dst;
    }

    @Benchmark
    public byte[] jdzBase64MimeVectorDecode() {
        return com.joad.jdz.base64.vector.Base64Vector.getMimeDecoder().decode(randomData);
    }

    @Benchmark
    public byte[] jdzBase64MimeVectorDecodeReuse() {
        com.joad.jdz.base64.vector.Base64Vector.getMimeDecoder().decode(randomData, dst);

        return dst;
    }

    @Benchmark
    public byte[] javaUtilBase64MimeDecode() {
        return java.util.Base64.getMimeDecoder().decode(randomData);
    }
    
    @Benchmark
    public byte[] javaUtilBase64MimeDecodeReuse() {
        java.util.Base64.getMimeDecoder().decode(randomData, dst);
        
        return dst;
    }

    @Benchmark
    public byte[] migBase64MimeDecode() {
        return com.migcomponents.migbase64.Base64.decode(randomData);
    }

    @Benchmark
    public byte[] migBase64MimeDecodeFast() {
        return com.migcomponents.migbase64.Base64.decodeFast(randomData);
    }

    @Benchmark
    public byte[] iharderBase64MimeDecode() throws IOException {
        return net.iharder.Base64.decode(randomData, 0, randomData.length, 8);
    }

    @Benchmark
    public byte[] apacheBase64MimeDecode() {
        return org.apache.commons.codec.binary.Base64.decodeBase64(randomData);
    }
}
