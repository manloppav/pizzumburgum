package com.example.pizzumburgum.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class DireccionDTO {

    @NotBlank(message = "La calle es obligatoria")
    @Size(max = 200)
    private String calle;

    @NotNull(message = "El número es obligatorio")
    @Min(value = 1)
    private Integer numero;

    @Size(max = 100)
    private String apartamento;

    @NotBlank(message = "El barrio es obligatorio")
    @Size(max = 100)
    private String barrio;

    private boolean principal = true;

}
