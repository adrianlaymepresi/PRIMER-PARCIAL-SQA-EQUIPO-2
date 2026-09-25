package com.company.inventory.util;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class CategoryExcelExporterBugTest {

    @Test
    void export_nullList_shouldNotCrash() {
        // Given
        CategoryExcelExporter exporter =
                new CategoryExcelExporter(null);

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        // When + Then
        assertDoesNotThrow(
                () -> exporter.export(response),
                "BUG: una lista null de categorías provoca NullPointerException"
        );
    }
}
