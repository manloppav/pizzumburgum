package com.example.pizzumburgum.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CarritoOperacionDTO {

    @NotNull(message = "usuarioId es obligatorio")
    private Long usuarioId;

    // Usado para agregar (producto/creación)
    @Min(value = 1, message = "La cantidad debe ser >= 1")
    private Integer cantidad;

    // Usado para actualizar items existentes
    @Min(value = 1, message = "La nueva cantidad debe ser >= 1")
    private Integer nuevaCantidad;

    /* ===== Getters & Setters ===== */

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public Integer getNuevaCantidad() { return nuevaCantidad; }
    public void setNuevaCantidad(Integer nuevaCantidad) { this.nuevaCantidad = nuevaCantidad; }

    /* Helpers de conveniencia para evitar nulls en el controller */
    public int getCantidadRequerida() {
        if (cantidad == null || cantidad < 1)
            throw new IllegalArgumentException("La cantidad debe ser >= 1");
        return cantidad;
    }

    public int getNuevaCantidadRequerida() {
        if (nuevaCantidad == null || nuevaCantidad < 1)
            throw new IllegalArgumentException("La nueva cantidad debe ser >= 1");
        return nuevaCantidad;
    }

    // Opcional: si preferís nombres más explícitos en el controller
    public int getCantidadRequeridaOrNueva() {
        return nuevaCantidad != null ? getNuevaCantidadRequerida() : getCantidadRequerida();
    }
}
