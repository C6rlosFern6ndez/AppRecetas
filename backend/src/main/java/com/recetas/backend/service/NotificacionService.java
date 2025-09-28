package com.recetas.backend.service;

import java.util.List;

import com.recetas.backend.domain.entity.Notificacion;
import com.recetas.backend.domain.model.enums.TipoNotificacion;

/**
 * Interfaz para los servicios relacionados con la gestión de notificaciones.
 */
public interface NotificacionService {

    /**
     * Crea una nueva notificación para un usuario.
     * 
     * @param usuarioId El ID del usuario que recibirá la notificación.
     * @param tipo      El tipo de notificación.
     * @param mensaje   El mensaje de la notificación.
     * @return La notificación creada.
     */
    Notificacion crearNotificacion(Integer usuarioId, TipoNotificacion tipo, String mensaje);

    /**
     * Obtiene todas las notificaciones para un usuario dado.
     * 
     * @param usuarioId El ID del usuario.
     * @return Una lista de notificaciones para el usuario.
     */
    List<Notificacion> obtenerNotificacionesUsuario(Integer usuarioId);

    /**
     * Marca una notificación como leída.
     * 
     * @param notificacionId El ID de la notificación a marcar como leída.
     */
    void marcarComoLeida(Integer notificacionId);

    /**
     * Elimina una notificación.
     * 
     * @param notificacionId El ID de la notificación a eliminar.
     */
    void eliminarNotificacion(Integer notificacionId);
}
