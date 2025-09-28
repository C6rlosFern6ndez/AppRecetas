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
        // Use setters for DTOs if constructors are not available or to be explicit
        signupRequestDto = new SignupRequestDto();
        signupRequestDto.setNombreUsuario("testUser");
        signupRequestDto.setEmail("test@example.com");
        signupRequestDto.setContrasena("password"); // Corrected from setPassword

        loginRequestDto = new LoginRequestDto();
        loginRequestDto.setNombreUsuarioOrEmail("test@example.com"); // Assuming LoginRequestDto uses email for login
        loginRequestDto.setContrasena("password");

        testUser = new Usuario();
        testUser.setId(1);
        testUser.setNombreUsuario("testUser");
        testUser.setEmail("test@example.com");
        testUser.setContrasena("password"); // This will be encoded by saveUser
        Set<Rol> roles = new HashSet<>();
        Rol userRole = new Rol();
        userRole.setNombre("ROLE_USER"); // Assuming Rol has a setName method
        roles.add(userRole);
        testUser.setRoles(roles);
    }

    @Test
    void registerUser_success() {
        when(userService.findByEmail(signupRequestDto.getEmail())).thenReturn(Optional.empty()); // User does not exist
        when(userService.saveUser(any(Usuario.class))).thenReturn(testUser); // Mock saveUser to return the created user

        ResponseEntity<?> response = authController.registrarUsuario(signupRequestDto); // Corrected method name

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        // The controller returns the savedUser object, not a string message
        assertEquals(testUser.getNombreUsuario(), ((Usuario) response.getBody()).getNombreUsuario());

        verify(userService, times(1)).findByEmail(signupRequestDto.getEmail());
        verify(userService, times(1)).saveUser(any(Usuario.class));
        verifyNoInteractions(authenticationManager, jwtUtils);
    }

    @Test
    void registerUser_userAlreadyExists() {
        when(userService.findByEmail(signupRequestDto.getEmail())).thenReturn(Optional.of(testUser)); // Simulate user
                                                                                                      // already exists

        ResponseEntity<?> response = authController.registrarUsuario(signupRequestDto); // Corrected method name

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("El correo electrónico ya está en uso.", response.getBody());

        verify(userService, times(1)).findByEmail(signupRequestDto.getEmail());
        verifyNoInteractions(userService, authenticationManager, jwtUtils); // Ensure saveUser is not called
    }

    @Test
    void loginUser_success() {
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication); // Set mock authentication

        String jwt = "mock_jwt_token";
        when(jwtUtils.generateJwtToken(authentication)).thenReturn(jwt);

        UserDetails userDetails = mock(UserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(testUser.getEmail()); // UserDetails username is typically email or
                                                                         // username
        when(userService.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser)); // Find user by email

        ResponseEntity<LoginResponseDto> response = authController.autenticarUsuario(loginRequestDto); // Corrected
                                                                                                       // method name

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(jwt, response.getBody().getToken());
        // The LoginResponseDto only contains the JWT token, not user details.
        // Remove assertions for user details that are not returned by the controller.
        // assertEquals(testUser.getId(), response.getBody().getId()); // Method getId()
        // is undefined for LoginResponseDto
        // assertEquals(testUser.getNombreUsuario(),
        // response.getBody().getNombreUsuario()); // Method getNombreUsuario() is
        // undefined for LoginResponseDto
        // assertEquals(testUser.getEmail(), response.getBody().getEmail()); // Method
        // getEmail() is undefined for LoginResponseDto
        // Assert roles correctly
        // assertEquals(Collections.singletonList("ROLE_USER"),
        // response.getBody().getRoles()); // Method getRoles() is undefined for
        // LoginResponseDto

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils, times(1)).generateJwtToken(authentication);
        verify(userService, times(1)).findByEmail(testUser.getEmail());
    }

    @Test
    void loginUser_invalidCredentials() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        ResponseEntity<LoginResponseDto> response = authController.autenticarUsuario(loginRequestDto); // Corrected
                                                                                                       // method name

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Credenciales inválidas", response.getBody());

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verifyNoInteractions(userService, jwtUtils);
    }

    @Test
    void loginUser_userNotFoundAfterAuth() {
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = "mock_jwt_token";
        when(jwtUtils.generateJwtToken(authentication)).thenReturn(jwt);

        UserDetails userDetails = mock(UserDetails.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("nonexistent@example.com"); // Username from UserDetails
        when(userService.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty()); // User not found

        ResponseEntity<LoginResponseDto> response = authController.autenticarUsuario(loginRequestDto); // Corrected
                                                                                                       // method name

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Usuario no encontrado tras autenticación", response.getBody()); // Updated error message

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils, times(1)).generateJwtToken(authentication);
        verify(userService, times(1)).findByEmail("nonexistent@example.com");
    }
}
