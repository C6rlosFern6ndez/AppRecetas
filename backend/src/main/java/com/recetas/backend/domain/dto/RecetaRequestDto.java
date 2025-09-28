package com.recetas.backend.domain.dto;

import org.springframework.web.multipart.MultipartFile;

import com.recetas.backend.domain.entity.Categoria;
import com.recetas.backend.domain.model.enums.Dificultad;

import lombok.Data;

import java.util.Set;

/**
 * DTO para la solicitud de creación o actualización de una receta, incluyendo
 * la imagen.
 */
@Data
public class RecetaRequestDto {
    private Integer id; // Para actualizaciones
    private String titulo;
    private String descripcion;
    private Integer tiempoPreparacion;
    private Dificultad dificultad;
    private Integer porciones;
    private MultipartFile imagenFile; // El archivo de imagen a subir
    private Set<Categoria> categorias; // Para asociar categorías
    // Nota: Pasos e Ingredientes se manejarían por separado o con DTOs más
    // complejos.
}
