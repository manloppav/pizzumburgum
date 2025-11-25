package com.example.pizzumburgum.controller;

import com.example.pizzumburgum.dto.request.TarjetaDTO;
import com.example.pizzumburgum.dto.response.TarjetaResponseDTO;
import com.example.pizzumburgum.repository.*;
import com.example.pizzumburgum.security.CustomUserDetails;
import com.example.pizzumburgum.service.TarjetaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tarjetas")
@RequiredArgsConstructor
@Validated
@CrossOrigin(origins = "*", maxAge = 3600)
public class TarjetaController {

    private final TarjetaService tarjetaService;

    /**
     * Listar tarjetas del usuario autenticado (para el frontend)
     */
    @GetMapping("/mis-tarjetas")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    public ResponseEntity<List<TarjetaResponseDTO>> listarMisTarjetas() {
        Long usuarioId = obtenerUsuarioIdAutenticado();
        List<TarjetaResponseDTO> tarjetas = tarjetaService.listarTarjetasDeUsuario(usuarioId);
        return ResponseEntity.ok(tarjetas);
    }

    /**
     * Crear nueva tarjeta
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    public ResponseEntity<TarjetaResponseDTO> crearTarjeta(@RequestBody @Valid TarjetaDTO dto) {
        Long usuarioId = obtenerUsuarioIdAutenticado();
        TarjetaResponseDTO tarjeta = tarjetaService.crearTarjeta(usuarioId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(tarjeta);
    }

    /**
     * Marcar tarjeta como principal
     */
    @PatchMapping("/{tarjetaId}/principal")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    public ResponseEntity<TarjetaResponseDTO> marcarComoPrincipal(@PathVariable Long tarjetaId) {
        Long usuarioId = obtenerUsuarioIdAutenticado();
        TarjetaResponseDTO tarjeta = tarjetaService.marcarTarjetaComoPrincipal(usuarioId, tarjetaId);
        return ResponseEntity.ok(tarjeta);
    }

    /* ===== HELPERS ===== */

    private Long obtenerUsuarioIdAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getId();
    }

    /* ===== MANEJO DE ERRORES ===== */

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
