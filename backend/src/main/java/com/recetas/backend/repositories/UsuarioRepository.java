package com.recetas.backend.repositories;

import com.recetas.backend.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByNombreUsuario(String nombreUsuario);

    Boolean existsByEmail(String email);

    Boolean existsByNombreUsuario(String nombreUsuario);
}
