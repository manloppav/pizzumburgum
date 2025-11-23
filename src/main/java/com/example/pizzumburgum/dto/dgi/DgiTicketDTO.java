package com.example.pizzumburgum.dto.dgi;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record DgiTicketDTO(
        Long id,
        LocalDateTime fechaHora,
        BigDecimal total,
        Long usuarioId,
        List<DgiTicketItemDTO> items,
        DgiPagoDTO pago
) {}
