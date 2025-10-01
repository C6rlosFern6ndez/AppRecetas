package com.recetas.backend.service;

import java.util.List;
import java.util.Optional;

import com.recetas.backend.domain.entity.Ingrediente;

public interface IngredienteService {
    List<Ingrediente> findAll();

    Optional<Ingrediente> findById(Integer id);

    Ingrediente save(Ingrediente ingrediente);

    void deleteById(Integer id);
}
