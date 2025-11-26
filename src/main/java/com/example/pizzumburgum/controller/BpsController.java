package com.example.pizzumburgum.controller;

import com.example.pizzumburgum.dto.bps.BpsUsuariosSistemaResponse;
import com.example.pizzumburgum.service.BpsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/external/bps")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "BPS", description = "Endpoints para el Banco de Previsión Social")
@SecurityRequirement(name = "BPS-API-KEY")
public class BpsController {

    private final BpsService bpsService;

    @Operation(
            summary = "Obtener cantidad de funcionarios del sistema",
            description = """
                    Retorna la cantidad total de usuarios con rol ADMIN (funcionarios)
                    que tienen acceso al sistema.
                    
                    **Información incluida:**
                    - Fecha de consulta
                    - Cantidad de funcionarios registrados
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Consulta exitosa",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BpsUsuariosSistemaResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "API Key no válida o ausente"
            )
    })

    @GetMapping("/usuarios-sistema")
    public ResponseEntity<BpsUsuariosSistemaResponse> getCantidadUsuariosSistema() {
        BpsUsuariosSistemaResponse resp = bpsService.obtenerCantidadFuncionariosSistema();
        return ResponseEntity.ok(resp);
    }
}