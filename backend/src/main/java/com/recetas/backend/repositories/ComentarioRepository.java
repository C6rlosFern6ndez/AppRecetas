package com.recetas.backend.repositories;

import com.recetas.backend.models.Comentario;
import com.recetas.backend.models.Receta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Integer> {
    Page<Comentario> findByReceta(Receta receta, Pageable pageable);
}
