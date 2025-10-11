package com.recetas.backend.auth;

import com.recetas.backend.models.Rol;
import com.recetas.backend.models.Usuario;
import com.recetas.backend.repositories.RolRepository;
import com.recetas.backend.repositories.UsuarioRepository;
import com.recetas.backend.security.JwtUtils;
import com.recetas.backend.security.UserDetailsImpl;
import com.recetas.backend.dtos.LoginDto;
import com.recetas.backend.dtos.RegistroDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AuthService {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    RolRepository rolRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    public String authenticateUser(LoginDto loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getContrasena()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtUtils.generateJwtToken(authentication);
    }

    public Usuario registerUser(RegistroDto signUpRequest) {
        if (usuarioRepository.existsByNombreUsuario(signUpRequest.getNombreUsuario())) {
            throw new RuntimeException("Error: ¡El nombre de usuario ya está en uso!");
        }

        if (usuarioRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new RuntimeException("Error: ¡El email ya está en uso!");
        }

        // Crear nuevo usuario
        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(signUpRequest.getNombreUsuario());
        usuario.setEmail(signUpRequest.getEmail());
        usuario.setContrasena(encoder.encode(signUpRequest.getContrasena()));

        // Asignar rol por defecto (USER)
        Rol userRol = rolRepository.findByNombre("USER")
                .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado."));
        usuario.setRol(userRol);

        return usuarioRepository.save(usuario);
    }
}
