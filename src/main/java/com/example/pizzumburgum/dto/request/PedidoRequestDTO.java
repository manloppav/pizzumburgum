package com.example.pizzumburgum.dto.request;

import com.example.pizzumburgum.component.actores.Direccion;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoRequestDTO {

    @NotNull(message = "El ID del cliente es obligatorio")
    private Long clienteId;

    @NotNull(message = "El ID del carrito es obligatorio")
    private Long carritoId;

    @Valid
    @NotNull(message = "La dirección de entrega es obligatoria")
    private Direccion direccionEntrega;

    @Size(max = 1000)
    private String observaciones;

    @NotNull(message = "El ID de la tarjeta es obligatorio")
    private Long tarjetaId;
}
