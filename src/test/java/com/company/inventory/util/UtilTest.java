package com.company.inventory.util;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class UtilTest {

    @Test
    void compressAndDecompress_success() {

        byte[] original =
                "Imagen de prueba para inventario"
                        .getBytes(StandardCharsets.UTF_8);

        byte[] compressed =
                Util.compressZLib(original);

        byte[] decompressed =
                Util.decompressZLib(compressed);

        assertAll(
                () -> assertNotNull(compressed),
                () -> assertTrue(compressed.length > 0),
                () -> assertArrayEquals(
                        original,
                        decompressed)
        );
    }

    @Test
    void compressAndDecompress_emptyArray() {

        byte[] original = new byte[0];

        byte[] compressed =
                Util.compressZLib(original);

        byte[] decompressed =
                Util.decompressZLib(compressed);

        assertAll(
                () -> assertNotNull(compressed),
                () -> assertNotNull(decompressed),
                () -> assertArrayEquals(
                        original,
                        decompressed)
        );
    }

    @Test
    void compressAndDecompress_largeData() {

        byte[] original = new byte[5000];

        for (int i = 0; i < original.length; i++) {
            original[i] = (byte) (i % 251);
        }

        byte[] compressed =
                Util.compressZLib(original);

        byte[] decompressed =
                Util.decompressZLib(compressed);

        assertArrayEquals(
                original,
                decompressed);
    }

    @Test
    void decompress_invalidData_returnsEmptyResult() {

        byte[] invalidData =
                new byte[]{1, 2, 3, 4, 5, 6};

        byte[] result =
                Util.decompressZLib(invalidData);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(0, result.length)
        );
    }
}