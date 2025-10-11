package com.recetas.backend.repositories;

import com.recetas.backend.models.Ingrediente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IngredienteRepository extends JpaRepository<Ingrediente, Integer> {
    Optional<Ingrediente> findByNombre(String nombre);
}
