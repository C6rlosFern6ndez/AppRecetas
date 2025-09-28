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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
    void registerUser_userAlreadyExists() {
        when(userService.registrarUsuario(any(SignupRequestDto.class)))
                .thenThrow(new IllegalArgumentException("El correo electrónico ya está en uso."));

        ResponseEntity<?> response = authController.registrarUsuario(signupRequestDto);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("El correo electrónico ya está en uso.", response.getBody());

        verify(userService, times(1)).registrarUsuario(any(SignupRequestDto.class));
    }

    @Test
    void loginUser_success() {
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        String jwt = "mock_jwt_token";
        when(jwtUtils.generateJwtToken(authentication)).thenReturn(jwt);

        ResponseEntity<LoginResponseDto> response = authController.autenticarUsuario(loginRequestDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(jwt, response.getBody().getToken());

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils, times(1)).generateJwtToken(authentication);
    }

    @Test
    void loginUser_invalidCredentials() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThrows(BadCredentialsException.class, () -> {
            authController.autenticarUsuario(loginRequestDto);
        });

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoInteractions(jwtUtils);
    }
}
