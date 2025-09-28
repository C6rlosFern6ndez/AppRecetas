package com.recetas.backend.domain.dto;

import lombok.Data;

/**
 * DTO para la solicitud de inicio de sesión.
 */
@Data
public class LoginRequestDto {
    private String nombreUsuarioOrEmail; // Puede ser nombre de usuario o email
    private String contrasena;
}
