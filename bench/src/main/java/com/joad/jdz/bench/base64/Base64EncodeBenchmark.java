package com.joad.jdz.bench.base64;

import java.util.Random;
import org.openjdk.jmh.annotations.Benchmark;
import com.joad.jdz.bench.BenchmarkBase;
import com.migcomponents.migbase64.Base64;

public class Base64EncodeBenchmark extends BenchmarkBase {
    private static final byte[] randomData = new byte[ARR_LEN];

    private static final int dl = Math.multiplyExact(4, (Math.addExact(randomData.length, 2) / 3));

    private static final byte[] dst = new byte[dl];

    static {
        new Random().nextBytes(randomData);
    }

    @Benchmark
    public byte[] jdzBase64VectorEncode() {
        return com.joad.jdz.base64.vector.Base64Vector.getEncoder().encode(randomData);
    }

    @Benchmark
    public byte[] jdzBase64VectorEncodeReuse() {
        com.joad.jdz.base64.vector.Base64Vector.getEncoder().encode(randomData, dst);

        return dst;
    }

    @Benchmark
    public byte[] jdzBase64ScalarEncode() {
        return com.joad.jdz.base64.scalar.Base64Scalar.getEncoder().encode(randomData);
    }

    @Benchmark
    public byte[] jdzBase64ScalarEncodeReuse() {
        com.joad.jdz.base64.scalar.Base64Scalar.getEncoder().encode(randomData, dst);

        return dst;
    }

    @Benchmark
    public byte[] javaUtilBase64Encode() {
        return java.util.Base64.getEncoder().encode(randomData);
    }

    @Benchmark
    public byte[] javaUtilBase64EncodeReuse() {
        java.util.Base64.getEncoder().encode(randomData, dst);

        return dst;
    }

    @Benchmark
    public byte[] migBase64Encode() {
        return Base64.encodeToByte(randomData, false);
    }

    @Benchmark
    public byte[] iharderBase64Encode() {
        return net.iharder.Base64.encodeBytesToBytes(randomData);
    }

    @Benchmark
    public byte[] apacheBase64Encode() {
        return org.apache.commons.codec.binary.Base64.encodeBase64(randomData);
    }
}