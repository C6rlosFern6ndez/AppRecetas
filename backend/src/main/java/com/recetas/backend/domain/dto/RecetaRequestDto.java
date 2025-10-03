package com.recetas.backend.domain.dto;

import com.recetas.backend.domain.model.enums.Dificultad;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

/**
 * DTO para la solicitud de creación o actualización de una receta.
 * La subida de imagen y la gestión de categorías se manejarán por separado o
 * con IDs.
 */
@Data
public class RecetaRequestDto {
    private Integer id; // Para actualizaciones

    @NotBlank(message = "El título no puede estar vacío")
    @Size(max = 100, message = "El título no puede exceder los 100 caracteres")
    private String titulo;

    @NotBlank(message = "La descripción no puede estar vacía")
    private String descripcion;

    @NotNull(message = "El tiempo de preparación no puede ser nulo")
    @Min(value = 1, message = "El tiempo de preparación debe ser al menos 1 minuto")
    private Integer tiempoPreparacion;

    @NotNull(message = "La dificultad no puede ser nula")
    private Dificultad dificultad;

    @NotNull(message = "Las porciones no pueden ser nulas")
    @Min(value = 1, message = "Las porciones deben ser al menos 1")
    private Integer porciones;

    private String urlImagen; // Para actualizar la URL de la imagen si ya existe

    private Set<Integer> categoriaIds; // Para asociar categorías por ID
    // Nota: Pasos e Ingredientes se manejarían por separado o con DTOs más
    // complejos.
}
