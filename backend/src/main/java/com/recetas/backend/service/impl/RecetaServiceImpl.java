package com.recetas.backend.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.recetas.backend.domain.entity.Receta;
import com.recetas.backend.domain.repository.RecetaRepository;
import com.recetas.backend.service.RecetaService;

/**
 * Implementación del servicio para la gestión de recetas.
 */
@Service
public class RecetaServiceImpl implements RecetaService {

    @Autowired
    private RecetaRepository recetaRepository;

    /**
     * Guarda una nueva receta o actualiza una existente.
     *
     * @param receta La receta a guardar.
     * @return La receta guardada.
     */
    @Override
    @Transactional
    public Receta guardarReceta(Receta receta) {
        // Aquí se podrían añadir validaciones adicionales antes de guardar.
        // Por ejemplo, verificar que el usuario que crea la receta tenga el rol
        // adecuado.
        return recetaRepository.save(receta);
    }

    /**
     * Obtiene una lista de todas las recetas.
     *
     * @return Una lista de todas las recetas.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Receta> obtenerTodasLasRecetas() {
        return recetaRepository.findAll();
    }

    /**
     * Obtiene una receta por su ID.
     *
     * @param id El ID de la receta.
     * @return Un Optional que contiene la receta si se encuentra, o vacío en caso
     *         contrario.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Receta> obtenerRecetaPorId(Integer id) {
        return recetaRepository.findById(id);
    }

    /**
     * Elimina una receta por su ID.
     *
     * @param id El ID de la receta a eliminar.
     */
    @Override
    @Transactional
    public void eliminarReceta(Integer id) {
        // Se podría añadir una verificación para asegurar que la receta existe antes de
        // eliminar.
        recetaRepository.deleteById(id);
    }
}
