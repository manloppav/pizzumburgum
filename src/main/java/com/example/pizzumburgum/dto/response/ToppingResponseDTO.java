package com.example.pizzumburgum.dto.response;

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
public class ToppingResponseDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precioAdicional;
    private Boolean disponible;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
