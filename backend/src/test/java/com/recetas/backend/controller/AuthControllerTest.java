package com.recetas.backend.controller;

import com.recetas.backend.domain.dto.LoginRequestDto;
import com.recetas.backend.domain.dto.LoginResponseDto;
import com.recetas.backend.domain.dto.SignupRequestDto;
import com.recetas.backend.domain.entity.Rol; // Import Rol
import com.recetas.backend.domain.entity.Usuario;
import com.recetas.backend.security.JwtUtils;
import com.recetas.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import com.recetas.backend.exception.EmailAlreadyInUseException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserService userService;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthController authController;

    private SignupRequestDto signupRequestDto;
    private LoginRequestDto loginRequestDto;
    private Usuario testUser;

    @BeforeEach
    void setUp() {
        signupRequestDto = new SignupRequestDto();
        signupRequestDto.setNombreUsuario("testUser");
        signupRequestDto.setEmail("test@example.com");
        signupRequestDto.setContrasena("password");

        loginRequestDto = new LoginRequestDto();
        loginRequestDto.setNombreUsuarioOrEmail("test@example.com");
        loginRequestDto.setContrasena("password");

        testUser = new Usuario();
        testUser.setId(1);
        testUser.setNombreUsuario("testUser");
        testUser.setEmail("test@example.com");
        testUser.setContrasena("encodedPassword");
        Set<Rol> roles = new HashSet<>();
        Rol userRole = new Rol();
        userRole.setNombre("ROLE_USER");
        roles.add(userRole);
        testUser.setRoles(roles);
    }

    @Test
    void registerUser_success() {
        when(userService.registrarUsuario(any(SignupRequestDto.class))).thenReturn(testUser);

        ResponseEntity<?> response = authController.registrarUsuario(signupRequestDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testUser.getNombreUsuario(), ((Usuario) response.getBody()).getNombreUsuario());

        verify(userService, times(1)).registrarUsuario(any(SignupRequestDto.class));
    }

    @Test
    void registerUser_emailAlreadyInUse() {
        when(userService.registrarUsuario(any(SignupRequestDto.class)))
                .thenThrow(new EmailAlreadyInUseException("El correo electrónico ya está en uso."));

        EmailAlreadyInUseException exception = assertThrows(EmailAlreadyInUseException.class, () -> {
            authController.registrarUsuario(signupRequestDto);
        });

        assertEquals("El correo electrónico ya está en uso.", exception.getMessage());
        verify(userService, times(1)).registrarUsuario(any(SignupRequestDto.class));
    }

    @Test
    void loginUser_success() {
        // Mockear el comportamiento de userService para encontrar el usuario
        when(userService.findByNombreUsuario(loginRequestDto.getNombreUsuarioOrEmail())).thenReturn(Optional.empty());
        when(userService.findByEmail(loginRequestDto.getNombreUsuarioOrEmail())).thenReturn(Optional.of(testUser));

        // Mockear la autenticación
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        String jwt = "mock_jwt_token";
        when(jwtUtils.generateJwtToken(authentication)).thenReturn(jwt);

        ResponseEntity<LoginResponseDto> response = authController.autenticarUsuario(loginRequestDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(jwt, response.getBody().getToken());

        verify(userService, times(1)).findByNombreUsuario(loginRequestDto.getNombreUsuarioOrEmail());
        verify(userService, times(1)).findByEmail(loginRequestDto.getNombreUsuarioOrEmail());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils, times(1)).generateJwtToken(authentication);
    }

    @Test
    void loginUser_userNotFound() {
        when(userService.findByNombreUsuario(loginRequestDto.getNombreUsuarioOrEmail())).thenReturn(Optional.empty());
        when(userService.findByEmail(loginRequestDto.getNombreUsuarioOrEmail())).thenReturn(Optional.empty());

        ResponseEntity<LoginResponseDto> response = authController.autenticarUsuario(loginRequestDto);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Credenciales inválidas", response.getBody().getToken());

        verify(userService, times(1)).findByNombreUsuario(loginRequestDto.getNombreUsuarioOrEmail());
        verify(userService, times(1)).findByEmail(loginRequestDto.getNombreUsuarioOrEmail());
        verifyNoInteractions(authenticationManager);
        verifyNoInteractions(jwtUtils);
    }

    @Test
    void loginUser_incorrectPassword() {
        // Mockear el comportamiento de userService para encontrar el usuario
        when(userService.findByNombreUsuario(loginRequestDto.getNombreUsuarioOrEmail())).thenReturn(Optional.empty());
        when(userService.findByEmail(loginRequestDto.getNombreUsuarioOrEmail())).thenReturn(Optional.of(testUser));

        // Mockear que la autenticación falla debido a credenciales incorrectas
        given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .willThrow(new org.springframework.security.authentication.BadCredentialsException("Bad credentials"));

        org.springframework.security.authentication.BadCredentialsException exception = assertThrows(
                org.springframework.security.authentication.BadCredentialsException.class, () -> {
                    authController.autenticarUsuario(loginRequestDto);
                });

        assertEquals("Bad credentials", exception.getMessage());
        verify(userService, times(1)).findByNombreUsuario(loginRequestDto.getNombreUsuarioOrEmail());
        verify(userService, times(1)).findByEmail(loginRequestDto.getNombreUsuarioOrEmail());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoInteractions(jwtUtils);
    }
}
