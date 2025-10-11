package com.recetas.backend.dtos;

import com.recetas.backend.models.Calificacion;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CalificacionDto {
    private Integer id;

    @NotNull(message = "La puntuación no puede ser nula")
    @Min(value = 1, message = "La puntuación debe ser al menos 1")
    @Max(value = 5, message = "La puntuación no puede exceder 5")
    private Integer puntuacion;

    private LocalDateTime fechaCalificacion;
    private UsuarioDto usuario; // Información básica del usuario que califica
    private Integer recetaId; // ID de la receta calificada

    public static CalificacionDto fromEntity(Calificacion calificacion) {
        CalificacionDto dto = new CalificacionDto();
        dto.setId(calificacion.getId());
        dto.setPuntuacion(calificacion.getPuntuacion());
        dto.setFechaCalificacion(calificacion.getFechaCalificacion());
        if (calificacion.getUsuario() != null) {
            dto.setUsuario(UsuarioDto.fromEntity(calificacion.getUsuario()));
        }
        if (calificacion.getReceta() != null) {
            dto.setRecetaId(calificacion.getReceta().getId());
        }
        return dto;
    }
}
