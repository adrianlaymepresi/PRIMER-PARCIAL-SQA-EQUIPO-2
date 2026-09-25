package com.company.inventory.services;

import com.company.inventory.dao.ICategoryDao;
import com.company.inventory.dao.IProductDao;
import com.company.inventory.model.Category;
import com.company.inventory.model.Product;
import com.company.inventory.respnose.ProductResponseRest;
import com.company.inventory.util.Util;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceImplBugTest {

    @Mock
    private ICategoryDao categoryDao;

    @Mock
    private IProductDao productDao;

    @InjectMocks
    private ProductServiceImpl service;

    private Category category;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        category = new Category();
        category.setId(1L);
        category.setName("Bebidas");
        category.setDescription("Categoría de bebidas");
    }

    @Test
    void deleteById_nonexistent_shouldReturnNotFound() {
        // When
        ResponseEntity<ProductResponseRest> response =
                service.deleteById(999L);

        // Then
        assertEquals(
                HttpStatus.NOT_FOUND,
                response.getStatusCode(),
                "BUG: eliminar un producto inexistente devuelve éxito"
        );
    }

    @Test
    void searchById_exception_shouldDescribeSearchOperation() {
        // Given
        when(productDao.findById(1L))
                .thenThrow(new RuntimeException("Fallo de base de datos"));

        // When
        ResponseEntity<ProductResponseRest> response =
                service.searchById(1L);

        String message =
                response.getBody()
                        .getMetadata()
                        .get(0)
                        .get("date");

        // Then
        assertAll(
                () -> assertEquals(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        response.getStatusCode()
                ),
                () -> assertEquals(
                        "Error al buscar producto",
                        message,
                        "BUG: una búsqueda fallida informa 'Error al guardar producto'"
                )
        );
    }

    @Test
    void save_negativePrice_shouldReturnBadRequest() {
        // Given
        Product product = createProduct(
                "Agua",
                -10,
                20
        );

        when(categoryDao.findById(1L))
                .thenReturn(Optional.of(category));

        when(productDao.save(product))
                .thenReturn(product);

        // When
        ResponseEntity<ProductResponseRest> response =
                service.save(product, 1L);

        // Then
        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode(),
                "BUG: el sistema permite registrar un producto con precio negativo"
        );
    }

    @Test
    void save_negativeAccount_shouldReturnBadRequest() {
        // Given
        Product product = createProduct(
                "Agua",
                10,
                -5
        );

        when(categoryDao.findById(1L))
                .thenReturn(Optional.of(category));

        when(productDao.save(product))
                .thenReturn(product);

        // When
        ResponseEntity<ProductResponseRest> response =
                service.save(product, 1L);

        // Then
        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode(),
                "BUG: el sistema permite registrar un producto con cantidad negativa"
        );
    }

    private Product createProduct(
            String name,
            int price,
            int account) {

        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setAccount(account);
        product.setPicture(
                Util.compressZLib(
                        "imagen".getBytes(StandardCharsets.UTF_8)
                )
        );

        return product;
    }
}
