package com.recetas.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recetas.backend.domain.dto.RecetaRequestDto;
import com.recetas.backend.domain.entity.Comentario;
import com.recetas.backend.domain.entity.Receta;
import com.recetas.backend.domain.entity.Usuario;
import com.recetas.backend.domain.model.enums.Dificultad;
import com.recetas.backend.domain.repository.UsuarioRepository;
import com.recetas.backend.security.AuthEntryPointJwt;
import com.recetas.backend.security.JwtUtils;
import com.recetas.backend.service.RecetaService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.*;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile; // Importar MultipartFile
import com.recetas.backend.service.ImageUploadService; // Importar ImageUploadService
import com.recetas.backend.service.NotificacionService; // Importar NotificacionService
import com.recetas.backend.service.IngredienteService; // Importar IngredienteService
import com.recetas.backend.service.UserService; // Importar UserService
import org.springframework.security.core.userdetails.UserDetailsService; // Importar UserDetailsService
import org.springframework.security.authentication.AuthenticationManager; // Importar AuthenticationManager

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.Arrays; // Importar Arrays

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.recetas.backend.config.SecurityConfig;

@WebMvcTest(RecetaController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class RecetaControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private RecetaService recetaService;

        @MockBean
        private UsuarioRepository usuarioRepository;

        @MockBean
        private ImageUploadService imageUploadService; // Añadido para limpiar
        @MockBean
        private NotificacionService notificacionService; // Añadido para limpiar
        @MockBean
        private IngredienteService ingredienteService; // Añadido para limpiar
        @MockBean
        private UserService userService; // Añadido para limpiar
        @MockBean
        private JwtUtils jwtUtils; // Añadido para limpiar
        @MockBean
        private UserDetailsService userDetailsService; // Añadido para limpiar
        @MockBean
        private AuthEntryPointJwt authEntryPointJwt; // Añadido para limpiar
        @MockBean
        private AuthenticationManager authenticationManager; // Añadido para que SecurityConfig pueda inyectarlo

        @Autowired
        private ObjectMapper objectMapper;

        private Usuario testUser;
        private Usuario anotherUser;
        private Receta testReceta;
        private RecetaRequestDto testRecetaRequestDto;

        @BeforeEach
        void setUp() {
                testUser = new Usuario();
                testUser.setId(1);
                testUser.setNombreUsuario("testuser");
                testUser.setEmail("test@example.com");
                testUser.setContrasena("password");

                anotherUser = new Usuario();
                anotherUser.setId(2);
                anotherUser.setNombreUsuario("anotheruser");
                anotherUser.setEmail("another@example.com");
                anotherUser.setContrasena("password2");

                // Esta parte no es necesaria si se usa @WithMockUser o .with(user)
                /*
                 * org.springframework.security.core.userdetails.User
                 * .withUsername(testUser.getNombreUsuario())
                 * .password(testUser.getContrasena())
                 * .roles("USER")
                 * .build();
                 */

                testReceta = new Receta();
                testReceta.setId(1);
                testReceta.setTitulo("Receta de Prueba");
                testReceta.setDescripcion("Descripción de la receta de prueba");
                testReceta.setUsuario(testUser);
                testReceta.setFechaCreacion(LocalDateTime.now());
                testReceta.setDificultad(Dificultad.FACIL);
                testReceta.setTiempoPreparacion(30);
                testReceta.setPorciones(4);
                testReceta.setUrlImagen("http://example.com/image.jpg");
                testReceta.setComentarios(new HashSet<>());
                testReceta.setCategorias(new HashSet<>());

                testRecetaRequestDto = new RecetaRequestDto();
                testRecetaRequestDto.setTitulo("Receta de Prueba DTO");
                testRecetaRequestDto.setDescripcion("Descripción de la receta de prueba DTO");
                testRecetaRequestDto.setDificultad(Dificultad.MEDIA);
                testRecetaRequestDto.setTiempoPreparacion(45);
                testRecetaRequestDto.setPorciones(6);
                testRecetaRequestDto.setCategoriaIds(new HashSet<>(Arrays.asList(1, 2)));
        }

        @Test
        @DisplayName("Debería crear una receta")
        @WithMockUser(username = "testuser", roles = { "USER" })
        void crearReceta_shouldReturnCreatedReceta() throws Exception {
                when(usuarioRepository.findByNombreUsuario(eq("testuser"))).thenReturn(Optional.of(testUser)); // Especificar
                                                                                                               // el
                                                                                                               // nombre
                                                                                                               // de
                                                                                                               // usuario
                when(recetaService.crearReceta(any(RecetaRequestDto.class), eq(testUser.getId())))
                                .thenReturn(testReceta);

                mockMvc.perform(post("/api/recetas")
                                // REMOVIDO: .with(user("testuser").roles("USER")) (Redundante con
                                // @WithMockUser)
                                .with(csrf()) // AÑADIDO: Necesario para peticiones POST, PUT, DELETE
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(testRecetaRequestDto)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.titulo").value("Receta de Prueba"));
        }

        @Test
        @DisplayName("Debería devolver 500 si el usuario no es encontrado al crear receta")
        @WithMockUser(username = "testuser", roles = { "USER" })
        void crearReceta_shouldReturnNotFoundIfUserNotFound() throws Exception {
                when(usuarioRepository.findByNombreUsuario(eq("testuser"))).thenReturn(Optional.empty()); // Especificar
                                                                                                          // el nombre
                                                                                                          // de usuario

                mockMvc.perform(post("/api/recetas")
                                // REMOVIDO: .with(user("testuser").roles("USER"))
                                .with(csrf()) // AÑADIDO: Necesario para peticiones POST, PUT, DELETE
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(testRecetaRequestDto)))
                                .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("Debería actualizar una receta")
        @WithMockUser(username = "testuser", roles = { "USER" })
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
                updatedReceta.setUrlImagen("http://example.com/updated_image.jpg");

                when(usuarioRepository.findByNombreUsuario(eq("testuser"))).thenReturn(Optional.of(testUser)); // Especificar
                                                                                                               // el
                                                                                                               // nombre
                                                                                                               // de
                                                                                                               // usuario
                when(recetaService.actualizarReceta(eq(1), any(RecetaRequestDto.class), eq(testUser.getId())))
                                .thenReturn(updatedReceta);

                mockMvc.perform(put("/api/recetas/{id}", 1)
                                // REMOVIDO: .with(user("testuser").roles("USER"))
                                .with(csrf()) // AÑADIDO: Necesario para peticiones POST, PUT, DELETE
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(testRecetaRequestDto)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.titulo").value("Receta Actualizada"));
        }

        @Test
        @DisplayName("Debería devolver 404 si la receta a actualizar no es encontrada")
        @WithMockUser(username = "testuser", roles = { "USER" })
        void actualizarReceta_shouldReturnNotFoundIfRecetaNotFound() throws Exception {
                when(usuarioRepository.findByNombreUsuario(eq("testuser"))).thenReturn(Optional.of(testUser)); // Especificar
                                                                                                               // el
                                                                                                               // nombre
                                                                                                               // de
                                                                                                               // usuario
                when(recetaService.actualizarReceta(eq(99), any(RecetaRequestDto.class), eq(testUser.getId())))
                                .thenThrow(new Exception("Receta no encontrada"));

                mockMvc.perform(put("/api/recetas/{id}", 99)
                                // REMOVIDO: .with(user("testuser").roles("USER"))
                                .with(csrf()) // AÑADIDO: Necesario para peticiones POST, PUT, DELETE
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(testRecetaRequestDto)))
                                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Debería devolver 403 si el usuario no es el propietario de la receta al actualizar")
        @WithMockUser(username = "anotheruser", roles = { "USER" })
        void actualizarReceta_shouldReturnForbiddenIfUserNotOwner() throws Exception {
                // En el mock, usamos 'anotherUser' que es el usuario de la prueba
                // (@WithMockUser)
                when(usuarioRepository.findByNombreUsuario(eq("anotheruser"))).thenReturn(Optional.of(anotherUser));
                when(recetaService.actualizarReceta(eq(1), any(RecetaRequestDto.class), eq(anotherUser.getId())))
                                .thenThrow(new Exception("No tienes permiso"));

                mockMvc.perform(put("/api/recetas/{id}", 1)
                                // CORREGIDO: Se elimina el with(user) redundante y contradictorio
                                .with(csrf()) // AÑADIDO: Necesario para peticiones POST, PUT, DELETE
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(testRecetaRequestDto)))
                                .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Debería subir una imagen para una receta")
        @WithMockUser(username = "testuser", roles = { "USER" })
        void subirImagenReceta_shouldReturnImageUrl() throws Exception {
                MockMultipartFile file = new MockMultipartFile("file", "test.jpg", MediaType.IMAGE_JPEG_VALUE,
                                "some-image".getBytes());
                String imageUrl = "http://example.com/new_image.jpg";

                when(recetaService.subirImagenReceta(eq(1), any(MultipartFile.class))).thenReturn(imageUrl);

                // Ya tenía .with(csrf()), se mantiene.
                mockMvc.perform(multipart("/api/recetas/{recetaId}/imagen", 1)
                                .file(file)
                                .with(csrf()))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").value(imageUrl));
        }

        @Test
        @DisplayName("Debería eliminar una imagen de una receta")
        @WithMockUser(username = "testuser", roles = { "USER" })
        void eliminarImagenReceta_shouldReturnNoContent() throws Exception {
                doNothing().when(recetaService).eliminarImagenReceta(eq(1));

                // Ya tenía .with(csrf()), se mantiene.
                mockMvc.perform(delete("/api/recetas/{recetaId}/imagen", 1)
                                .with(csrf()))
                                .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Debería dar 'me gusta' a una receta")
        @WithMockUser(username = "testuser", roles = { "USER" })
        void darMeGusta_shouldReturnOkStatus() throws Exception {
                when(usuarioRepository.findByNombreUsuario(eq("testuser"))).thenReturn(Optional.of(testUser)); // Especificar
                                                                                                               // el
                                                                                                               // nombre
                                                                                                               // de
                                                                                                               // usuario
                doNothing().when(recetaService).darMeGusta(eq(testUser.getId()), eq(1));

                // Ya tenía .with(csrf()), se mantiene.
                mockMvc.perform(post("/api/recetas/{recetaId}/like", 1)
                                .with(csrf()))
                                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Debería devolver 500 si el usuario no es encontrado al dar 'me gusta'")
        @WithMockUser(username = "testuser", roles = { "USER" })
        void darMeGusta_shouldReturnNotFoundIfUserNotFound() throws Exception {
                when(usuarioRepository.findByNombreUsuario(eq("testuser"))).thenReturn(Optional.empty()); // Especificar
                                                                                                          // el nombre
                                                                                                          // de usuario

                // Ya tenía .with(csrf()), se mantiene.
                mockMvc.perform(post("/api/recetas/{recetaId}/like", 1)
                                .with(csrf()))
                                .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("Debería quitar el 'me gusta' de una receta")
        @WithMockUser(username = "testuser", roles = { "USER" })
        void quitarMeGusta_shouldReturnOkStatus() throws Exception {
                when(usuarioRepository.findByNombreUsuario(eq("testuser"))).thenReturn(Optional.of(testUser)); // Especificar
                                                                                                               // el
                                                                                                               // nombre
                                                                                                               // de
                                                                                                               // usuario
                doNothing().when(recetaService).quitarMeGusta(eq(testUser.getId()), eq(1));

                // Ya tenía .with(csrf()), se mantiene.
                mockMvc.perform(delete("/api/recetas/{recetaId}/like", 1)
                                .with(csrf()))
                                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Debería devolver 404 si el usuario no es encontrado al quitar 'me gusta'")
        @WithMockUser(username = "testuser", roles = { "USER" })
        void quitarMeGusta_shouldReturnNotFoundIfUserNotFound() throws Exception {
                when(usuarioRepository.findByNombreUsuario(eq("testuser"))).thenReturn(Optional.empty()); // Especificar
                                                                                                          // el nombre
                                                                                                          // de usuario

                // Ya tenía .with(csrf()), se mantiene.
                mockMvc.perform(delete("/api/recetas/{recetaId}/like", 1)
                                .with(csrf()))
                                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Debería agregar un comentario a una receta")
        @WithMockUser(username = "testuser", roles = { "USER" })
        void agregarComentario_shouldReturnCreatedComment() throws Exception {
                Comentario newComentario = new Comentario();
                newComentario.setComentario("¡Qué rica receta!");
                newComentario.setUsuario(testUser);
                newComentario.setReceta(testReceta);

                when(usuarioRepository.findByNombreUsuario(eq("testuser"))).thenReturn(Optional.of(testUser)); // Especificar
                                                                                                               // el
                                                                                                               // nombre
                                                                                                               // de
                                                                                                               // usuario
                when(recetaService.agregarComentario(eq(1), eq(testUser.getId()), eq("¡Qué rica receta!")))
                                .thenReturn(newComentario);

                mockMvc.perform(post("/api/recetas/{recetaId}/comments", 1)
                                // REMOVIDO: .with(user("testuser").roles("USER"))
                                .with(csrf()) // AÑADIDO: Necesario para peticiones POST, PUT, DELETE
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(newComentario)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.comentario").value("¡Qué rica receta!"));
        }

        @Test
        @DisplayName("Debería devolver 404 si el usuario no es encontrado al agregar comentario")
        @WithMockUser(username = "testuser", roles = { "USER" })
        void agregarComentario_shouldReturnNotFoundIfUserNotFound() throws Exception {
                Comentario newComentario = new Comentario();
                newComentario.setComentario("¡Qué rica receta!");

                when(usuarioRepository.findByNombreUsuario(eq("testuser"))).thenReturn(Optional.empty()); // Especificar
                                                                                                          // el nombre
                                                                                                          // de usuario

                mockMvc.perform(post("/api/recetas/{recetaId}/comments", 1)
                                // REMOVIDO: .with(user("testuser").roles("USER"))
                                .with(csrf()) // AÑADIDO: Necesario para peticiones POST, PUT, DELETE
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
        @DisplayName("Debería obtener una receta por ID")
        void obtenerRecetaPorId_shouldReturnReceta() throws Exception {
                when(recetaService.obtenerRecetaOExcepcion(anyInt())).thenReturn(testReceta);

                mockMvc.perform(get("/api/recetas/{id}", 1)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.titulo").value("Receta de Prueba"));
        }

        @Test
        @DisplayName("Debería devolver 404 si la receta no es encontrada por ID")
        void obtenerRecetaPorId_shouldReturnNotFound() throws Exception {
                when(recetaService.obtenerRecetaOExcepcion(anyInt()))
                                .thenThrow(new Exception("Receta no encontrada"));

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
                // Asegurarse de que el mock devuelva una PageImpl con contenido real
                Page<Receta> page = new PageImpl<>(Collections.singletonList(testReceta), pageable, 1);
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
        @DisplayName("Debería eliminar una receta")
        @WithMockUser(username = "testuser", roles = { "USER" })
        void eliminarReceta_shouldReturnNoContent() throws Exception {
                when(usuarioRepository.findByNombreUsuario(eq("testuser"))).thenReturn(Optional.of(testUser)); // Especificar
                                                                                                               // el
                                                                                                               // nombre
                                                                                                               // de
                                                                                                               // usuario
                doNothing().when(recetaService).eliminarReceta(eq(1), eq(testUser.getId()));

                // Ya tenía .with(csrf()), se mantiene.
                mockMvc.perform(delete("/api/recetas/{id}", 1).with(csrf()))
                                .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Debería devolver 404 si la receta a eliminar no es encontrada")
        @WithMockUser(username = "testuser", roles = { "USER" })
        void eliminarReceta_shouldReturnNotFoundIfRecetaNotFound() throws Exception {
                when(usuarioRepository.findByNombreUsuario(eq("testuser"))).thenReturn(Optional.of(testUser)); // Especificar
                                                                                                               // el
                                                                                                               // nombre
                                                                                                               // de
                                                                                                               // usuario
                doThrow(new Exception("Receta no encontrada")).when(recetaService).eliminarReceta(
                                eq(99),
                                eq(testUser.getId()));

                // Ya tenía .with(csrf()), se mantiene.
                mockMvc.perform(delete("/api/recetas/{id}", 99).with(csrf()))
                                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Debería devolver 403 si el usuario no es el propietario de la receta al eliminar")
        @WithMockUser(username = "anotheruser", roles = { "USER" })
        void eliminarReceta_shouldReturnForbiddenIfUserNotOwner() throws Exception {
                when(usuarioRepository.findByNombreUsuario(eq("anotheruser"))).thenReturn(Optional.of(anotherUser));
                doThrow(new Exception("No tienes permiso")).when(recetaService).eliminarReceta(eq(1),
                                eq(anotherUser.getId()));

                // Ya tenía .with(csrf()), se mantiene.
                mockMvc.perform(delete("/api/recetas/{id}", 1)
                                .with(csrf()))
                                .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Debería calificar una receta")
        @WithMockUser(username = "testuser", roles = { "USER" })
        void calificarReceta_shouldReturnOkStatus() throws Exception {
                when(usuarioRepository.findByNombreUsuario(eq("testuser"))).thenReturn(Optional.of(testUser)); // Especificar
                                                                                                               // el
                                                                                                               // nombre
                                                                                                               // de
                                                                                                               // usuario
                doNothing().when(recetaService).calificarReceta(eq(testUser.getId()), eq(1), eq(5));

                mockMvc.perform(post("/api/recetas/{recetaId}/calificar", 1)
                                .param("puntuacion", "5")
                                .with(csrf())) // Ya tenía .with(csrf()), se mantiene.
                                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Debería obtener la calificación de una receta")
        @WithMockUser(username = "testuser", roles = { "USER" }) // Simular usuario autenticado
        void obtenerCalificacionDeReceta_shouldReturnRating() throws Exception {
                when(usuarioRepository.findByNombreUsuario(eq("testuser"))).thenReturn(Optional.of(testUser)); // Especificar
                                                                                                               // el
                                                                                                               // nombre
                                                                                                               // de
                                                                                                               // usuario
                // Nota: Este test no necesita .with(csrf()) porque es un GET.
                when(recetaService.obtenerCalificacionDeReceta(eq(testUser.getId()), eq(1))).thenReturn(4);

                mockMvc.perform(get("/api/recetas/{recetaId}/calificacion", 1))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").value(4));
        }

        @Test
        @DisplayName("Debería agregar una categoría a una receta")
        @WithMockUser(username = "testuser", roles = { "USER" })
        void agregarCategoria_shouldReturnUpdatedReceta() throws Exception {
                when(recetaService.agregarCategoria(eq(1), eq(101))).thenReturn(testReceta);

                mockMvc.perform(post("/api/recetas/{recetaId}/categorias/{categoriaId}", 1, 101)
                                .with(csrf())) // Ya tenía .with(csrf()), se mantiene.
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(testReceta.getId()));
        }

        @Test
        @DisplayName("Debería eliminar una categoría de una receta")
        @WithMockUser(username = "testuser", roles = { "USER" })
        void eliminarCategoria_shouldReturnUpdatedReceta() throws Exception {
                when(recetaService.eliminarCategoria(eq(1), eq(101))).thenReturn(testReceta);

                mockMvc.perform(delete("/api/recetas/{recetaId}/categorias/{categoriaId}", 1, 101)
                                .with(csrf())) // Ya tenía .with(csrf()), se mantiene.
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(testReceta.getId()));
        }
}
