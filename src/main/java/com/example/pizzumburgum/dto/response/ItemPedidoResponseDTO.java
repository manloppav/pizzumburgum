package com.example.pizzumburgum.dto.response;

import com.example.pizzumburgum.enums.CantidadCarnes;
import com.example.pizzumburgum.enums.TamanoPizza;
import com.example.pizzumburgum.enums.TipoMasa;
import com.example.pizzumburgum.enums.TipoQueso;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemPedidoResponseDTO {

    private Long id;
    private Long productoId;
    private String productoNombre;
    private Integer cantidad;
    private TamanoPizza tamanioPizza;
    private TipoMasa tipoMasa;
    private TipoQueso tipoQueso;
    private List<String> toppingsPizza;
    private CantidadCarnes cantidadCarnes;
    private List<String> toppingsHamburguesa;
    private List<String> aderezos;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
    private String observaciones;
}