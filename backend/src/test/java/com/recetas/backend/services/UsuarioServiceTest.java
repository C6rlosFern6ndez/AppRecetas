package com.recetas.backend.services;

import com.recetas.backend.dtos.UsuarioDto;
import com.recetas.backend.exceptions.ResourceNotFoundException;
import com.recetas.backend.models.Rol;
import com.recetas.backend.models.Seguidor;
import com.recetas.backend.models.SeguidorId;
import com.recetas.backend.models.Usuario;
import com.recetas.backend.repositories.SeguidorRepository;
import com.recetas.backend.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private SeguidorRepository seguidorRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario1;
    private Usuario usuario2;
    private Rol userRole;

    @BeforeEach
    void setUp() {
        userRole = new Rol(1, "USER");

        usuario1 = new Usuario();
        usuario1.setId(1);
        usuario1.setNombreUsuario("user1");
        usuario1.setEmail("user1@example.com");
        usuario1.setContrasena("pass1");
        usuario1.setUrlFotoPerfil("foto1.jpg");
        usuario1.setFechaRegistro(LocalDateTime.now());
        usuario1.setRol(userRole);
        usuario1.setSiguiendo(new HashSet<>());
        usuario1.setSeguidores(new HashSet<>());

        usuario2 = new Usuario();
        usuario2.setId(2);
        usuario2.setNombreUsuario("user2");
        usuario2.setEmail("user2@example.com");
        usuario2.setContrasena("pass2");
        usuario2.setUrlFotoPerfil("foto2.jpg");
        usuario2.setFechaRegistro(LocalDateTime.now());
        usuario2.setRol(userRole);
        usuario2.setSiguiendo(new HashSet<>());
        usuario2.setSeguidores(new HashSet<>());
    }

    @Test
    void testGetUsuarioByIdFound() {
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario1));

        UsuarioDto result = usuarioService.getUsuarioById(1);

        assertNotNull(result);
        assertEquals("user1", result.getNombreUsuario());
    }

    @Test
    void testGetUsuarioByIdNotFound() {
        when(usuarioRepository.findById(99)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            usuarioService.getUsuarioById(99);
        });

        assertEquals("Usuario no encontrado con ID: 99", exception.getMessage());
    }

    @Test
    void testUpdateUsuarioSuccess() {
        UsuarioDto updatedDto = new UsuarioDto();
        updatedDto.setNombreUsuario("updatedUser1");
        updatedDto.setEmail("updated1@example.com");
        updatedDto.setUrlFotoPerfil("updated_foto1.jpg");

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario1));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario1);

        UsuarioDto result = usuarioService.updateUsuario(1, updatedDto);

        assertNotNull(result);
        assertEquals("updatedUser1", result.getNombreUsuario());
        assertEquals("updated1@example.com", result.getEmail());
        assertEquals("updated_foto1.jpg", result.getUrlFotoPerfil());
    }

    @Test
    void testUpdateUsuarioNotFound() {
        UsuarioDto updatedDto = new UsuarioDto();
        when(usuarioRepository.findById(99)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            usuarioService.updateUsuario(99, updatedDto);
        });

        assertEquals("Usuario no encontrado con ID: 99", exception.getMessage());
    }

    @Test
    void testDeleteUsuarioSuccess() {
        when(usuarioRepository.existsById(1)).thenReturn(true);
        doNothing().when(usuarioRepository).deleteById(1);

        usuarioService.deleteUsuario(1);

        verify(usuarioRepository, times(1)).deleteById(1);
    }

    @Test
    void testDeleteUsuarioNotFound() {
        when(usuarioRepository.existsById(99)).thenReturn(false);

        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            usuarioService.deleteUsuario(99);
        });

        assertEquals("Usuario no encontrado con ID: 99", exception.getMessage());
    }

    @Test
    void testFollowUsuarioSuccess() {
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario1));
        when(usuarioRepository.findById(2)).thenReturn(Optional.of(usuario2));
        when(seguidorRepository.existsById(any(SeguidorId.class))).thenReturn(false);
        when(seguidorRepository.save(any(Seguidor.class))).thenReturn(new Seguidor(usuario1, usuario2));

        usuarioService.followUsuario(1, 2);

        verify(seguidorRepository, times(1)).save(any(Seguidor.class));
    }

    @Test
    void testFollowUsuarioAlreadyFollowing() {
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario1));
        when(usuarioRepository.findById(2)).thenReturn(Optional.of(usuario2));
        when(seguidorRepository.existsById(any(SeguidorId.class))).thenReturn(true);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.followUsuario(1, 2);
        });

        assertEquals("Ya sigues a este usuario.", exception.getMessage());
        verify(seguidorRepository, never()).save(any(Seguidor.class));
    }

    @Test
    void testUnfollowUsuarioSuccess() {
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario1));
        when(usuarioRepository.findById(2)).thenReturn(Optional.of(usuario2));
        when(seguidorRepository.existsById(any(SeguidorId.class))).thenReturn(true);
        doNothing().when(seguidorRepository).deleteById(any(SeguidorId.class));

        usuarioService.unfollowUsuario(1, 2);

        verify(seguidorRepository, times(1)).deleteById(any(SeguidorId.class));
    }

    @Test
    void testUnfollowUsuarioNotFollowing() {
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario1));
        when(usuarioRepository.findById(2)).thenReturn(Optional.of(usuario2));
        when(seguidorRepository.existsById(any(SeguidorId.class))).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.unfollowUsuario(1, 2);
        });

        assertEquals("No sigues a este usuario.", exception.getMessage());
        verify(seguidorRepository, never()).deleteById(any(SeguidorId.class));
    }

    @Test
    void testGetSeguidores() {
        Pageable pageable = PageRequest.of(0, 10);
        Seguidor seguidorRelacion = new Seguidor(usuario2, usuario1); // user2 sigue a user1
        Page<Seguidor> seguidoresPage = new PageImpl<>(Collections.singletonList(seguidorRelacion), pageable, 1);

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario1));
        when(seguidorRepository.findBySeguido(usuario1, pageable)).thenReturn(seguidoresPage);

        Page<UsuarioDto> result = usuarioService.getSeguidores(1, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("user2", result.getContent().get(0).getNombreUsuario());
    }

    @Test
    void testGetSiguiendo() {
        Pageable pageable = PageRequest.of(0, 10);
        Seguidor siguiendoRelacion = new Seguidor(usuario1, usuario2); // user1 sigue a user2
        Page<Seguidor> siguiendoPage = new PageImpl<>(Collections.singletonList(siguiendoRelacion), pageable, 1);

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario1));
        when(seguidorRepository.findBySeguidor(usuario1, pageable)).thenReturn(siguiendoPage);

        Page<UsuarioDto> result = usuarioService.getSiguiendo(1, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("user2", result.getContent().get(0).getNombreUsuario());
    }

    @Test
    void testGetAllUsuarios() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Usuario> usuarioPage = new PageImpl<>(Arrays.asList(usuario1, usuario2), pageable, 2);

        when(usuarioRepository.findAll(pageable)).thenReturn(usuarioPage);

        Page<UsuarioDto> result = usuarioService.getAllUsuarios(pageable);

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertEquals("user1", result.getContent().get(0).getNombreUsuario());
        assertEquals("user2", result.getContent().get(1).getNombreUsuario());
    }
}
