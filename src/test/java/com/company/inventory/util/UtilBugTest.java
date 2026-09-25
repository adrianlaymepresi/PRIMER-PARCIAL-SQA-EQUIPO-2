package com.company.inventory.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class UtilBugTest {

    @Test
    void decompress_corruptedData_shouldSignalFailure() {
        // Given
        byte[] corrupted =
                new byte[]{1, 2, 3, 4, 5, 6};

        // When + Then
        assertThrows(
                IllegalArgumentException.class,
                () -> Util.decompressZLib(corrupted),
                "BUG: los datos comprimidos corruptos se silencian y se devuelve un arreglo vacío"
        );
    }
}
