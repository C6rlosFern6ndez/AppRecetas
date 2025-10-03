package com.recetas.backend.service;

import com.recetas.backend.domain.dto.RecetaRequestDto;
import com.recetas.backend.domain.entity.Comentario;
import com.recetas.backend.domain.entity.Receta;
import com.recetas.backend.domain.model.enums.Dificultad;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.Set;

/**
 * Interfaz para los servicios relacionados con la gestión de recetas.
 */
public interface RecetaService {

    /**
     * Crea una nueva receta.
     *
     * @param recetaDto DTO con los datos de la receta.
     * @param usuarioId ID del usuario que crea la receta.
     * @return La receta creada.
     */
    Receta crearReceta(RecetaRequestDto recetaDto, Integer usuarioId);

    /**
     * Actualiza una receta existente.
     *
     * @param id        ID de la receta a actualizar.
     * @param recetaDto DTO con los datos actualizados de la receta.
     * @param usuarioId ID del usuario que realiza la actualización.
     * @return La receta actualizada.
     */
    Receta actualizarReceta(Integer id, RecetaRequestDto recetaDto, Integer usuarioId);

    /**
     * Sube una imagen para una receta.
     *
     * @param recetaId   ID de la receta.
     * @param imagenFile Archivo de imagen.
     * @return La URL de la imagen subida.
     */
    String subirImagenReceta(Integer recetaId, MultipartFile imagenFile);

    /**
     * Elimina una imagen de una receta.
     *
     * @param recetaId ID de la receta.
     */
    void eliminarImagenReceta(Integer recetaId);

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
     * @param recetaId        El ID de la receta a la que se añade el comentario.
     * @param usuarioId       El ID del usuario que comenta.
     * @param comentarioTexto El texto del comentario.
     * @return El comentario guardado.
     */
    Comentario agregarComentario(Integer recetaId, Integer usuarioId, String comentarioTexto);

    /**
     * Obtiene todos los comentarios de una receta específica.
     *
     * @param recetaId El ID de la receta.
     * @return Un conjunto de comentarios para la receta dada.
     */
    Set<Comentario> obtenerComentariosDeReceta(Integer recetaId);

    /**
     * Busca una receta por su ID.
     *
     * @param id El ID de la receta a buscar.
     * @return La receta si se encuentra, o lanza una excepción.
     */
    Receta obtenerRecetaOExcepcion(Integer id);

    /**
     * Obtiene todas las recetas paginadas.
     *
     * @param pageable Objeto Pageable para la paginación y ordenación.
     * @return Una página de recetas.
     */
    Page<Receta> obtenerTodasLasRecetas(Pageable pageable);

    /**
     * Obtiene una receta por su ID.
     *
     * @param id El ID de la receta a buscar.
     * @return Un Optional que contiene la receta si se encuentra.
     */
    Optional<Receta> obtenerRecetaPorId(Integer id);

    /**
     * Busca recetas por criterios.
     *
     * @param titulo               Título de la receta (parcial).
     * @param ingredienteNombre    Nombre de un ingrediente (parcial).
     * @param dificultad           Nivel de dificultad.
     * @param tiempoPreparacionMax Tiempo máximo de preparación.
     * @param categoriaNombre      Nombre de una categoría (parcial).
     * @param pageable             Objeto Pageable para la paginación y ordenación.
     * @return Una página de recetas que coinciden con los criterios.
     */
    Page<Receta> buscarRecetas(String titulo, String ingredienteNombre, Dificultad dificultad,
            Integer tiempoPreparacionMax, String categoriaNombre, Pageable pageable);

    /**
     * Elimina una receta.
     *
     * @param id        ID de la receta a eliminar.
     * @param usuarioId ID del usuario que elimina la receta.
     */
    void eliminarReceta(Integer id, Integer usuarioId);

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

    /**
     * Agrega una categoría a una receta.
     *
     * @param recetaId    ID de la receta.
     * @param categoriaId ID de la categoría a agregar.
     * @return La receta actualizada.
     */
    Receta agregarCategoria(Integer recetaId, Integer categoriaId);

    /**
     * Elimina una categoría de una receta.
     *
     * @param recetaId    ID de la receta.
     * @param categoriaId ID de la categoría a eliminar.
     * @return La receta actualizada.
     */
    Receta eliminarCategoria(Integer recetaId, Integer categoriaId);
}
