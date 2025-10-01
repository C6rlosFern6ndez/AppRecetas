package com.recetas.backend.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.recetas.backend.domain.entity.Ingrediente;
import com.recetas.backend.domain.repository.IngredienteRepository;
import com.recetas.backend.service.IngredienteService;

@Service
public class IngredienteServiceImpl implements IngredienteService {

    private final IngredienteRepository ingredienteRepository;

    public IngredienteServiceImpl(IngredienteRepository ingredienteRepository) {
        this.ingredienteRepository = ingredienteRepository;
    }

    @Override
    public List<Ingrediente> findAll() {
        return ingredienteRepository.findAll();
    }

    @Override
    public Optional<Ingrediente> findById(Integer id) {
        return ingredienteRepository.findById(id);
    }

    @Override
    public Ingrediente save(Ingrediente ingrediente) {
        return ingredienteRepository.save(ingrediente);
    }

    @Override
    public void deleteById(Integer id) {
        ingredienteRepository.deleteById(id);
    }
}
