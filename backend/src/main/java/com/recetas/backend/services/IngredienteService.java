package com.recetas.backend.services;

import com.recetas.backend.dtos.IngredienteDto;
import com.recetas.backend.exceptions.ResourceNotFoundException;
import com.recetas.backend.models.Ingrediente;
import com.recetas.backend.repositories.IngredienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IngredienteService {

    @Autowired
    private IngredienteRepository ingredienteRepository;

    public Page<IngredienteDto> getAllIngredientes(Pageable pageable) {
        return ingredienteRepository.findAll(pageable).map(IngredienteDto::fromEntity);
    }

    public IngredienteDto getIngredienteById(Integer id) {
        Ingrediente ingrediente = ingredienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ingrediente no encontrado con ID: " + id));
        return IngredienteDto.fromEntity(ingrediente);
    }

    @Transactional
    public IngredienteDto createIngrediente(IngredienteDto ingredienteDto) {
        if (ingredienteRepository.findByNombre(ingredienteDto.getNombre()).isPresent()) {
            throw new RuntimeException("Error: ¡El nombre del ingrediente ya existe!");
        }
        Ingrediente ingrediente = new Ingrediente();
        ingrediente.setNombre(ingredienteDto.getNombre());
        return IngredienteDto.fromEntity(ingredienteRepository.save(ingrediente));
    }

    @Transactional
    public IngredienteDto updateIngrediente(Integer id, IngredienteDto ingredienteDto) {
        Ingrediente ingrediente = ingredienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ingrediente no encontrado con ID: " + id));

        if (ingredienteRepository.findByNombre(ingredienteDto.getNombre()).isPresent() &&
                !ingredienteRepository.findByNombre(ingredienteDto.getNombre()).get().getId().equals(id)) {
            throw new RuntimeException("Error: ¡El nombre del ingrediente ya existe!");
        }

        ingrediente.setNombre(ingredienteDto.getNombre());
        return IngredienteDto.fromEntity(ingredienteRepository.save(ingrediente));
    }

    @Transactional
    public void deleteIngrediente(Integer id) {
        if (!ingredienteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ingrediente no encontrado con ID: " + id);
        }
        ingredienteRepository.deleteById(id);
    }
}
