package com.company.inventory.controller;

import com.company.inventory.model.Category;
import com.company.inventory.respnose.CategoryResponseRest;
import com.company.inventory.services.ICategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CategoryRestControllerBugTest {

    private MockMvc mockMvc;

    @InjectMocks
    private CategoryRestController controller;

    @Mock
    private ICategoryService service;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    void exportToExcel_serviceError_shouldReturnControlled500() throws Exception {
        // Given
        CategoryResponseRest body = new CategoryResponseRest();
        body.setMetadata(
                "Respuesta nok",
                "-1",
                "Error al consultar categorías"
        );

        when(service.search())
                .thenReturn(
                        new ResponseEntity<>(
                                body,
                                HttpStatus.INTERNAL_SERVER_ERROR
                        )
                );

        // When + Then
        // BUG esperado actualmente:
        // el controller intenta exportar aunque el service falló y termina en NPE.
        mockMvc.perform(
                        get("/api/v1/categories/export/excel")
                )
                .andExpect(status().isInternalServerError());

        verify(service).search();
    }
}
