package com.recetas.backend.dtos;

import com.recetas.backend.models.Paso;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PasoDto {
    private Integer id;

    @NotNull(message = "El orden del paso no puede ser nulo")
    @Min(value = 1, message = "El orden del paso debe ser al menos 1")
    private Integer orden;

    @NotBlank(message = "La descripción del paso no puede estar vacía")
    private String descripcion;

    public static PasoDto fromEntity(Paso paso) {
        PasoDto dto = new PasoDto();
        dto.setId(paso.getId());
        dto.setOrden(paso.getOrden());
        dto.setDescripcion(paso.getDescripcion());
        return dto;
    }
}
