package com.company.inventory.services;

import com.company.inventory.dao.ICategoryDao;
import com.company.inventory.model.Category;
import com.company.inventory.respnose.CategoryResponseRest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceImplTest {

    @Mock
    private ICategoryDao categoryDao;

    @InjectMocks
    private CategoryServiceImpl service;

    private List<Category> list;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        list = new ArrayList<>();
        list.add(createCategory(1L, "Abarrotes", "Distintos tipos de abarrotes"));
        list.add(createCategory(2L, "Lacteos", "Distintos tipos de lacteos"));
    }

    // =========================
    // SEARCH
    // =========================

    @Test
    void search_success() {

        // Given
        when(categoryDao.findAll()).thenReturn(list);

        // When
        ResponseEntity<CategoryResponseRest> response = service.search();

        // Then
        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals(2,
                        response.getBody()
                                .getCategoryResponse()
                                .getCategory()
                                .size()),
                () -> assertEquals("Abarrotes",
                        response.getBody()
                                .getCategoryResponse()
                                .getCategory()
                                .get(0)
                                .getName()),
                () -> assertEquals("Respuesta ok",
                        response.getBody()
                                .getMetadata()
                                .get(0)
                                .get("type"))
        );

        verify(categoryDao).findAll();
    }

    @Test
    void search_exception() {

        // Given
        when(categoryDao.findAll())
                .thenThrow(new RuntimeException("Error al consultar"));

        // When
        ResponseEntity<CategoryResponseRest> response = service.search();

        // Then
        assertAll(
                () -> assertEquals(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals("Respuesta nok",
                        response.getBody()
                                .getMetadata()
                                .get(0)
                                .get("type"))
        );

        verify(categoryDao).findAll();
    }

    // =========================
    // SEARCH BY ID
    // =========================

    @Test
    void searchById_success() {

        // Given
        Category category = list.get(0);

        when(categoryDao.findById(1L))
                .thenReturn(Optional.of(category));

        // When
        ResponseEntity<CategoryResponseRest> response =
                service.searchById(1L);

        // Then
        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals(1,
                        response.getBody()
                                .getCategoryResponse()
                                .getCategory()
                                .size()),
                () -> assertEquals("Abarrotes",
                        response.getBody()
                                .getCategoryResponse()
                                .getCategory()
                                .get(0)
                                .getName())
        );

        verify(categoryDao).findById(1L);
    }

    @Test
    void searchById_notFound() {

        // Given
        when(categoryDao.findById(99L))
                .thenReturn(Optional.empty());

        // When
        ResponseEntity<CategoryResponseRest> response =
                service.searchById(99L);

        // Then
        assertAll(
                () -> assertEquals(
                        HttpStatus.NOT_FOUND,
                        response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals("Respuesta nok",
                        response.getBody()
                                .getMetadata()
                                .get(0)
                                .get("type"))
        );

        verify(categoryDao).findById(99L);
    }

    @Test
    void searchById_exception() {

        // Given
        when(categoryDao.findById(1L))
                .thenThrow(new RuntimeException("Error de consulta"));

        // When
        ResponseEntity<CategoryResponseRest> response =
                service.searchById(1L);

        // Then
        assertAll(
                () -> assertEquals(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals("Respuesta nok",
                        response.getBody()
                                .getMetadata()
                                .get(0)
                                .get("type"))
        );

        verify(categoryDao).findById(1L);
    }

    // =========================
    // SAVE
    // =========================

    @Test
    void save_success() {

        // Given
        Category category =
                createCategory(3L, "Bebidas", "Distintos tipos de bebidas");

        when(categoryDao.save(category))
                .thenReturn(category);

        // When
        ResponseEntity<CategoryResponseRest> response =
                service.save(category);

        // Then
        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals("Bebidas",
                        response.getBody()
                                .getCategoryResponse()
                                .getCategory()
                                .get(0)
                                .getName())
        );

        verify(categoryDao).save(category);
    }

    @Test
    void save_error() {

        // Given
        Category category =
                createCategory(3L, "Bebidas", "Distintos tipos de bebidas");

        when(categoryDao.save(category))
                .thenReturn(null);

        // When
        ResponseEntity<CategoryResponseRest> response =
                service.save(category);

        // Then
        assertAll(
                () -> assertEquals(
                        HttpStatus.BAD_REQUEST,
                        response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals("Respuesta nok",
                        response.getBody()
                                .getMetadata()
                                .get(0)
                                .get("type"))
        );

        verify(categoryDao).save(category);
    }

    @Test
    void save_exception() {

        // Given
        Category category =
                createCategory(3L, "Bebidas", "Distintos tipos de bebidas");

        when(categoryDao.save(category))
                .thenThrow(new RuntimeException("Error al guardar"));

        // When
        ResponseEntity<CategoryResponseRest> response =
                service.save(category);

        // Then
        assertAll(
                () -> assertEquals(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals("Respuesta nok",
                        response.getBody()
                                .getMetadata()
                                .get(0)
                                .get("type"))
        );

        verify(categoryDao).save(category);
    }

    // =========================
    // UPDATE
    // =========================

    @Test
    void update_success() {

        // Given
        Category existing =
                createCategory(1L, "Anterior", "Descripcion anterior");

        Category incoming =
                createCategory(null, "Actualizada", "Nueva descripcion");

        when(categoryDao.findById(1L))
                .thenReturn(Optional.of(existing));

        when(categoryDao.save(any(Category.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        ResponseEntity<CategoryResponseRest> response =
                service.update(incoming, 1L);

        // Then
        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals("Actualizada",
                        response.getBody()
                                .getCategoryResponse()
                                .getCategory()
                                .get(0)
                                .getName()),
                () -> assertEquals("Nueva descripcion",
                        response.getBody()
                                .getCategoryResponse()
                                .getCategory()
                                .get(0)
                                .getDescription())
        );

        verify(categoryDao).findById(1L);
        verify(categoryDao).save(existing);
    }

    @Test
    void update_categoryNotFound() {

        // Given
        Category incoming =
                createCategory(null, "Actualizada", "Nueva descripcion");

        when(categoryDao.findById(99L))
                .thenReturn(Optional.empty());

        // When
        ResponseEntity<CategoryResponseRest> response =
                service.update(incoming, 99L);

        // Then
        assertAll(
                () -> assertEquals(
                        HttpStatus.NOT_FOUND,
                        response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals("Respuesta nok",
                        response.getBody()
                                .getMetadata()
                                .get(0)
                                .get("type"))
        );

        verify(categoryDao).findById(99L);
        verify(categoryDao, never()).save(any());
    }

    @Test
    void update_saveReturnsNull() {

        // Given
        Category existing =
                createCategory(1L, "Anterior", "Descripcion anterior");

        Category incoming =
                createCategory(null, "Actualizada", "Nueva descripcion");

        when(categoryDao.findById(1L))
                .thenReturn(Optional.of(existing));

        when(categoryDao.save(existing))
                .thenReturn(null);

        // When
        ResponseEntity<CategoryResponseRest> response =
                service.update(incoming, 1L);

        // Then
        assertAll(
                () -> assertEquals(
                        HttpStatus.BAD_REQUEST,
                        response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals("Respuesta nok",
                        response.getBody()
                                .getMetadata()
                                .get(0)
                                .get("type"))
        );

        verify(categoryDao).findById(1L);
        verify(categoryDao).save(existing);
    }

    @Test
    void update_exception() {

        // Given
        Category incoming =
                createCategory(null, "Actualizada", "Nueva descripcion");

        when(categoryDao.findById(1L))
                .thenThrow(new RuntimeException("Error de actualizacion"));

        // When
        ResponseEntity<CategoryResponseRest> response =
                service.update(incoming, 1L);

        // Then
        assertAll(
                () -> assertEquals(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals("Respuesta nok",
                        response.getBody()
                                .getMetadata()
                                .get(0)
                                .get("type"))
        );

        verify(categoryDao).findById(1L);
    }

    // =========================
    // DELETE
    // =========================

    @Test
    void deleteById_success() {

        // When
        ResponseEntity<CategoryResponseRest> response =
                service.deleteById(1L);

        // Then
        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals("respuesta ok",
                        response.getBody()
                                .getMetadata()
                                .get(0)
                                .get("type"))
        );

        verify(categoryDao).deleteById(1L);
    }

    @Test
    void deleteById_exception() {

        // Given
        doThrow(new RuntimeException("Error al eliminar"))
                .when(categoryDao)
                .deleteById(1L);

        // When
        ResponseEntity<CategoryResponseRest> response =
                service.deleteById(1L);

        // Then
        assertAll(
                () -> assertEquals(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals("Respuesta nok",
                        response.getBody()
                                .getMetadata()
                                .get(0)
                                .get("type"))
        );

        verify(categoryDao).deleteById(1L);
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
}