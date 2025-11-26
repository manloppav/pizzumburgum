package com.example.pizzumburgum.controller;

import com.example.pizzumburgum.dto.dgi.DgiTicketsResponse;
import com.example.pizzumburgum.service.DgiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/external/dgi")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "DGI", description = "Endpoints para la Dirección General Impositiva")
@SecurityRequirement(name = "DGI-API-KEY")
public class DgiController {

    private final DgiService dgiService;

    @Operation(
            summary = "Obtener tickets de venta por fecha",
            description = """
                    Retorna todos los tickets (pedidos) generados en una fecha específica.
                    
                    **Información incluida:**
                    - Cantidad total de tickets
                    - Total monetario del día
                    - Detalle de cada ticket con sus items
                    - Información de pago asociada
                    
                    **Formato de fecha:** YYYY-MM-DD (ISO 8601)
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Tickets encontrados exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DgiTicketsResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Formato de fecha inválido"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "API Key no válida o ausente"
            )
    })

    @GetMapping("/tickets")
    public ResponseEntity<DgiTicketsResponse> getTicketsByFecha(
            @Parameter(
                    description = "Fecha de consulta en formato YYYY-MM-DD",
                    example = "2025-11-25",
                    required = true
            )
            @RequestParam("fecha")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fecha
    ) {
        DgiTicketsResponse resp = dgiService.obtenerTicketsPorFecha(fecha);
        return ResponseEntity.ok(resp);
    }
}
