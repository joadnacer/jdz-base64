package com.joad.jdz.bench.base64;

import java.io.IOException;
import java.util.Random;
import org.openjdk.jmh.annotations.Benchmark;
import com.joad.jdz.bench.BenchmarkBase;

public class Base64DecodeBenchmark extends BenchmarkBase {
    private static final byte[] randomDataDec = new byte[ARR_LEN];

    static {
        new Random().nextBytes(randomDataDec);
    }

    private static final byte[] randomData = java.util.Base64.getEncoder().encode(randomDataDec);
    
    private static final byte[] dst = new byte[com.joad.jdz.base64.scalar.Base64Scalar.getDecoder().getDecodedLength(randomData)];

    @Benchmark
    public byte[] jdzBase64ScalarDecode() {
        return com.joad.jdz.base64.scalar.Base64Scalar.getDecoder().decode(randomData);
    }

    @Benchmark
    public byte[] jdzBase64ScalarDecodeReuse() {
        com.joad.jdz.base64.scalar.Base64Scalar.getDecoder().decode(randomData, dst);

        return dst;
    }

    @Benchmark
    public byte[] jdzBase64VectorDecode() {
        return com.joad.jdz.base64.vector.Base64Vector.getDecoder().decode(randomData);
    }

    @Benchmark
    public byte[] jdzBase64VectorDecodeReuse() {
        com.joad.jdz.base64.vector.Base64Vector.getDecoder().decode(randomData, dst);

        return dst;
    }

    @Benchmark
    public byte[] jdzBase64ScalarDecodeFast() {
        return com.joad.jdz.base64.scalar.Base64Scalar.getDecoder().decodeFast(randomData);
    }

    @Benchmark
    public byte[] jdzBase64ScalarDecodeReuseFast() {
        com.joad.jdz.base64.scalar.Base64Scalar.getDecoder().decodeFast(randomData, dst);

        return dst;
    }

    @Benchmark
    public byte[] jdzBase64VectorDecodeFast() {
        return com.joad.jdz.base64.vector.Base64Vector.getDecoder().decodeFast(randomData);
    }

    @Benchmark
    public byte[] jdzBase64VectorDecodeReuseFast() {
        com.joad.jdz.base64.vector.Base64Vector.getDecoder().decodeFast(randomData, dst);

        return dst;
    }

    @Benchmark
    public byte[] javaUtilBase64Decode() {
        return java.util.Base64.getDecoder().decode(randomData);
    }
    
    @Benchmark
    public byte[] javaUtilBase64DecodeReuse() {
        java.util.Base64.getDecoder().decode(randomData, dst);
        
        return dst;
    }

    @Benchmark
    public byte[] migBase64Decode() {
        return com.migcomponents.migbase64.Base64.decode(randomData);
    }

    @Benchmark
    public byte[] migBase64DecodeFast() {
        return com.migcomponents.migbase64.Base64.decodeFast(randomData);
    }

    @Benchmark
    public byte[] iharderBase64Decode() throws IOException {
        return net.iharder.Base64.decode(randomData, 0, randomData.length, 0);
    }
    
    @Benchmark
    public byte[] apacheBase64Decode() {
        return org.apache.commons.codec.binary.Base64.decodeBase64(randomData);
    }
}
