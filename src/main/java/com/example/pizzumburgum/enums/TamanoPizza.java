package com.example.pizzumburgum.enums;

public enum TamanoPizza {
    PEQUEÑA("Pequeña"),
    MEDIANA("Mediana"),
    GRANDE("Grande");

    private final String nombre;

    TamanoPizza(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}