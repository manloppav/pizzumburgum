package com.example.pizzumburgum.dto.response;

import com.example.pizzumburgum.enums.TipoTarjeta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TarjetaResponseDTO {

    private Long id;
    private String ultimos4Digitos;
    private String fechaVencimiento;
    private String titular;
    private TipoTarjeta tipo;
    private boolean principal;

    // Campo helper para mostrar en el frontend
    public String getTarjetaEnmascarada() {
        return tipo.name() + " **** **** **** " + ultimos4Digitos;
    }
}
