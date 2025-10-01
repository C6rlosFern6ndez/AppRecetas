package com.recetas.backend.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO para la solicitud de inicio de sesión.
 */
@Data
public class LoginRequestDto {
    @NotBlank(message = "El nombre de usuario o email no puede estar vacío")
    private String nombreUsuarioOrEmail; // Puede ser nombre de usuario o email

    @NotBlank(message = "La contraseña no puede estar vacía")
    private String contrasena;
}
