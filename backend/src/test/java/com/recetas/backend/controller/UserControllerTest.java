package com.recetas.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recetas.backend.domain.entity.Usuario;
import com.recetas.backend.domain.repository.UsuarioRepository;
import com.recetas.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean; // Importar MockBean
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean // Usar @MockBean
    private UserService userService;

    @MockBean // Usar @MockBean
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Usuario testUser;
    private Usuario followedUser;

    @BeforeEach
    void setUp() {
        testUser = new Usuario();
        testUser.setId(1);
        testUser.setNombreUsuario("testuser");
        testUser.setEmail("test@example.com");
        testUser.setContrasena("password");

        followedUser = new Usuario();
        followedUser.setId(2);
        followedUser.setNombreUsuario("followeduser");
        followedUser.setEmail("followed@example.com");
        followedUser.setContrasena("password");
    }

    @Test
    @DisplayName("Debería seguir a un usuario")
    @WithMockUser(username = "testuser")
    void seguirUsuario_shouldReturnOkStatus() throws Exception {
        when(usuarioRepository.findByNombreUsuario(any(String.class))).thenReturn(Optional.of(testUser));
        doNothing().when(userService).seguirUsuario(any(Integer.class), any(Integer.class));

        mockMvc.perform(post("/api/users/{seguidoId}/follow", 2)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Debería dejar de seguir a un usuario")
    @WithMockUser(username = "testuser")
    void dejarDeSeguirUsuario_shouldReturnOkStatus() throws Exception {
        when(usuarioRepository.findByNombreUsuario(any(String.class))).thenReturn(Optional.of(testUser));
        doNothing().when(userService).dejarDeSeguirUsuario(any(Integer.class), any(Integer.class));

        mockMvc.perform(delete("/api/users/{seguidoId}/unfollow", 2)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Debería obtener la lista de seguidores de un usuario")
    void obtenerSeguidores_shouldReturnListOfFollowers() throws Exception {
        Set<Usuario> followers = new HashSet<>();
        followers.add(testUser);
        when(userService.obtenerSeguidores(anyInt())).thenReturn(followers);

        mockMvc.perform(get("/api/users/{userId}/followers", 2)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].nombreUsuario").value("testuser"));
    }

    @Test
    @DisplayName("Debería obtener la lista de usuarios seguidos por un usuario")
    void obtenerSeguidos_shouldReturnListOfFollowing() throws Exception {
        Set<Usuario> following = new HashSet<>();
        following.add(followedUser);
        when(userService.obtenerSeguidos(anyInt())).thenReturn(following);

        mockMvc.perform(get("/api/users/{userId}/following", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].nombreUsuario").value("followeduser"));
    }
}
