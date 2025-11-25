package com.example.pizzumburgum.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CrearPedidoDTO {

    @NotNull(message = "La tarjeta es obligatoria")
    private Long tarjetaId;

    @Size(max = 1000, message = "Las observaciones no pueden exceder 1000 caracteres")
    private String observaciones;

    @NotBlank(message = "La dirección de entrega es obligatoria")
    @Size(max = 255)
    private String direccionEntrega;

}
