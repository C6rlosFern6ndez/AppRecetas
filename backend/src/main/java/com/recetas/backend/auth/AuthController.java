package com.recetas.backend.auth;

import com.recetas.backend.dtos.JwtResponse;
import com.recetas.backend.dtos.LoginDto;
import com.recetas.backend.dtos.RegistroDto;
import com.recetas.backend.dtos.UsuarioDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.recetas.backend.models.Usuario;
import com.recetas.backend.security.UserDetailsImpl;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.validation.Valid;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/public/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginDto loginRequest) {
        String jwt = authService.authenticateUser(loginRequest);
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal();

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                userDetails.getAuthorities().stream().map(item -> item.getAuthority()).collect(Collectors.toList())));
    }

    @PostMapping("/registro")
    public ResponseEntity<UsuarioDto> registerUser(@Valid @RequestBody RegistroDto signUpRequest) {
        Usuario usuario = authService.registerUser(signUpRequest);
        return new ResponseEntity<>(UsuarioDto.fromEntity(usuario), HttpStatus.CREATED);
    }
}
