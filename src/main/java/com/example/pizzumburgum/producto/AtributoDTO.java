package com.example.pizzumburgum.producto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AtributoDTO {

    @NotBlank(message = "El nombre del atributo es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombre;

    @Size(max = 100, message = "El campo 'aplicaA' no puede exceder 100 caracteres")
    private String aplicaA;
}
