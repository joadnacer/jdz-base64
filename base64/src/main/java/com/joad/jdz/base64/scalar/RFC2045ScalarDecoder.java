package com.joad.jdz.base64.scalar;

import static com.joad.jdz.base64.scalar.ScalarUtils.*;
import static com.joad.jdz.base64.Base64Tables.*;
import static com.joad.jdz.base64.scalar.ScalarDecoderVars.*;

import java.util.Arrays;
import com.joad.jdz.base64.Base64Decoder;

public class RFC2045ScalarDecoder extends Base64Decoder {
    private static final int[] SIMPLE_LOOKUP = new int[256];

    static {
        Arrays.fill(SIMPLE_LOOKUP, -1);
        for (int i = 0; i < 64; i++)
            SIMPLE_LOOKUP[BASE64[i]] = i;
    }
    
    @Override
    protected int decode(byte[] src, byte[] dst, int sl, int dl) {
        int sp = 0;
        int dp = 0;

        while (dp < dl - 3) { 
            int bits = LOOKUP[0][src[sp++]]
                     | LOOKUP[1][src[sp++]]
                     | LOOKUP[2][src[sp++]]
                     | LOOKUP[3][src[sp++]];

            if (bits > VALID_MAX) {
                sp -= 4;

                bits = LOOKUP[0][src[sp++]];

                if (bits > VALID_MAX)
                    continue;
                
                for (int i = 1; i < 4;) {
                    int b = LOOKUP[i][src[sp++]];
    
                    if (b < VALID_MAX) {
                        bits |= b;
                        i++;
                    }
                }
            }
                
            INT_LE.set(dst, dp, bits);

            dp += 3;
        }

        int remaining = dl - dp;

        return switch (remaining) {
            case 3 -> decodeLastThreeDestBytesMime(src, dst, LOOKUP, sp, dp);
            case 2 -> decodeLastTwoDestBytesMime(src, dst, LOOKUP, sp, dp);
            case 1 -> decodeLastDestByteMime(src, dst, LOOKUP, sp, dp);
            default -> dp;
        };
    }

    @Override
    protected int decodeFast(byte[] src, byte[] dst, int sl, int dl) {
        int sp = 0;
        int dp = 0;

        for (; dp < dl - 57; sp += 78, dp += 57) {
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

        int invalid = 0;

        for (int sp = 0; sp < sl - pad; sp++) {
            if (SIMPLE_LOOKUP[src[sp]] < 0)
                invalid++;
        }

        int vsl = sl - invalid;

        if ((vsl & MOD4) != 0)
            throw new IllegalArgumentException("Number of valid input bytes not divisible by 4");

        return vsl / 4 * 3 - pad;
    }

    @Override
    protected int getDecodedLengthFast(byte[] src, int pad) {
        int sl = src.length;

        int vsl = sl - (sl/78) * 2;

        if ((vsl & MOD4) != 0)
            throw new IllegalArgumentException("Number of valid input bytes not divisible by 4");

        return vsl / 4 * 3 - pad;
    }
}
