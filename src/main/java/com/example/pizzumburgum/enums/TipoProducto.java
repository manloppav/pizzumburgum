package com.example.pizzumburgum.enums;

public enum TipoProducto {
    PIZZA("Pizza", "Pizzas de diferentes sabores"),
    HAMBURGUESA("Hamburguesa", "Hamburguesas gourmet"),
    BEBIDA("Bebida", "Bebidas frías y calientes"),
    ACOMPAÑAMIENTO("Acompañamiento", "Acompañamientos y guarniciones");

    private final String nombre;
    private final String descripcion;

    TipoProducto(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public boolean requiereConfiguracion() {
        return this == PIZZA || this == HAMBURGUESA;
    }
}