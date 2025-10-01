package com.recetas.backend.service;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.recetas.backend.domain.entity.Calificacion;
import com.recetas.backend.domain.entity.Comentario;
import com.recetas.backend.domain.entity.Notificacion;
import com.recetas.backend.domain.entity.Receta;
import com.recetas.backend.domain.model.enums.Dificultad; // Importar Dificultad
import com.recetas.backend.domain.model.enums.TipoNotificacion;

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
     * Añade un comentario a una receta.
     * 
     * @param comentario El comentario a añadir.
     * @return El comentario guardado.
     */
    Comentario agregarComentario(Comentario comentario);

    /**
     * Obtiene todos los comentarios de una receta específica.
     * 
     * @param recetaId El ID de la receta.
     * @return Una lista de comentarios para la receta dada.
     */
    Set<Comentario> obtenerComentariosDeReceta(Integer recetaId);

    /**
     * Busca una receta por su ID.
     * 
     * @param id El ID de la receta a buscar.
     * @return La receta si se encuentra, o null si no existe.
     */
    Receta findById(Integer id);

    Receta guardarReceta(Receta receta);

    Page<Receta> obtenerTodasLasRecetas(Pageable pageable);

    Optional<Receta> obtenerRecetaPorId(Integer id);

    Page<Receta> buscarRecetas(String titulo, String ingredienteNombre, Dificultad dificultad,
            Integer tiempoPreparacionMax, String categoriaNombre, Pageable pageable);

    void eliminarReceta(Integer id);

    /**
     * Califica una receta.
     *
     * @param usuarioId  El ID del usuario que califica.
     * @param recetaId   El ID de la receta a calificar.
     * @param puntuacion La puntuación dada (ej. 1-5).
     */
    void calificarReceta(Integer usuarioId, Integer recetaId, Integer puntuacion);

    /**
     * Obtiene la calificación de una receta por un usuario específico.
     *
     * @param usuarioId El ID del usuario.
     * @param recetaId  El ID de la receta.
     * @return La calificación si existe, o null si no.
     */
    Integer obtenerCalificacionDeReceta(Integer usuarioId, Integer recetaId);

    Receta agregarCategoria(Integer recetaId, Integer categoriaId);

    Receta eliminarCategoria(Integer recetaId, Integer categoriaId);
}
