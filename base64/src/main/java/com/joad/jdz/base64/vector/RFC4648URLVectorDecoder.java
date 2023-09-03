package com.joad.jdz.base64.vector;

import static com.joad.jdz.base64.vector.ScalarUtils.*;
import static com.joad.jdz.base64.vector.VectorUtils.*;
import static com.joad.jdz.base64.vector.VectorDecoderVars.*;

import com.joad.jdz.base64.Base64Decoder;

public class RFC4648URLVectorDecoder extends Base64Decoder {

    @Override
    protected int decode(byte[] src, byte[] dst, int sl, int dl) {
        int sp = 0;
        int dp = 0;

        for (; dp < dl - SPECIES_LENGTH; sp += SP_INC, dp += DP_INC) { 
            decodeBytesVectorizedValidating(src, dst, SHIFT_URL, MASK_URL, UNDERSCORE_BYTE, UNDERSCORE_SWAP, sp, dp);
        }

        for (; sp < sl - 4; sp += 4, dp += 3) {
            decodeFourBytesValidating(src, dst, LOOKUP_URL, sp, dp);
        }

        int remaining = sl - sp;

        return switch (remaining) {
            case 4 -> decodeLastFourBytesValidating(src, dst, LOOKUP_URL, sp, dp);
            case 3 -> decodeLastThreeBytesValidating(src, dst, LOOKUP_URL, sp, dp);
            case 2 -> decodeLastTwoBytesValidating(src, dst, LOOKUP_URL, sp, dp);
            default -> dp;
        };
    }

    @Override
    protected int decodeFast(byte[] src, byte[] dst, int sl, int dl) {
        int sp = 0;
        int dp = 0;

        for (; dp < dl - SPECIES_LENGTH; sp += SP_INC, dp += DP_INC) { 
            decodeBytesVectorized(src, dst, SHIFT_URL, UNDERSCORE_BYTE, UNDERSCORE_SWAP, sp, dp);
        }

        for (; sp < sl - 4; sp += 4, dp += 3) {
            decodeFourBytes(src, dst, LOOKUP_URL, sp, dp);
        }

        int remaining = sl - sp;

        return switch (remaining) {
            case 4 -> decodeLastFourBytes(src, dst, LOOKUP_URL, sp, dp);
            case 3 -> decodeLastThreeBytes(src, dst, LOOKUP_URL, sp, dp);
            case 2 -> decodeLastTwoBytes(src, dst, LOOKUP_URL, sp, dp);
            default -> dp;
        };
    }

    @Override
    protected int getDecodedLength(byte[] src, int pad) {
        int sl = src.length;

        if ((sl & MOD4) != 0)
            throw new IllegalArgumentException("Number of input bytes not divisible by 4");

        return sl / 4 * 3 - pad;
    }

    @Override
    protected int getDecodedLengthFast(byte[] src, int pad) {
        return getDecodedLength(src, pad);
    }
}