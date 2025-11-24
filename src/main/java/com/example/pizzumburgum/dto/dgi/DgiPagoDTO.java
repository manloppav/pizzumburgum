package com.example.pizzumburgum.dto.dgi;

import java.math.BigDecimal;

public record DgiPagoDTO(
        BigDecimal monto,
        String codigoAutorizacion
) {}
