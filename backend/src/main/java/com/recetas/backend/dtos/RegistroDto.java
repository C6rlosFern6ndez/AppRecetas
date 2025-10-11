package com.recetas.backend.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistroDto {
    @NotBlank(message = "El nombre de usuario no puede estar vacío")
    @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
    private String nombreUsuario;

    @NotBlank(message = "El email no puede estar vacío")
    @Size(max = 100, message = "El email no puede exceder los 100 caracteres")
    @Email(message = "El email debe tener un formato válido")
    private String email;

    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 6, max = 40, message = "La contraseña debe tener entre 6 y 40 caracteres")
    private String contrasena;
}
