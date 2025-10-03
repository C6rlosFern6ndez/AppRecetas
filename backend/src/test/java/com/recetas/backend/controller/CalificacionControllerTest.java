package com.recetas.backend.controller;

import com.recetas.backend.domain.entity.Usuario;
import com.recetas.backend.domain.repository.UsuarioRepository;
import com.recetas.backend.service.CalificacionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import com.recetas.backend.domain.entity.Calificacion;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CalificacionController.class)
@AutoConfigureMockMvc(addFilters = false)
class CalificacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean // Usar @MockBean para inyectar un mock en el contexto de Spring
    private CalificacionService calificacionService;

    @MockBean // Usar @MockBean
    private UsuarioRepository usuarioRepository;

    private Usuario testUser;
    private Calificacion testCalificacion;

    @BeforeEach
    void setUp() {
        testUser = new Usuario();
        testUser.setId(1);
        testUser.setNombreUsuario("testuser");
        testUser.setEmail("test@example.com");
        testUser.setContrasena("password");

        testCalificacion = new Calificacion();
        testCalificacion.setId(1);
        testCalificacion.setPuntuacion(5);
        testCalificacion.setUsuario(testUser);
        // No se establece la receta aquí, ya que se mockea en el servicio
    }

    @Test
    @DisplayName("Debería calificar una receta correctamente")
    @WithMockUser(username = "testuser")
    void calificarReceta_shouldReturnCreatedStatus() throws Exception {
        when(usuarioRepository.findByNombreUsuario(any(String.class))).thenReturn(Optional.of(testUser));
        when(calificacionService.calificarReceta(any(Integer.class), any(Integer.class), any(Integer.class)))
                .thenReturn(testCalificacion);

        mockMvc.perform(post("/calificaciones/receta/{recetaId}", 1).with(csrf())
                .param("puntuacion", "5")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Debería devolver 404 si el usuario no es encontrado al calificar")
    @WithMockUser(username = "nonexistentuser")
    void calificarReceta_shouldReturnNotFoundIfUserNotFound() throws Exception {
        when(usuarioRepository.findByNombreUsuario(any(String.class))).thenReturn(Optional.empty());

        mockMvc.perform(post("/calificaciones/receta/{recetaId}", 1).with(csrf())
                .param("puntuacion", "5")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Debería eliminar una calificación correctamente")
    @WithMockUser(username = "testuser")
    void eliminarCalificacion_shouldReturnNoContentStatus() throws Exception {
        when(usuarioRepository.findByNombreUsuario(any(String.class))).thenReturn(Optional.of(testUser));
        doNothing().when(calificacionService).eliminarCalificacion(any(Integer.class), any(Integer.class));

        mockMvc.perform(delete("/calificaciones/receta/{recetaId}", 1).with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Debería devolver 404 si el usuario no es encontrado al eliminar calificación")
    @WithMockUser(username = "nonexistentuser")
    void eliminarCalificacion_shouldReturnNotFoundIfUserNotFound() throws Exception {
        when(usuarioRepository.findByNombreUsuario(any(String.class))).thenReturn(Optional.empty());

        mockMvc.perform(delete("/calificaciones/receta/{recetaId}", 1).with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
