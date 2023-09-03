package com.joad.jdz.base64.vector;

import static com.joad.jdz.base64.Base64Tables.*;
import static com.joad.jdz.base64.vector.ScalarUtils.*;
import static com.joad.jdz.base64.vector.VectorUtils.*;
import static com.joad.jdz.base64.vector.VectorDecoderVars.*;

import java.util.Arrays;
import com.joad.jdz.base64.Base64Decoder;

public class RFC2045VectorDecoder extends Base64Decoder {
    private static final int[] SIMPLE_LOOKUP = new int[256];

    static {
        Arrays.fill(SIMPLE_LOOKUP, -1);
        for (int i = 0; i < 64; i++)
            SIMPLE_LOOKUP[BASE64[i]] = i;
    }
    
    private static final int NUM_ITERS = 64 / SPECIES_LENGTH + 1;

    @Override
    protected int decode(byte[] src, byte[] dst, int sl, int dl) {
        int sp = 0;
        int dp = 0;

        while (dp < dl - SPECIES_LENGTH) { 
            int invalid = decodeBytesVectorizedMime(src, dst, SHIFT, SLASH_BYTE, SLASH_SWAP, sp, dp);

            if (invalid != SPECIES_LENGTH) {
                int spInc = invalid - (invalid & MOD4);

                sp += spInc;
                dp += spInc / 4 * 3;

                int bits = 0;

                for (int i = 0; i < 4;) {
                    int b = LOOKUP[i][src[sp++]];

                    if (b <= VALID_MAX) {
                        bits |= b;
                        i++;
                    }
                }
                
                INT_LE.set(dst, dp, bits);

                dp += 3;
            }
            else {
                dp += DP_INC;
                sp += SP_INC;
            }
        }

        while (dp < dl - 3) {
            int bits = 0;
                for (int i = 0; i < 4;) {
                    int b = LOOKUP[i][src[sp++]];

                    if (b <= VALID_MAX) {
                        bits |= b;
                        i++;
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

        int end = dl - (NUM_ITERS - 1) * DP_INC - SPECIES_LENGTH;

        for (; dp < end; sp += 78, dp += 57) {
            for (int i = 0; i < NUM_ITERS; i++)
                decodeBytesVectorized(src, dst, SHIFT, SLASH_BYTE, SLASH_SWAP, sp + SP_INC * i, dp + DP_INC * i);
        }

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

        int sp = 0;
        for (; sp < sl - SPECIES_LENGTH; sp += SPECIES_LENGTH) {
            invalid += countInvalidBytesVectorized(src, sp);
        }

        while (sp < sl - pad) {
            if (SIMPLE_LOOKUP[src[sp++]] < 0)
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
