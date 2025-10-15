package com.example.pizzumburgum.components;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Pedido {
    private int id_pedido;        // PK
    private LocalDateTime fecha;  // fecha y hora del pedido
    private EstadoPedido estado;  // EN_COLA, EN_PREPARACION, EN_CAMINO, ENTREGADO, CANCELADO
    private BigDecimal total;     // importe total

    //
    private int id_cliente;       // → Cliente
    private int id_domicilio;     // → Domicilio
    private int id_medio_pago;    // → Medio_Pago
}

