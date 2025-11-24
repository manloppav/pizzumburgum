package com.example.pizzumburgum.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponseDTO {

    private String token;
    private String tipo = "Bearer";
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String rol;

    public AuthResponseDTO(String token, Long id, String nombre, String apellido, String email, String rol) {
        this.token = token;
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.rol = rol;
    }
}
