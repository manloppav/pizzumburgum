package com.example.pizzumburgum.enums;

public enum EstadoPago {
    PENDIENTE("Pendiente", "El pago está pendiente de procesamiento"),
    PROCESANDO("Procesando", "El pago está siendo procesado"),
    APROBADO("Aprobado", "El pago ha sido aprobado exitosamente"),
    RECHAZADO("Rechazado", "El pago ha sido rechazado");

    private final String nombre;
    private final String descripcion;

    EstadoPago(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public boolean esExitoso() {
        return this == APROBADO;
    }

    public boolean esDefinitivo() {
        return this == APROBADO || this == RECHAZADO;
    }
}