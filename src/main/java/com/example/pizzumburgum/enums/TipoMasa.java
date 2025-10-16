package com.example.pizzumburgum.enums;

public enum TipoMasa {
    TRADICIONAL("Tradicional", "Masa tradicional clásica"),
    FINA("Fina", "Masa delgada y crujiente"),
    GRUESA("Gruesa", "Masa esponjosa y gruesa"),
    INTEGRAL("Integral", "Masa integral saludable"),
    SIN_GLUTEN("Sin Gluten", "Masa apta para celíacos");

    private final String nombre;
    private final String descripcion;

    TipoMasa(String nombre, String descripcion) {
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