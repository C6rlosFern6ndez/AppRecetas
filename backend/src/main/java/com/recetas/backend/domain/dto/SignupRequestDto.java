package com.recetas.backend.domain.dto;

import lombok.Data;

/**
 * DTO para la solicitud de registro de un nuevo usuario.
 */
@Data
public class SignupRequestDto {
    private String nombreUsuario;
    private String email;
    private String contrasena;
}
