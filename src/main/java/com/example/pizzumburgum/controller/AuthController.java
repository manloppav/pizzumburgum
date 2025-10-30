package com.example.pizzumburgum.controller;

import com.example.pizzumburgum.dto.request.LoginDTO;
import com.example.pizzumburgum.dto.request.RegistroUsuarioDTO;
import com.example.pizzumburgum.dto.response.AuthResponseDTO;
import com.example.pizzumburgum.exception.RegistroException;
import com.example.pizzumburgum.security.CustomUserDetails;
import com.example.pizzumburgum.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private final AuthService authService;

    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(@Valid @RequestBody RegistroUsuarioDTO registroDTO) {
        try {
            // Registro público siempre crea CLIENTEs
            AuthResponseDTO response = authService.registrarUsuario(registroDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RegistroException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/registro-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> registrarUsuarioPorAdmin(@Valid @RequestBody RegistroUsuarioDTO registroDTO) {
        try {
            // Obtener el rol del usuario autenticado
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String rolSolicitante = userDetails.getRol();

            // Permitir que el admin especifique el rol
            AuthResponseDTO response = authService.registrarUsuario(registroDTO, rolSolicitante);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RegistroException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            AuthResponseDTO response = authService.login(loginDTO);
            return ResponseEntity.ok(response);
        } catch (RegistroException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

}