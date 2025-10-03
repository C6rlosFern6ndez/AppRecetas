package com.recetas.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recetas.backend.config.SecurityConfig;
import com.recetas.backend.domain.entity.Categoria;
import com.recetas.backend.service.CategoriaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import com.recetas.backend.security.AuthEntryPointJwt;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoriaController.class)
@Import(SecurityConfig.class) // Es importante importar la configuración de seguridad si la usas.
@ActiveProfiles("test")
public class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoriaService categoriaService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private AuthEntryPointJwt authEntryPointJwt;

    @MockBean
    private com.recetas.backend.security.JwtUtils jwtUtils;

    @Autowired
    private ObjectMapper objectMapper;

    private Categoria categoria1;
    private Categoria categoria2;

    @BeforeEach
    void setUp() {
        categoria1 = new Categoria(1, "Desayuno");
        categoria2 = new Categoria(2, "Almuerzo");
    }

    @Test
    @DisplayName("Debería obtener todas las categorías")
    @WithMockUser(username = "testuser")
    void getAllCategorias_shouldReturnListOfCategories() throws Exception {
        List<Categoria> categorias = Arrays.asList(categoria1, categoria2);
        when(categoriaService.findAll()).thenReturn(categorias);

        mockMvc.perform(get("/api/categorias")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].nombre").value("Desayuno"))
                .andExpect(jsonPath("$[1].nombre").value("Almuerzo"));
    }

    @Test
    @DisplayName("Debería obtener una categoría por ID")
    @WithMockUser(username = "testuser")
    void getCategoriaById_shouldReturnCategory() throws Exception {
        when(categoriaService.findById(1)).thenReturn(Optional.of(categoria1));

        mockMvc.perform(get("/api/categorias/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Desayuno"));
    }

    @Test
    @DisplayName("Debería devolver 404 si la categoría no es encontrada por ID")
    @WithMockUser(username = "testuser")
    void getCategoriaById_shouldReturnNotFound() throws Exception {
        when(categoriaService.findById(anyInt())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/categorias/{id}", 99)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Debería crear una nueva categoría")
    @WithMockUser(username = "testuser", roles = { "ADMIN" })
    void createCategoria_shouldReturnCreatedCategory() throws Exception {
        Categoria newCategoria = new Categoria(null, "Cena");
        Categoria savedCategoria = new Categoria(3, "Cena");

        when(categoriaService.save(any(Categoria.class))).thenReturn(savedCategoria);

        mockMvc.perform(post("/api/categorias")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCategoria)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.nombre").value("Cena"));
    }

    @Test
    @DisplayName("Debería actualizar una categoría existente")
    @WithMockUser(username = "testuser", roles = "ADMIN") // Asumiendo que la actualización requiere rol ADMIN
    void updateCategoria_shouldReturnUpdatedCategory() throws Exception {
        Categoria updatedCategoria = new Categoria(1, "Desayuno Actualizado");
        when(categoriaService.findById(anyInt())).thenReturn(Optional.of(categoria1));
        when(categoriaService.save(any(Categoria.class))).thenReturn(updatedCategoria);

        mockMvc.perform(put("/api/categorias/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedCategoria)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Desayuno Actualizado"));
    }

    @Test
    @DisplayName("Debería devolver 404 si la categoría a actualizar no es encontrada")
    @WithMockUser(username = "testuser", roles = "ADMIN") // Asumiendo que la actualización requiere rol ADMIN
    void updateCategoria_shouldReturnNotFound() throws Exception {
        Categoria updatedCategoria = new Categoria(99, "Inexistente");
        when(categoriaService.findById(anyInt())).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/categorias/{id}", 99)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedCategoria)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Debería eliminar una categoría existente")
    @WithMockUser(username = "testuser", roles = "ADMIN") // Asumiendo que la eliminación requiere rol ADMIN
    void deleteCategoria_shouldReturnNoContent() throws Exception {
        when(categoriaService.findById(anyInt())).thenReturn(Optional.of(categoria1));
        doNothing().when(categoriaService).deleteById(anyInt());

        mockMvc.perform(delete("/api/categorias/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Debería devolver 404 si la categoría a eliminar no es encontrada")
    @WithMockUser(username = "testuser", roles = "ADMIN") // Asumiendo que la eliminación requiere rol ADMIN
    void deleteCategoria_shouldReturnNotFound() throws Exception {
        when(categoriaService.findById(anyInt())).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/categorias/{id}", 99)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
