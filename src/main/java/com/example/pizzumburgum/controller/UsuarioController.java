package com.example.pizzumburgum.controller;

import com.example.pizzumburgum.dto.request.DireccionDTO;
import com.example.pizzumburgum.dto.request.TarjetaDTO;
import com.example.pizzumburgum.dto.response.DireccionResponseDTO;
import com.example.pizzumburgum.entities.Usuario;
import com.example.pizzumburgum.security.CustomUserDetails;
import com.example.pizzumburgum.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PutMapping("/{id}/direccion")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    public ResponseEntity<Usuario> cambiarDireccion(
            @PathVariable Long id,
            @RequestBody @Valid DireccionDTO body) {

        validarAccesoUsuario(id);

        // Llamar al mét-odo modificado (ahora retorna DireccionResponseDTO)
        usuarioService.cambiarDireccion(id, body);

        // Retornar el usuario completo para mantener compatibilidad
        Usuario u = usuarioService.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        return ResponseEntity.ok(u);
    }

    @GetMapping("/{id}/direcciones")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')") // Ajusta según tu seguridad
    public ResponseEntity<List<DireccionResponseDTO>> listarDirecciones(@PathVariable Long id) {
        // IMPORTANTE: Validar que el usuario autenticado sea el mismo o admin
        validarAccesoUsuario(id);

        List<DireccionResponseDTO> direcciones = usuarioService.listarDirecciones(id);
        return ResponseEntity.ok(direcciones);
    }

    @PostMapping("/{id}/direcciones")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    public ResponseEntity<DireccionResponseDTO> crearDireccion(
            @PathVariable Long id,
            @RequestBody @Valid DireccionDTO body) {

        validarAccesoUsuario(id);

        DireccionResponseDTO direccion = usuarioService.cambiarDireccion(id, body);
        return ResponseEntity.status(HttpStatus.CREATED).body(direccion);
    }

    @PatchMapping("/{id}/direcciones/{direccionId}/principal")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    public ResponseEntity<DireccionResponseDTO> marcarComoPrincipal(
            @PathVariable Long id,
            @PathVariable Long direccionId) {

        validarAccesoUsuario(id);

        DireccionResponseDTO direccion = usuarioService.marcarDireccionComoPrincipal(id, direccionId);
        return ResponseEntity.ok(direccion);
    }

    @DeleteMapping("/{id}/direcciones/{direccionId}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    public ResponseEntity<Void> eliminarDireccion(
            @PathVariable Long id,
            @PathVariable Long direccionId) {

        validarAccesoUsuario(id);
        usuarioService.eliminarDireccion(id, direccionId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/tarjeta")
    public ResponseEntity<Usuario> cambiarTarjeta(@PathVariable Long id,
                                                  @RequestBody @Valid TarjetaDTO body) {
        Usuario u = usuarioService.cambiarTarjeta(id, body);
        return ResponseEntity.ok(u);
    }

    // ========== Helper: Validar acceso del usuario ==========
    private void validarAccesoUsuario(Long usuarioId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("Usuario no autenticado");
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // Permitir si es el mismo usuario o es admin
        if (!userDetails.getId().equals(usuarioId) &&
                !userDetails.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new SecurityException("No tienes permiso para acceder a este recurso");
        }
    }

    // ========== Manejo de errores ==========
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<String> handleSecurityException(SecurityException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }
}