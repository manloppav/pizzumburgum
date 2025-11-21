package com.example.pizzumburgum.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PedidoItemDTO {
    private Long id;
    private String nombreItem; // Nombre del producto o creación
    private String tipo; // "PRODUCTO" o "CREACION"
    private Integer cantidad;
    private BigDecimal subtotal;
    private Long productoId;
    private Long creacionId;
}
