package com.example.pizzumburgum.enums;

public enum TipoQueso {
    MOZZARELLA("Mozzarella", "Queso mozzarella tradicional"),
    CHEDDAR("Cheddar", "Queso cheddar americano"),
    PARMESANO("Parmesano", "Queso parmesano italiano"),
    AZUL("Azul", "Queso azul gourmet"),
    PROVOLONE("Provolone", "Queso provolone ahumado"),
    VEGANO("Vegano", "Queso vegano sin lactosa");

    private final String nombre;
    private final String descripcion;

    TipoQueso(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }
}