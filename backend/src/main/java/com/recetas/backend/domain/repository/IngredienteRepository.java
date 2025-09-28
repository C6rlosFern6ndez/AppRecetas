package com.recetas.backend.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.recetas.backend.domain.entity.Ingrediente;

/**
 * Repositorio para la entidad Ingrediente.
 */
@Repository
public interface IngredienteRepository extends JpaRepository<Ingrediente, Integer> {
    // Aquí se pueden añadir métodos de consulta personalizados si son necesarios.
    // Por ejemplo, para buscar ingredientes por nombre.
}
