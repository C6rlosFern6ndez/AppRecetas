package com.recetas.backend.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.recetas.backend.domain.entity.RecetaIngrediente;
import com.recetas.backend.domain.entity.RecetaIngredienteId;

@Repository
public interface RecetaIngredienteRepository extends JpaRepository<RecetaIngrediente, RecetaIngredienteId> {
}
