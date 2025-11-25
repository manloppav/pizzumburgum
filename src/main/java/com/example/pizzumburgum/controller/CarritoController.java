package com.example.pizzumburgum.controller;

import com.example.pizzumburgum.dto.request.CarritoOperacionDTO;
import com.example.pizzumburgum.dto.response.CarritoDTO;
import com.example.pizzumburgum.entities.Carrito;
import com.example.pizzumburgum.service.CarritoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrito")
@Validated
public class CarritoController {

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    /**
     * ================== Productos sueltos ==================
     */

    // Agregar producto suelto al carrito
    @PostMapping("/productos/{productoId}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    public ResponseEntity<?> agregarProducto(
            @PathVariable Long productoId,
            @RequestBody @Valid CarritoOperacionDTO dto) {

        try {
            Carrito carrito = carritoService.agregarProductoSuelto(
                    dto.getUsuarioId(), productoId, dto.getCantidadRequerida());
            CarritoDTO carritoDTO = carritoService.convertirADTO(carrito);
            return ResponseEntity.ok(carritoDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * ================== Creaciones (pizzas/hamburguesas) ==================
     */

    // Agregar creación al carrito
    @PostMapping("/creaciones/{creacionId}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    public ResponseEntity<?> agregarCreacion(
            @PathVariable Long creacionId,
            @RequestBody @Valid CarritoOperacionDTO dto) {

        try {
            Carrito carrito = carritoService.agregarCreacion(
                    dto.getUsuarioId(), creacionId, dto.getCantidadRequerida());
            CarritoDTO carritoDTO = carritoService.convertirADTO(carrito);
            return ResponseEntity.ok(carritoDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * ================== Items existentes ==================
     */

    // Actualizar cantidad de un item del carrito (por id del item)
    @PutMapping("/items/{carritoItemId}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    public ResponseEntity<?> actualizarCantidadItem(
            @PathVariable Long carritoItemId,
            @RequestBody @Valid CarritoOperacionDTO dto) {
        try {
            Carrito carrito = carritoService.actualizarCantidad(
                    dto.getUsuarioId(), carritoItemId, dto.getNuevaCantidadRequerida());
            return ResponseEntity.ok(carritoService.convertirADTO(carrito));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<CarritoDTO> obtenerCarrito(@RequestParam Long usuarioId) {
        try {
            Carrito carrito = carritoService.obtenerOCrearCarrito(usuarioId);
            CarritoDTO dto = carritoService.convertirADTO(carrito);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @DeleteMapping("/items/{carritoItemId}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    public ResponseEntity<?> eliminarItem(
            @PathVariable Long carritoItemId,
            @RequestParam Long usuarioId) {
        try {
            Carrito carrito = carritoService.eliminarItem(usuarioId, carritoItemId);
            return ResponseEntity.ok(carritoService.convertirADTO(carrito));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * ================== Manejo de errores simples ==================
     */

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
