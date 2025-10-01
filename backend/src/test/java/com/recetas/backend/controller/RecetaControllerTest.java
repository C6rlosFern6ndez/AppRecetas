package com.recetas.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recetas.backend.domain.dto.RecetaRequestDto;
import com.recetas.backend.domain.entity.Comentario;
import com.recetas.backend.domain.entity.Receta;
import com.recetas.backend.domain.entity.Usuario;
import com.recetas.backend.domain.mapper.RecetaMapper;
import com.recetas.backend.domain.model.enums.Dificultad;
import com.recetas.backend.domain.repository.UsuarioRepository;
import com.recetas.backend.service.RecetaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean; // Importar MockBean
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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

@WebMvcTest(RecetaController.class)
class RecetaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean // Usar @MockBean
    private RecetaService recetaService;

    @MockBean // Usar @MockBean
    private UsuarioRepository usuarioRepository;

    @MockBean // Usar @MockBean
    private RecetaMapper recetaMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private Usuario testUser;
    private Receta testReceta;
    private RecetaRequestDto testRecetaRequestDto;

    @BeforeEach
    void setUp() {
        testUser = new Usuario();
        testUser.setId(1);
        testUser.setNombreUsuario("testuser");
        testUser.setEmail("test@example.com");
        testUser.setContrasena("password");

        testReceta = new Receta();
        testReceta.setId(1);
        testReceta.setTitulo("Receta de Prueba");
        testReceta.setDescripcion("Descripción de la receta de prueba");
        testReceta.setUsuario(testUser);
        testReceta.setFechaCreacion(LocalDateTime.now());
        testReceta.setDificultad(Dificultad.FACIL);
        testReceta.setTiempoPreparacion(30);
        testReceta.setPorciones(4);

        testRecetaRequestDto = new RecetaRequestDto();
        testRecetaRequestDto.setTitulo("Receta de Prueba DTO");
        testRecetaRequestDto.setDescripcion("Descripción de la receta de prueba DTO");
        testRecetaRequestDto.setDificultad(Dificultad.MEDIA);
        testRecetaRequestDto.setTiempoPreparacion(45);
        testRecetaRequestDto.setPorciones(6);
    }

    @Test
    @DisplayName("Debería dar 'me gusta' a una receta")
    @WithMockUser(username = "testuser")
    void darMeGusta_shouldReturnOkStatus() throws Exception {
        when(usuarioRepository.findByNombreUsuario(any(String.class))).thenReturn(Optional.of(testUser));
        doNothing().when(recetaService).darMeGusta(any(Integer.class), any(Integer.class));

        mockMvc.perform(post("/api/recetas/{recetaId}/like", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Debería devolver 404 si el usuario no es encontrado al dar 'me gusta'")
    @WithMockUser(username = "nonexistentuser")
    void darMeGusta_shouldReturnNotFoundIfUserNotFound() throws Exception {
        when(usuarioRepository.findByNombreUsuario(any(String.class))).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/recetas/{recetaId}/like", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Debería quitar el 'me gusta' de una receta")
    @WithMockUser(username = "testuser")
    void quitarMeGusta_shouldReturnOkStatus() throws Exception {
        when(usuarioRepository.findByNombreUsuario(any(String.class))).thenReturn(Optional.of(testUser));
        doNothing().when(recetaService).quitarMeGusta(any(Integer.class), any(Integer.class));

        mockMvc.perform(delete("/api/recetas/{recetaId}/like", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Debería devolver 404 si el usuario no es encontrado al quitar 'me gusta'")
    @WithMockUser(username = "nonexistentuser")
    void quitarMeGusta_shouldReturnNotFoundIfUserNotFound() throws Exception {
        when(usuarioRepository.findByNombreUsuario(any(String.class))).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/recetas/{recetaId}/like", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Debería agregar un comentario a una receta")
    @WithMockUser(username = "testuser")
    void agregarComentario_shouldReturnCreatedComment() throws Exception {
        Comentario newComentario = new Comentario();
        newComentario.setComentario("¡Qué rica receta!");
        newComentario.setUsuario(testUser);
        newComentario.setReceta(testReceta);

        when(usuarioRepository.findByNombreUsuario(any(String.class))).thenReturn(Optional.of(testUser));
        when(recetaService.findById(anyInt())).thenReturn(testReceta);
        when(recetaService.agregarComentario(any(Comentario.class))).thenReturn(newComentario);

        mockMvc.perform(post("/api/recetas/{recetaId}/comments", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newComentario)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.comentario").value("¡Qué rica receta!"));
    }

    @Test
    @DisplayName("Debería devolver 404 si el usuario no es encontrado al agregar comentario")
    @WithMockUser(username = "nonexistentuser")
    void agregarComentario_shouldReturnNotFoundIfUserNotFound() throws Exception {
        Comentario newComentario = new Comentario();
        newComentario.setComentario("¡Qué rica receta!");

        when(usuarioRepository.findByNombreUsuario(any(String.class))).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/recetas/{recetaId}/comments", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newComentario)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Debería obtener comentarios de una receta")
    void obtenerComentariosDeReceta_shouldReturnListOfComments() throws Exception {
        Comentario comentario1 = new Comentario();
        comentario1.setId(1);
        comentario1.setComentario("Comentario 1");
        Comentario comentario2 = new Comentario();
        comentario2.setId(2);
        comentario2.setComentario("Comentario 2");
        Set<Comentario> comentarios = new HashSet<>(Arrays.asList(comentario1, comentario2));

        when(recetaService.obtenerComentariosDeReceta(anyInt())).thenReturn(comentarios);

        mockMvc.perform(get("/api/recetas/{recetaId}/comments", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));
    }

    @Test
    @DisplayName("Debería crear una receta")
    @WithMockUser(username = "testuser")
    void crearReceta_shouldReturnCreatedReceta() throws Exception {
        when(usuarioRepository.findByNombreUsuario(any(String.class))).thenReturn(Optional.of(testUser));
        when(recetaMapper.toEntity(any(RecetaRequestDto.class))).thenReturn(testReceta);
        when(recetaService.guardarReceta(any(Receta.class))).thenReturn(testReceta);

        mockMvc.perform(post("/api/recetas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRecetaRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Receta de Prueba"));
    }

    @Test
    @DisplayName("Debería devolver 404 si el usuario no es encontrado al crear receta")
    @WithMockUser(username = "nonexistentuser")
    void crearReceta_shouldReturnNotFoundIfUserNotFound() throws Exception {
        when(usuarioRepository.findByNombreUsuario(any(String.class))).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/recetas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRecetaRequestDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Debería obtener una receta por ID")
    void obtenerRecetaPorId_shouldReturnReceta() throws Exception {
        when(recetaService.obtenerRecetaPorId(anyInt())).thenReturn(Optional.of(testReceta));

        mockMvc.perform(get("/api/recetas/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Receta de Prueba"));
    }

    @Test
    @DisplayName("Debería devolver 404 si la receta no es encontrada por ID")
    void obtenerRecetaPorId_shouldReturnNotFound() throws Exception {
        when(recetaService.obtenerRecetaPorId(anyInt())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/recetas/{id}", 99)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Debería obtener todas las recetas paginadas")
    void obtenerTodasLasRecetas_shouldReturnPagedRecetas() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        PageImpl<Receta> page = new PageImpl<>(Collections.singletonList(testReceta), pageable, 1);
        when(recetaService.obtenerTodasLasRecetas(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/recetas")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()").value(1))
                .andExpect(jsonPath("$.content[0].titulo").value("Receta de Prueba"));
    }

    @Test
    @DisplayName("Debería buscar recetas por criterios")
    void buscarRecetas_shouldReturnPagedRecetas() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);
        PageImpl<Receta> page = new PageImpl<>(Collections.singletonList(testReceta), pageable, 1);
        when(recetaService.buscarRecetas(any(String.class), any(String.class), any(Dificultad.class),
                any(Integer.class), any(String.class), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/recetas/search")
                .param("titulo", "Prueba")
                .param("dificultad", "FACIL")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.size()").value(1))
                .andExpect(jsonPath("$.content[0].titulo").value("Receta de Prueba"));
    }

    @Test
    @DisplayName("Debería actualizar una receta")
    @WithMockUser(username = "testuser")
    void actualizarReceta_shouldReturnUpdatedReceta() throws Exception {
        Receta updatedReceta = new Receta();
        updatedReceta.setId(1);
        updatedReceta.setTitulo("Receta Actualizada");
        updatedReceta.setDescripcion("Descripción actualizada");
        updatedReceta.setUsuario(testUser);
        updatedReceta.setFechaCreacion(LocalDateTime.now());
        updatedReceta.setDificultad(Dificultad.MEDIA);
        updatedReceta.setTiempoPreparacion(40);
        updatedReceta.setPorciones(5);

        when(usuarioRepository.findByNombreUsuario(any(String.class))).thenReturn(Optional.of(testUser));
        when(recetaService.findById(anyInt())).thenReturn(testReceta);
        doNothing().when(recetaMapper).updateEntityFromDto(any(RecetaRequestDto.class), any(Receta.class));
        when(recetaService.guardarReceta(any(Receta.class))).thenReturn(updatedReceta);

        mockMvc.perform(put("/api/recetas/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRecetaRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Receta Actualizada"));
    }

    @Test
    @DisplayName("Debería devolver 404 si la receta a actualizar no es encontrada")
    @WithMockUser(username = "testuser")
    void actualizarReceta_shouldReturnNotFoundIfRecetaNotFound() throws Exception {
        when(usuarioRepository.findByNombreUsuario(any(String.class))).thenReturn(Optional.of(testUser));
        when(recetaService.findById(anyInt())).thenReturn(null);

        mockMvc.perform(put("/api/recetas/{id}", 99)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRecetaRequestDto)))
                .andExpect(status().isForbidden()); // Debería ser FORBIDDEN si el usuario no es el propietario o la
                                                    // receta no existe
    }

    @Test
    @DisplayName("Debería devolver 403 si el usuario no es el propietario de la receta al actualizar")
    @WithMockUser(username = "anotheruser")
    void actualizarReceta_shouldReturnForbiddenIfUserNotOwner() throws Exception {
        Usuario anotherUser = new Usuario();
        anotherUser.setId(2);
        anotherUser.setNombreUsuario("anotheruser");

        when(usuarioRepository.findByNombreUsuario("anotheruser")).thenReturn(Optional.of(anotherUser));
        when(recetaService.findById(anyInt())).thenReturn(testReceta); // testReceta pertenece a testUser (ID 1)

        mockMvc.perform(put("/api/recetas/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testRecetaRequestDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Debería eliminar una receta")
    @WithMockUser(username = "testuser")
    void eliminarReceta_shouldReturnNoContent() throws Exception {
        when(usuarioRepository.findByNombreUsuario(any(String.class))).thenReturn(Optional.of(testUser));
        when(recetaService.findById(anyInt())).thenReturn(testReceta);
        doNothing().when(recetaService).eliminarReceta(anyInt());

        mockMvc.perform(delete("/api/recetas/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Debería devolver 404 si la receta a eliminar no es encontrada")
    @WithMockUser(username = "testuser")
    void eliminarReceta_shouldReturnNotFoundIfRecetaNotFound() throws Exception {
        when(usuarioRepository.findByNombreUsuario(any(String.class))).thenReturn(Optional.of(testUser));
        when(recetaService.findById(anyInt())).thenReturn(null);

        mockMvc.perform(delete("/api/recetas/{id}", 99)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden()); // Debería ser FORBIDDEN si el usuario no es el propietario o la
                                                    // receta no existe
    }

    @Test
    @DisplayName("Debería devolver 403 si el usuario no es el propietario de la receta al eliminar")
    @WithMockUser(username = "anotheruser")
    void eliminarReceta_shouldReturnForbiddenIfUserNotOwner() throws Exception {
        Usuario anotherUser = new Usuario();
        anotherUser.setId(2);
        anotherUser.setNombreUsuario("anotheruser");

        when(usuarioRepository.findByNombreUsuario("anotheruser")).thenReturn(Optional.of(anotherUser));
        when(recetaService.findById(anyInt())).thenReturn(testReceta); // testReceta pertenece a testUser (ID 1)

        mockMvc.perform(delete("/api/recetas/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
