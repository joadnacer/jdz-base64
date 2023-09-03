package com.joad.jdz.bench.base64;

import java.io.IOException;
import java.util.Random;
import org.openjdk.jmh.annotations.Benchmark;
import com.joad.jdz.bench.BenchmarkBase;
import com.migcomponents.migbase64.Base64;

public class Base64MimeEncodeBenchmark extends BenchmarkBase {
    private static final byte[] randomData = new byte[ARR_LEN];

    private static final int dl = com.joad.jdz.base64.scalar.Base64Scalar.getMimeEncoder().getEncodedLength(randomData);

    private static final byte[] dst = new byte[dl];

    static {
        new Random().nextBytes(randomData);
    }

    @Benchmark
    public byte[] jdzBase64MimeVectorEncode() {
        return com.joad.jdz.base64.vector.Base64Vector.getMimeEncoder().encode(randomData);
    }

    @Benchmark
    public byte[] jdzBase64MimeVectorEncodeReuse() {
        com.joad.jdz.base64.vector.Base64Vector.getMimeEncoder().encode(randomData, dst);

        return dst;
    }

    @Benchmark
    public byte[] jdzBase64MimeScalarEncode() {
        return com.joad.jdz.base64.scalar.Base64Scalar.getMimeEncoder().encode(randomData);
    }

    @Benchmark
    public byte[] jdzBase64MimeScalarEncodeReuse() {
        com.joad.jdz.base64.scalar.Base64Scalar.getMimeEncoder().encode(randomData, dst);

        return dst;
    }

    @Benchmark
    public byte[] javaUtilBase64MimeEncode() {
        return java.util.Base64.getMimeEncoder().encode(randomData);
    }

    @Benchmark
    public byte[] javaUtilBase64MimeEncodeReuse() {
        java.util.Base64.getMimeEncoder().encode(randomData, dst);

        return dst;
    }

    @Benchmark
    public byte[] migBase64MimeEncode() {
        return Base64.encodeToByte(randomData, true);
    }

    @Benchmark
    public byte[] iharderBase64MimeEncode() throws IOException {
        return net.iharder.Base64.encodeBytesToBytes(randomData, 0, randomData.length, 8);
    }

    @Benchmark
    public byte[] apacheBase64MimeEncode() {
        return org.apache.commons.codec.binary.Base64.encodeBase64Chunked(randomData);
    }
}
