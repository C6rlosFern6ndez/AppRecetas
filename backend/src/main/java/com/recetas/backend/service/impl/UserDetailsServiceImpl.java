package com.recetas.backend.service.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.recetas.backend.domain.entity.Usuario;
import com.recetas.backend.domain.repository.UsuarioRepository;

/**
 * Implementación del servicio UserDetailsService para cargar datos de usuario.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Carga los detalles del usuario por su nombre de usuario o email.
     *
     * @param usernameOrEmail El nombre de usuario o email del usuario.
     * @return Los detalles del usuario.
     * @throws UsernameNotFoundException Si el usuario no es encontrado.
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        // Intentar buscar por nombre de usuario primero, luego por email
        Usuario usuario = usuarioRepository.findByNombreUsuario(usernameOrEmail)
                .orElseGet(() -> usuarioRepository.findByEmail(usernameOrEmail)
                        .orElse(null));

        if (usuario == null) {
            throw new UsernameNotFoundException(
                    "Usuario no encontrado con el nombre de usuario o email: " + usernameOrEmail);
        }

        // Convertir los roles del usuario a GrantedAuthority
        Set<GrantedAuthority> authorities = new HashSet<>();
        if (usuario.getRoles() != null) {
            authorities = usuario.getRoles().stream()
                    .map(rol -> new SimpleGrantedAuthority("ROLE_" + rol.getNombre())) // Prefijo ROLE_ es estándar en
                                                                                       // Spring Security
                    .collect(Collectors.toSet());
        }

        // Devolver un objeto User de Spring Security
        return org.springframework.security.core.userdetails.User.builder()
                .username(usuario.getNombreUsuario()) // Usar nombre de usuario para el principal
                .password(usuario.getContrasena())
                .authorities(authorities)
                .build();
    }
}
