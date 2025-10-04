package com.recetas.backend.service.impl;

import com.recetas.backend.domain.dto.RecetaRequestDto;
import com.recetas.backend.domain.entity.*;
import com.recetas.backend.domain.model.enums.Dificultad;
import com.recetas.backend.domain.model.enums.TipoNotificacion;
import com.recetas.backend.domain.repository.*;
import com.recetas.backend.service.ImageUploadService;
import com.recetas.backend.service.NotificacionService;
import com.recetas.backend.service.RecetaService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementación de los servicios relacionados con la gestión de recetas.
 */
@Service
public class RecetaServiceImpl implements RecetaService {

    private final RecetaRepository recetaRepository;
    private final UsuarioRepository usuarioRepository;
    private final MeGustaRecetaRepository meGustaRecetaRepository;
    private final ComentarioRepository comentarioRepository;
    private final CalificacionRepository calificacionRepository;
    private final NotificacionService notificacionService;
    private final CategoriaRepository categoriaRepository;
    private final ImageUploadService imageUploadService; // Inyectar ImageUploadService

    public RecetaServiceImpl(RecetaRepository recetaRepository, UsuarioRepository usuarioRepository,
            MeGustaRecetaRepository meGustaRecetaRepository, ComentarioRepository comentarioRepository,
            CalificacionRepository calificacionRepository, NotificacionService notificacionService,
            CategoriaRepository categoriaRepository, ImageUploadService imageUploadService) {
        this.recetaRepository = recetaRepository;
        this.usuarioRepository = usuarioRepository;
        this.meGustaRecetaRepository = meGustaRecetaRepository;
        this.comentarioRepository = comentarioRepository;
        this.calificacionRepository = calificacionRepository;
        this.notificacionService = notificacionService;
        this.categoriaRepository = categoriaRepository;
        this.imageUploadService = imageUploadService;
    }

