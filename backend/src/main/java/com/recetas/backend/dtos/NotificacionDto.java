package com.recetas.backend.dtos;

import com.recetas.backend.models.Notificacion;
import com.recetas.backend.models.Notificacion.TipoNotificacion;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificacionDto {
    private Integer id;
    private Integer usuarioId; // ID del usuario que recibe la notificación
    private TipoNotificacion tipo;
    private Integer emisorId; // ID del usuario que origina la notificación (opcional)
    private Integer recetaId; // ID de la receta relacionada (opcional)
    private String mensaje;
    private Boolean leida;
    private LocalDateTime fechaCreacion;

    public static NotificacionDto fromEntity(Notificacion notificacion) {
        NotificacionDto dto = new NotificacionDto();
        dto.setId(notificacion.getId());
        dto.setUsuarioId(notificacion.getUsuario().getId());
        dto.setTipo(notificacion.getTipo());
        if (notificacion.getEmisor() != null) {
            dto.setEmisorId(notificacion.getEmisor().getId());
        }
        if (notificacion.getReceta() != null) {
            dto.setRecetaId(notificacion.getReceta().getId());
        }
        dto.setMensaje(notificacion.getMensaje());
        dto.setLeida(notificacion.getLeida());
        dto.setFechaCreacion(notificacion.getFechaCreacion());
        return dto;
    }
}
