package com.recetas.backend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
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
        return usuarioRepository.findByNombreUsuario(usernameOrEmail)
                .or(() -> usuarioRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado con el nombre de usuario o email: " + usernameOrEmail));
    }
}
