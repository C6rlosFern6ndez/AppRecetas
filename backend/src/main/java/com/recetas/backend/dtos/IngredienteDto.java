package com.recetas.backend.dtos;

import com.recetas.backend.models.Ingrediente;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class IngredienteDto {
    private Integer id;

    @NotBlank(message = "El nombre del ingrediente no puede estar vacío")
    @Size(max = 100, message = "El nombre del ingrediente no puede exceder los 100 caracteres")
    private String nombre;

    public static IngredienteDto fromEntity(Ingrediente ingrediente) {
        IngredienteDto dto = new IngredienteDto();
        dto.setId(ingrediente.getId());
        dto.setNombre(ingrediente.getNombre());
        return dto;
    }
}
