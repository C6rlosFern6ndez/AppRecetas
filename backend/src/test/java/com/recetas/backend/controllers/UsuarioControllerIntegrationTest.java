package com.recetas.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recetas.backend.dtos.UsuarioDto;
import com.recetas.backend.models.Rol;
import com.recetas.backend.models.Seguidor;
import com.recetas.backend.models.SeguidorId;
import com.recetas.backend.models.Usuario;
import com.recetas.backend.repositories.RolRepository;
import com.recetas.backend.repositories.SeguidorRepository;
import com.recetas.backend.repositories.UsuarioRepository;
import com.recetas.backend.security.JwtUtils;
import com.recetas.backend.security.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class UsuarioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private SeguidorRepository seguidorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    private Usuario user1;
    private Usuario user2;
    private Usuario adminUser;
    private String user1Token;
    private String adminToken;

    @BeforeEach
    void setUp() {
        usuarioRepository.deleteAll();
        rolRepository.deleteAll();
        seguidorRepository.deleteAll();

        Rol userRole = new Rol(null, "USER");
        Rol adminRole = new Rol(null, "ADMIN");
        rolRepository.save(userRole);
        rolRepository.save(adminRole);

        userRole = rolRepository.findByNombre("USER").get();
        adminRole = rolRepository.findByNombre("ADMIN").get();

        user1 = new Usuario();
        user1.setId(1);
        user1.setNombreUsuario("user1");
        user1.setEmail("user1@example.com");
        user1.setContrasena(passwordEncoder.encode("password"));
        user1.setRol(userRole);
        user1.setFechaRegistro(LocalDateTime.now());
        usuarioRepository.save(user1);

        user2 = new Usuario();
        user2.setId(2);
        user2.setNombreUsuario("user2");
        user2.setEmail("user2@example.com");
        user2.setContrasena(passwordEncoder.encode("password"));
        user2.setRol(userRole);
        user2.setFechaRegistro(LocalDateTime.now());
        usuarioRepository.save(user2);

        adminUser = new Usuario();
        adminUser.setId(3);
        adminUser.setNombreUsuario("admin");
        adminUser.setEmail("admin@example.com");
        adminUser.setContrasena(passwordEncoder.encode("adminpass"));
        adminUser.setRol(adminRole);
        adminUser.setFechaRegistro(LocalDateTime.now());
        usuarioRepository.save(adminUser);

        UserDetailsImpl userDetails1 = UserDetailsImpl.build(user1);
        user1Token = jwtUtils.generateJwtToken(
                new UsernamePasswordAuthenticationToken(userDetails1, null, userDetails1.getAuthorities()));

        UserDetailsImpl adminDetails = UserDetailsImpl.build(adminUser);
        adminToken = jwtUtils.generateJwtToken(
                new UsernamePasswordAuthenticationToken(adminDetails, null, adminDetails.getAuthorities()));

        SecurityContextHolder.clearContext();
    }

    @Test
    void testGetAllUsuarios() throws Exception {
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.content[0].nombreUsuario", is("user1")))
                .andExpect(jsonPath("$.content[1].nombreUsuario", is("user2")))
                .andExpect(jsonPath("$.content[2].nombreUsuario", is("admin")));
    }

    @Test
    void testGetUsuarioById() throws Exception {
        mockMvc.perform(get("/api/usuarios/{id}", user1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreUsuario", is("user1")));
    }

    @Test
    void testGetUsuarioByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/usuarios/{id}", 99))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateUsuarioSuccess() throws Exception {
        UsuarioDto updatedDto = new UsuarioDto();
        updatedDto.setNombreUsuario("user1Updated");
        updatedDto.setEmail("user1updated@example.com");
        updatedDto.setUrlFotoPerfil("new_photo.jpg");

        mockMvc.perform(put("/api/usuarios/{id}", user1.getId())
                .header("Authorization", "Bearer " + user1Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreUsuario", is("user1Updated")))
                .andExpect(jsonPath("$.email", is("user1updated@example.com")));

        assertEquals("user1Updated", usuarioRepository.findById(user1.getId()).get().getNombreUsuario());
    }

    @Test
    void testUpdateUsuarioForbidden() throws Exception {
        UsuarioDto updatedDto = new UsuarioDto();
        updatedDto.setNombreUsuario("user1Updated");

        mockMvc.perform(put("/api/usuarios/{id}", user2.getId()) // user1 intenta actualizar user2
                .header("Authorization", "Bearer " + user1Token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeleteUsuarioAsAdminSuccess() throws Exception {
        mockMvc.perform(delete("/api/usuarios/admin/{id}", user1.getId())
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        assertFalse(usuarioRepository.existsById(user1.getId()));
    }

    @Test
    void testDeleteUsuarioAsUserForbidden() throws Exception {
        mockMvc.perform(delete("/api/usuarios/admin/{id}", user2.getId())
                .header("Authorization", "Bearer " + user1Token))
                .andExpect(status().isForbidden());
    }

    @Test
    void testFollowUsuarioSuccess() throws Exception {
        mockMvc.perform(post("/api/usuarios/{id}/seguir", user2.getId())
                .header("Authorization", "Bearer " + user1Token))
                .andExpect(status().isNoContent());

        assertTrue(seguidorRepository.existsById(new SeguidorId(user1, user2)));
    }

    @Test
    void testFollowUsuarioAlreadyFollowing() throws Exception {
        seguidorRepository.save(new Seguidor(user1, user2));

        mockMvc.perform(post("/api/usuarios/{id}/seguir", user2.getId())
                .header("Authorization", "Bearer " + user1Token))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUnfollowUsuarioSuccess() throws Exception {
        seguidorRepository.save(new Seguidor(user1, user2));

        mockMvc.perform(delete("/api/usuarios/{id}/seguir", user2.getId())
                .header("Authorization", "Bearer " + user1Token))
                .andExpect(status().isNoContent());

        assertFalse(seguidorRepository.existsById(new SeguidorId(user1, user2)));
    }

    @Test
    void testUnfollowUsuarioNotFollowing() throws Exception {
        mockMvc.perform(delete("/api/usuarios/{id}/seguir", user2.getId())
                .header("Authorization", "Bearer " + user1Token))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetSeguidores() throws Exception {
        seguidorRepository.save(new Seguidor(user2, user1)); // user2 sigue a user1

        mockMvc.perform(get("/api/usuarios/{id}/seguidores", user1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].nombreUsuario", is("user2")));
    }

    @Test
    void testGetSiguiendo() throws Exception {
        seguidorRepository.save(new Seguidor(user1, user2)); // user1 sigue a user2

        mockMvc.perform(get("/api/usuarios/{id}/siguiendo", user1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].nombreUsuario", is("user2")));
    }
}
