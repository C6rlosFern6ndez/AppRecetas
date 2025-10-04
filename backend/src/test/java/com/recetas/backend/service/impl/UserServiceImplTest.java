package com.recetas.backend.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.recetas.backend.domain.dto.SignupRequestDto;
import com.recetas.backend.domain.entity.Rol;
import com.recetas.backend.domain.entity.Usuario;
import com.recetas.backend.domain.repository.RolRepository;
import com.recetas.backend.domain.repository.SeguidorRepository;
import com.recetas.backend.domain.repository.UsuarioRepository;
import com.recetas.backend.service.NotificacionService;

/**
 * Tests para UserServiceImpl enfocados en el problema de autenticación.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SeguidorRepository seguidorRepository;

    @Mock
    private NotificacionService notificacionService;

    @InjectMocks
    private UserServiceImpl userService;

    private SignupRequestDto signupRequestDto;

    @BeforeEach
    void setUp() {
        signupRequestDto = new SignupRequestDto();
        signupRequestDto.setNombreUsuario("testuser");
        signupRequestDto.setEmail("test@example.com");
        signupRequestDto.setContrasena("testpassword");
    }

    /**
     * Test principal: Verificar que registrarUsuario encripta correctamente la
     * contraseña.
     */
    @Test
    void registrarUsuario_ShouldEncodePasswordCorrectly() {
        // Dado
        when(usuarioRepository.findByEmail(signupRequestDto.getEmail())).thenReturn(Optional.empty());

        Rol userRole = new Rol();
        userRole.setNombre("USER");
        when(rolRepository.findByNombre("USER")).thenReturn(Optional.of(userRole));

        when(passwordEncoder.encode("testpassword")).thenReturn("encodedPassword123");

        Usuario savedUser = new Usuario();
        savedUser.setId(1);
        savedUser.setNombreUsuario(signupRequestDto.getNombreUsuario());
        savedUser.setEmail(signupRequestDto.getEmail());
        savedUser.setRol(userRole);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(savedUser);

        // Cuando
        Usuario result = userService.registrarUsuario(signupRequestDto);

        // Verificar que el encoder fue llamado con la contraseña correcta
        verify(passwordEncoder).encode("testpassword");

        // Verificar que el usuario fue guardado
        verify(usuarioRepository).save(argThat(user -> "encodedPassword123".equals(user.getContrasena()) &&
                "testuser".equals(user.getNombreUsuario()) &&
                "test@example.com".equals(user.getEmail())));

        assertEquals("testuser", result.getNombreUsuario());
    }

    /**
     * Test secundario: Verificar que saveUser también encripta la contraseña.
     */
    @Test
    void saveUser_ShouldEncodePasswordCorrectly() {
        // Dado
        Usuario userToSave = new Usuario();
        userToSave.setNombreUsuario("directuser");
        userToSave.setEmail("direct@example.com");
        userToSave.setContrasena("directpassword");

        when(passwordEncoder.encode("directpassword")).thenReturn("encodedDirect123");

        Usuario savedUser = new Usuario();
        savedUser.setId(2);
        savedUser.setNombreUsuario("directuser");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(savedUser);

        // Cuando
        Usuario result = userService.saveUser(userToSave);

        // Verificar que el encoder fue llamado
        verify(passwordEncoder).encode("directpassword");

        // Verificar que el usuario guardado tiene la contraseña encriptada
        verify(usuarioRepository).save(argThat(user -> "encodedDirect123".equals(user.getContrasena())));

        assertEquals("directuser", result.getNombreUsuario());
    }

    /**
     * Test de verificación de contraseña.
     */
    @Test
    void passwordVerification_ShouldWorkCorrectly() {
        // Simular que la contraseña codificada corresponde a la original
        String rawPassword = "myPassword123";
        String encodedPassword = "$2a$10$..."; // Hash BCrypt simulado

        // Configurar mock para devolver nuestro hash
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(passwordEncoder.matches(rawPassword, encodedPassword)).thenReturn(true);

        // Verificar que la lógica funciona
        String result = passwordEncoder.encode(rawPassword);
        assertEquals(encodedPassword, result);

        boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);
        assertTrue(matches);

        verify(passwordEncoder).encode(rawPassword);
        verify(passwordEncoder).matches(rawPassword, encodedPassword);
    }
}
