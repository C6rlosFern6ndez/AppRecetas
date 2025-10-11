package com.recetas.backend.dtos;

import com.recetas.backend.models.Receta;
import com.recetas.backend.models.Receta.Dificultad;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class RecetaDto {
    private Integer id;

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

    private String urlImagen;
    private LocalDateTime fechaCreacion;
    private UsuarioDto usuario; // Información básica del usuario creador
    private List<CategoriaDto> categorias;
    private List<PasoDto> pasos;
    private List<RecetaIngredienteDto> ingredientes;
    private List<ComentarioDto> comentarios;
    private List<CalificacionDto> calificaciones;
    private Long totalLikes; // Para mostrar el número de likes

    public static RecetaDto fromEntity(Receta receta) {
        RecetaDto dto = new RecetaDto();
        dto.setId(receta.getId());
        dto.setTitulo(receta.getTitulo());
        dto.setDescripcion(receta.getDescripcion());
        dto.setTiempoPreparacion(receta.getTiempoPreparacion());
        dto.setDificultad(receta.getDificultad());
        dto.setPorciones(receta.getPorciones());
        dto.setUrlImagen(receta.getUrlImagen());
        dto.setFechaCreacion(receta.getFechaCreacion());
        if (receta.getUsuario() != null) {
            dto.setUsuario(UsuarioDto.fromEntity(receta.getUsuario()));
        }
        if (receta.getCategorias() != null) {
            dto.setCategorias(
                    receta.getCategorias().stream().map(CategoriaDto::fromEntity).collect(Collectors.toList()));
        }
        if (receta.getPasos() != null) {
            dto.setPasos(receta.getPasos().stream().map(PasoDto::fromEntity).collect(Collectors.toList()));
        }
        if (receta.getIngredientes() != null) {
            dto.setIngredientes(receta.getIngredientes().stream().map(RecetaIngredienteDto::fromEntity)
                    .collect(Collectors.toList()));
        }
        if (receta.getComentarios() != null) {
            dto.setComentarios(
                    receta.getComentarios().stream().map(ComentarioDto::fromEntity).collect(Collectors.toList()));
        }
        if (receta.getCalificaciones() != null) {
            dto.setCalificaciones(
                    receta.getCalificaciones().stream().map(CalificacionDto::fromEntity).collect(Collectors.toList()));
        }
        // totalLikes se calculará en el servicio o controlador
        return dto;
    }
}
