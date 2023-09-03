package com.joad.jdz.base64.vector;

import static com.joad.jdz.base64.vector.VectorSpeciesSelection.*;
import static com.joad.jdz.base64.vector.VectorDecoderVars.*;

import jdk.incubator.vector.ByteVector;
import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.ShortVector;
import jdk.incubator.vector.VectorMask;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorShuffle;

/*
 * The methods in this class are heavily inspired by the following articles by Wojciech Mula:
 * 
 * http://0x80.pl/notesen/2016-01-12-sse-base64-encoding.html
 * http://0x80.pl/notesen/2016-01-17-sse-base64-decoding.html
 */
public class VectorUtils {
    private static final VectorShuffle<Byte> ENC_SHUFFLE = VectorShuffle.fromArray(BYTE_SPECIES, new int[] 
                                        {1, 0, 2, 1, 4, 3, 5, 4, 7, 6, 8, 7, 10, 9, 11, 10, 13, 12, 14, 13,
                                        16, 15, 17, 16, 19, 18, 20, 19, 22, 21, 23, 22, 25, 24, 26, 25, 
                                        28, 27, 29, 28, 31, 30, 32, 31, 34, 33, 35, 34, 37, 36, 38, 37,
                                        40, 39, 41, 40, 43, 42, 44, 43, 46, 45, 47, 46, 49, 48, 50, 49},
                                        0);

    private static final ShortVector AC_MASK = IntVector.broadcast(INT_SPECIES, 0x0fc0fc00).reinterpretAsShorts();

    private static final ShortVector AC_SHIFT = IntVector.broadcast(INT_SPECIES, 0x0006000a).reinterpretAsShorts();

    private static final ShortVector BD_MASK = IntVector.broadcast(INT_SPECIES, 0x003f03f0).reinterpretAsShorts();
    
    private static final ShortVector BD_SHIFT = IntVector.broadcast(INT_SPECIES, 0x00080004).reinterpretAsShorts();

    private static final VectorShuffle<Byte> DEC_PACK = switch (BYTE_SPECIES.length()) {
        case 16 -> VectorShuffle.fromArray(BYTE_SPECIES, new int[] {
                                        2, 1, 0, 6, 5, 4, 10, 9, 8, 14, 13, 12, 0, 0, 0, 0
                                        }, 0);

        case 32 -> VectorShuffle.fromArray(BYTE_SPECIES, new int[] {
                                        2, 1, 0, 6, 5, 4, 10, 9, 8, 14, 13, 12, 18, 17, 16,
                                        22, 21, 20, 26, 25, 24, 30, 29, 28,
                                        0, 0, 0, 0, 0, 0, 0, 0
                                        }, 0);

        case 64 -> VectorShuffle.fromArray(BYTE_SPECIES, new int[] {
                                        2, 1, 0, 6, 5, 4, 10, 9, 8, 14, 13, 12, 18, 17, 16,
                                        22, 21, 20, 26, 25, 24, 30, 29, 28, 34, 33, 32, 38, 37, 36,
                                        42, 41, 40, 46, 45, 44, 50, 49, 48, 54, 53, 52, 58, 57, 56,
                                        62, 61, 60, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
        }, 0);

        default -> throw new ExceptionInInitializerError("Byte species length unsupported");
    };

    private static final ByteVector BIT_POS_LUT = ByteVector.fromArray(BYTE_SPECIES, new byte[] {
                                        0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, (byte) 0x80, 0, 0, 0, 0, 0, 0, 0, 0,
                                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                    }, 0);

    static void encodeBytesVectorized(byte[] src, byte[] dst, ByteVector shiftLookup, int sp, int dp) {
        ByteVector input       = ByteVector.fromArray(BYTE_SPECIES, src, sp);
        ByteVector shuffled    = input.rearrange(ENC_SHUFFLE);
        ShortVector shortShuf  = shuffled.reinterpretAsShorts();

        ShortVector ac = shortShuf.and(AC_MASK);
        ShortVector bd = shortShuf.and(BD_MASK);
        
        ShortVector acShifted = ac.lanewise(VectorOperators.LSHR, AC_SHIFT);
        ShortVector bdShifted = bd.lanewise(VectorOperators.LSHL, BD_SHIFT);

        ByteVector noLookupRes = bdShifted.or(acShifted).reinterpretAsBytes();

        VectorMask<Byte> lt26      = noLookupRes.lt((byte) 26);
        VectorMask<Byte> lt52      = noLookupRes.lt((byte) 52);
        VectorMask<Byte> thirteens = lt52.andNot(lt26);

        ByteVector subbed = noLookupRes.sub((byte) 51)
                                       .lanewise(VectorOperators.AND, 0x00, lt52)
                                       .blend((byte) 13, thirteens);

        ByteVector shifts = subbed.selectFrom(shiftLookup);

        ByteVector res = noLookupRes.add(shifts);

        res.intoArray(dst, dp);
    }

