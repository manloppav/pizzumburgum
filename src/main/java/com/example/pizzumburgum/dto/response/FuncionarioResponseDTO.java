package com.example.pizzumburgum.dto.response;

import com.example.pizzumburgum.enums.RolUsuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FuncionarioResponseDTO {

    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private RolUsuario rol;
    private Boolean activo;
    private String codigoEmpleado;
    private LocalDate fechaContratacion;
    private BigDecimal salario;
    private String cargo;
    private Boolean disponible;
    private String turno;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }
}
