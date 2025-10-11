package com.recetas.backend.dtos;

import com.recetas.backend.models.Rol;
import com.recetas.backend.models.Usuario;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UsuarioDto {
    private Integer id;
    private String nombreUsuario;
    private String email;
    private String urlFotoPerfil;
    private LocalDateTime fechaRegistro;
    private String rol; // Solo el nombre del rol

    public static UsuarioDto fromEntity(Usuario usuario) {
        UsuarioDto dto = new UsuarioDto();
        dto.setId(usuario.getId());
        dto.setNombreUsuario(usuario.getNombreUsuario());
        dto.setEmail(usuario.getEmail());
        dto.setUrlFotoPerfil(usuario.getUrlFotoPerfil());
        dto.setFechaRegistro(usuario.getFechaRegistro());
        dto.setRol(usuario.getRol().getNombre());
        return dto;
    }
}
