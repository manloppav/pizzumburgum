package com.example.pizzumburgum.dto.dgi;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record DgiTicketsResponse(
        LocalDate fecha,
        Integer cantidad,
        BigDecimal totalDelDia,
        List<DgiTicketDTO> tickets
) {}
