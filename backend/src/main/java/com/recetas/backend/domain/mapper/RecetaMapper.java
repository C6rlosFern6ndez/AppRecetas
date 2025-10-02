package com.recetas.backend.domain.mapper;

import com.recetas.backend.domain.dto.RecetaRequestDto;
import com.recetas.backend.domain.entity.Receta;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RecetaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "categorias", ignore = true)
    @Mapping(target = "ingredientes", ignore = true)
    @Mapping(target = "pasos", ignore = true)
    @Mapping(target = "comentarios", ignore = true)
    @Mapping(target = "calificaciones", ignore = true)
    @Mapping(target = "meGustas", ignore = true)
    @Mapping(target = "urlImagen", ignore = true) // Ignorar urlImagen ya que no está en el DTO
    Receta toEntity(RecetaRequestDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    @Mapping(target = "fechaCreacion", ignore = true)
    @Mapping(target = "categorias", ignore = true)
    @Mapping(target = "ingredientes", ignore = true)
    @Mapping(target = "pasos", ignore = true)
    @Mapping(target = "comentarios", ignore = true)
    @Mapping(target = "calificaciones", ignore = true)
    @Mapping(target = "meGustas", ignore = true)
    @Mapping(target = "urlImagen", ignore = true) // Ignorar urlImagen ya que no está en el DTO
    void updateEntityFromDto(RecetaRequestDto dto, @MappingTarget Receta entity);
}
