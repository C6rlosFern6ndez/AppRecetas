package com.recetas.backend.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.recetas.backend.domain.entity.Receta;

/**
 * Repositorio para la entidad Receta.
 */
@Repository
public interface RecetaRepository extends JpaRepository<Receta, Integer> {
    // Aquí se pueden añadir métodos de consulta personalizados si son necesarios.
    // Por ejemplo, para buscar recetas por título, por usuario, etc.
}
