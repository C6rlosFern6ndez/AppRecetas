package com.recetas.backend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recetas.backend.dtos.CategoriaDto;
import com.recetas.backend.models.Categoria;
import com.recetas.backend.repositories.CategoriaRepository;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*; // Añadir esta importación estática

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class CategoriaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private JwtUtils jwtUtils;

    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() {
        // Limpiar y configurar roles y usuarios para las pruebas
        categoriaRepository.deleteAll();
        usuarioRepository.deleteAll();
        rolRepository.deleteAll();

        com.recetas.backend.models.Rol userRoleEntity = new com.recetas.backend.models.Rol();
        userRoleEntity.setId(1);
        userRoleEntity.setNombre("USER");
        rolRepository.save(userRoleEntity);

        com.recetas.backend.models.Rol adminRoleEntity = new com.recetas.backend.models.Rol();
        adminRoleEntity.setId(2);
        adminRoleEntity.setNombre("ADMIN");
        rolRepository.save(adminRoleEntity);

        com.recetas.backend.models.Rol superAdminRoleEntity = new com.recetas.backend.models.Rol();
        superAdminRoleEntity.setId(3);
        superAdminRoleEntity.setNombre("SUPERADMIN");
        rolRepository.save(superAdminRoleEntity);

        com.recetas.backend.models.Rol userRole = rolRepository.findByNombre("USER").get();
        com.recetas.backend.models.Rol adminRole = rolRepository.findByNombre("ADMIN").get();

        com.recetas.backend.models.Usuario user = new com.recetas.backend.models.Usuario();
        user.setId(1);
        user.setNombreUsuario("testuser");
        user.setEmail("user@example.com");
        user.setContrasena("password"); // BCrypt en un entorno real
        user.setRol(userRole);
        usuarioRepository.save(user);

        com.recetas.backend.models.Usuario admin = new com.recetas.backend.models.Usuario();
        admin.setId(2);
        admin.setNombreUsuario("testadmin");
        admin.setEmail("admin@example.com");
        admin.setContrasena("adminpass"); // BCrypt en un entorno real
        admin.setRol(adminRole);
        usuarioRepository.save(admin);

        // Generar tokens JWT para usuarios de prueba
        UserDetailsImpl userDetailsUser = UserDetailsImpl.build(user);
        userToken = jwtUtils.generateJwtToken(new UsernamePasswordAuthenticationToken(userDetailsUser, null,
                userDetailsUser.getAuthorities()));

        UserDetailsImpl userDetailsAdmin = UserDetailsImpl.build(admin);
        adminToken = jwtUtils.generateJwtToken(new UsernamePasswordAuthenticationToken(userDetailsAdmin, null,
                userDetailsAdmin.getAuthorities()));

        // Asegurarse de que el contexto de seguridad esté limpio
        SecurityContextHolder.clearContext();
    }

    @Test
    void testGetAllCategorias() throws Exception {
        categoriaRepository.save(new Categoria(null, "Postres", null));
        categoriaRepository.save(new Categoria(null, "Vegetariano", null));

        mockMvc.perform(get("/api/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].nombre", is("Postres")))
                .andExpect(jsonPath("$.content[1].nombre", is("Vegetariano")));
    }

    @Test
    void testGetCategoriaById() throws Exception {
        Categoria categoria = categoriaRepository.save(new Categoria(null, "Postres", null));

        mockMvc.perform(get("/api/categorias/{id}", categoria.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Postres")));
    }

    @Test
    void testGetCategoriaByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/categorias/{id}", 99))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateCategoriaAsAdmin() throws Exception {
        CategoriaDto newCategoria = new CategoriaDto();
        newCategoria.setNombre("Carnes");

        mockMvc.perform(post("/api/categorias/admin")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCategoria)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre", is("Carnes")));

        assertEquals(1, categoriaRepository.findByNombre("Carnes").get().getId());
    }

    @Test
    void testCreateCategoriaAsUserForbidden() throws Exception {
        CategoriaDto newCategoria = new CategoriaDto();
        newCategoria.setNombre("Carnes");

        mockMvc.perform(post("/api/categorias/admin")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCategoria)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreateCategoriaUnauthorized() throws Exception {
        CategoriaDto newCategoria = new CategoriaDto();
        newCategoria.setNombre("Carnes");

        mockMvc.perform(post("/api/categorias/admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCategoria)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testUpdateCategoriaAsAdmin() throws Exception {
        Categoria categoria = categoriaRepository.save(new Categoria(null, "Postres", null));
        CategoriaDto updatedCategoria = new CategoriaDto();
        updatedCategoria.setNombre("Postres Actualizados");

        mockMvc.perform(put("/api/categorias/admin/{id}", categoria.getId())
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedCategoria)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Postres Actualizados")));

        assertEquals("Postres Actualizados", categoriaRepository.findById(categoria.getId()).get().getNombre());
    }

    @Test
    void testUpdateCategoriaAsUserForbidden() throws Exception {
        Categoria categoria = categoriaRepository.save(new Categoria(null, "Postres", null));
        CategoriaDto updatedCategoria = new CategoriaDto();
        updatedCategoria.setNombre("Postres Actualizados");

        mockMvc.perform(put("/api/categorias/admin/{id}", categoria.getId())
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedCategoria)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeleteCategoriaAsAdmin() throws Exception {
        Categoria categoria = categoriaRepository.save(new Categoria(null, "Postres", null));

        mockMvc.perform(delete("/api/categorias/admin/{id}", categoria.getId())
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());

        assertFalse(categoriaRepository.existsById(categoria.getId()));
    }

    @Test
    void testDeleteCategoriaAsUserForbidden() throws Exception {
        Categoria categoria = categoriaRepository.save(new Categoria(null, "Postres", null));

        mockMvc.perform(delete("/api/categorias/admin/{id}", categoria.getId())
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }
}
