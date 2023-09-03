package com.joad.jdz.base64.vector;

import static com.joad.jdz.base64.Base64Tables.*;
import static com.joad.jdz.base64.vector.ScalarUtils.*;
import static com.joad.jdz.base64.vector.VectorUtils.*;
import static com.joad.jdz.base64.vector.VectorEncoderVars.*;

import com.joad.jdz.base64.Base64Encoder;

public class RFC4648URLVectorEncoder extends Base64Encoder {

    @Override
    public int encode(byte[] src, byte[] dst) {
        int sl = src.length;
        int dp = 0;
        int sp = 0;

        for (; sp < sl - SPECIES_LENGTH; sp += SP_INC, dp += DP_INC) { 
            encodeBytesVectorized(src, dst, SHIFT_URL, sp, dp);
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
    public int getEncodedLength(byte[] src) {
       return Math.multiplyExact(4, (Math.addExact(src.length, 2) / 3));
    }
}
