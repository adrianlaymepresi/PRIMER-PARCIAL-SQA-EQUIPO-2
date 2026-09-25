package com.company.inventory.controller;

import com.company.inventory.model.Category;
import com.company.inventory.model.Product;
import com.company.inventory.respnose.ProductResponseRest;
import com.company.inventory.services.IProductService;
import com.company.inventory.util.Util;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProductRestControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private ProductRestController controller;

    @Mock
    private IProductService productService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    void save_success() throws Exception {

        byte[] original =
                "imagen-producto"
                        .getBytes(StandardCharsets.UTF_8);

        MockMultipartFile picture =
                new MockMultipartFile(
                        "picture",
                        "producto.jpg",
                        "image/jpeg",
                        original
                );

        Product saved =
                createProduct(
                        1L,
                        "Agua",
                        10,
                        20
                );

        when(productService.save(
                any(Product.class),
                eq(1L)))
                .thenReturn(
                        ResponseEntity.ok(
                                responseWith(saved)
                        )
                );

        mockMvc.perform(
                        multipart("/api/v1/products")
                                .file(picture)
                                .param("name", "Agua")
                                .param("price", "10")
                                .param("account", "20")
                                .param("categoryId", "1")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath(
                                "$.product.products[0].name"
                        ).value("Agua")
                );

        ArgumentCaptor<Product> captor =
                ArgumentCaptor.forClass(Product.class);

        verify(productService)
                .save(
                        captor.capture(),
                        eq(1L)
                );

        Product sent = captor.getValue();

        assertAll(
                () -> assertEquals(
                        "Agua",
                        sent.getName()
                ),
                () -> assertEquals(
                        10,
                        sent.getPrice()
                ),
                () -> assertEquals(
                        20,
                        sent.getAccount()
                ),
                () -> assertArrayEquals(
                        original,
                        Util.decompressZLib(
                                sent.getPicture()
                        )
                )
        );
    }

    @Test
    void searchById_success() throws Exception {

        Product product =
                createProduct(
                        1L,
                        "Agua",
                        10,
                        20
                );

        when(productService.searchById(1L))
                .thenReturn(
                        ResponseEntity.ok(
                                responseWith(product)
                        )
                );

        mockMvc.perform(
                        get("/api/v1/products/{id}", 1L)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath(
                                "$.product.products[0].name"
                        ).value("Agua")
                );

        verify(productService).searchById(1L);
    }

    @Test
    void searchById_error() throws Exception {

        ProductResponseRest body =
                errorResponse(
                        "Producto no encontrado"
                );

        when(productService.searchById(99L))
                .thenReturn(
                        new ResponseEntity<>(
                                body,
                                HttpStatus.NOT_FOUND
                        )
                );

        mockMvc.perform(
                        get("/api/v1/products/{id}", 99L)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.metadata").exists());

        verify(productService).searchById(99L);
    }

    @Test
    void searchByName_success() throws Exception {

        Product product =
                createProduct(
                        1L,
                        "Agua Mineral",
                        12,
                        15
                );

        when(productService.searchByName("agua"))
                .thenReturn(
                        ResponseEntity.ok(
                                responseWith(product)
                        )
                );

        mockMvc.perform(
                        get(
                                "/api/v1/products/filter/{name}",
                                "agua"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath(
                                "$.product.products[0].name"
                        ).value("Agua Mineral")
                );

        verify(productService)
                .searchByName("agua");
    }

    @Test
    void searchByName_error() throws Exception {

        when(productService.searchByName("nada"))
                .thenReturn(
                        new ResponseEntity<>(
                                errorResponse(
                                        "Productos no encontrados"
                                ),
                                HttpStatus.NOT_FOUND
                        )
                );

        mockMvc.perform(
                        get(
                                "/api/v1/products/filter/{name}",
                                "nada"
                        )
                )
                .andExpect(status().isNotFound());

        verify(productService)
                .searchByName("nada");
    }

    @Test
    void search_success() throws Exception {

        Product product1 =
                createProduct(
                        1L,
                        "Agua",
                        10,
                        20
                );

        Product product2 =
                createProduct(
                        2L,
                        "Leche",
                        15,
                        30
                );

        when(productService.search())
                .thenReturn(
                        ResponseEntity.ok(
                                responseWith(
                                        product1,
                                        product2
                                )
                        )
                );

        mockMvc.perform(
                        get("/api/v1/products")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath(
                                "$.product.products.length()"
                        ).value(2)
                );

        verify(productService).search();
    }

    @Test
    void search_error() throws Exception {

        when(productService.search())
                .thenReturn(
                        new ResponseEntity<>(
                                errorResponse(
                                        "Error al buscar productos"
                                ),
                                HttpStatus.INTERNAL_SERVER_ERROR
                        )
                );

        mockMvc.perform(
                        get("/api/v1/products")
                )
                .andExpect(
                        status().isInternalServerError()
                );

        verify(productService).search();
    }

    @Test
    void delete_success() throws Exception {

        when(productService.deleteById(1L))
                .thenReturn(
                        ResponseEntity.ok(
                                new ProductResponseRest()
                        )
                );

        mockMvc.perform(
                        delete("/api/v1/products/{id}", 1L)
                )
                .andExpect(status().isOk());

        verify(productService).deleteById(1L);
    }

    @Test
    void delete_error() throws Exception {

        when(productService.deleteById(1L))
                .thenReturn(
                        new ResponseEntity<>(
                                errorResponse(
                                        "Error al eliminar producto"
                                ),
                                HttpStatus.INTERNAL_SERVER_ERROR
                        )
                );

        mockMvc.perform(
                        delete("/api/v1/products/{id}", 1L)
                )
                .andExpect(
                        status().isInternalServerError()
                );

        verify(productService).deleteById(1L);
    }

    @Test
    void update_success() throws Exception {

        byte[] original =
                "imagen-actualizada"
                        .getBytes(StandardCharsets.UTF_8);

        MockMultipartFile picture =
                new MockMultipartFile(
                        "picture",
                        "nuevo.jpg",
                        "image/jpeg",
                        original
                );

        Product updated =
                createProduct(
                        1L,
                        "Agua Actualizada",
                        25,
                        50
                );

        when(productService.update(
                any(Product.class),
                eq(2L),
                eq(1L)))
                .thenReturn(
                        ResponseEntity.ok(
                                responseWith(updated)
                        )
                );

        MockMultipartHttpServletRequestBuilder request =
                multipart(
                        "/api/v1/products/{id}",
                        1L
                );

        request.with(httpRequest -> {
            httpRequest.setMethod("PUT");
            return httpRequest;
        });

        mockMvc.perform(
                        request
                                .file(picture)
                                .param(
                                        "name",
                                        "Agua Actualizada"
                                )
                                .param("price", "25")
                                .param("account", "50")
                                .param("categoryId", "2")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath(
                                "$.product.products[0].name"
                        ).value("Agua Actualizada")
                );

        ArgumentCaptor<Product> captor =
                ArgumentCaptor.forClass(Product.class);

        verify(productService)
                .update(
                        captor.capture(),
                        eq(2L),
                        eq(1L)
                );

        Product sent = captor.getValue();

        assertArrayEquals(
                original,
                Util.decompressZLib(
                        sent.getPicture()
                )
        );
    }

    @Test
    void update_error() throws Exception {

        MockMultipartFile picture =
                new MockMultipartFile(
                        "picture",
                        "nuevo.jpg",
                        "image/jpeg",
                        new byte[]{1, 2, 3}
                );

        when(productService.update(
                any(Product.class),
                eq(1L),
                eq(99L)))
                .thenReturn(
                        new ResponseEntity<>(
                                errorResponse(
                                        "Producto no actualizado"
                                ),
                                HttpStatus.NOT_FOUND
                        )
                );

        MockMultipartHttpServletRequestBuilder request =
                multipart(
                        "/api/v1/products/{id}",
                        99L
                );

        request.with(httpRequest -> {
            httpRequest.setMethod("PUT");
            return httpRequest;
        });

        mockMvc.perform(
                        request
                                .file(picture)
                                .param("name", "Prueba")
                                .param("price", "10")
                                .param("account", "5")
                                .param("categoryId", "1")
                )
                .andExpect(status().isNotFound());

        verify(productService)
                .update(
                        any(Product.class),
                        eq(1L),
                        eq(99L)
                );
    }

    @Test
    void exportToExcel_success() throws Exception {

        Category category =
                new Category();

        category.setId(1L);
        category.setName("Bebidas");

        Product product =
                createProduct(
                        1L,
                        "Agua",
                        10,
                        20
                );

        product.setCategory(category);

        when(productService.search())
                .thenReturn(
                        ResponseEntity.ok(
                                responseWith(product)
                        )
                );

        mockMvc.perform(
                        get("/api/v1/products/export/excel")
                )
                .andExpect(status().isOk())
                .andExpect(
                        header().string(
                                "Content-Disposition",
                                "attachment; filename=result_product.xlsx"
                        )
                )
                .andExpect(
                        content().contentType(
                                "application/octet-stream"
                        )
                )
                .andExpect(result ->
                        assertTrue(
                                result.getResponse()
                                        .getContentAsByteArray()
                                        .length > 0
                        )
                );

        verify(productService).search();
    }

    private Product createProduct(
            Long id,
            String name,
            int price,
            int account) {

        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setPrice(price);
        product.setAccount(account);

        return product;
    }

    private ProductResponseRest responseWith(
            Product... products) {

        ProductResponseRest response =
                new ProductResponseRest();

        response.getProduct()
                .setProducts(
                        List.of(products)
                );

        response.setMetadata(
                "Respuesta ok",
                "00",
                "Operacion exitosa"
        );

        return response;
    }

    private ProductResponseRest errorResponse(
            String message) {

        ProductResponseRest response =
                new ProductResponseRest();

        response.setMetadata(
                "respuesta nok",
                "-1",
                message
        );

        return response;
    }
}