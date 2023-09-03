package com.joad.jdz.base64.vector;

import java.util.HashMap;
import java.util.Map;
import jdk.incubator.vector.ByteVector;
import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorSpecies;

class VectorSpeciesSelection {
    static final VectorSpecies<Byte> BYTE_SPECIES;

    static final VectorSpecies<Integer> INT_SPECIES;

    static {
        Map<String, VectorSpecies<Byte>> BYTE_SPECIES_MAP = new HashMap<>();

        Map<String, VectorSpecies<Integer>> INT_SPECIES_MAP = new HashMap<>();

        BYTE_SPECIES_MAP.put("128", ByteVector.SPECIES_128);
        BYTE_SPECIES_MAP.put("256", ByteVector.SPECIES_256);
        BYTE_SPECIES_MAP.put("512", ByteVector.SPECIES_512);

        INT_SPECIES_MAP.put("128", IntVector.SPECIES_128);
        INT_SPECIES_MAP.put("256", IntVector.SPECIES_256);
        INT_SPECIES_MAP.put("512", IntVector.SPECIES_512);

        String speciesLength = System.getProperty("jdz.vector.size");

        if (speciesLength != null) {
            if (!BYTE_SPECIES_MAP.containsKey(speciesLength))
                throw new ExceptionInInitializerError(speciesLength + " is not a valid vector size - please pick one of 128, 256 or 512");

            BYTE_SPECIES = BYTE_SPECIES_MAP.get(speciesLength);

            INT_SPECIES = INT_SPECIES_MAP.get(speciesLength);
        }
        else {
            if (ByteVector.SPECIES_PREFERRED.equals(ByteVector.SPECIES_64)) {
                throw new ExceptionInInitializerError("Vectorized base64 encoding/decoding requires 128 or greater bit vector support - system preferred is 64.");
            }

            BYTE_SPECIES = ByteVector.SPECIES_PREFERRED;

            INT_SPECIES = IntVector.SPECIES_PREFERRED;
        }
    }
}
