package com.example.pizzumburgum.dto.dgi;

import java.math.BigDecimal;

public record DgiTicketItemDTO(
        Long id,
        Integer cantidad,
        BigDecimal subtotal,
        Long productoId,   // puede ser null si es "creación"
        Long creacionId    // puede ser null si es "producto"
) {}