package com.example.pizzumburgum.web;

import com.example.pizzumburgum.dto.request.CarritoOperacionDTO;
import com.example.pizzumburgum.entities.Carrito;
import com.example.pizzumburgum.service.CarritoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    /** ================== Productos sueltos ================== */

    // Agregar producto suelto al carrito
    @PostMapping("/productos/{productoId}")
    public ResponseEntity<Carrito> agregarProducto(
            @PathVariable Long productoId,
            @RequestBody @Valid CarritoOperacionDTO dto) {

        Carrito resp = carritoService.agregarProductoSuelto(
                dto.getUsuarioId(), productoId, dto.getCantidadRequerida());
        return ResponseEntity.ok(resp);
    }

    /** ================== Creaciones (pizzas/hamburguesas) ================== */

    // Agregar creación al carrito
    @PostMapping("/creaciones/{creacionId}")
    public ResponseEntity<Carrito> agregarCreacion(
            @PathVariable Long creacionId,
            @RequestBody @Valid CarritoOperacionDTO dto) {

        Carrito resp = carritoService.agregarCreacion(
                dto.getUsuarioId(), creacionId, dto.getCantidadRequerida());
        return ResponseEntity.ok(resp);
    }

    /** ================== Items existentes ================== */

    // Actualizar cantidad de un item del carrito (por id del item)
    @PutMapping("/items/{carritoItemId}")
    public ResponseEntity<Carrito> actualizarCantidadItem(
            @PathVariable Long carritoItemId,
            @RequestBody @Valid CarritoOperacionDTO dto) {

        // En el service actualizarCantidad espera (usuarioId, carritoItemId, nuevaCantidad)
        Carrito resp = carritoService.actualizarCantidad(
                dto.getUsuarioId(), carritoItemId, dto.getNuevaCantidad());
        return ResponseEntity.ok(resp);
    }

    /** ================== Manejo de errores simples ================== */

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
