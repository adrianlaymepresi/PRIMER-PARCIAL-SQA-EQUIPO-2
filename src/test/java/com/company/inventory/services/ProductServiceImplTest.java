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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductServiceImplTest {

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
        category.setDescription("Categoria de bebidas");
    }

    // =========================
    // SAVE
    // =========================

    @Test
    void save_success() {

        Product product = createProduct(
                null,
                "Agua",
                10,
                20,
                null,
                compressedPicture("imagen-agua"));

        when(categoryDao.findById(1L))
                .thenReturn(Optional.of(category));

        when(productDao.save(product))
                .thenReturn(product);

        ResponseEntity<ProductResponseRest> response =
                service.save(product, 1L);

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertSame(category, product.getCategory()),
                () -> assertEquals("Agua",
                        response.getBody()
                                .getProduct()
                                .getProducts()
                                .get(0)
                                .getName())
        );

        verify(categoryDao).findById(1L);
        verify(productDao).save(product);
    }

    @Test
    void save_categoryNotFound() {

        Product product = createProduct(
                null, "Agua", 10, 20, null,
                compressedPicture("imagen"));

        when(categoryDao.findById(99L))
                .thenReturn(Optional.empty());

        ResponseEntity<ProductResponseRest> response =
                service.save(product, 99L);

        assertAll(
                () -> assertEquals(
                        HttpStatus.NOT_FOUND,
                        response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals("respuesta nok",
                        response.getBody()
                                .getMetadata()
                                .get(0)
                                .get("type"))
        );

        verify(categoryDao).findById(99L);
        verify(productDao, never()).save(any());
    }

    @Test
    void save_productNotSaved() {

        Product product = createProduct(
                null, "Agua", 10, 20, null,
                compressedPicture("imagen"));

        when(categoryDao.findById(1L))
                .thenReturn(Optional.of(category));

        when(productDao.save(product))
                .thenReturn(null);

        ResponseEntity<ProductResponseRest> response =
                service.save(product, 1L);

        assertAll(
                () -> assertEquals(
                        HttpStatus.BAD_REQUEST,
                        response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals("respuesta nok",
                        response.getBody()
                                .getMetadata()
                                .get(0)
                                .get("type"))
        );

        verify(categoryDao).findById(1L);
        verify(productDao).save(product);
    }

    @Test
    void save_exception() {

        Product product = createProduct(
                null, "Agua", 10, 20, null,
                compressedPicture("imagen"));

        when(categoryDao.findById(1L))
                .thenThrow(new RuntimeException("Error"));

        ResponseEntity<ProductResponseRest> response =
                service.save(product, 1L);

        assertAll(
                () -> assertEquals(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals("respuesta nok",
                        response.getBody()
                                .getMetadata()
                                .get(0)
                                .get("type"))
        );

        verify(categoryDao).findById(1L);
    }

    // =========================
    // SEARCH BY ID
    // =========================

    @Test
    void searchById_success() {

        byte[] original =
                "imagen-original".getBytes(StandardCharsets.UTF_8);

        Product product = createProduct(
                1L,
                "Agua",
                10,
                20,
                category,
                Util.compressZLib(original));

        when(productDao.findById(1L))
                .thenReturn(Optional.of(product));

        ResponseEntity<ProductResponseRest> response =
                service.searchById(1L);

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals("Agua",
                        response.getBody()
                                .getProduct()
                                .getProducts()
                                .get(0)
                                .getName()),
                () -> assertArrayEquals(
                        original,
                        response.getBody()
                                .getProduct()
                                .getProducts()
                                .get(0)
                                .getPicture())
        );

        verify(productDao).findById(1L);
    }

    @Test
    void searchById_notFound() {

        when(productDao.findById(99L))
                .thenReturn(Optional.empty());

        ResponseEntity<ProductResponseRest> response =
                service.searchById(99L);

        assertAll(
                () -> assertEquals(
                        HttpStatus.NOT_FOUND,
                        response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals("respuesta nok",
                        response.getBody()
                                .getMetadata()
                                .get(0)
                                .get("type"))
        );

        verify(productDao).findById(99L);
    }

    @Test
    void searchById_exception() {

        when(productDao.findById(1L))
                .thenThrow(new RuntimeException("Error"));

        ResponseEntity<ProductResponseRest> response =
                service.searchById(1L);

        assertAll(
                () -> assertEquals(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals("respuesta nok",
                        response.getBody()
                                .getMetadata()
                                .get(0)
                                .get("type"))
        );

        verify(productDao).findById(1L);
    }

    // =========================
    // SEARCH BY NAME
    // =========================

    @Test
    void searchByName_success() {

        byte[] original1 =
                "imagen-1".getBytes(StandardCharsets.UTF_8);

        byte[] original2 =
                "imagen-2".getBytes(StandardCharsets.UTF_8);

        Product product1 = createProduct(
                1L, "Agua", 10, 20, category,
                Util.compressZLib(original1));

        Product product2 = createProduct(
                2L, "Agua Mineral", 12, 15, category,
                Util.compressZLib(original2));

        when(productDao.findByNameContainingIgnoreCase("agua"))
                .thenReturn(List.of(product1, product2));

        ResponseEntity<ProductResponseRest> response =
                service.searchByName("agua");

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals(2,
                        response.getBody()
                                .getProduct()
                                .getProducts()
                                .size()),
                () -> assertArrayEquals(
                        original1,
                        response.getBody()
                                .getProduct()
                                .getProducts()
                                .get(0)
                                .getPicture())
        );

        verify(productDao)
                .findByNameContainingIgnoreCase("agua");
    }

    @Test
    void searchByName_noResults() {

        when(productDao.findByNameContainingIgnoreCase("inexistente"))
                .thenReturn(List.of());

        ResponseEntity<ProductResponseRest> response =
                service.searchByName("inexistente");

        assertAll(
                () -> assertEquals(
                        HttpStatus.NOT_FOUND,
                        response.getStatusCode()),
                () -> assertNotNull(response.getBody())
        );

        verify(productDao)
                .findByNameContainingIgnoreCase("inexistente");
    }

    @Test
    void searchByName_exception() {

        when(productDao.findByNameContainingIgnoreCase("agua"))
                .thenThrow(new RuntimeException("Error"));

        ResponseEntity<ProductResponseRest> response =
                service.searchByName("agua");

        assertEquals(
                HttpStatus.INTERNAL_SERVER_ERROR,
                response.getStatusCode());

        verify(productDao)
                .findByNameContainingIgnoreCase("agua");
    }

    // =========================
    // DELETE
    // =========================

    @Test
    void deleteById_success() {

        ResponseEntity<ProductResponseRest> response =
                service.deleteById(1L);

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals("Respuesta ok",
                        response.getBody()
                                .getMetadata()
                                .get(0)
                                .get("type"))
        );

        verify(productDao).deleteById(1L);
    }

    @Test
    void deleteById_exception() {

        doThrow(new RuntimeException("Error"))
                .when(productDao)
                .deleteById(1L);

        ResponseEntity<ProductResponseRest> response =
                service.deleteById(1L);

        assertEquals(
                HttpStatus.INTERNAL_SERVER_ERROR,
                response.getStatusCode());

        verify(productDao).deleteById(1L);
    }

    // =========================
    // SEARCH ALL
    // =========================

    @Test
    void search_success() {

        byte[] original =
                "imagen-producto".getBytes(StandardCharsets.UTF_8);

        Product product = createProduct(
                1L,
                "Agua",
                10,
                20,
                category,
                Util.compressZLib(original));

        when(productDao.findAll())
                .thenReturn(List.of(product));

        ResponseEntity<ProductResponseRest> response =
                service.search();

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals(1,
                        response.getBody()
                                .getProduct()
                                .getProducts()
                                .size()),
                () -> assertArrayEquals(
                        original,
                        response.getBody()
                                .getProduct()
                                .getProducts()
                                .get(0)
                                .getPicture())
        );

        verify(productDao).findAll();
    }

    @Test
    void search_noProducts() {

        when(productDao.findAll())
                .thenReturn(List.of());

        ResponseEntity<ProductResponseRest> response =
                service.search();

        assertEquals(
                HttpStatus.NOT_FOUND,
                response.getStatusCode());

        verify(productDao).findAll();
    }

    @Test
    void search_exception() {

        when(productDao.findAll())
                .thenThrow(new RuntimeException("Error"));

        ResponseEntity<ProductResponseRest> response =
                service.search();

        assertEquals(
                HttpStatus.INTERNAL_SERVER_ERROR,
                response.getStatusCode());

        verify(productDao).findAll();
    }

    // =========================
    // UPDATE
    // =========================

    @Test
    void update_success() {

        Category newCategory = new Category();
        newCategory.setId(2L);
        newCategory.setName("Nueva categoria");

        Product existing = createProduct(
                1L,
                "Anterior",
                5,
                2,
                category,
                compressedPicture("anterior"));

        Product incoming = createProduct(
                null,
                "Actualizado",
                50,
                100,
                null,
                compressedPicture("nueva"));

        when(categoryDao.findById(2L))
                .thenReturn(Optional.of(newCategory));

        when(productDao.findById(1L))
                .thenReturn(Optional.of(existing));

        when(productDao.save(existing))
                .thenReturn(existing);

        ResponseEntity<ProductResponseRest> response =
                service.update(incoming, 2L, 1L);

        Product result =
                response.getBody()
                        .getProduct()
                        .getProducts()
                        .get(0);

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertEquals("Actualizado", result.getName()),
                () -> assertEquals(50, result.getPrice()),
                () -> assertEquals(100, result.getAccount()),
                () -> assertSame(newCategory, result.getCategory()),
                () -> assertArrayEquals(
                        incoming.getPicture(),
                        result.getPicture())
        );

        verify(categoryDao).findById(2L);
        verify(productDao).findById(1L);
        verify(productDao).save(existing);
    }

    @Test
    void update_categoryNotFound() {

        Product incoming = createProduct(
                null, "Actualizado", 50, 100,
                null, compressedPicture("imagen"));

        when(categoryDao.findById(99L))
                .thenReturn(Optional.empty());

        ResponseEntity<ProductResponseRest> response =
                service.update(incoming, 99L, 1L);

        assertEquals(
                HttpStatus.NOT_FOUND,
                response.getStatusCode());

        verify(categoryDao).findById(99L);
        verify(productDao, never()).findById(anyLong());
        verify(productDao, never()).save(any());
    }

    @Test
    void update_productNotFound() {

        Product incoming = createProduct(
                null, "Actualizado", 50, 100,
                null, compressedPicture("imagen"));

        when(categoryDao.findById(1L))
                .thenReturn(Optional.of(category));

        when(productDao.findById(99L))
                .thenReturn(Optional.empty());

        ResponseEntity<ProductResponseRest> response =
                service.update(incoming, 1L, 99L);

        assertEquals(
                HttpStatus.NOT_FOUND,
                response.getStatusCode());

        verify(categoryDao).findById(1L);
        verify(productDao).findById(99L);
        verify(productDao, never()).save(any());
    }

    @Test
    void update_saveReturnsNull() {

        Product existing = createProduct(
                1L, "Anterior", 5, 2,
                category, compressedPicture("anterior"));

        Product incoming = createProduct(
                null, "Actualizado", 50, 100,
                null, compressedPicture("nuevo"));

        when(categoryDao.findById(1L))
                .thenReturn(Optional.of(category));

        when(productDao.findById(1L))
                .thenReturn(Optional.of(existing));

        when(productDao.save(existing))
                .thenReturn(null);

        ResponseEntity<ProductResponseRest> response =
                service.update(incoming, 1L, 1L);

        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode());

        verify(productDao).save(existing);
    }

    @Test
    void update_exception() {

        Product incoming = createProduct(
                null, "Actualizado", 50, 100,
                null, compressedPicture("nuevo"));

        when(categoryDao.findById(1L))
                .thenThrow(new RuntimeException("Error"));

        ResponseEntity<ProductResponseRest> response =
                service.update(incoming, 1L, 1L);

        assertEquals(
                HttpStatus.INTERNAL_SERVER_ERROR,
                response.getStatusCode());

        verify(categoryDao).findById(1L);
    }

    private Product createProduct(
            Long id,
            String name,
            int price,
            int account,
            Category category,
            byte[] picture) {

        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setPrice(price);
        product.setAccount(account);
        product.setCategory(category);
        product.setPicture(picture);

        return product;
    }

    private byte[] compressedPicture(String value) {
        return Util.compressZLib(
                value.getBytes(StandardCharsets.UTF_8));
    }
}