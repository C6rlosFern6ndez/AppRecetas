package com.recetas.backend.repositories;

import com.recetas.backend.models.RecetaIngrediente;
import com.recetas.backend.models.RecetaIngredienteId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecetaIngredienteRepository extends JpaRepository<RecetaIngrediente, RecetaIngredienteId> {
}
