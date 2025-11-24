package com.example.pizzumburgum.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PedidoCrearRequestDTO {

    @NotNull(message = "El id de la tarjeta es obligatorio")
    private Long tarjetaId;

    @Size(max = 1000, message = "La nota no puede exceder 1000 caracteres")
    private String nota;

    @Size(max = 255, message = "La dirección no puede exceder 255 caracteres")
    private String direccionEntrega;

    // getters y setters
    public Long getTarjetaId() { return tarjetaId; }
    public void setTarjetaId(Long tarjetaId) { this.tarjetaId = tarjetaId; }

    public String getNota() { return nota; }
    public void setNota(String nota) { this.nota = nota; }

    public String getDireccionEntrega() { return direccionEntrega; }
    public void setDireccionEntrega(String direccionEntrega) { this.direccionEntrega = direccionEntrega; }
}