    /**
     * Crea una nueva receta.
     *
     * @param recetaDto DTO con los datos de la receta.
     * @param usuarioId ID del usuario que crea la receta.
     * @return La receta creada.
     * @throws RuntimeException si el usuario no existe.
     */
    @Override
    @Transactional
    public Receta crearReceta(RecetaRequestDto recetaDto, Integer usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + usuarioId));

        Receta nuevaReceta = new Receta();
        nuevaReceta.setTitulo(recetaDto.getTitulo());
        nuevaReceta.setDescripcion(recetaDto.getDescripcion());
        nuevaReceta.setTiempoPreparacion(recetaDto.getTiempoPreparacion());
        nuevaReceta.setDificultad(recetaDto.getDificultad());
        nuevaReceta.setPorciones(recetaDto.getPorciones());
        nuevaReceta.setUsuario(usuario);
        nuevaReceta.setFechaCreacion(LocalDateTime.now());

        // Asignar categorías
        if (recetaDto.getCategoriaIds() != null && !recetaDto.getCategoriaIds().isEmpty()) {
            Set<Categoria> categorias = recetaDto.getCategoriaIds().stream()
                    .map(catId -> categoriaRepository.findById(catId)
                            .orElseThrow(() -> new RuntimeException(
                                    "Categoría no encontrada con id: " + catId)))
                    .collect(Collectors.toSet());
            nuevaReceta.setCategorias(categorias);
        } else {
            nuevaReceta.setCategorias(new HashSet<>());
        }

        return recetaRepository.save(nuevaReceta);
    }

    /**
     * Actualiza una receta existente.
     *
     * @param id        ID de la receta a actualizar.
     * @param recetaDto DTO con los datos actualizados de la receta.
     * @param usuarioId ID del usuario que realiza la actualización.
     * @return La receta actualizada.
     * @throws RuntimeException      si la receta no existe.
     * @throws IllegalStateException si el usuario no es el propietario de la
     *                               receta.
     * @throws RuntimeException      si alguna categoría no existe.
     */
    @Override
    @Transactional
    public Receta actualizarReceta(Integer id, RecetaRequestDto recetaDto, Integer usuarioId) {
        Receta recetaExistente = recetaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada con ID: " + id));

        if (!recetaExistente.getUsuario().getId().equals(usuarioId)) {
            throw new IllegalStateException("No tienes permiso para actualizar esta receta.");
        }

        recetaExistente.setTitulo(recetaDto.getTitulo());
        recetaExistente.setDescripcion(recetaDto.getDescripcion());
        recetaExistente.setTiempoPreparacion(recetaDto.getTiempoPreparacion());
        recetaExistente.setDificultad(recetaDto.getDificultad());
        recetaExistente.setPorciones(recetaDto.getPorciones());
        recetaExistente.setUrlImagen(recetaDto.getUrlImagen()); // Permitir actualizar la URL de la imagen

        // Actualizar categorías
        if (recetaDto.getCategoriaIds() != null) {
            Set<Categoria> nuevasCategorias = recetaDto.getCategoriaIds().stream()
                    .map(catId -> categoriaRepository.findById(catId)
                            .orElseThrow(() -> new RuntimeException(
                                    "Categoría no encontrada con id: " + catId)))
                    .collect(Collectors.toSet());
            recetaExistente.setCategorias(nuevasCategorias);
        } else {
            recetaExistente.setCategorias(new HashSet<>());
        }

        return recetaRepository.save(recetaExistente);
    }

    /**
     * Sube una imagen para una receta.
     *
     * @param recetaId   ID de la receta.
     * @param imagenFile Archivo de imagen.
     * @return La URL de la imagen subida.
     * @throws RuntimeException si la receta no existe.
     * @throws RuntimeException si ocurre un error al subir la imagen.
     */
    @Override
    @Transactional
    public String subirImagenReceta(Integer recetaId, MultipartFile imagenFile) {
        Receta receta = recetaRepository.findById(recetaId)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada con ID: " + recetaId));

        try {
            // Obtener el nombre de la categoría (si existe) y el título de la receta para
            // el nombre del archivo
            String categoryName = receta.getCategorias().stream()
                    .findFirst() // Tomar la primera categoría si hay varias
                    .map(Categoria::getNombre)
                    .orElse("General");
            String recipeTitle = receta.getTitulo();

            // El servicio de subida de imágenes ahora devuelve un mapa con URL y deleteHash
            Map<String, String> uploadResult = imageUploadService.uploadImage(imagenFile, categoryName, recipeTitle);

            // Extraer la URL y el deleteHash del resultado
            String imageUrl = uploadResult.get("url");
            String deleteHash = uploadResult.get("deleteHash");

            // Actualizar la receta con la URL y el deleteHash de la imagen
            receta.setUrlImagen(imageUrl);
            receta.setDeleteHashImagen(deleteHash);
            recetaRepository.save(receta);

            return imageUrl;
        } catch (Exception e) {
            throw new RuntimeException(
                    "Error al subir la imagen para la receta " + recetaId + ": " + e.getMessage());
        }
    }

    /**
     * Elimina una imagen de una receta.
     *
     * @param recetaId ID de la receta.
     * @throws RuntimeException si la receta no existe.
     * @throws RuntimeException si ocurre un error al eliminar la imagen de imgbb.
     */
    @Override
    @Transactional
    public void eliminarImagenReceta(Integer recetaId) {
        Receta receta = recetaRepository.findById(recetaId)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada con ID: " + recetaId));

        try {
            // Si la receta tiene un deleteHash, eliminar la imagen de imgbb primero
            if (receta.getDeleteHashImagen() != null && !receta.getDeleteHashImagen().trim().isEmpty()) {
                imageUploadService.deleteImage(receta.getDeleteHashImagen());
            }

            // Luego eliminar las referencias de la base de datos
            receta.setUrlImagen(null);
            receta.setDeleteHashImagen(null);
            recetaRepository.save(receta);
        } catch (Exception e) {
            // Si falla la eliminación en imgbb, aún así eliminamos las referencias locales
            System.err.println("Error al eliminar la imagen de imgbb: " + e.getMessage());
            receta.setUrlImagen(null);
            receta.setDeleteHashImagen(null);
            recetaRepository.save(receta);
        }
    }

    /**
     * Da "me gusta" a una receta.
     *
     * @param usuarioId El ID del usuario que da "me gusta".
     * @param recetaId  El ID de la receta a la que se da "me gusta".
     * @throws RuntimeException      si el usuario no existe.
     * @throws RuntimeException      si la receta no existe.
     * @throws IllegalStateException si el usuario ya ha dado "me gusta" a la
     *                               receta.
     */
    @Override
    @Transactional
    public void darMeGusta(Integer usuarioId, Integer recetaId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + usuarioId));
        Receta receta = recetaRepository.findById(recetaId)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada con id: " + recetaId));

        if (meGustaRecetaRepository.existsById_UsuarioIdAndId_RecetaId(usuarioId, recetaId)) {
            throw new IllegalStateException("Ya has dado 'me gusta' a esta receta.");
        }

        MeGustaRecetaId id = new MeGustaRecetaId(usuarioId, recetaId);
        MeGustaReceta meGusta = new MeGustaReceta(id, usuario, receta);
        meGustaRecetaRepository.save(meGusta);

        // Crear notificacion
        notificacionService.crearNotificacion(receta.getUsuario().getId(), TipoNotificacion.ME_GUSTA_RECETA, usuarioId,
                recetaId.longValue());
    }

    /**
     * Quita el "me gusta" de una receta.
     *
     * @param usuarioId El ID del usuario que quita el "me gusta".
     * @param recetaId  El ID de la receta a la que se quita el "me gusta".
     */
    @Override
    @Transactional
    public void quitarMeGusta(Integer usuarioId, Integer recetaId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new RuntimeException("Usuario no encontrado con id: " + usuarioId);
        }
        if (!recetaRepository.existsById(recetaId)) {
            throw new RuntimeException("Receta no encontrada con id: " + recetaId);
        }

        MeGustaRecetaId id = new MeGustaRecetaId(usuarioId, recetaId);

        if (!meGustaRecetaRepository.existsById(id)) {
            throw new IllegalStateException("No has dado 'me gusta' a esta receta.");
        }

        meGustaRecetaRepository.deleteById(id);
    }

    /**
     * Añade un comentario a una receta.
     *
     * @param recetaId        El ID de la receta a la que se añade el comentario.
     * @param usuarioId       El ID del usuario que comenta.
     * @param comentarioTexto El texto del comentario.
     * @return El comentario guardado.
     */
    @Override
    @Transactional
    public Comentario agregarComentario(Integer recetaId, Integer usuarioId, String comentarioTexto) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + usuarioId));
        Receta receta = recetaRepository.findById(recetaId)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada con ID: " + recetaId));

        if (comentarioTexto == null || comentarioTexto.trim().isEmpty()) {
            throw new IllegalArgumentException("El comentario no puede estar vacío.");
        }

        Comentario nuevoComentario = new Comentario();
        nuevoComentario.setComentario(comentarioTexto);
        nuevoComentario.setUsuario(usuario);
        nuevoComentario.setReceta(receta);
        nuevoComentario.setFechaComentario(LocalDateTime.now());

        return comentarioRepository.save(nuevoComentario);
    }

    /**
     * Obtiene todos los comentarios de una receta específica.
     *
     * @param recetaId El ID de la receta.
     * @return Un conjunto de comentarios para la receta dada.
     */
    @Override
    @Transactional(readOnly = true)
    public Set<Comentario> obtenerComentariosDeReceta(Integer recetaId) {
        Receta receta = recetaRepository.findById(recetaId)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada con ID: " + recetaId));
        return receta.getComentarios();
    }

    /**
     * Busca una receta por su ID.
     *
     * @param id El ID de la receta a buscar.
     * @return La receta si se encuentra, o lanza una excepción.
     */
    @Override
    @Transactional(readOnly = true)
    public Receta obtenerRecetaOExcepcion(Integer id) {
        return recetaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada con ID: " + id));
    }

    /**
     * Obtiene todas las recetas con paginación y ordenación.
     *
     * @param pageable Objeto Pageable para la paginación y ordenación.
     * @return Una página de recetas.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Receta> obtenerTodasLasRecetas(Pageable pageable) {
        return recetaRepository.findAll(pageable);
    }

    /**
     * Obtiene una receta por su ID.
     *
     * @param id El ID de la receta a buscar.
     * @return Un Optional que contiene la receta si se encuentra.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Receta> obtenerRecetaPorId(Integer id) {
        return recetaRepository.findById(id);
    }

    /**
     * Busca recetas por varios criterios con paginación y ordenación.
     *
     * @param titulo               Título de la receta (parcial).
     * @param ingredienteNombre    Nombre de un ingrediente (parcial).
     * @param dificultad           Nivel de dificultad.
     * @param tiempoPreparacionMax Tiempo máximo de preparación.
     * @param categoriaNombre      Nombre de una categoría (parcial).
     * @param pageable             Objeto Pageable para la paginación y ordenación.
     * @return Una página de recetas que coinciden con los criterios.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Receta> buscarRecetas(String titulo, String ingredienteNombre, Dificultad dificultad,
            Integer tiempoPreparacionMax, String categoriaNombre, Pageable pageable) {
        return recetaRepository.buscarRecetas(titulo, ingredienteNombre, dificultad, tiempoPreparacionMax,
                categoriaNombre, pageable);
    }

    /**
     * Elimina una receta.
     *
     * @param id        ID de la receta a eliminar.
     * @param usuarioId ID del usuario que elimina la receta.
     */
    @Override
    @Transactional
    public void eliminarReceta(Integer id, Integer usuarioId) {
        Receta receta = recetaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada con ID: " + id));

        if (!receta.getUsuario().getId().equals(usuarioId)) {
            throw new IllegalStateException("No tienes permiso para eliminar esta receta.");
        }
        recetaRepository.delete(receta);
    }

    /**
     * Califica una receta.
     *
     * @param usuarioId  El ID del usuario que califica.
     * @param recetaId   El ID de la receta a calificar.
     * @param puntuacion La puntuación dada (ej. 1-5).
     * @throws RuntimeException         si el usuario no existe.
     * @throws RuntimeException         si la receta no existe.
     * @throws IllegalArgumentException si la puntuación es inválida.
     */
    @Override
    @Transactional
    public void calificarReceta(Integer usuarioId, Integer recetaId, Integer puntuacion) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + usuarioId));
        Receta receta = recetaRepository.findById(recetaId)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada con id: " + recetaId));

        // Validar la puntuación
        if (puntuacion < 1 || puntuacion > 5) {
            throw new IllegalArgumentException("La puntuación debe estar entre 1 y 5.");
        }

        // Buscar si ya existe una calificación para esta receta por este usuario
        Optional<Calificacion> calificacionExistente = calificacionRepository.findByUsuarioIdAndRecetaId(usuarioId,
                recetaId);

        if (calificacionExistente.isPresent()) {
            // Si existe, actualizar la puntuación
            Calificacion calificacion = calificacionExistente.get();
            calificacion.setPuntuacion(puntuacion);
            calificacion.setFechaCalificacion(LocalDateTime.now()); // Actualizar fecha de calificación
            calificacionRepository.save(calificacion);
        } else {
            // Si no existe, crear una nueva calificación
            Calificacion nuevaCalificacion = new Calificacion(null, puntuacion, LocalDateTime.now(), usuario, receta);
            calificacionRepository.save(nuevaCalificacion);
        }
    }

    /**
     * Obtiene la calificación de una receta por un usuario específico.
     *
     * @param usuarioId El ID del usuario.
     * @param recetaId  El ID de la receta.
     * @return La calificación si existe, o null si no.
     */
    @Override
    @Transactional(readOnly = true)
    public Integer obtenerCalificacionDeReceta(Integer usuarioId, Integer recetaId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new RuntimeException("Usuario no encontrado con id: " + usuarioId);
        }
        if (!recetaRepository.existsById(recetaId)) {
            throw new RuntimeException("Receta no encontrada con id: " + recetaId);
        }
        Optional<Calificacion> calificacion = calificacionRepository.findByUsuarioIdAndRecetaId(usuarioId, recetaId);
        return calificacion.map(Calificacion::getPuntuacion).orElse(null);
    }

    /**
     * Agrega una categoría a una receta.
     *
     * @param recetaId    ID de la receta.
     * @param categoriaId ID de la categoría a agregar.
     * @return La receta actualizada.
     */
    @Override
    @Transactional
    public Receta agregarCategoria(Integer recetaId, Integer categoriaId) {
        Receta receta = recetaRepository.findById(recetaId)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada con id: " + recetaId));
        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(
                        () -> new RuntimeException("Categoria no encontrada con id: " + categoriaId));
        receta.getCategorias().add(categoria);
        return recetaRepository.save(receta);
    }

    /**
     * Elimina una categoría de una receta.
     *
     * @param recetaId    ID de la receta.
     * @param categoriaId ID de la categoría a eliminar.
     * @return La receta actualizada.
     * @throws RuntimeException
     */
    @Override
    @Transactional
    public Receta eliminarCategoria(Integer recetaId, Integer categoriaId) throws RuntimeException {
        Receta receta = recetaRepository.findById(recetaId)
                .orElseThrow(() -> new RuntimeException("Receta no encontrada con id: " + recetaId));
        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new RuntimeException("Categoria no encontrada con id: " + categoriaId));
        receta.getCategorias().remove(categoria);
        return recetaRepository.save(receta);
    }

}
