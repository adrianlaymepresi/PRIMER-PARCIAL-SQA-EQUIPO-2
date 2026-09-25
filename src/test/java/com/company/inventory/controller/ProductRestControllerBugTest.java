package com.company.inventory.controller;

import com.company.inventory.model.Product;
import com.company.inventory.respnose.ProductResponseRest;
import com.company.inventory.services.IProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProductRestControllerBugTest {

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
    void save_blankName_shouldReturnBadRequest() throws Exception {
        // Given
        MockMultipartFile picture =
                new MockMultipartFile(
                        "picture",
                        "producto.jpg",
                        "image/jpeg",
                        "imagen".getBytes(StandardCharsets.UTF_8)
                );

        when(productService.save(any(Product.class), eq(1L)))
                .thenReturn(ResponseEntity.ok(successResponse()));

        // When + Then
        mockMvc.perform(
                        multipart("/api/v1/products")
                                .file(picture)
                                .param("name", "")
                                .param("price", "10")
                                .param("account", "20")
                                .param("categoryId", "1")
                )
                .andExpect(
                        status().isBadRequest()
                );
    }

    @Test
    void save_emptyPicture_shouldReturnBadRequest() throws Exception {
        // Given
        MockMultipartFile picture =
                new MockMultipartFile(
                        "picture",
                        "vacio.jpg",
                        "image/jpeg",
                        new byte[0]
                );

        when(productService.save(any(Product.class), eq(1L)))
                .thenReturn(ResponseEntity.ok(successResponse()));

        // When + Then
        mockMvc.perform(
                        multipart("/api/v1/products")
                                .file(picture)
                                .param("name", "Agua")
                                .param("price", "10")
                                .param("account", "20")
                                .param("categoryId", "1")
                )
                .andExpect(
                        status().isBadRequest()
                );
    }

    @Test
    void save_nonImageFile_shouldReturnBadRequest() throws Exception {
        // Given
        MockMultipartFile picture =
                new MockMultipartFile(
                        "picture",
                        "archivo.txt",
                        "text/plain",
                        "esto no es una imagen"
                                .getBytes(StandardCharsets.UTF_8)
                );

        when(productService.save(any(Product.class), eq(1L)))
                .thenReturn(ResponseEntity.ok(successResponse()));

        // When + Then
        mockMvc.perform(
                        multipart("/api/v1/products")
                                .file(picture)
                                .param("name", "Agua")
                                .param("price", "10")
                                .param("account", "20")
                                .param("categoryId", "1")
                )
                .andExpect(
                        status().isBadRequest()
                );
    }

    private ProductResponseRest successResponse() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Agua");
        product.setPrice(10);
        product.setAccount(20);

        ProductResponseRest response = new ProductResponseRest();
        response.getProduct().setProducts(List.of(product));
        response.setMetadata(
                "respuesta ok",
                "00",
                "Producto guardado"
        );

        return response;
    }
}
