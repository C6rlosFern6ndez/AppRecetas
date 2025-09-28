package com.recetas.backend.service;

import com.recetas.backend.domain.entity.Calificacion;

/**
 * Interfaz para los servicios relacionados con la gestión de calificaciones.
 */
public interface CalificacionService {

    /**
     * Crea o actualiza la calificación de una receta.
     * Si el usuario ya ha calificado la receta, se actualiza la puntuación.
     * 
     * @param usuarioId  El ID del usuario que califica.
     * @param recetaId   El ID de la receta a calificar.
     * @param puntuacion La puntuación dada (ej. 1-5).
     * @return La calificación creada o actualizada.
     */
    Calificacion calificarReceta(Integer usuarioId, Integer recetaId, Integer puntuacion);

    /**
     * Elimina la calificación de una receta por un usuario.
     * 
     * @param usuarioId El ID del usuario cuya calificación se eliminará.
     * @param recetaId  El ID de la receta cuya calificación se eliminará.
     */
    void eliminarCalificacion(Integer usuarioId, Integer recetaId);

    /**
     * Busca una calificación por su ID.
     * 
     * @param id El ID de la calificación a buscar.
     * @return La calificación si se encuentra, o null si no existe.
     */
    Calificacion findById(Integer id);
}
