package com.example.pizzumburgum.controller;

import com.example.pizzumburgum.dto.bps.BpsUsuariosSistemaResponse;
import com.example.pizzumburgum.service.BpsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/external/bps")
@RequiredArgsConstructor
public class BpsController {

    private final BpsService bpsService;

    // Ejemplo: GET /api/external/bps/usuarios-sistema
    @GetMapping("/usuarios-sistema")
    public ResponseEntity<BpsUsuariosSistemaResponse> getCantidadUsuariosSistema() {
        BpsUsuariosSistemaResponse resp = bpsService.obtenerCantidadFuncionariosSistema();
        return ResponseEntity.ok(resp);
    }
}