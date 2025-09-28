package com.recetas.backend.service;

import com.recetas.backend.domain.entity.Comentario;

/**
 * Interfaz para los servicios relacionados con la gestión de comentarios.
 */
public interface ComentarioService {

    /**
     * Crea un nuevo comentario para una receta.
     * 
     * @param comentario El contenido del comentario.
     * @param usuarioId  El ID del usuario que crea el comentario.
     * @param recetaId   El ID de la receta a la que se añade el comentario.
     * @return El comentario creado.
     */
    Comentario crearComentario(String comentario, Integer usuarioId, Integer recetaId);

    /**
     * Elimina un comentario.
     * 
     * @param comentarioId El ID del comentario a eliminar.
     * @param usuarioId    El ID del usuario que intenta eliminar el comentario
     *                     (para verificación de permisos).
     */
    void eliminarComentario(Integer comentarioId, Integer usuarioId);

    /**
     * Busca un comentario por su ID.
     * 
     * @param id El ID del comentario a buscar.
     * @return El comentario si se encuentra, o null si no existe.
     */
    Comentario findById(Integer id);
}
