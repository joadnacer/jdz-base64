package com.joad.jdz.base64.vector;

import static com.joad.jdz.base64.vector.VectorDecoderVars.*;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.nio.ByteOrder;

class ScalarUtils {
    private final static VarHandle LONG_BE =
        MethodHandles.byteArrayViewVarHandle(long[].class, ByteOrder.BIG_ENDIAN);
    
    private final static VarHandle LONG_LE =
        MethodHandles.byteArrayViewVarHandle(long[].class, ByteOrder.LITTLE_ENDIAN);
    
    private final static VarHandle INT_BE =
        MethodHandles.byteArrayViewVarHandle(int[].class, ByteOrder.BIG_ENDIAN);
    
    private final static VarHandle INT_LE =
        MethodHandles.byteArrayViewVarHandle(int[].class, ByteOrder.LITTLE_ENDIAN);

    static void encodeSixBytes(byte[] src, byte[] dst, char[] lookup, int sp, int dp) {
        long bits = (long) LONG_BE.get(src, sp);

        dst[dp++] = (byte) lookup[(int) (bits >>> 58) & 0x3f];
        dst[dp++] = (byte) lookup[(int) (bits >>> 52) & 0x3f];
        dst[dp++] = (byte) lookup[(int) (bits >>> 46) & 0x3f];
        dst[dp++] = (byte) lookup[(int) (bits >>> 40) & 0x3f];
        dst[dp++] = (byte) lookup[(int) (bits >>> 34) & 0x3f];
        dst[dp++] = (byte) lookup[(int) (bits >>> 28) & 0x3f];
        dst[dp++] = (byte) lookup[(int) (bits >>> 22) & 0x3f];
        dst[dp]   = (byte) lookup[(int) (bits >>> 16) & 0x3f];
    }

    static void encodeThreeBytes(byte[] src, byte[] dst, char[] lookup, int sp, int dp) {
        int bits = (int) INT_BE.get(src, sp);

        dst[dp++] = (byte) lookup[(bits >>> 26) & 0x3f];
        dst[dp++] = (byte) lookup[(bits >>> 20) & 0x3f];
        dst[dp++] = (byte) lookup[(bits >>> 14) & 0x3f];
        dst[dp]   = (byte) lookup[(bits >>> 8) & 0x3f];
    }

    static void decodeFourBytes(byte[] src, byte[] dst, int[][] lookup, int sp, int dp) {
        int bits = lookup[0][src[sp++]]
                 | lookup[1][src[sp++]]
                 | lookup[2][src[sp++]]
                 | lookup[3][src[sp]];

        INT_LE.set(dst, dp, bits);
    }

    static void decodeFourBytesValidating(byte[] src, byte[] dst, int[][] lookup, int sp, int dp) {
        int osp = sp;

        int bits = lookup[0][src[sp++]]
                 | lookup[1][src[sp++]]
                 | lookup[2][src[sp++]]
                 | lookup[3][src[sp]];

        assertValidBase64(src, bits, osp);

        INT_LE.set(dst, dp, bits);
    }

    static void decodeEightBytes(byte[] src, byte[] dst, int[][] lookup, int sp, int dp) {
        long bits1 = lookup[0][src[sp++]]
                   | lookup[1][src[sp++]]
                   | lookup[2][src[sp++]]
                   | lookup[3][src[sp++]];
        
        long bits2 = lookup[0][src[sp++]]
                   | lookup[1][src[sp++]]
                   | lookup[2][src[sp++]]
                   | lookup[3][src[sp]];

        long bits = bits1 | (bits2 << 24);

        LONG_LE.set(dst, dp, bits);
    }

    static void decodeEightBytesValidating(byte[] src, byte[] dst, int[][] lookup, int sp, int dp) {
        int osp = sp;

        long bits1 = lookup[0][src[sp++]]
                   | lookup[1][src[sp++]]
                   | lookup[2][src[sp++]]
                   | lookup[3][src[sp++]];
        
        long bits2 = lookup[0][src[sp++]]
                   | lookup[1][src[sp++]]
                   | lookup[2][src[sp++]]
                   | lookup[3][src[sp]];

        assertValidBase64(src, (int) bits1, osp);
        assertValidBase64(src, (int) bits2, osp);

        long bits = bits1 | (bits2 << 24);

        LONG_LE.set(dst, dp, bits);
    }

    static int encodeLastByte(byte[] src, byte[] dst, char[] lookup, int sp, int dp) {
        int b0 = src[sp] & 0xff;

        dst[dp++] = (byte) lookup[b0 >> 2];
        dst[dp++] = (byte) lookup[(b0 << 4) & 0x3f];
        dst[dp++] = '=';
        dst[dp++] = '=';

        return dp;
    }
    
    static int encodeLastTwoBytes(byte[] src, byte[] dst, char[] lookup, int sp, int dp) {
        int b0 = src[sp++] & 0xff;
        int b1 = src[sp] & 0xff;

        dst[dp++] = (byte) lookup[b0 >> 2];
        dst[dp++] = (byte) lookup[(b0 << 4) & 0x3f | (b1 >> 4)];
        dst[dp++] = (byte) lookup[(b1 << 2) & 0x3f];
        dst[dp++] = '=';

        return dp;
    }

