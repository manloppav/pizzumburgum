package com.example.pizzumburgum.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarritoItemDTO {

    private Long id;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;

    // Para producto
    private Long productoId;
    private String productoNombre;

    // Para creación
    private Long creacionId;
    private String creacionNombre;
}
