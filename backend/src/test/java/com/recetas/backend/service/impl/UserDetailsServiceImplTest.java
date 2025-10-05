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
    @DisplayName("Debería cargar el usuario por email")
    void loadUserByUsername_shouldLoadUserByEmail() {
        // Simula el comportamiento del repositorio para encontrar por email
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(usuario));

        // Llama al método del servicio
        UserDetails userDetails = userDetailsService.loadUserByUsername("test@example.com");

        // Verifica que los detalles del usuario no sean nulos y el nombre de usuario
        // sea el esperado
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());

        // Verifica que el método findByEmail del repositorio fue llamado una vez
        verify(usuarioRepository, times(1)).findByEmail(anyString());
        verify(usuarioRepository, times(0)).findByNombreUsuario(anyString()); // No debería llamar a findByNombreUsuario
    }

    @Test
    @DisplayName("Debería lanzar UsernameNotFoundException si el usuario no es encontrado por email")
    void loadUserByUsername_shouldThrowUsernameNotFoundException() {
        // Simula el comportamiento del repositorio para no encontrar el usuario por
        // email
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Verifica que se lanza la excepción esperada
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("nonexistent@example.com"));

        // Verifica el mensaje de la excepción
        assertEquals("Usuario no encontrado con el email: nonexistent@example.com", exception.getMessage());

        // Verifica que el método findByEmail del repositorio fue llamado una vez
        verify(usuarioRepository, times(1)).findByEmail(anyString());
        verify(usuarioRepository, times(0)).findByNombreUsuario(anyString()); // No debería llamar a findByNombreUsuario
    }
}
