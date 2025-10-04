package com.recetas.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.recetas.backend.domain.dto.LoginRequestDto;
import com.recetas.backend.domain.dto.LoginResponseDto;
import com.recetas.backend.domain.dto.SignupRequestDto;
import com.recetas.backend.domain.entity.Usuario;
import com.recetas.backend.security.JwtUtils;
import com.recetas.backend.service.UserService;

/**
 * Controlador para manejar las operaciones de autenticación.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * Endpoint para el registro de nuevos usuarios.
     *
     * @param signupRequestDto DTO con los datos del nuevo usuario.
     * @return ResponseEntity con el usuario creado o un mensaje de error.
     */
    @PostMapping("/signup")
    public ResponseEntity<?> registrarUsuario(@Valid @RequestBody SignupRequestDto signupRequestDto) {
        Usuario usuario = userService.registrarUsuario(signupRequestDto);
        // En una aplicación real, aquí podrías devolver un DTO más ligero o un mensaje
        // de éxito.
        // Por ahora, devolvemos el usuario completo para verificación.
        return new ResponseEntity<>(usuario, HttpStatus.CREATED);
    }

    /**
     * Endpoint para el inicio de sesión de usuarios.
     *
     * @param loginRequestDto DTO con las credenciales del usuario (email).
     * @return ResponseEntity con el token JWT o un mensaje de error.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> autenticarUsuario(@Valid @RequestBody LoginRequestDto loginRequestDto) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(),
                        loginRequestDto.getContrasena()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        return ResponseEntity.ok(new LoginResponseDto(jwt));
    }
}
