package com.recetas.backend.dtos;

import lombok.Data;

import java.util.List;

@Data
public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private Integer id;
    private String nombreUsuario;
    private String email;
    private List<String> roles;

    public JwtResponse(String accessToken, Integer id, String nombreUsuario, String email, List<String> roles) {
        this.token = accessToken;
        this.id = id;
        this.nombreUsuario = nombreUsuario;
        this.email = email;
        this.roles = roles;
    }
}
