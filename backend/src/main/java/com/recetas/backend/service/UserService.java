package com.recetas.backend.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.recetas.backend.domain.dto.SignupRequestDto;
import com.recetas.backend.domain.entity.Rol;
import com.recetas.backend.domain.entity.Usuario;
import com.recetas.backend.domain.repository.RolRepository;
import com.recetas.backend.domain.repository.UsuarioRepository;

/**
 * Servicio para la gestión de usuarios.
 */
@Service
public class UserService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param signupRequestDto DTO con los datos del nuevo usuario.
     * @return El usuario recién creado.
     * @throws IllegalArgumentException si el nombre de usuario o el email ya
     *                                  existen.
     */
    public Usuario registrarUsuario(SignupRequestDto signupRequestDto) {
        if (usuarioRepository.existsByNombreUsuario(signupRequestDto.getNombreUsuario())) {
            throw new IllegalArgumentException(
                    "El nombre de usuario '" + signupRequestDto.getNombreUsuario() + "' ya está en uso.");
        }
        if (usuarioRepository.existsByEmail(signupRequestDto.getEmail())) {
            throw new IllegalArgumentException("El email '" + signupRequestDto.getEmail() + "' ya está en uso.");
        }

        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(signupRequestDto.getNombreUsuario());
        usuario.setEmail(signupRequestDto.getEmail());
        usuario.setContrasena(passwordEncoder.encode(signupRequestDto.getContrasena()));

        // Asignar rol por defecto (USER)
        Rol rolUsuario = rolRepository.findByNombre("USER")
                .orElseThrow(() -> new IllegalArgumentException("Rol USER no encontrado"));
        Set<Rol> roles = new HashSet<>();
        roles.add(rolUsuario);
        usuario.setRoles(roles);

        return usuarioRepository.save(usuario);
    }

    // Otros métodos del servicio (login, etc.) irán aquí
}
