package com.example.pizzumburgum.enums;

public enum CantidadCarnes {
    UNA(1, "Una carne"),
    DOS(2, "Dos carnes"),
    TRES(3, "Tres carnes");

    private final int cantidad;
    private final String descripcion;

    CantidadCarnes(int cantidad, String descripcion) {
        this.cantidad = cantidad;
        this.descripcion = descripcion;
    }

    public int getCantidad() {
        return cantidad;
    }

    public String getDescripcion() {
        return descripcion;
    }
}