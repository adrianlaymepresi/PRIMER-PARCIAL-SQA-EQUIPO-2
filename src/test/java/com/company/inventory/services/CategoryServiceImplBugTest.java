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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CategoryServiceImplBugTest {

    @Mock
    private ICategoryDao categoryDao;

    @InjectMocks
    private CategoryServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deleteById_nonexistent_shouldReturnNotFound() {
        // Given: el repositorio no genera excepción al intentar eliminar ese ID.

        // When
        ResponseEntity<CategoryResponseRest> response =
                service.deleteById(999L);

        // Then
        assertEquals(
                HttpStatus.NOT_FOUND,
                response.getStatusCode(),
                "BUG: eliminar una categoría inexistente devuelve éxito"
        );
    }

    @Test
    void save_blankName_shouldReturnBadRequest() {
        // Given
        Category category = new Category();
        category.setName("");
        category.setDescription("Categoría sin nombre");

        when(categoryDao.save(category)).thenReturn(category);

        // When
        ResponseEntity<CategoryResponseRest> response =
                service.save(category);

        // Then
        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode(),
                "BUG: se permite registrar una categoría con nombre vacío"
        );

        verify(categoryDao, never()).save(any(Category.class));
    }

    @Test
    void update_blankName_shouldReturnBadRequest() {
        // Given
        Category existing = new Category();
        existing.setId(1L);
        existing.setName("Bebidas");
        existing.setDescription("Descripción anterior");

        Category incoming = new Category();
        incoming.setName("");
        incoming.setDescription("Descripción actualizada");

        when(categoryDao.findById(1L))
                .thenReturn(Optional.of(existing));

        when(categoryDao.save(any(Category.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        ResponseEntity<CategoryResponseRest> response =
                service.update(incoming, 1L);

        // Then
        assertEquals(
                HttpStatus.BAD_REQUEST,
                response.getStatusCode(),
                "BUG: se permite actualizar una categoría dejando el nombre vacío"
        );
    }
}
