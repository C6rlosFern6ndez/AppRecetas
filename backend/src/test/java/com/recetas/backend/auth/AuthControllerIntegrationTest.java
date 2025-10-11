package com.recetas.backend.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recetas.backend.dtos.LoginDto;
import com.recetas.backend.dtos.RegistroDto;
import com.recetas.backend.models.Rol;
import com.recetas.backend.models.Usuario;
import com.recetas.backend.repositories.RolRepository;
import com.recetas.backend.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.is; // Asegurarse de que esta importación esté presente y sea correcta
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Rol userRole;

    @BeforeEach
    void setUp() {
        usuarioRepository.deleteAll();
        rolRepository.deleteAll();

        userRole = new Rol(null, "USER");
        rolRepository.save(userRole);
    }

    @Test
    void testRegisterUserSuccess() throws Exception {
        RegistroDto registroDto = new RegistroDto();
        registroDto.setNombreUsuario("newuser");
        registroDto.setEmail("newuser@example.com");
        registroDto.setContrasena("password123");

        mockMvc.perform(post("/api/public/auth/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registroDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombreUsuario", is("newuser")))
                .andExpect(jsonPath("$.email", is("newuser@example.com")));

        assertTrue(usuarioRepository.findByEmail("newuser@example.com").isPresent());
    }

    @Test
    void testRegisterUserEmailAlreadyExists() throws Exception {
        Usuario existingUser = new Usuario();
        existingUser.setNombreUsuario("existinguser");
        existingUser.setEmail("test@example.com");
        existingUser.setContrasena(passwordEncoder.encode("password"));
        existingUser.setRol(userRole);
        usuarioRepository.save(existingUser);

        RegistroDto registroDto = new RegistroDto();
        registroDto.setNombreUsuario("anotheruser");
        registroDto.setEmail("test@example.com"); // Email ya existente
        registroDto.setContrasena("password123");

        mockMvc.perform(post("/api/public/auth/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registroDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Error: ¡El email ya está en uso!")));
    }

    @Test
    void testRegisterUserNameAlreadyExists() throws Exception {
        Usuario existingUser = new Usuario();
        existingUser.setNombreUsuario("existinguser"); // Nombre de usuario ya existente
        existingUser.setEmail("test@example.com");
        existingUser.setContrasena(passwordEncoder.encode("password"));
        existingUser.setRol(userRole);
        usuarioRepository.save(existingUser);

        RegistroDto registroDto = new RegistroDto();
        registroDto.setNombreUsuario("existinguser");
        registroDto.setEmail("another@example.com");
        registroDto.setContrasena("password123");

        mockMvc.perform(post("/api/public/auth/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registroDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is("Error: ¡El nombre de usuario ya está en uso!")));
    }

    @Test
    void testLoginUserSuccess() throws Exception {
        Usuario user = new Usuario();
        user.setNombreUsuario("testuser");
        user.setEmail("test@example.com");
        user.setContrasena(passwordEncoder.encode("password123"));
        user.setRol(userRole);
        usuarioRepository.save(user);

        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("test@example.com");
        loginDto.setContrasena("password123");

        mockMvc.perform(post("/api/public/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.nombreUsuario", is("testuser")))
                .andExpect(jsonPath("$.email", is("test@example.com")));
    }

    @Test
    void testLoginUserInvalidCredentials() throws Exception {
        Usuario user = new Usuario();
        user.setNombreUsuario("testuser");
        user.setEmail("test@example.com");
        user.setContrasena(passwordEncoder.encode("password123"));
        user.setRol(userRole);
        usuarioRepository.save(user);

        LoginDto loginDto = new LoginDto();
        loginDto.setEmail("test@example.com");
        loginDto.setContrasena("wrongpassword"); // Contraseña incorrecta

        mockMvc.perform(post("/api/public/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isUnauthorized());
    }
}
