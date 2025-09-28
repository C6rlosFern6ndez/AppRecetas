package com.recetas.backend.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.recetas.backend.domain.entity.RecetaIngrediente;
import com.recetas.backend.domain.entity.RecetaIngredienteId;

/**
 * Repositorio para la entidad RecetaIngrediente.
 */
@Repository
public interface RecetaIngredienteRepository extends JpaRepository<RecetaIngrediente, RecetaIngredienteId> {
    // Aquí se pueden añadir métodos de consulta personalizados si son necesarios.
    // Por ejemplo, para obtener los ingredientes de una receta específica.
}