    static int encodeLastThreeBytes(byte[] src, byte[] dst, char[] lookup, int sp, int dp) {
        int b0 = src[sp++] & 0xff;
        int b1 = src[sp++] & 0xff;
        int b2 = src[sp] & 0xff;

        dst[dp++] = (byte) lookup[b0 >> 2];
        dst[dp++] = (byte) lookup[(b0 << 4) & 0x3f | (b1 >> 4)];
        dst[dp++] = (byte) lookup[(b1 << 2) & 0x3f | (b2 >> 6)];
        dst[dp++] = (byte) lookup[b2 & 0x3f];

        return dp;
    }

    static int decodeLastFourBytes(byte[] src, byte[] dst, int[][] lookup, int sp, int dp) {
        int bits = lookup[0][src[sp++]]
                 | lookup[1][src[sp++]]
                 | lookup[2][src[sp++]]
                 | lookup[3][src[sp]];

        dst[dp++] = (byte) bits;
        dst[dp++] = (byte) (bits >> 8);
        dst[dp++] = (byte) (bits >> 16);

        return dp;
    }
    
    static int decodeLastThreeBytes(byte[] src, byte[] dst, int[][] lookup, int sp, int dp) {
        int bits = lookup[0][src[sp++]]
                 | lookup[1][src[sp++]]
                 | lookup[2][src[sp]];

        dst[dp++] = (byte) bits;
        dst[dp++] = (byte) (bits >> 8);

        return dp;
    }

    static int decodeLastTwoBytes(byte[] src, byte[] dst, int[][] lookup, int sp, int dp) {
        int bits = lookup[0][src[sp++]]
                 | lookup[1][src[sp]];

        dst[dp++] = (byte) bits;
        
        return dp;
    }

    static int decodeLastFourBytesValidating(byte[] src, byte[] dst, int[][] lookup, int sp, int dp) {
        int osp = sp;

        int bits = lookup[0][src[sp++]]
                 | lookup[1][src[sp++]]
                 | lookup[2][src[sp++]]
                 | lookup[3][src[sp]];
        
        assertValidBase64(src, bits, osp);

        dst[dp++] = (byte) bits;
        dst[dp++] = (byte) (bits >> 8);
        dst[dp++] = (byte) (bits >> 16);

        return dp;
    }
    
    static int decodeLastThreeBytesValidating(byte[] src, byte[] dst, int[][] lookup, int sp, int dp) {
        int osp = sp;

        int bits = lookup[0][src[sp++]]
                 | lookup[1][src[sp++]]
                 | lookup[2][src[sp]];

        assertValidBase64(src, bits, osp);

        dst[dp++] = (byte) bits;
        dst[dp++] = (byte) (bits >> 8);

        return dp;
    }

    static int decodeLastTwoBytesValidating(byte[] src, byte[] dst, int[][] lookup, int sp, int dp) {
        int osp = sp;

        int bits = lookup[0][src[sp++]]
                 | lookup[1][src[sp]];

        assertValidBase64(src, bits, osp);

        dst[dp++] = (byte) bits;
        
        return dp;
    }

    static int decodeLastThreeDestBytesMime(byte[] src, byte[] dst, int[][] lookup, int sp, int dp) {
        int bits = 0;
        for (int i = 0; i < 4;) {
            int b = lookup[i][src[sp++]];

            if (b <= VALID_MAX) {
                bits |= b;
                i++;
            }
        }

        dst[dp++] = (byte) bits;
        dst[dp++] = (byte) (bits >> 8);
        dst[dp++] = (byte) (bits >> 16);

        return dp;
    }

    static int decodeLastTwoDestBytesMime(byte[] src, byte[] dst, int[][] lookup, int sp, int dp) {
        int bits = 0;
        for (int i = 0; i < 3;) {
            int b = lookup[i][src[sp++]];

            if (b <= VALID_MAX) {
                bits |= b;
                i++;
            }
        }

        dst[dp++] = (byte) bits;
        dst[dp++] = (byte) (bits >> 8);

        return dp;
    }

    static int decodeLastDestByteMime(byte[] src, byte[] dst, int[][] lookup, int sp, int dp) {
        int bits = 0;
        for (int i = 0; i < 2;) {
            int b = lookup[i][src[sp++]];

            if (b <= VALID_MAX) {
                bits |= b;
                i++;
            }
        }

        dst[dp++] = (byte) bits;

        return dp;
    }

    private static void assertValidBase64(byte[] src, int bits, int sp) {
        if (bits > VALID_MAX) {
            int ip = sp + Integer.numberOfLeadingZeros(bits) - 4;

            throw new IllegalArgumentException("Illegal base64 character " + Integer.toString(src[ip], 16) + " at position " + ip);
        }
    }
}
