package com.recetas.backend.dtos;

import com.recetas.backend.models.RecetaIngrediente;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RecetaIngredienteDto {
    @NotNull(message = "El ID de la receta no puede ser nulo")
    private Integer recetaId;

    @NotNull(message = "El ID del ingrediente no puede ser nulo")
    private Integer ingredienteId;

    @NotBlank(message = "La cantidad no puede estar vacía")
    private String cantidad;

    private IngredienteDto ingrediente; // Para mostrar la información del ingrediente

    public static RecetaIngredienteDto fromEntity(RecetaIngrediente recetaIngrediente) {
        RecetaIngredienteDto dto = new RecetaIngredienteDto();
        dto.setRecetaId(recetaIngrediente.getReceta().getId());
        dto.setIngredienteId(recetaIngrediente.getIngrediente().getId());
        dto.setCantidad(recetaIngrediente.getCantidad());
        if (recetaIngrediente.getIngrediente() != null) {
            dto.setIngrediente(IngredienteDto.fromEntity(recetaIngrediente.getIngrediente()));
        }
        return dto;
    }
}
