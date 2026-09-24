package com.company.inventory.controller;

import com.company.inventory.model.Category;
import com.company.inventory.respnose.CategoryResponseRest;
import com.company.inventory.services.ICategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CategoryRestControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private CategoryRestController controller;

    @Mock
    private ICategoryService service;

    private List<Category> list;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        list = new ArrayList<>();

        list.add(createCategory(
                1L,
                "Abarrotes",
                "Distintos tipos de abarrotes"
        ));

        list.add(createCategory(
                2L,
                "Lacteos",
                "Distintos tipos de lacteos"
        ));
    }

    // =========================
    // GET ALL
    // =========================

    @Test
    void searchCategories_success() throws Exception {

        CategoryResponseRest body = responseWith(list);
        body.setMetadata(
                "Respuesta ok",
                "00",
                "Respuesta exitosa"
        );

        when(service.search())
                .thenReturn(
                        new ResponseEntity<>(
                                body,
                                HttpStatus.OK
                        )
                );

        mockMvc.perform(
                        get("/api/v1/categories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryResponse").exists())
                .andExpect(
                        jsonPath(
                                "$.categoryResponse.category.length()"
                        ).value(2)
                )
                .andExpect(
                        jsonPath(
                                "$.categoryResponse.category[0].name"
                        ).value("Abarrotes")
                );

        verify(service, times(1)).search();
    }

    @Test
    void searchCategories_error() throws Exception {

        CategoryResponseRest body =
                new CategoryResponseRest();

        body.setMetadata(
                "Respuesta nok",
                "-1",
                "Error al consultar"
        );

        when(service.search())
                .thenReturn(
                        new ResponseEntity<>(
                                body,
                                HttpStatus.INTERNAL_SERVER_ERROR
                        )
                );

        mockMvc.perform(
                        get("/api/v1/categories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.metadata").exists());

        verify(service, times(1)).search();
    }

    // =========================
    // GET BY ID
    // =========================

    @Test
    void searchCategoriesById_success() throws Exception {

        CategoryResponseRest body =
                responseWith(List.of(list.get(0)));

        body.setMetadata(
                "Respuesta ok",
                "00",
                "Categoria encontrada"
        );

        when(service.searchById(1L))
                .thenReturn(
                        new ResponseEntity<>(
                                body,
                                HttpStatus.OK
                        )
                );

        mockMvc.perform(
                        get("/api/v1/categories/{id}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath(
                                "$.categoryResponse.category[0].name"
                        ).value("Abarrotes")
                );

        verify(service).searchById(1L);
    }

    @Test
    void searchCategoriesById_notFound() throws Exception {

        CategoryResponseRest body =
                new CategoryResponseRest();

        body.setMetadata(
                "Respuesta nok",
                "-1",
                "Categoria no encontrada"
        );

        when(service.searchById(99L))
                .thenReturn(
                        new ResponseEntity<>(
                                body,
                                HttpStatus.NOT_FOUND
                        )
                );

        mockMvc.perform(
                        get("/api/v1/categories/{id}", 99L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.metadata").exists());

        verify(service).searchById(99L);
    }

    // =========================
    // SAVE
    // =========================

    @Test
    void save_success() throws Exception {

        Category category =
                createCategory(
                        3L,
                        "Bebidas",
                        "Distintos tipos de bebidas"
                );

        CategoryResponseRest body =
                responseWith(List.of(category));

        body.setMetadata(
                "Respuesta ok",
                "00",
                "Categoria guardada"
        );

        when(service.save(category))
                .thenReturn(
                        new ResponseEntity<>(
                                body,
                                HttpStatus.OK
                        )
                );

        mockMvc.perform(
                        post("/api/v1/categories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                category
                                        )
                                )
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath(
                                "$.categoryResponse.category[0].name"
                        ).value("Bebidas")
                );

        verify(service).save(category);
    }

    @Test
    void save_error() throws Exception {

        Category category =
                createCategory(
                        3L,
                        "Bebidas",
                        "Distintos tipos de bebidas"
                );

        CategoryResponseRest body =
                new CategoryResponseRest();

        body.setMetadata(
                "Respuesta nok",
                "-1",
                "Categoria no guardada"
        );

        when(service.save(category))
                .thenReturn(
                        new ResponseEntity<>(
                                body,
                                HttpStatus.BAD_REQUEST
                        )
                );

        mockMvc.perform(
                        post("/api/v1/categories")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                category
                                        )
                                )
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.metadata").exists());

        verify(service).save(category);
    }

    // =========================
    // UPDATE
    // =========================

    @Test
    void update_success() throws Exception {

        Category category =
                createCategory(
                        1L,
                        "Abarrotes Actualizado",
                        "Descripcion actualizada"
                );

        CategoryResponseRest body =
                responseWith(List.of(category));

        body.setMetadata(
                "Respuesta ok",
                "00",
                "Categoria actualizada"
        );

        when(service.update(category, 1L))
                .thenReturn(
                        new ResponseEntity<>(
                                body,
                                HttpStatus.OK
                        )
                );

        mockMvc.perform(
                        put("/api/v1/categories/{id}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                category
                                        )
                                )
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath(
                                "$.categoryResponse.category[0].name"
                        ).value("Abarrotes Actualizado")
                );

        verify(service).update(category, 1L);
    }

    @Test
    void update_error() throws Exception {

        Category category =
                createCategory(
                        1L,
                        "Actualizada",
                        "Descripcion"
                );

        CategoryResponseRest body =
                new CategoryResponseRest();

        body.setMetadata(
                "Respuesta nok",
                "-1",
                "Error al actualizar"
        );

        when(service.update(category, 1L))
                .thenReturn(
                        new ResponseEntity<>(
                                body,
                                HttpStatus.INTERNAL_SERVER_ERROR
                        )
                );

        mockMvc.perform(
                        put("/api/v1/categories/{id}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                        objectMapper.writeValueAsString(
                                                category
                                        )
                                )
                )
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.metadata").exists());

        verify(service).update(category, 1L);
    }

    // =========================
    // DELETE
    // =========================

    @Test
    void delete_success() throws Exception {

        CategoryResponseRest body =
                new CategoryResponseRest();

        body.setMetadata(
                "respuesta ok",
                "00",
                "Registro eliminado"
        );

        when(service.deleteById(1L))
                .thenReturn(
                        new ResponseEntity<>(
                                body,
                                HttpStatus.OK
                        )
                );

        mockMvc.perform(
                        delete("/api/v1/categories/{id}", 1L)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metadata").exists());

        verify(service).deleteById(1L);
    }

    @Test
    void delete_error() throws Exception {

        CategoryResponseRest body =
                new CategoryResponseRest();

        body.setMetadata(
                "Respuesta nok",
                "-1",
                "Error al eliminar"
        );

        when(service.deleteById(1L))
                .thenReturn(
                        new ResponseEntity<>(
                                body,
                                HttpStatus.INTERNAL_SERVER_ERROR
                        )
                );

        mockMvc.perform(
                        delete("/api/v1/categories/{id}", 1L)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isInternalServerError());

        verify(service).deleteById(1L);
    }

    // =========================
    // EXPORT EXCEL
    // =========================

    @Test
    void exportToExcel_success() throws Exception {

        CategoryResponseRest body =
                responseWith(list);

        when(service.search())
                .thenReturn(
                        new ResponseEntity<>(
                                body,
                                HttpStatus.OK
                        )
                );

        mockMvc.perform(
                        get("/api/v1/categories/export/excel")
                )
                .andExpect(status().isOk())
                .andExpect(
                        header().string(
                                "Content-Disposition",
                                "attachment; filename=result_category.xlsx"
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

        verify(service).search();
    }

    private Category createCategory(
            Long id,
            String name,
            String description) {

        Category category = new Category();
        category.setId(id);
        category.setName(name);
        category.setDescription(description);

        return category;
    }

    private CategoryResponseRest responseWith(
            List<Category> categories) {

        CategoryResponseRest response =
                new CategoryResponseRest();

        response.getCategoryResponse()
                .setCategory(categories);

        return response;
    }
}