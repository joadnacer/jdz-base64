package com.joad.jdz.base64.scalar;

import static com.joad.jdz.base64.Base64Tables.*;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.ByteOrder;
import java.util.Arrays;

class ScalarDecoderVars {
    static final int[][] LOOKUP = new int[4][256];

    static final int[][] LOOKUP_URL = new int[4][256];

    final static VarHandle INT_LE =
        MethodHandles.byteArrayViewVarHandle(int[].class, ByteOrder.LITTLE_ENDIAN);

    static {
        for (int i = 0; i < 4; i++) {
            Arrays.fill(LOOKUP[i], 1 << (24 +  3 - i));
            Arrays.fill(LOOKUP_URL[i], 1 << (24 + 3 - i));
        }
        
        for (int i = 0; i < 64; i++) {
            LOOKUP[0][BASE64[i]] = i << 2;
            LOOKUP[1][BASE64[i]] = (i >> 4) | ((i & 0x0f) << 12);
            LOOKUP[2][BASE64[i]] = ((i & 0x3) << 22) | ((i & 0x3c) << 6);
            LOOKUP[3][BASE64[i]] = i << 16;

            LOOKUP_URL[0][BASE64_URL[i]] = i << 2;
            LOOKUP_URL[1][BASE64_URL[i]] = (i >> 4) | ((i & 0x0f) << 12);
            LOOKUP_URL[2][BASE64_URL[i]] = ((i & 0x3) << 22) | ((i & 0x3c) << 6);
            LOOKUP_URL[3][BASE64_URL[i]] = i << 16;
        }
    }
    
    static final int VALID_MAX = (1 << 24) - 1;

    static final int MOD4 = 3;
}