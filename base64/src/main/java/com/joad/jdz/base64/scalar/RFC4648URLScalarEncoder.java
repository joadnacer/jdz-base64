package com.joad.jdz.base64.scalar;

import static com.joad.jdz.base64.scalar.ScalarUtils.*;
import static com.joad.jdz.base64.Base64Tables.*;

import com.joad.jdz.base64.Base64Encoder;

public class RFC4648URLScalarEncoder extends Base64Encoder {

    @Override
    public int encode(final byte[] src, final byte[] dst) {
        int sl = src.length;

        int sp = 0;
        int dp = 0;

        for (; sp < src.length - 57; sp += 57, dp += 76) {
            encodeSixBytes(src, dst, BASE64_URL, sp, dp);
            encodeSixBytes(src, dst, BASE64_URL, sp + 6, dp + 8);
            encodeSixBytes(src, dst, BASE64_URL, sp + 12, dp + 16);
            encodeSixBytes(src, dst, BASE64_URL, sp + 18, dp + 24);
            encodeSixBytes(src, dst, BASE64_URL, sp + 24, dp + 32);
            encodeSixBytes(src, dst, BASE64_URL, sp + 30, dp + 40);
            encodeSixBytes(src, dst, BASE64_URL, sp + 36, dp + 48);
            encodeSixBytes(src, dst, BASE64_URL, sp + 42, dp + 56);
            encodeSixBytes(src, dst, BASE64_URL, sp + 48, dp + 64);
            encodeThreeBytes(src, dst, BASE64_URL, sp + 54, dp + 72);
        }

        for (; sp < src.length - 3; sp += 3, dp += 4) {
            encodeThreeBytes(src, dst, BASE64_URL, sp, dp);
        }

        int remaining = sl - sp;

        return switch (remaining) {
            case 3 -> encodeLastThreeBytes(src, dst, BASE64_URL, sp, dp);
            case 2 -> encodeLastTwoBytes(src, dst, BASE64_URL, sp, dp);
            case 1 -> encodeLastByte(src, dst, BASE64_URL, sp, dp);
            default -> dp;
        };
    }

    @Override
    public int getEncodedLength(final byte[] src) {
       return Math.multiplyExact(4, (Math.addExact(src.length, 2) / 3));
    }
}