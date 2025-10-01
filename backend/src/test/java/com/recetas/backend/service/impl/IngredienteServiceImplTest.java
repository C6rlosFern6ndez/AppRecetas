package com.recetas.backend.service.impl;

import com.recetas.backend.domain.entity.Ingrediente;
import com.recetas.backend.domain.repository.IngredienteRepository;
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
class IngredienteServiceImplTest {

    @Mock
    private IngredienteRepository ingredienteRepository;

    @InjectMocks
    private IngredienteServiceImpl ingredienteService;

    private Ingrediente ingrediente1;
    private Ingrediente ingrediente2;

    @BeforeEach
    void setUp() {
        ingrediente1 = new Ingrediente(1, "Sal");
        ingrediente2 = new Ingrediente(2, "Pimienta");
    }

    @Test
    @DisplayName("Debería obtener todos los ingredientes")
    void findAll_shouldReturnListOfIngredientes() {
        // Simula el comportamiento del repositorio para devolver una lista de
        // ingredientes
        when(ingredienteRepository.findAll()).thenReturn(Arrays.asList(ingrediente1, ingrediente2));

        // Llama al método del servicio
        List<Ingrediente> ingredientes = ingredienteService.findAll();

        // Verifica que la lista no sea nula y contenga los ingredientes esperados
        assertNotNull(ingredientes);
        assertEquals(2, ingredientes.size());
        assertTrue(ingredientes.contains(ingrediente1));
        assertTrue(ingredientes.contains(ingrediente2));

        // Verifica que el método findAll del repositorio fue llamado una vez
        verify(ingredienteRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Debería obtener un ingrediente por ID")
    void findById_shouldReturnIngrediente() {
        // Simula el comportamiento del repositorio para devolver un ingrediente
        // opcional
        when(ingredienteRepository.findById(anyInt())).thenReturn(Optional.of(ingrediente1));

        // Llama al método del servicio
        Optional<Ingrediente> foundIngrediente = ingredienteService.findById(1);

        // Verifica que el ingrediente esté presente y sea el esperado
        assertTrue(foundIngrediente.isPresent());
        assertEquals(ingrediente1.getNombre(), foundIngrediente.get().getNombre());

        // Verifica que el método findById del repositorio fue llamado una vez
        verify(ingredienteRepository, times(1)).findById(anyInt());
    }

    @Test
    @DisplayName("Debería devolver Optional.empty si el ingrediente no es encontrado por ID")
    void findById_shouldReturnEmptyOptional() {
        // Simula el comportamiento del repositorio para devolver un Optional vacío
        when(ingredienteRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Llama al método del servicio
        Optional<Ingrediente> foundIngrediente = ingredienteService.findById(99);

        // Verifica que el Optional esté vacío
        assertFalse(foundIngrediente.isPresent());

        // Verifica que el método findById del repositorio fue llamado una vez
        verify(ingredienteRepository, times(1)).findById(anyInt());
    }

    @Test
    @DisplayName("Debería guardar un nuevo ingrediente")
    void save_shouldReturnSavedIngrediente() {
        // Simula el comportamiento del repositorio para devolver el ingrediente
        // guardado
        when(ingredienteRepository.save(any(Ingrediente.class))).thenReturn(ingrediente1);

        // Llama al método del servicio
        Ingrediente savedIngrediente = ingredienteService.save(ingrediente1);

        // Verifica que el ingrediente guardado no sea nulo y sea el esperado
        assertNotNull(savedIngrediente);
        assertEquals(ingrediente1.getNombre(), savedIngrediente.getNombre());

        // Verifica que el método save del repositorio fue llamado una vez
        verify(ingredienteRepository, times(1)).save(any(Ingrediente.class));
    }

    @Test
    @DisplayName("Debería eliminar un ingrediente por ID")
    void deleteById_shouldDeleteIngrediente() {
        // Simula el comportamiento del repositorio para no hacer nada al eliminar
        doNothing().when(ingredienteRepository).deleteById(anyInt());

        // Llama al método del servicio
        ingredienteService.deleteById(1);

        // Verifica que el método deleteById del repositorio fue llamado una vez
        verify(ingredienteRepository, times(1)).deleteById(anyInt());
    }
}
