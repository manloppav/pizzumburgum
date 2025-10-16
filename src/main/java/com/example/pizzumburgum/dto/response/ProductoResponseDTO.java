package com.example.pizzumburgum.dto.response;

import com.example.pizzumburgum.enums.TipoProducto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoResponseDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private TipoProducto tipo;
    private String categoriaNombre;
    private String imagenUrl;
    private Boolean disponible;
    private Boolean activo;
    private Integer stock;
    private String variante;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
