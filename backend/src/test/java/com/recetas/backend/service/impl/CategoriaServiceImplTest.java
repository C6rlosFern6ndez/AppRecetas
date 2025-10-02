package com.recetas.backend.service.impl;

import com.recetas.backend.domain.entity.Categoria;
import com.recetas.backend.domain.repository.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceImplTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaServiceImpl categoriaService;

    private Categoria categoria1;
    private Categoria categoria2;

    @BeforeEach
    void setUp() {
        categoria1 = new Categoria(1, "Desayuno");
        categoria2 = new Categoria(2, "Almuerzo");
    }

    @Test
    @DisplayName("Debería obtener todas las categorías")
    void findAll_shouldReturnListOfCategories() {
        // Simula el comportamiento del repositorio para devolver una lista de
        // categorías
        when(categoriaRepository.findAll()).thenReturn(Arrays.asList(categoria1, categoria2));

        // Llama al método del servicio
        List<Categoria> categorias = categoriaService.findAll();

        // Verifica que la lista no sea nula y contenga las categorías esperadas
        assertNotNull(categorias);
        assertEquals(2, categorias.size());
        assertTrue(categorias.contains(categoria1));
        assertTrue(categorias.contains(categoria2));

        // Verifica que el método findAll del repositorio fue llamado una vez
        verify(categoriaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería obtener una categoría por ID")
    void findById_shouldReturnCategory() {
        // Simula el comportamiento del repositorio para devolver una categoría opcional
        when(categoriaRepository.findById(anyInt())).thenReturn(Optional.of(categoria1));

        // Llama al método del servicio
        Optional<Categoria> foundCategoria = categoriaService.findById(1);

        // Verifica que la categoría esté presente y sea la esperada
        assertTrue(foundCategoria.isPresent());
        assertEquals(categoria1.getNombre(), foundCategoria.get().getNombre());

        // Verifica que el método findById del repositorio fue llamado una vez
        verify(categoriaRepository, times(1)).findById(anyInt());
    }

    @Test
    @DisplayName("Debería devolver Optional.empty si la categoría no es encontrada por ID")
    void findById_shouldReturnEmptyOptional() {
        // Simula el comportamiento del repositorio para devolver un Optional vacío
        when(categoriaRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Llama al método del servicio
        Optional<Categoria> foundCategoria = categoriaService.findById(99);

        // Verifica que el Optional esté vacío
        assertFalse(foundCategoria.isPresent());

        // Verifica que el método findById del repositorio fue llamado una vez
        verify(categoriaRepository, times(1)).findById(anyInt());
    }

    @Test
    @DisplayName("Debería guardar una nueva categoría")
    void save_shouldReturnSavedCategory() {
        // Simula el comportamiento del repositorio para devolver la categoría guardada
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria1);

        // Llama al método del servicio
        Categoria savedCategoria = categoriaService.save(categoria1);

        // Verifica que la categoría guardada no sea nula y sea la esperada
        assertNotNull(savedCategoria);
        assertEquals(categoria1.getNombre(), savedCategoria.getNombre());

        // Verifica que el método save del repositorio fue llamado una vez
        verify(categoriaRepository, times(1)).save(any(Categoria.class));
    }

    @Test
    @DisplayName("Debería eliminar una categoría por ID")
    void deleteById_shouldDeleteCategory() {
        // Simula el comportamiento del repositorio para no hacer nada al eliminar
        doNothing().when(categoriaRepository).deleteById(anyInt());

        // Llama al método del servicio
        categoriaService.deleteById(1);

        // Verifica que el método deleteById del repositorio fue llamado una vez
        verify(categoriaRepository, times(1)).deleteById(anyInt());
    }
}
