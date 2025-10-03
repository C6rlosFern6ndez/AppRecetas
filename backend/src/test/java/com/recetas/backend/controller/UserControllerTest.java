package com.recetas.backend.controller;

import com.recetas.backend.domain.entity.Usuario;
import com.recetas.backend.domain.repository.UsuarioRepository;
import com.recetas.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.recetas.backend.config.SecurityConfig;
import com.recetas.backend.security.AuthEntryPointJwt;
import com.recetas.backend.security.JwtUtils;
import com.recetas.backend.service.impl.UserDetailsServiceImpl;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean; // Importar MockBean
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.recetas.backend.domain.entity.Rol;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import({ SecurityConfig.class, UserDetailsServiceImpl.class, AuthEntryPointJwt.class, JwtUtils.class })
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean // Usar @MockBean
    private UserService userService;

    @MockBean // Usar @MockBean
    private UsuarioRepository usuarioRepository;

    private Usuario testUserEntity;
    private Usuario followedUserEntity;
    private User testUserDetails;

    @BeforeEach
    void setUp() {
        testUserEntity = new Usuario();
        testUserEntity.setId(1);
        testUserEntity.setNombreUsuario("testuser");
        testUserEntity.setEmail("test@example.com");
        testUserEntity.setContrasena("password");
        testUserEntity.setRol(new Rol(1, "USER")); // Asignar un rol al usuario de prueba

        followedUserEntity = new Usuario();
        followedUserEntity.setId(2);
        followedUserEntity.setNombreUsuario("followeduser");
        followedUserEntity.setEmail("followed@example.com");
        followedUserEntity.setContrasena("password");
        followedUserEntity.setRol(new Rol(1, "USER")); // Asignar un rol al usuario seguido

        testUserDetails = new User(
                testUserEntity.getNombreUsuario(),
                testUserEntity.getContrasena(),
                java.util.List.of(new SimpleGrantedAuthority(testUserEntity.getRol().getNombre())));
    }

    @Test
    @DisplayName("Debería seguir a un usuario")
    void seguirUsuario_shouldReturnOkStatus() throws Exception {
        when(usuarioRepository.findByNombreUsuario(any(String.class))).thenReturn(Optional.of(testUserEntity));
        doNothing().when(userService).seguirUsuario(any(Integer.class), any(Integer.class));

        mockMvc.perform(post("/api/users/{seguidoId}/follow", 2)
                .with(user(testUserDetails))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Debería dejar de seguir a un usuario")
    void dejarDeSeguirUsuario_shouldReturnOkStatus() throws Exception {
        when(usuarioRepository.findByNombreUsuario(any(String.class))).thenReturn(Optional.of(testUserEntity));
        doNothing().when(userService).dejarDeSeguirUsuario(any(Integer.class), any(Integer.class));

        mockMvc.perform(delete("/api/users/{seguidoId}/unfollow", 2)
                .with(user(testUserDetails))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Debería obtener la lista de seguidores de un usuario")
    @WithMockUser(username = "testuser", roles = "USER")
    void obtenerSeguidores_shouldReturnListOfFollowers() throws Exception {
        Set<Usuario> followers = new HashSet<>();
        followers.add(testUserEntity);
        when(userService.obtenerSeguidores(anyInt())).thenReturn(followers);

        mockMvc.perform(get("/api/users/{userId}/followers", 2)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].nombreUsuario").value("testuser"));
    }

    @Test
    @DisplayName("Debería obtener la lista de usuarios seguidos por un usuario")
    @WithMockUser(username = "testuser", roles = "USER")
    void obtenerSeguidos_shouldReturnListOfFollowing() throws Exception {
        Set<Usuario> following = new HashSet<>();
        following.add(followedUserEntity);
        when(userService.obtenerSeguidos(anyInt())).thenReturn(following);

        mockMvc.perform(get("/api/users/{userId}/following", 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].nombreUsuario").value("followeduser"));
    }
}
