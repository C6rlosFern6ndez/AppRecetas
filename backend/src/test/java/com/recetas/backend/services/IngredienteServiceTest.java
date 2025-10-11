package com.recetas.backend.services;

import com.recetas.backend.dtos.IngredienteDto;
import com.recetas.backend.exceptions.ResourceNotFoundException;
import com.recetas.backend.models.Ingrediente;
import com.recetas.backend.repositories.IngredienteRepository;
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
public class IngredienteServiceTest {

    @Mock
    private IngredienteRepository ingredienteRepository;

    @InjectMocks
    private IngredienteService ingredienteService;

    private Ingrediente ingrediente1;
    private Ingrediente ingrediente2;
    private IngredienteDto ingredienteDto1;
    private IngredienteDto ingredienteDto2;

    @BeforeEach
    void setUp() {
        ingrediente1 = new Ingrediente(1, "Sal", null);
        ingrediente2 = new Ingrediente(2, "Pimienta", null);

        ingredienteDto1 = IngredienteDto.fromEntity(ingrediente1);
        ingredienteDto2 = IngredienteDto.fromEntity(ingrediente2);
    }

    @Test
    void testGetAllIngredientes() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Ingrediente> ingredientePage = new PageImpl<>(Arrays.asList(ingrediente1, ingrediente2), pageable, 2);
        when(ingredienteRepository.findAll(pageable)).thenReturn(ingredientePage);

        Page<IngredienteDto> result = ingredienteService.getAllIngredientes(pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals("Sal", result.getContent().get(0).getNombre());
        assertEquals("Pimienta", result.getContent().get(1).getNombre());
    }

    @Test
    void testGetIngredienteByIdFound() {
        when(ingredienteRepository.findById(1)).thenReturn(Optional.of(ingrediente1));

        IngredienteDto result = ingredienteService.getIngredienteById(1);

        assertNotNull(result);
        assertEquals("Sal", result.getNombre());
    }

    @Test
    void testGetIngredienteByIdNotFound() {
        when(ingredienteRepository.findById(99)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            ingredienteService.getIngredienteById(99);
        });

        assertEquals("Ingrediente no encontrado con ID: 99", exception.getMessage());
    }

    @Test
    void testCreateIngredienteSuccess() {
        IngredienteDto newIngredienteDto = new IngredienteDto();
        newIngredienteDto.setNombre("Azúcar");

        Ingrediente newIngrediente = new Ingrediente(null, "Azúcar", null);
        Ingrediente savedIngrediente = new Ingrediente(3, "Azúcar", null);

        when(ingredienteRepository.findByNombre("Azúcar")).thenReturn(Optional.empty());
        when(ingredienteRepository.save(any(Ingrediente.class))).thenReturn(savedIngrediente);

        IngredienteDto result = ingredienteService.createIngrediente(newIngredienteDto);

        assertNotNull(result);
        assertEquals("Azúcar", result.getNombre());
        assertEquals(3, result.getId());
    }

    @Test
    void testCreateIngredienteNameExists() {
        IngredienteDto newIngredienteDto = new IngredienteDto();
        newIngredienteDto.setNombre("Sal");

        when(ingredienteRepository.findByNombre("Sal")).thenReturn(Optional.of(ingrediente1));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            ingredienteService.createIngrediente(newIngredienteDto);
        });

        assertEquals("Error: ¡El nombre del ingrediente ya existe!", exception.getMessage());
    }

    @Test
    void testUpdateIngredienteSuccess() {
        IngredienteDto updatedIngredienteDto = new IngredienteDto();
        updatedIngredienteDto.setNombre("Sal Marina");

        Ingrediente updatedIngrediente = new Ingrediente(1, "Sal Marina", null);

        when(ingredienteRepository.findById(1)).thenReturn(Optional.of(ingrediente1));
        when(ingredienteRepository.findByNombre("Sal Marina")).thenReturn(Optional.empty());
        when(ingredienteRepository.save(any(Ingrediente.class))).thenReturn(updatedIngrediente);

        IngredienteDto result = ingredienteService.updateIngrediente(1, updatedIngredienteDto);

        assertNotNull(result);
        assertEquals("Sal Marina", result.getNombre());
        assertEquals(1, result.getId());
    }

    @Test
    void testUpdateIngredienteNotFound() {
        IngredienteDto updatedDto = new IngredienteDto();
        when(ingredienteRepository.findById(99)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            ingredienteService.updateIngrediente(99, updatedDto);
        });

        assertEquals("Ingrediente no encontrado con ID: 99", exception.getMessage());
    }

    @Test
    void testUpdateIngredienteNameExistsForOtherId() {
        IngredienteDto updatedDto = new IngredienteDto();
        updatedDto.setNombre("Pimienta"); // Nombre de ingrediente2

        when(ingredienteRepository.findById(1)).thenReturn(Optional.of(ingrediente1));
        when(ingredienteRepository.findByNombre("Pimienta")).thenReturn(Optional.of(ingrediente2));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            ingredienteService.updateIngrediente(1, updatedDto);
        });

        assertEquals("Error: ¡El nombre del ingrediente ya existe!", exception.getMessage());
    }

    @Test
    void testDeleteIngredienteSuccess() {
        when(ingredienteRepository.existsById(1)).thenReturn(true);
        doNothing().when(ingredienteRepository).deleteById(1);

        ingredienteService.deleteIngrediente(1);

        verify(ingredienteRepository, times(1)).deleteById(1);
    }

    @Test
    void testDeleteIngredienteNotFound() {
        when(ingredienteRepository.existsById(99)).thenReturn(false);

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            ingredienteService.deleteIngrediente(99);
        });

        assertEquals("Ingrediente no encontrado con ID: 99", exception.getMessage());
    }
}
