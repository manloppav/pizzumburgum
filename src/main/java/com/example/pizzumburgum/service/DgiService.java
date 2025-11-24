package com.example.pizzumburgum.service;

import com.example.pizzumburgum.dto.dgi.DgiPagoDTO;
import com.example.pizzumburgum.dto.dgi.DgiTicketDTO;
import com.example.pizzumburgum.dto.dgi.DgiTicketItemDTO;
import com.example.pizzumburgum.dto.dgi.DgiTicketsResponse;
import com.example.pizzumburgum.entities.Pedido;
import com.example.pizzumburgum.repository.PedidoRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DgiService {

    private final PedidoRepositorio pedidoRepositorio;

    @Transactional(readOnly = true)
    public DgiTicketsResponse obtenerTicketsPorFecha(LocalDate fecha) {
        // día calendario [00:00, 24:00)
        LocalDateTime start = fecha.atStartOfDay();
        LocalDateTime end = fecha.plusDays(1).atStartOfDay();

        List<Long> ids = pedidoRepositorio.findIdsWithPagoByDateRange(start, end);
        if (ids.isEmpty()) {
            return new DgiTicketsResponse(
                    fecha, 0, new BigDecimal("0.00"), List.of()
            );
        }

        List<Pedido> pedidos = pedidoRepositorio.findAllWithItemsAndPagoByIds(ids);

        // Mapear a DTO
        List<DgiTicketDTO> dtos = pedidos.stream().map(p -> {
            List<DgiTicketItemDTO> items = p.getItems().stream()
                    .map(it -> new DgiTicketItemDTO(
                            it.getId(),
                            it.getCantidad(),
                            it.getSubtotal(),
                            it.getProducto() != null ? it.getProducto().getId() : null,
                            it.getCreacion() != null ? it.getCreacion().getId() : null
                    ))
                    .toList();

            DgiPagoDTO pagoDto = (p.getPago() != null)
                    ? new DgiPagoDTO(p.getPago().getMonto(), p.getPago().getCodigoAutorizacion())
                    : null;

            return new DgiTicketDTO(
                    p.getId(),
                    p.getFechaHora(),
                    p.getPrecioTotal(),
                    p.getUsuario() != null ? p.getUsuario().getId() : null,
                    items,
                    pagoDto
            );
        }).toList();

        BigDecimal totalDelDia = dtos.stream()
                .map(DgiTicketDTO::total)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        return new DgiTicketsResponse(
                fecha,
                dtos.size(),
                totalDelDia,
                dtos
        );
    }
}