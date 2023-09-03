package com.joad.jdz.base64.scalar;

import static com.joad.jdz.base64.scalar.ScalarDecoderVars.*;
import static com.joad.jdz.base64.scalar.ScalarUtils.*;

import com.joad.jdz.base64.Base64Decoder;

public class RFC4648ScalarDecoder extends Base64Decoder {

    @Override
    protected int decode(byte[] src, byte[] dst, int sl, int dl) {
        int sp = 0;
        int dp = 0;

        for (; dp < dl - 57; sp += 76, dp += 57) {
            decodeEightBytesValidating(src, dst, LOOKUP, sp, dp);
            decodeEightBytesValidating(src, dst, LOOKUP, sp + 8, dp + 6);
            decodeEightBytesValidating(src, dst, LOOKUP, sp + 16, dp + 12);
            decodeEightBytesValidating(src, dst, LOOKUP, sp + 24, dp + 18);
            decodeEightBytesValidating(src, dst, LOOKUP, sp + 32, dp + 24);
            decodeEightBytesValidating(src, dst, LOOKUP, sp + 40, dp + 30);
            decodeEightBytesValidating(src, dst, LOOKUP, sp + 48, dp + 36);
            decodeEightBytesValidating(src, dst, LOOKUP, sp + 56, dp + 42);
            decodeEightBytesValidating(src, dst, LOOKUP, sp + 64, dp + 48);
            decodeFourBytesValidating(src, dst, LOOKUP, sp + 72, dp + 54);
        }

        for (; sp < sl - 4; sp += 4, dp += 3) {
            decodeFourBytesValidating(src, dst, LOOKUP, sp, dp);
        }

        int remaining = sl - sp;

        return switch (remaining) {
            case 4 -> decodeLastFourBytesValidating(src, dst, LOOKUP, sp, dp);
            case 3 -> decodeLastThreeBytesValidating(src, dst, LOOKUP, sp, dp);
            case 2 -> decodeLastTwoBytesValidating(src, dst, LOOKUP, sp, dp);
            default -> dp;
        };
    }

    @Override
    protected int decodeFast(byte[] src, byte[] dst, int sl, int dl) {
        int sp = 0;
        int dp = 0;

        for (; dp < dl - 57; sp += 76, dp += 57) {
            decodeEightBytes(src, dst, LOOKUP, sp, dp);
            decodeEightBytes(src, dst, LOOKUP, sp + 8, dp + 6);
            decodeEightBytes(src, dst, LOOKUP, sp + 16, dp + 12);
            decodeEightBytes(src, dst, LOOKUP, sp + 24, dp + 18);
            decodeEightBytes(src, dst, LOOKUP, sp + 32, dp + 24);
            decodeEightBytes(src, dst, LOOKUP, sp + 40, dp + 30);
            decodeEightBytes(src, dst, LOOKUP, sp + 48, dp + 36);
            decodeEightBytes(src, dst, LOOKUP, sp + 56, dp + 42);
            decodeEightBytes(src, dst, LOOKUP, sp + 64, dp + 48);
            decodeFourBytes(src, dst, LOOKUP, sp + 72, dp + 54);
        }

        for (; sp < sl - 4; sp += 4, dp += 3) {
            decodeFourBytes(src, dst, LOOKUP, sp, dp);
        }

        int remaining = sl - sp;

        return switch (remaining) {
            case 4 -> decodeLastFourBytes(src, dst, LOOKUP, sp, dp);
            case 3 -> decodeLastThreeBytes(src, dst, LOOKUP, sp, dp);
            case 2 -> decodeLastTwoBytes(src, dst, LOOKUP, sp, dp);
            default -> dp;
        };
    }

    @Override
    protected int getDecodedLength(byte[] src, int pad) {
        int sl = src.length;

        if ((sl & MOD4) != 0)
            throw new IllegalArgumentException("Number of input bytes not divisible by 4");

        return sl/4 * 3 - pad;
    }

    @Override
    protected int getDecodedLengthFast(byte[] src, int pad) {
        return getDecodedLength(src, pad);
    }
}
