package com.recetas.backend.auth;

import com.recetas.backend.dtos.LoginDto;
import com.recetas.backend.dtos.RegistroDto;
import com.recetas.backend.models.Rol;
import com.recetas.backend.models.Usuario;
import com.recetas.backend.repositories.RolRepository;
import com.recetas.backend.repositories.UsuarioRepository;
import com.recetas.backend.security.JwtUtils;
import com.recetas.backend.security.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthService authService;

    private LoginDto loginDto;
    private RegistroDto registroDto;
    private Usuario usuario;
    private Rol userRole;

    @BeforeEach
    void setUp() {
        loginDto = new LoginDto();
        loginDto.setEmail("test@example.com");
        loginDto.setContrasena("password");

        registroDto = new RegistroDto();
        registroDto.setNombreUsuario("testuser");
        registroDto.setEmail("test@example.com");
        registroDto.setContrasena("password");

        userRole = new Rol(1, "USER");
        usuario = new Usuario();
        usuario.setId(1);
        usuario.setNombreUsuario("testuser");
        usuario.setEmail("test@example.com");
        usuario.setContrasena("encodedPassword");
        usuario.setRol(userRole);
    }

    @Test
    void testAuthenticateUserSuccess() {
        Authentication authentication = mock(Authentication.class);
        UserDetailsImpl userDetails = UserDetailsImpl.build(usuario);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn("mockJwtToken");

        String jwt = authService.authenticateUser(loginDto);

        assertNotNull(jwt);
        assertEquals("mockJwtToken", jwt);
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils, times(1)).generateJwtToken(authentication);
    }

    @Test
    void testRegisterUserSuccess() {
        when(usuarioRepository.existsByEmail(registroDto.getEmail())).thenReturn(false);
        when(usuarioRepository.existsByNombreUsuario(registroDto.getNombreUsuario())).thenReturn(false);
        when(passwordEncoder.encode(registroDto.getContrasena())).thenReturn("encodedPassword");
        when(rolRepository.findByNombre("USER")).thenReturn(Optional.of(userRole));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario registeredUser = authService.registerUser(registroDto);

        assertNotNull(registeredUser);
        assertEquals("testuser", registeredUser.getNombreUsuario());
        assertEquals("test@example.com", registeredUser.getEmail());
        assertEquals("encodedPassword", registeredUser.getContrasena());
        assertEquals("USER", registeredUser.getRol().getNombre());

        verify(usuarioRepository, times(1)).existsByEmail(registroDto.getEmail());
        verify(usuarioRepository, times(1)).existsByNombreUsuario(registroDto.getNombreUsuario());
        verify(passwordEncoder, times(1)).encode(registroDto.getContrasena());
        verify(rolRepository, times(1)).findByNombre("USER");
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void testRegisterUserEmailExists() {
        when(usuarioRepository.existsByEmail(registroDto.getEmail())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.registerUser(registroDto);
        });

        assertEquals("Error: ¡El email ya está en uso!", exception.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void testRegisterUserNameExists() {
        when(usuarioRepository.existsByEmail(registroDto.getEmail())).thenReturn(false);
        when(usuarioRepository.existsByNombreUsuario(registroDto.getNombreUsuario())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.registerUser(registroDto);
        });

        assertEquals("Error: ¡El nombre de usuario ya está en uso!", exception.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void testRegisterUserRoleNotFound() {
        when(usuarioRepository.existsByEmail(registroDto.getEmail())).thenReturn(false);
        when(usuarioRepository.existsByNombreUsuario(registroDto.getNombreUsuario())).thenReturn(false);
        when(passwordEncoder.encode(registroDto.getContrasena())).thenReturn("encodedPassword");
        when(rolRepository.findByNombre("USER")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.registerUser(registroDto);
        });

        assertEquals("Error: Rol de usuario no encontrado.", exception.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }
}
