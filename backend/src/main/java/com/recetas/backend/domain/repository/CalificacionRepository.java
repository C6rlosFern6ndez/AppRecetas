package com.recetas.backend.domain.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.recetas.backend.domain.entity.Calificacion;

@Repository
public interface CalificacionRepository extends JpaRepository<Calificacion, Integer> {

    /**
     * Encuentra una calificación por el ID del usuario y el ID de la receta.
     * 
     * @param usuarioId El ID del usuario.
     * @param recetaId  El ID de la receta.
     * @return Un Optional que contiene la calificación si se encuentra, o vacío si
     *         no.
     */
    Optional<Calificacion> findByUsuarioIdAndRecetaId(Integer usuarioId, Integer recetaId);
}