    static void decodeBytesVectorized(byte[] src, byte[] dst, ByteVector shiftLookup, byte swapKey, byte swapVal, int sp, int dp) {
        ByteVector input = ByteVector.fromArray(BYTE_SPECIES, src, sp);

        ByteVector higherNibble = input.lanewise(VectorOperators.LSHR, 4);

        ByteVector sh           = higherNibble.selectFrom(shiftLookup);
        VectorMask<Byte> eqSwap = input.eq(swapKey);
        ByteVector shift        = sh.blend(swapVal, eqSwap);

        IntVector shifted = input.add(shift).reinterpretAsInts();

        IntVector ca = shifted.and(0x003f003f);
        IntVector db = shifted.and(0x3f003f00);

        IntVector t0 = db.lanewise(VectorOperators.LSHR, 8).or(ca.lanewise(VectorOperators.LSHL, 6));
        IntVector t1 = t0.lanewise(VectorOperators.LSHR, 16).or(t0.lanewise(VectorOperators.LSHL, 12));

        ByteVector res = t1.reinterpretAsBytes().rearrange(DEC_PACK);

        res.intoArray(dst, dp);
    }

    static void decodeBytesVectorizedValidating(byte[] src, byte[] dst, ByteVector shiftLookup, ByteVector mask, byte swapKey, byte swapVal, int sp, int dp) {
        ByteVector input = ByteVector.fromArray(BYTE_SPECIES, src, sp);

        ByteVector higherNibble = input.lanewise(VectorOperators.LSHR, 4);
        ByteVector lowerNibble  = input.and((byte) 0x0f);

        ByteVector sh           = higherNibble.selectFrom(shiftLookup);
        VectorMask<Byte> eqSwap = input.eq(swapKey);
        ByteVector shift        = sh.blend(swapVal, eqSwap);

        ByteVector masked       = lowerNibble.selectFrom(mask);
        ByteVector bit          = higherNibble.selectFrom(BIT_POS_LUT);
        ByteVector maskedAndBit = masked.and(bit);

        boolean invalid = maskedAndBit.eq((byte) 0).anyTrue();

        if (invalid) {
            int ip = sp + maskedAndBit.eq((byte) 0).firstTrue();

            throw new IllegalArgumentException("Illegal base64 character " + Integer.toString(src[ip], 16) + " at position " + ip);
        }

        IntVector shifted = input.add(shift).reinterpretAsInts();

        IntVector ca = shifted.and(0x003f003f);
        IntVector db = shifted.and(0x3f003f00);

        IntVector t0 = db.lanewise(VectorOperators.LSHR, 8).or(ca.lanewise(VectorOperators.LSHL, 6));
        IntVector t1 = t0.lanewise(VectorOperators.LSHR, 16).or(t0.lanewise(VectorOperators.LSHL, 12));

        ByteVector res = t1.reinterpretAsBytes().rearrange(DEC_PACK);

        res.intoArray(dst, dp);
    }

    static int decodeBytesVectorizedMime(byte[] src, byte[] dst, ByteVector shiftLookup, byte swapKey, byte swapVal, int sp, int dp) {
        ByteVector input = ByteVector.fromArray(BYTE_SPECIES, src, sp);

        ByteVector higherNibble = input.lanewise(VectorOperators.LSHR, 4);
        ByteVector lowerNibble  = input.and((byte) 0x0f);

        ByteVector sh           = higherNibble.selectFrom(shiftLookup);
        VectorMask<Byte> eqSwap = input.eq(swapKey);
        ByteVector shift        = sh.blend(swapVal, eqSwap);

        ByteVector masked       = lowerNibble.selectFrom(MASK);
        ByteVector bit          = higherNibble.selectFrom(BIT_POS_LUT);
        ByteVector maskedAndBit = masked.and(bit);

        int firstInvalid = maskedAndBit.eq((byte) 0).firstTrue();

        IntVector shifted = input.add(shift).reinterpretAsInts();

        IntVector ca = shifted.and(0x003f003f);
        IntVector db = shifted.and(0x3f003f00);

        IntVector t0 = db.lanewise(VectorOperators.LSHR, 8).or(ca.lanewise(VectorOperators.LSHL, 6));
        IntVector t1 = t0.lanewise(VectorOperators.LSHR, 16).or(t0.lanewise(VectorOperators.LSHL, 12));

        ByteVector res = t1.reinterpretAsBytes().rearrange(DEC_PACK);

        res.intoArray(dst, dp);

        return firstInvalid;
    }

    static int countInvalidBytesVectorized(byte[] src, int sp) {
        ByteVector input = ByteVector.fromArray(BYTE_SPECIES, src, sp);

        ByteVector higherNibble = input.lanewise(VectorOperators.LSHR, 4);
        ByteVector lowerNibble  = input.and((byte) 0x0f);

        ByteVector masked       = lowerNibble.selectFrom(MASK);
        ByteVector bit          = higherNibble.selectFrom(BIT_POS_LUT);
        ByteVector maskedAndBit = masked.and(bit);

        int invalid = maskedAndBit.eq((byte) 0).trueCount();

        return invalid;
    }
}
