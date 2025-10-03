package com.recetas.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recetas.backend.domain.entity.Ingrediente;
import com.recetas.backend.service.IngredienteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = IngredienteController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@ActiveProfiles("test")
class IngredienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IngredienteService ingredienteService;

    @Autowired
    private ObjectMapper objectMapper;

    private Ingrediente ingrediente1;
    private Ingrediente ingrediente2;

    @BeforeEach
    void setUp() {
        ingrediente1 = new Ingrediente(1, "Sal");
        ingrediente2 = new Ingrediente(2, "Pimienta");
    }

    @Test
    @DisplayName("Debería obtener todos los ingredientes")
    void getAllIngredientes_shouldReturnListOfIngredientes() throws Exception {
        List<Ingrediente> ingredientes = Arrays.asList(ingrediente1, ingrediente2);
        when(ingredienteService.findAll()).thenReturn(ingredientes);

        mockMvc.perform(get("/api/ingredientes")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(ingredientes.size()))
                .andExpect(jsonPath("$[0].nombre").value("Sal"))
                .andExpect(jsonPath("$[1].nombre").value("Pimienta"));
    }

    @Test
    @DisplayName("Debería obtener un ingrediente por ID")
    void getIngredienteById_shouldReturnIngrediente() throws Exception {
        when(ingredienteService.findById(anyInt())).thenReturn(Optional.of(ingrediente1));

        mockMvc.perform(get("/api/ingredientes/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Sal"));
    }

    @Test
    @DisplayName("Debería devolver 404 si el ingrediente no es encontrado por ID")
    void getIngredienteById_shouldReturnNotFound() throws Exception {
        when(ingredienteService.findById(anyInt())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/ingredientes/{id}", 99)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Debería crear un nuevo ingrediente")
    void createIngrediente_shouldReturnCreatedIngrediente() throws Exception {
        Ingrediente newIngrediente = new Ingrediente(null, "Azúcar");
        Ingrediente savedIngrediente = new Ingrediente(3, "Azúcar");
        when(ingredienteService.save(any(Ingrediente.class))).thenReturn(savedIngrediente);

        mockMvc.perform(post("/api/ingredientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newIngrediente)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.nombre").value("Azúcar"));
    }

    @Test
    @DisplayName("Debería actualizar un ingrediente existente")
    void updateIngrediente_shouldReturnUpdatedIngrediente() throws Exception {
        Ingrediente updatedIngrediente = new Ingrediente(1, "Sal Marina");
        when(ingredienteService.findById(anyInt())).thenReturn(Optional.of(ingrediente1));
        when(ingredienteService.save(any(Ingrediente.class))).thenReturn(updatedIngrediente);

        mockMvc.perform(put("/api/ingredientes/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedIngrediente)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Sal Marina"));
    }

    @Test
    @DisplayName("Debería devolver 404 si el ingrediente a actualizar no es encontrado")
    void updateIngrediente_shouldReturnNotFound() throws Exception {
        Ingrediente updatedIngrediente = new Ingrediente(99, "Inexistente");
        when(ingredienteService.findById(anyInt())).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/ingredientes/{id}", 99)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedIngrediente)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Debería eliminar un ingrediente existente")
    void deleteIngrediente_shouldReturnNoContent() throws Exception {
        when(ingredienteService.findById(anyInt())).thenReturn(Optional.of(ingrediente1));
        doNothing().when(ingredienteService).deleteById(anyInt());

        mockMvc.perform(delete("/api/ingredientes/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Debería devolver 404 si el ingrediente a eliminar no es encontrado")
    void deleteIngrediente_shouldReturnNotFound() throws Exception {
        when(ingredienteService.findById(anyInt())).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/ingredientes/{id}", 99)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
