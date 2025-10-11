package com.recetas.backend.repositories;

import com.recetas.backend.models.Calificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.recetas.backend.models.Receta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

@Repository
public interface CalificacionRepository extends JpaRepository<Calificacion, Integer> {
    Optional<Calificacion> findByRecetaIdAndUsuarioId(Integer recetaId, Integer usuarioId);

    Page<Calificacion> findByReceta(Receta receta, Pageable pageable);
}
