package com.recetas.backend.dtos;

import com.recetas.backend.models.Comentario;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ComentarioDto {
    private Integer id;

    @NotBlank(message = "El comentario no puede estar vacío")
    private String comentario;

    private LocalDateTime fechaComentario;
    private UsuarioDto usuario; // Información básica del usuario que comenta

    public static ComentarioDto fromEntity(Comentario comentario) {
        ComentarioDto dto = new ComentarioDto();
        dto.setId(comentario.getId());
        dto.setComentario(comentario.getComentario());
        dto.setFechaComentario(comentario.getFechaComentario());
        if (comentario.getUsuario() != null) {
            dto.setUsuario(UsuarioDto.fromEntity(comentario.getUsuario()));
        }
        return dto;
    }
}
