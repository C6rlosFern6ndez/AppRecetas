package com.recetas.backend.dtos;

import com.recetas.backend.models.Categoria;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CategoriaDto {
    private Integer id;

    @NotBlank(message = "El nombre de la categoría no puede estar vacío")
    @Size(max = 50, message = "El nombre de la categoría no puede exceder los 50 caracteres")
    private String nombre;

    public static CategoriaDto fromEntity(Categoria categoria) {
        CategoriaDto dto = new CategoriaDto();
        dto.setId(categoria.getId());
        dto.setNombre(categoria.getNombre());
        return dto;
    }
}
