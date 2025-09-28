package com.recetas.backend.service;

import java.util.List;
import java.util.Optional;

import com.recetas.backend.domain.entity.Receta;

/**
 * Interfaz para el servicio de gestión de recetas.
 */
public interface RecetaService {

    /**
     * Guarda una nueva receta o actualiza una existente.
     *
     * @param receta La receta a guardar.
     * @return La receta guardada.
     */
    Receta guardarReceta(Receta receta);

    /**
     * Obtiene una lista de todas las recetas.
     *
     * @return Una lista de todas las recetas.
     */
    List<Receta> obtenerTodasLasRecetas();

    /**
     * Obtiene una receta por su ID.
     *
     * @param id El ID de la receta.
     * @return Un Optional que contiene la receta si se encuentra, o vacío en caso
     *         contrario.
     */
    Optional<Receta> obtenerRecetaPorId(Integer id);

    /**
     * Elimina una receta por su ID.
     *
     * @param id El ID de la receta a eliminar.
     */
    void eliminarReceta(Integer id);

    // Aquí se pueden añadir métodos para operaciones más específicas,
    // como buscar recetas por usuario, por categoría, etc.
}
