package com.example.pizzumburgum.dto.response;

import com.example.pizzumburgum.enums.EstadoPago;
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
public class PagoResponseDTO {

    private Long id;
    private String codigoTransaccion;
    private BigDecimal monto;
    private EstadoPago estado;
    private String tarjetaEnmascarada;
    private String mensajeRespuesta;
    private String codigoAutorizacion;
    private LocalDateTime fechaProcesamiento;
    private LocalDateTime fechaCreacion;
}
