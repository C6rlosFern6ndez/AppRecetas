package com.recetas.backend.service;

import java.util.List;
import java.util.Optional;

import com.recetas.backend.domain.entity.Receta;

/**
 * Interfaz para los servicios relacionados con la gestión de recetas.
 */
public interface RecetaService {

    /**
     * Da "me gusta" a una receta.
     * 
     * @param usuarioId El ID del usuario que da "me gusta".
     * @param recetaId  El ID de la receta a la que se da "me gusta".
     */
    void darMeGusta(Integer usuarioId, Integer recetaId);

    /**
     * Quita el "me gusta" de una receta.
     * 
     * @param usuarioId El ID del usuario que quita el "me gusta".
     * @param recetaId  El ID de la receta a la que se quita el "me gusta".
     */
    void quitarMeGusta(Integer usuarioId, Integer recetaId);

    /**
     * Busca una receta por su ID.
     * 
     * @param id El ID de la receta a buscar.
     * @return La receta si se encuentra, o null si no existe.
     */
    Receta findById(Integer id);

    Receta guardarReceta(Receta receta);

    List<Receta> obtenerTodasLasRecetas();

    Optional<Receta> obtenerRecetaPorId(Integer id);

    void eliminarReceta(Integer id);
}
