package com.example.pizzumburgum.enums;

public enum EstadoPedido {
    EN_COLA("En Cola", "El pedido está en cola esperando ser preparado"),
    PREPARACION("En Preparación", "El pedido está siendo preparado"),
    EN_CAMINO("En Camino", "El pedido está en camino al cliente"),
    ENTREGADO("Entregado", "El pedido ha sido entregado");

    private final String nombre;
    private final String descripcion;

    EstadoPedido(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public boolean puedeTransicionarA(EstadoPedido nuevoEstado) {
        switch (this) {
            case EN_COLA:
                return nuevoEstado == PREPARACION;
            case PREPARACION:
                return nuevoEstado == EN_CAMINO;
            case EN_CAMINO:
                return nuevoEstado == ENTREGADO;
            default:
                return false;
        }
    }

    public boolean esFinal() {
        return this == ENTREGADO;
    }
}
