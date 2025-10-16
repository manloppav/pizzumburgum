package com.example.pizzumburgum.dto.response;

import com.example.pizzumburgum.component.actores.Direccion;
import com.example.pizzumburgum.enums.RolUsuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteResponseDTO {

    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private RolUsuario rol;
    private Boolean activo;
    private Direccion direccionPrincipal;
    private String documentoIdentidad;
    private LocalDate fechaNacimiento;
    private String preferencias;
    private Integer puntosLealtad;
    private String tarjetaEnmascarada;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
}