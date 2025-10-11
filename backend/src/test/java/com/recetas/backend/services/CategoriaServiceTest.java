package com.recetas.backend.services;

import com.recetas.backend.dtos.CategoriaDto;
import com.recetas.backend.exceptions.ResourceNotFoundException;
import com.recetas.backend.models.Categoria;
import com.recetas.backend.repositories.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    private Categoria categoria1;
    private Categoria categoria2;
    private CategoriaDto categoriaDto1;
    private CategoriaDto categoriaDto2;

    @BeforeEach
    void setUp() {
        categoria1 = new Categoria(1, "Postres", null);
        categoria2 = new Categoria(2, "Vegetariano", null);

        categoriaDto1 = CategoriaDto.fromEntity(categoria1);
        categoriaDto2 = CategoriaDto.fromEntity(categoria2);
    }

    @Test
    void testGetAllCategorias() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Categoria> categoriaPage = new PageImpl<>(Arrays.asList(categoria1, categoria2), pageable, 2);
        when(categoriaRepository.findAll(pageable)).thenReturn(categoriaPage);

        Page<CategoriaDto> result = categoriaService.getAllCategorias(pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals("Postres", result.getContent().get(0).getNombre());
        assertEquals("Vegetariano", result.getContent().get(1).getNombre());
    }

    @Test
    void testGetCategoriaByIdFound() {
        when(categoriaRepository.findById(1)).thenReturn(Optional.of(categoria1));

        CategoriaDto result = categoriaService.getCategoriaById(1);

        assertNotNull(result);
        assertEquals("Postres", result.getNombre());
    }

    @Test
    void testGetCategoriaByIdNotFound() {
        when(categoriaRepository.findById(99)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            categoriaService.getCategoriaById(99);
        });

        assertEquals("Categoría no encontrada con ID: 99", exception.getMessage());
    }

    @Test
    void testCreateCategoriaSuccess() {
        CategoriaDto newCategoriaDto = new CategoriaDto();
        newCategoriaDto.setNombre("Carnes");

        Categoria newCategoria = new Categoria(null, "Carnes", null);
        Categoria savedCategoria = new Categoria(3, "Carnes", null);

        when(categoriaRepository.findByNombre("Carnes")).thenReturn(Optional.empty());
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(savedCategoria);

        CategoriaDto result = categoriaService.createCategoria(newCategoriaDto);

        assertNotNull(result);
        assertEquals("Carnes", result.getNombre());
        assertEquals(3, result.getId());
    }

    @Test
    void testCreateCategoriaNameExists() {
        CategoriaDto newCategoriaDto = new CategoriaDto();
        newCategoriaDto.setNombre("Postres");

        when(categoriaRepository.findByNombre("Postres")).thenReturn(Optional.of(categoria1));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            categoriaService.createCategoria(newCategoriaDto);
        });

        assertEquals("Error: ¡El nombre de la categoría ya existe!", exception.getMessage());
    }

    @Test
    void testUpdateCategoriaSuccess() {
        CategoriaDto updatedCategoriaDto = new CategoriaDto();
        updatedCategoriaDto.setNombre("Postres Modificado");

        Categoria updatedCategoria = new Categoria(1, "Postres Modificado", null);

        when(categoriaRepository.findById(1)).thenReturn(Optional.of(categoria1));
        when(categoriaRepository.findByNombre("Postres Modificado")).thenReturn(Optional.empty());
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(updatedCategoria);

        CategoriaDto result = categoriaService.updateCategoria(1, updatedCategoriaDto);

        assertNotNull(result);
        assertEquals("Postres Modificado", result.getNombre());
        assertEquals(1, result.getId());
    }

    @Test
    void testUpdateCategoriaNotFound() {
        CategoriaDto updatedCategoriaDto = new CategoriaDto();
        updatedCategoriaDto.setNombre("Inexistente");

        when(categoriaRepository.findById(99)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            categoriaService.updateCategoria(99, updatedCategoriaDto);
        });

        assertEquals("Categoría no encontrada con ID: 99", exception.getMessage());
    }

    @Test
    void testUpdateCategoriaNameExistsForOtherId() {
        CategoriaDto updatedCategoriaDto = new CategoriaDto();
        updatedCategoriaDto.setNombre("Vegetariano"); // Nombre de categoria2

        when(categoriaRepository.findById(1)).thenReturn(Optional.of(categoria1));
        when(categoriaRepository.findByNombre("Vegetariano")).thenReturn(Optional.of(categoria2));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            categoriaService.updateCategoria(1, updatedCategoriaDto);
        });

        assertEquals("Error: ¡El nombre de la categoría ya existe!", exception.getMessage());
    }

    @Test
    void testDeleteCategoriaSuccess() {
        when(categoriaRepository.existsById(1)).thenReturn(true);
        doNothing().when(categoriaRepository).deleteById(1);

        categoriaService.deleteCategoria(1);

        verify(categoriaRepository, times(1)).deleteById(1);
    }

    @Test
    void testDeleteCategoriaNotFound() {
        when(categoriaRepository.existsById(99)).thenReturn(false);

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            categoriaService.deleteCategoria(99);
        });

        assertEquals("Categoría no encontrada con ID: 99", exception.getMessage());
    }
}
