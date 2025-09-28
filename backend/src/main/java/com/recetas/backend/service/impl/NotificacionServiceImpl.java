package com.recetas.backend.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.recetas.backend.domain.entity.Notificacion;
import com.recetas.backend.domain.entity.Usuario;
import com.recetas.backend.domain.model.enums.TipoNotificacion;
import com.recetas.backend.domain.repository.NotificacionRepository;
import com.recetas.backend.domain.repository.RecetaRepository;
import com.recetas.backend.domain.repository.UsuarioRepository;
import com.recetas.backend.service.NotificacionService;

/**
 * Implementación de los servicios relacionados con la gestión de
 * notificaciones.
 */
@Service
public class NotificacionServiceImpl implements NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final RecetaRepository recetaRepository;

    public NotificacionServiceImpl(NotificacionRepository notificacionRepository, UsuarioRepository usuarioRepository,
            RecetaRepository recetaRepository) {
        this.notificacionRepository = notificacionRepository;
        this.usuarioRepository = usuarioRepository;
        this.recetaRepository = recetaRepository;
    }

    /**
     * Crea una nueva notificación para un usuario.
     * 
     * @param usuarioId El ID del usuario que recibirá la notificación.
     * @param tipo      El tipo de notificación.
     * @param mensaje   El mensaje de la notificación.
     * @return La notificación creada.
     */
    @Override
    @Transactional
    public Notificacion crearNotificacion(Integer usuarioId, TipoNotificacion tipo, String mensaje) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Notificacion notificacion = new Notificacion();
        notificacion.setUsuario(usuario);
        notificacion.setTipo(tipo);
        // El mensaje puede ser genérico o construido dinámicamente en el futuro.
        // Por ahora, usamos el mensaje proporcionado.
        notificacion.setMensaje(mensaje);
        notificacion.setLeida(false);
        notificacion.setFechaCreacion(java.time.LocalDateTime.now());

        // Si el tipo de notificación requiere un emisor o una receta, se deberían
        // añadir parámetros adicionales al método o deducirlos de la lógica de negocio.
        // Por ejemplo, para una notificación de "nuevo seguidor", el emisor sería el
        // seguidor.
        // Para una notificación de "nuevo comentario", el emisor sería el autor del
        // comentario.

        return notificacionRepository.save(notificacion);
    }

    /**
     * Obtiene todas las notificaciones para un usuario dado.
     * 
     * @param usuarioId El ID del usuario.
     * @return Una lista de notificaciones para el usuario.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Notificacion> obtenerNotificacionesUsuario(Integer usuarioId) {
        // Se asume que el UsuarioRepository tiene un método para encontrar el usuario
        // y que la entidad Usuario tiene una relación OneToMany con Notificacion.
        // Si no es así, se debe buscar directamente por usuario_id en
        // NotificacionRepository.
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        // Si la entidad Usuario tiene una colección de notificaciones:
        // return usuario.getNotificaciones();

        // Si no, buscamos por el ID del usuario en la tabla de notificaciones:
        return notificacionRepository.findByUsuarioId(usuarioId); // Asumiendo que existe este método en el repo
    }

    /**
     * Marca una notificación como leída.
     * 
     * @param notificacionId El ID de la notificación a marcar como leída.
     */
    @Override
    @Transactional
    public void marcarComoLeida(Integer notificacionId) {
        Notificacion notificacion = notificacionRepository.findById(notificacionId)
                .orElseThrow(() -> new IllegalArgumentException("Notificación no encontrada"));
        notificacion.setLeida(true);
        notificacionRepository.save(notificacion);
    }

    /**
     * Elimina una notificación.
     * 
     * @param notificacionId El ID de la notificación a eliminar.
     */
    @Override
    @Transactional
    public void eliminarNotificacion(Integer notificacionId) {
        if (!notificacionRepository.existsById(notificacionId)) {
            throw new IllegalArgumentException("Notificación no encontrada");
        }
        notificacionRepository.deleteById(notificacionId);
    }
}
