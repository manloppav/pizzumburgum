package com.example.pizzumburgum.dto.request;

import com.example.pizzumburgum.enums.EstadoPedido;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PedidoDTO {
    private Long id;
    private LocalDateTime fechaHora;
    private EstadoPedido estado;
    private BigDecimal precioTotal;
    private String observaciones;
    private String direccionEntrega;
    private String nombreCliente;
    private String emailCliente;
    private List<PedidoItemDTO> items;

}
