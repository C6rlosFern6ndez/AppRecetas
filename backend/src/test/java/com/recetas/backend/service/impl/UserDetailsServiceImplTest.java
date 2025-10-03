package com.recetas.backend.service.impl;

import com.recetas.backend.domain.entity.Usuario;
import com.recetas.backend.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class UserDetailsServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1);
        usuario.setNombreUsuario("testuser");
        usuario.setEmail("test@example.com");
        usuario.setContrasena("password");
    }

    @Test
    @DisplayName("Debería cargar el usuario por nombre de usuario")
    void loadUserByUsername_shouldLoadUserByUsername() {
        // Simula el comportamiento del repositorio para encontrar por nombre de usuario
        when(usuarioRepository.findByNombreUsuario(anyString())).thenReturn(Optional.of(usuario));

        // Llama al método del servicio
        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        // Verifica que los detalles del usuario no sean nulos y el nombre de usuario
        // sea el esperado
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());

        // Verifica que el método findByNombreUsuario del repositorio fue llamado una
        // vez
        verify(usuarioRepository, times(1)).findByNombreUsuario(anyString());
        verify(usuarioRepository, times(0)).findByEmail(anyString()); // No debería llamar a findByEmail si encuentra
                                                                      // por nombre de usuario
    }

    @Test
    @DisplayName("Debería cargar el usuario por email si no se encuentra por nombre de usuario")
    void loadUserByUsername_shouldLoadUserByEmail() {
        // Simula el comportamiento del repositorio para no encontrar por nombre de
        // usuario, pero sí por email
        when(usuarioRepository.findByNombreUsuario(anyString())).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(usuario));

        // Llama al método del servicio
        UserDetails userDetails = userDetailsService.loadUserByUsername("test@example.com");

        // Verifica que los detalles del usuario no sean nulos y el nombre de usuario
        // sea el esperado (que en este caso es el nombre de usuario)
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());

        // Verifica que ambos métodos del repositorio fueron llamados
        verify(usuarioRepository, times(1)).findByNombreUsuario(anyString());
        verify(usuarioRepository, times(1)).findByEmail(anyString());
    }

    @Test
    @DisplayName("Debería lanzar UsernameNotFoundException si el usuario no es encontrado")
    void loadUserByUsername_shouldThrowUsernameNotFoundException() {
        // Simula el comportamiento del repositorio para no encontrar el usuario
        when(usuarioRepository.findByNombreUsuario(anyString())).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Verifica que se lanza la excepción esperada
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("nonexistent"));

        // Verifica el mensaje de la excepción
        assertEquals("Usuario no encontrado con el nombre de usuario o email: nonexistent", exception.getMessage());

        // Verifica que ambos métodos del repositorio fueron llamados
        verify(usuarioRepository, times(1)).findByNombreUsuario(anyString());
        verify(usuarioRepository, times(1)).findByEmail(anyString());
    }
}
