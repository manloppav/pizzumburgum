package com.example.pizzumburgum.enums;

public enum RolUsuario {
    USUARIO("Usuario", "Cliente del sistema"),
    FUNCIONARIO("Funcionario", "Funcionario con privilegios administrativos");

    private final String nombre;
    private final String descripcion;

    RolUsuario(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public boolean esFuncionario() {
        return this == FUNCIONARIO;
    }
}
