package com.recetas.backend.domain.dto;

import lombok.Data;

/**
 * DTO para la respuesta de inicio de sesión, conteniendo el token JWT.
 */
@Data
public class LoginResponseDto {
    private String token;
    private String tipoOperacion = "Bearer"; // Prefijo estándar para tokens JWT

    public LoginResponseDto(String token) {
        this.token = token;
    }
}
