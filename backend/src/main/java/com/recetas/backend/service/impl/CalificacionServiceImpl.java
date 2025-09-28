package com.recetas.backend.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.recetas.backend.domain.entity.Calificacion;
import com.recetas.backend.domain.entity.Receta;
import com.recetas.backend.domain.entity.Usuario;
import com.recetas.backend.domain.repository.CalificacionRepository;
import com.recetas.backend.domain.repository.RecetaRepository;
import com.recetas.backend.domain.repository.UsuarioRepository;
import com.recetas.backend.service.CalificacionService;

/**
 * Implementación de los servicios relacionados con la gestión de
 * calificaciones.
 */
@Service
public class CalificacionServiceImpl implements CalificacionService {

    private final CalificacionRepository calificacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final RecetaRepository recetaRepository;

    public CalificacionServiceImpl(CalificacionRepository calificacionRepository, UsuarioRepository usuarioRepository,
            RecetaRepository recetaRepository) {
        this.calificacionRepository = calificacionRepository;
        this.usuarioRepository = usuarioRepository;
        this.recetaRepository = recetaRepository;
    }

    /**
     * Crea o actualiza la calificación de una receta.
     * Si el usuario ya ha calificado la receta, se actualiza la puntuación.
     * 
     * @param usuarioId  El ID del usuario que califica.
     * @param recetaId   El ID de la receta a calificar.
     * @param puntuacion La puntuación dada (ej. 1-5).
     * @return La calificación creada o actualizada.
     */
    @Override
    @Transactional
    public Calificacion calificarReceta(Integer usuarioId, Integer recetaId, Integer puntuacion) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        Receta receta = recetaRepository.findById(recetaId)
                .orElseThrow(() -> new IllegalArgumentException("Receta no encontrada"));

        // Validar la puntuación (ej. entre 1 y 5)
        if (puntuacion < 1 || puntuacion > 5) {
            throw new IllegalArgumentException("La puntuación debe estar entre 1 y 5.");
        }

        // Buscar si ya existe una calificación para este usuario y receta
        Optional<Calificacion> calificacionExistente = calificacionRepository.findByUsuarioIdAndRecetaId(usuarioId,
                recetaId);

        Calificacion calificacion;
        if (calificacionExistente.isPresent()) {
            // Si existe, la actualizamos
            calificacion = calificacionExistente.get();
            calificacion.setPuntuacion(puntuacion);
            calificacion.setFechaCalificacion(java.time.LocalDateTime.now()); // Actualizar fecha de calificación
        } else {
            // Si no existe, la creamos
            calificacion = new Calificacion();
            calificacion.setUsuario(usuario);
            calificacion.setReceta(receta);
            calificacion.setPuntuacion(puntuacion);
            calificacion.setFechaCalificacion(java.time.LocalDateTime.now());
        }

        return calificacionRepository.save(calificacion);
    }

    /**
     * Elimina la calificación de una receta por un usuario.
     * 
     * @param usuarioId El ID del usuario cuya calificación se eliminará.
     * @param recetaId  El ID de la receta cuya calificación se eliminará.
     */
    @Override
    @Transactional
    public void eliminarCalificacion(Integer usuarioId, Integer recetaId) {
        // Buscar la calificación por usuario y receta
        Calificacion calificacion = calificacionRepository.findByUsuarioIdAndRecetaId(usuarioId, recetaId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró una calificación para este usuario y receta."));

        calificacionRepository.delete(calificacion);
    }

    /**
     * Busca una calificación por su ID.
     * 
     * @param id El ID de la calificación a buscar.
     * @return La calificación si se encuentra, o null si no existe.
     */
    @Override
    @Transactional(readOnly = true)
    public Calificacion findById(Integer id) {
        return calificacionRepository.findById(id).orElse(null);
    }
}
