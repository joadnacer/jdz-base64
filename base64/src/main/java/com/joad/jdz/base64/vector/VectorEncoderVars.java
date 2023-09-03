package com.joad.jdz.base64.vector;

import static com.joad.jdz.base64.vector.VectorSpeciesSelection.*;

import jdk.incubator.vector.ByteVector;

 class VectorEncoderVars {
    static final ByteVector SHIFT = ByteVector.fromArray(BYTE_SPECIES, 
                                                    new byte[] {'A', '0' - 52, '0' - 52, '0' - 52, '0' - 52, '0' - 52,
                                                    '0' - 52, '0' - 52, '0' - 52, '0' - 52, '0' - 52, '+' - 62,
                                                    '/' - 63, 'a' - 26, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 0);

    static final ByteVector SHIFT_URL  = ByteVector.fromArray(BYTE_SPECIES, 
                                                    new byte[] {'A', '0' - 52, '0' - 52, '0' - 52, '0' - 52, '0' - 52,
                                                    '0' - 52, '0' - 52, '0' - 52, '0' - 52, '0' - 52, '-' - 62,
                                                    '_' - 63, 'a' - 26, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                                                    0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, 0);

    static final int SPECIES_LENGTH = BYTE_SPECIES.length();

    static final int DP_INC = SPECIES_LENGTH;

    static final int SP_INC = DP_INC / 4 * 3;
}
