package com.example.pizzumburgum.controller;

import com.example.pizzumburgum.dto.dgi.DgiTicketsResponse;
import com.example.pizzumburgum.service.DgiService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/external/dgi")
@RequiredArgsConstructor
public class DgiController {

    private final DgiService dgiService;

    // Ejemplo: GET /api/external/dgi/tickets?fecha=2025-09-01
    @GetMapping("/tickets")
    public ResponseEntity<DgiTicketsResponse> getTicketsByFecha(
            @RequestParam("fecha") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha
    ) {
        DgiTicketsResponse resp = dgiService.obtenerTicketsPorFecha(fecha);
        return ResponseEntity.ok(resp);
    }
}
