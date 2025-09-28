package com.recetas.backend.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.recetas.backend.domain.entity.Paso;

/**
 * Repositorio para la entidad Paso.
 */
@Repository
public interface PasoRepository extends JpaRepository<Paso, Integer> {
    // Aquí se pueden añadir métodos de consulta personalizados si son necesarios.
    // Por ejemplo, para obtener los pasos de una receta específica.
}
