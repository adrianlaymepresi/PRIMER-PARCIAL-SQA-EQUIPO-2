package com.company.inventory.util;

import com.company.inventory.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class ProductExcelExporterBugTest {

    @Test
    void export_nullList_shouldNotCrash() {
        // Given
        ProductExcelExporter exporter =
                new ProductExcelExporter(null);

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        // When + Then
        assertDoesNotThrow(
                () -> exporter.export(response),
                "BUG: una lista null de productos provoca NullPointerException"
        );
    }

    @Test
    void export_productWithoutCategory_shouldNotCrash() {
        // Given
        Product product = new Product();
        product.setId(1L);
        product.setName("Producto sin categoría");
        product.setPrice(10);
        product.setAccount(5);
        product.setCategory(null);

        ProductExcelExporter exporter =
                new ProductExcelExporter(List.of(product));

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        // When + Then
        assertDoesNotThrow(
                () -> exporter.export(response),
                "BUG: un producto sin categoría provoca NullPointerException durante la exportación"
        );
    }
}
