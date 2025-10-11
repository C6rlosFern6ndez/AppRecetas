package com.recetas.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recetas.backend.dtos.IngredienteDto;
import com.recetas.backend.models.Ingrediente;
import com.recetas.backend.models.Rol;
import com.recetas.backend.models.Usuario;
import com.recetas.backend.repositories.IngredienteRepository;
import com.recetas.backend.repositories.RolRepository;
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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class IngredienteControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IngredienteRepository ingredienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    private Usuario adminUser;
    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() {
        ingredienteRepository.deleteAll();
        usuarioRepository.deleteAll();
        rolRepository.deleteAll();

        Rol userRole = new Rol(null, "USER");
        Rol adminRole = new Rol(null, "ADMIN");
        rolRepository.save(userRole);
        rolRepository.save(adminRole);

        userRole = rolRepository.findByNombre("USER").get();
        adminRole = rolRepository.findByNombre("ADMIN").get();

        Usuario normalUser = new Usuario();
        normalUser.setId(1);
        normalUser.setNombreUsuario("testuser");
        normalUser.setEmail("user@example.com");
        normalUser.setContrasena(passwordEncoder.encode("password"));
        normalUser.setRol(userRole);
        usuarioRepository.save(normalUser);

        adminUser = new Usuario();
        adminUser.setId(2);
        adminUser.setNombreUsuario("testadmin");
        adminUser.setEmail("admin@example.com");
        adminUser.setContrasena(passwordEncoder.encode("adminpass"));
        adminUser.setRol(adminRole);
        usuarioRepository.save(adminUser);

        UserDetailsImpl userDetailsNormal = UserDetailsImpl.build(normalUser);
        userToken = jwtUtils.generateJwtToken(
                new UsernamePasswordAuthenticationToken(userDetailsNormal, null, userDetailsNormal.getAuthorities()));

        UserDetailsImpl userDetailsAdmin = UserDetailsImpl.build(adminUser);
        adminToken = jwtUtils.generateJwtToken(
                new UsernamePasswordAuthenticationToken(userDetailsAdmin, null, userDetailsAdmin.getAuthorities()));

        SecurityContextHolder.clearContext();
    }

    @Test
    void testGetAllIngredientes() throws Exception {
        ingredienteRepository.save(new Ingrediente(null, "Sal", null));
        ingredienteRepository.save(new Ingrediente(null, "Pimienta", null));

        mockMvc.perform(get("/api/ingredientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].nombre", is("Sal")))
                .andExpect(jsonPath("$.content[1].nombre", is("Pimienta")));
    }

    @Test
    void testGetIngredienteById() throws Exception {
        Ingrediente ingrediente = ingredienteRepository.save(new Ingrediente(null, "Sal", null));

        mockMvc.perform(get("/api/ingredientes/{id}", ingrediente.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Sal")));
    }

    @Test
    void testGetIngredienteByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/ingredientes/{id}", 99))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateIngredienteAsAdmin() throws Exception {
        IngredienteDto newIngrediente = new IngredienteDto();
        newIngrediente.setNombre("Azúcar");

        mockMvc.perform(post("/api/ingredientes/admin")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newIngrediente)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre", is("Azúcar")));

        assertTrue(ingredienteRepository.findByNombre("Azúcar").isPresent());
    }

    @Test
    void testCreateIngredienteAsUserForbidden() throws Exception {
        IngredienteDto newIngrediente = new IngredienteDto();
        newIngrediente.setNombre("Azúcar");

        mockMvc.perform(post("/api/ingredientes/admin")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newIngrediente)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreateIngredienteUnauthorized() throws Exception {
        IngredienteDto newIngrediente = new IngredienteDto();
        newIngrediente.setNombre("Azúcar");

        mockMvc.perform(post("/api/ingredientes/admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newIngrediente)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUpdateIngredienteAsAdmin() throws Exception {
        Ingrediente ingrediente = ingredienteRepository.save(new Ingrediente(null, "Sal", null));
        IngredienteDto updatedIngrediente = new IngredienteDto();
        updatedIngrediente.setNombre("Sal Marina");

        mockMvc.perform(put("/api/ingredientes/admin/{id}", ingrediente.getId())
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedIngrediente)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Sal Marina")));

        assertEquals("Sal Marina", ingredienteRepository.findById(ingrediente.getId()).get().getNombre());
    }

    @Test
    void testUpdateIngredienteAsUserForbidden() throws Exception {
        Ingrediente ingrediente = ingredienteRepository.save(new Ingrediente(null, "Sal", null));
        IngredienteDto updatedIngrediente = new IngredienteDto();
        updatedIngrediente.setNombre("Sal Marina");

        mockMvc.perform(put("/api/ingredientes/admin/{id}", ingrediente.getId())
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedIngrediente)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeleteIngredienteAsAdmin() throws Exception {
        Ingrediente ingrediente = ingredienteRepository.save(new Ingrediente(null, "Sal", null));

        mockMvc.perform(delete("/api/ingredientes/admin/{id}", ingrediente.getId())
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        assertFalse(ingredienteRepository.existsById(ingrediente.getId()));
    }

    @Test
    void testDeleteIngredienteAsUserForbidden() throws Exception {
        Ingrediente ingrediente = ingredienteRepository.save(new Ingrediente(null, "Sal", null));

        mockMvc.perform(delete("/api/ingredientes/admin/{id}", ingrediente.getId())
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }
}
