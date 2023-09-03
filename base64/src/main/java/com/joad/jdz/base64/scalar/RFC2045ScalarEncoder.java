package com.joad.jdz.base64.scalar;

import static com.joad.jdz.base64.Base64Tables.*;
import static com.joad.jdz.base64.scalar.ScalarUtils.*;

import com.joad.jdz.base64.Base64Encoder;

public class RFC2045ScalarEncoder extends Base64Encoder {

    @Override
    public int encode(byte[] src, byte[] dst) {
        int sl = src.length;

        int sp = 0;
        int dp = 0;

        for (int ldp = 76; ldp < dst.length; ldp += 78) {
            dst[ldp] = '\r';
            dst[ldp+1] = '\n';
        }

        for (; sp < src.length - 57; sp += 57, dp += 78) {
            encodeSixBytes(src, dst, BASE64, sp, dp);
            encodeSixBytes(src, dst, BASE64, sp + 6, dp + 8);
            encodeSixBytes(src, dst, BASE64, sp + 12, dp + 16);
            encodeSixBytes(src, dst, BASE64, sp + 18, dp + 24);
            encodeSixBytes(src, dst, BASE64, sp + 24, dp + 32);
            encodeSixBytes(src, dst, BASE64, sp + 30, dp + 40);
            encodeSixBytes(src, dst, BASE64, sp + 36, dp + 48);
            encodeSixBytes(src, dst, BASE64, sp + 42, dp + 56);
            encodeSixBytes(src, dst, BASE64, sp + 48, dp + 64);
            encodeThreeBytes(src, dst, BASE64, sp + 54, dp + 72);
        }

        for (; sp < src.length - 3; sp += 3, dp += 4) {
            encodeThreeBytes(src, dst, BASE64, sp, dp);
        }

        int remaining = sl - sp;

        return switch (remaining) {
            case 3 -> encodeLastThreeBytes(src, dst, BASE64, sp, dp);
            case 2 -> encodeLastTwoBytes(src, dst, BASE64, sp, dp);
            case 1 -> encodeLastByte(src, dst, BASE64, sp, dp);
            default -> dp;
        };
    }

    @Override
    public int getEncodedLength(byte[] src) {
       int rawLen = Math.multiplyExact(4, (Math.addExact(src.length, 2) / 3));

       return rawLen + ((rawLen - 1)/76) * 2;
    }
}