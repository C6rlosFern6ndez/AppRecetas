package com.recetas.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recetas.backend.domain.entity.Notificacion;
import com.recetas.backend.domain.entity.Usuario;
import com.recetas.backend.domain.model.enums.TipoNotificacion;
import com.recetas.backend.domain.repository.UsuarioRepository;
import com.recetas.backend.service.NotificacionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificacionController.class)
class NotificacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private NotificacionService notificacionService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Usuario testUser;
    private Notificacion notificacion1;
    private Notificacion notificacion2;

    @BeforeEach
    void setUp() {
        testUser = new Usuario();
        testUser.setId(1);
        testUser.setNombreUsuario("testuser");
        testUser.setEmail("test@example.com");
        testUser.setContrasena("password");

        notificacion1 = new Notificacion(1, testUser, TipoNotificacion.NUEVO_COMENTARIO, null, null,
                "Nuevo comentario en tu receta", false, LocalDateTime.now());
        notificacion2 = new Notificacion(2, testUser, TipoNotificacion.ME_GUSTA_RECETA, null, null,
                "A alguien le gusta tu receta", true, LocalDateTime.now());
    }

    @Test
    @DisplayName("Debería obtener las notificaciones del usuario autenticado")
    @WithMockUser(username = "testuser")
    void obtenerNotificaciones_shouldReturnListOfNotifications() throws Exception {
        List<Notificacion> notificaciones = Arrays.asList(notificacion1, notificacion2);
        when(usuarioRepository.findByNombreUsuario(any(String.class))).thenReturn(Optional.of(testUser));
        when(notificacionService.obtenerNotificacionesUsuario(any(Integer.class))).thenReturn(notificaciones);

        mockMvc.perform(get("/notificaciones")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(notificaciones.size()))
                .andExpect(jsonPath("$[0].mensaje").value("Nuevo comentario en tu receta"))
                .andExpect(jsonPath("$[1].mensaje").value("A alguien le gusta tu receta"));
    }

    @Test
    @DisplayName("Debería devolver 404 si el usuario no es encontrado al obtener notificaciones")
    @WithMockUser(username = "nonexistentuser")
    void obtenerNotificaciones_shouldReturnNotFoundIfUserNotFound() throws Exception {
        when(usuarioRepository.findByNombreUsuario(any(String.class))).thenReturn(Optional.empty());

        mockMvc.perform(get("/notificaciones")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Debería marcar una notificación como leída")
    @WithMockUser(username = "testuser")
    void marcarComoLeida_shouldReturnOkStatus() throws Exception {
        doNothing().when(notificacionService).marcarComoLeida(anyInt());

        mockMvc.perform(post("/notificaciones/{id}/leida", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Debería eliminar una notificación")
    @WithMockUser(username = "testuser")
    void eliminarNotificacion_shouldReturnNoContentStatus() throws Exception {
        doNothing().when(notificacionService).eliminarNotificacion(anyInt());

        mockMvc.perform(delete("/notificaciones/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
