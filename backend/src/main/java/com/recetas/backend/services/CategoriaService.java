package com.recetas.backend.services;

import com.recetas.backend.dtos.CategoriaDto;
import com.recetas.backend.exceptions.ResourceNotFoundException;
import com.recetas.backend.models.Categoria;
import com.recetas.backend.repositories.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    public Page<CategoriaDto> getAllCategorias(Pageable pageable) {
        return categoriaRepository.findAll(pageable).map(CategoriaDto::fromEntity);
    }

    public CategoriaDto getCategoriaById(Integer id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));
        return CategoriaDto.fromEntity(categoria);
    }

    @Transactional
    public CategoriaDto createCategoria(CategoriaDto categoriaDto) {
        if (categoriaRepository.findByNombre(categoriaDto.getNombre()).isPresent()) {
            throw new RuntimeException("Error: ¡El nombre de la categoría ya existe!");
        }
        Categoria categoria = new Categoria();
        categoria.setNombre(categoriaDto.getNombre());
        return CategoriaDto.fromEntity(categoriaRepository.save(categoria));
    }

    @Transactional
    public CategoriaDto updateCategoria(Integer id, CategoriaDto categoriaDto) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con ID: " + id));

        if (categoriaRepository.findByNombre(categoriaDto.getNombre()).isPresent() &&
                !categoriaRepository.findByNombre(categoriaDto.getNombre()).get().getId().equals(id)) {
            throw new RuntimeException("Error: ¡El nombre de la categoría ya existe!");
        }

        categoria.setNombre(categoriaDto.getNombre());
        return CategoriaDto.fromEntity(categoriaRepository.save(categoria));
    }

    @Transactional
    public void deleteCategoria(Integer id) {
        if (!categoriaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Categoría no encontrada con ID: " + id);
        }
        categoriaRepository.deleteById(id);
    }
}
