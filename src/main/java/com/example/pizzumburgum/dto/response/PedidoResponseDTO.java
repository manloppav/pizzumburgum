package com.example.pizzumburgum.dto.response;

import com.example.pizzumburgum.component.actores.Direccion;
import com.example.pizzumburgum.enums.EstadoPedido;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoResponseDTO {

    private Long id;
    private String numeroPedido;
    private Long clienteId;
    private String clienteNombre;
    private List<ItemPedidoResponseDTO> items;
    private EstadoPedido estado;
    private BigDecimal total;
    private Direccion direccionEntrega;
    private String observaciones;
    private PagoResponseDTO pago;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private LocalDateTime fechaEntrega;
}