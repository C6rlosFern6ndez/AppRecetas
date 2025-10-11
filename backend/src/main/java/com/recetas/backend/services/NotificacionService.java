package com.recetas.backend.services;

import com.recetas.backend.dtos.NotificacionDto;
import com.recetas.backend.exceptions.ResourceNotFoundException;
import com.recetas.backend.models.Notificacion;
import com.recetas.backend.models.Notificacion.TipoNotificacion;
import com.recetas.backend.models.Receta;
import com.recetas.backend.models.Usuario;
import com.recetas.backend.repositories.NotificacionRepository;
import com.recetas.backend.repositories.RecetaRepository;
import com.recetas.backend.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class NotificacionService {

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RecetaRepository recetaRepository;

    public Page<NotificacionDto> getNotificacionesByUsuario(Integer usuarioId, Pageable pageable) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));
        return notificacionRepository.findByUsuario(usuario, pageable).map(NotificacionDto::fromEntity);
    }

    @Transactional
    public NotificacionDto marcarComoLeida(Integer notificacionId, Integer usuarioId) {
        Notificacion notificacion = notificacionRepository.findById(notificacionId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Notificación no encontrada con ID: " + notificacionId));

        if (!notificacion.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("No tienes permiso para modificar esta notificación.");
        }

        notificacion.setLeida(true);
        return NotificacionDto.fromEntity(notificacionRepository.save(notificacion));
    }

    @Transactional
    public void marcarTodasComoLeidas(Integer usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));

        notificacionRepository.findByUsuarioAndLeidaFalse(usuario).forEach(notificacion -> {
            notificacion.setLeida(true);
            notificacionRepository.save(notificacion);
        });
    }

    @Transactional
    public void deleteNotificacion(Integer notificacionId, Integer usuarioId) {
        Notificacion notificacion = notificacionRepository.findById(notificacionId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Notificación no encontrada con ID: " + notificacionId));

        if (!notificacion.getUsuario().getId().equals(usuarioId)) {
            throw new RuntimeException("No tienes permiso para eliminar esta notificación.");
        }
        notificacionRepository.delete(notificacion);
    }

    @Transactional
    public NotificacionDto createNotificacion(Integer usuarioId, TipoNotificacion tipo, Integer emisorId,
            Integer recetaId, String mensaje) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));

        Usuario emisor = null;
        if (emisorId != null) {
            emisor = usuarioRepository.findById(emisorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Emisor no encontrado con ID: " + emisorId));
        }

        Receta receta = null;
        if (recetaId != null) {
            receta = recetaRepository.findById(recetaId)
                    .orElseThrow(() -> new ResourceNotFoundException("Receta no encontrada con ID: " + recetaId));
        }

        Notificacion notificacion = new Notificacion();
        notificacion.setUsuario(usuario);
        notificacion.setTipo(tipo);
        notificacion.setEmisor(emisor);
        notificacion.setReceta(receta);
        notificacion.setMensaje(mensaje);
        notificacion.setLeida(false);
        notificacion.setFechaCreacion(LocalDateTime.now());

        return NotificacionDto.fromEntity(notificacionRepository.save(notificacion));
    }
}
