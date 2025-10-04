package com.recetas.backend.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.recetas.backend.domain.entity.Notificacion;
import com.recetas.backend.domain.entity.Receta;
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
    public Usuario usuario;

    public NotificacionServiceImpl(NotificacionRepository notificacionRepository, UsuarioRepository usuarioRepository,
            RecetaRepository recetaRepository) {
        this.notificacionRepository = notificacionRepository;
        this.usuarioRepository = usuarioRepository;
        this.recetaRepository = recetaRepository;
    }

    @Override
    @Transactional
    public Notificacion crearNotificacion(Integer usuarioId, TipoNotificacion tipo, Integer emisorId, Long recetaId) {
        usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario receptor no encontrado"));
        Usuario emisor = usuarioRepository.findById(emisorId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario emisor no encontrado"));

        Notificacion notificacion = new Notificacion();
        notificacion.setUsuario(usuario);
        notificacion.setTipo(tipo);
        notificacion.setEmisor(emisor);

        String mensaje;
        switch (tipo) {
            case NUEVO_SEGUIDOR:
                mensaje = String.format("El usuario %s ha comenzado a seguirte.", emisor.getNombreUsuario());
                break;
            case ME_GUSTA_RECETA:
                Receta recetaLike = recetaRepository.findById(recetaId.intValue())
                        .orElseThrow(() -> new IllegalArgumentException("Receta no encontrada"));
                notificacion.setReceta(recetaLike);
                mensaje = String.format("A %s le ha gustado tu receta '%s'.", emisor.getNombreUsuario(),
                        recetaLike.getTitulo());
                break;
            case NUEVO_COMENTARIO:
                Receta recetaComentario = recetaRepository.findById(recetaId.intValue())
                        .orElseThrow(() -> new IllegalArgumentException("Receta no encontrada"));
                notificacion.setReceta(recetaComentario);
                mensaje = String.format("%s ha comentado en tu receta '%s'.", emisor.getNombreUsuario(),
                        recetaComentario.getTitulo());
                break;
            default:
                throw new IllegalArgumentException("Tipo de notificación no soportado");
        }

        notificacion.setMensaje(mensaje);
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
        usuario = usuarioRepository.findById(usuarioId)
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
