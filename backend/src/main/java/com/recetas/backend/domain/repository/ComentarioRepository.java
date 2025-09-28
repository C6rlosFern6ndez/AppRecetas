package com.recetas.backend.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.recetas.backend.domain.entity.Comentario;

/**
 * Repositorio para la entidad Comentario.
 */
@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Integer> {
    // Aquí se pueden añadir métodos de consulta personalizados si son necesarios.
    // Por ejemplo, para obtener los comentarios de una receta específica.
}
