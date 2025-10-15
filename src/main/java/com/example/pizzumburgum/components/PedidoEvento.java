package com.example.pizzumburgum.components;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PedidoEvento {
    private long id_evento;                 // PK del evento
    private int id_pedido;                  // FK → Pedido
    private LocalDateTime timestamp;        // momento del cambio de estado
    private EstadoPedido estado;            // estado asignado en ese instante
}
