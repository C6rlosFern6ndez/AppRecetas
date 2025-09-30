package com.recetas.backend.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.recetas.backend.domain.entity.Calificacion;
import com.recetas.backend.domain.entity.Comentario;
import com.recetas.backend.domain.entity.MeGustaReceta;
import com.recetas.backend.domain.entity.MeGustaRecetaId;
import com.recetas.backend.domain.entity.Receta;
import com.recetas.backend.domain.entity.Usuario;
import com.recetas.backend.domain.repository.CalificacionRepository;
import com.recetas.backend.domain.repository.ComentarioRepository;
import com.recetas.backend.domain.repository.MeGustaRecetaRepository;
import com.recetas.backend.domain.repository.NotificacionRepository;
import com.recetas.backend.domain.repository.RecetaRepository;
import com.recetas.backend.domain.repository.UsuarioRepository;
import com.recetas.backend.exception.ComentarioException;
import com.recetas.backend.exception.MeGustaException;
import com.recetas.backend.exception.RecetaNoEncontradaException;
import com.recetas.backend.exception.UsuarioNoEncontradoException;
import com.recetas.backend.domain.entity.Notificacion;
import com.recetas.backend.domain.model.enums.TipoNotificacion;
import com.recetas.backend.service.RecetaService;

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
    private final NotificacionRepository notificacionRepository; // Added NotificacionRepository

    public RecetaServiceImpl(RecetaRepository recetaRepository, UsuarioRepository usuarioRepository,
            MeGustaRecetaRepository meGustaRecetaRepository, ComentarioRepository comentarioRepository,
            CalificacionRepository calificacionRepository, NotificacionRepository notificacionRepository) { // Added
                                                                                                            // NotificacionRepository
                                                                                                            // to
                                                                                                            // constructor
        this.recetaRepository = recetaRepository;
        this.usuarioRepository = usuarioRepository;
        this.meGustaRecetaRepository = meGustaRecetaRepository;
        this.comentarioRepository = comentarioRepository;
        this.calificacionRepository = calificacionRepository;
        this.notificacionRepository = notificacionRepository; // Initialize NotificacionRepository
    }

    /**
     * Da "me gusta" a una receta.
     *
     * @param usuarioId El ID del usuario que da "me gusta".
     * @param recetaId  El ID de la receta a la que se da "me gusta".
     */
    @Override
    @Transactional
    public void darMeGusta(Integer usuarioId, Integer recetaId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado con id: " + usuarioId));
        Receta receta = recetaRepository.findById(recetaId)
                .orElseThrow(() -> new RecetaNoEncontradaException("Receta no encontrada con id: " + recetaId));

        if (meGustaRecetaRepository.existsById_UsuarioIdAndId_RecetaId(usuarioId, recetaId)) {
            throw new MeGustaException("Ya has dado 'me gusta' a esta receta.");
        }

        MeGustaRecetaId id = new MeGustaRecetaId(usuarioId, recetaId);
        MeGustaReceta meGusta = new MeGustaReceta(id, usuario, receta);
        meGustaRecetaRepository.save(meGusta);
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
            throw new UsuarioNoEncontradoException("Usuario no encontrado con id: " + usuarioId);
        }
        if (!recetaRepository.existsById(recetaId)) {
            throw new RecetaNoEncontradaException("Receta no encontrada con id: " + recetaId);
        }

        MeGustaRecetaId id = new MeGustaRecetaId(usuarioId, recetaId);

        if (!meGustaRecetaRepository.existsById(id)) {
            throw new MeGustaException("No has dado 'me gusta' a esta receta.");
        }

        meGustaRecetaRepository.deleteById(id);
    }

    /**
     * Busca una receta por su ID.
     *
     * @param id El ID de la receta a buscar.
     * @return La receta si se encuentra, o null si no existe.
     */
    @Override
    @Transactional(readOnly = true)
    public Receta findById(Integer id) {
        return recetaRepository.findById(id).orElse(null);
    }

    @Override
    public Receta guardarReceta(Receta receta) {
        return recetaRepository.save(receta);
    }

    @Override
    public List<Receta> obtenerTodasLasRecetas() {
        return recetaRepository.findAll();
    }

    @Override
    public Optional<Receta> obtenerRecetaPorId(Integer id) {
        return recetaRepository.findById(id);
    }

    @Override
    public void eliminarReceta(Integer id) {
        recetaRepository.deleteById(id);
    }

    /**
     * Califica una receta.
     *
     * @param usuarioId  El ID del usuario que califica.
     * @param recetaId   El ID de la receta a calificar.
     * @param puntuacion La puntuación dada (ej. 1-5).
     */
    @Override
    @Transactional
    public void calificarReceta(Integer usuarioId, Integer recetaId, Integer puntuacion) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        Receta receta = recetaRepository.findById(recetaId)
                .orElseThrow(() -> new IllegalArgumentException("Receta no encontrada"));

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
        Optional<Calificacion> calificacion = calificacionRepository.findByUsuarioIdAndRecetaId(usuarioId, recetaId);
        return calificacion.map(Calificacion::getPuntuacion).orElse(null);
    }

    /**
     * Añade un comentario a una receta.
     *
     * @param comentario El comentario a añadir.
     * @return El comentario guardado.
     */
    @Override
    @Transactional
    public Comentario agregarComentario(Comentario comentario) {
        if (comentario.getComentario() == null || comentario.getComentario().trim().isEmpty()) {
            throw new ComentarioException("El comentario no puede estar vacío.");
        }
        if (comentario.getUsuario() == null) {
            throw new ComentarioException("El comentario debe estar asociado a un usuario.");
        }
        if (comentario.getReceta() == null) {
            throw new ComentarioException("El comentario debe estar asociado a una receta.");
        }
        return comentarioRepository.save(comentario);
    }

    /**
     * Obtiene todos los comentarios de una receta específica.
     *
     * @param recetaId El ID de la receta.
     * @return Una lista de comentarios para la receta dada.
     */
    @Override
    @Transactional(readOnly = true)
    public Set<Comentario> obtenerComentariosDeReceta(Integer recetaId) {
        Receta receta = recetaRepository.findById(recetaId)
                .orElseThrow(() -> new IllegalArgumentException("Receta no encontrada"));
        // La entidad Receta tiene un Set<Comentario> llamado 'comentarios'
        return receta.getComentarios();
    }

    @Override
    public void crearNotificacion(Integer destinatarioId, TipoNotificacion tipo, Integer emisorId, Integer recetaId,
            String mensaje) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'crearNotificacion'");
    }
}
