package com.example.pizzumburgum.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DireccionResponseDTO {

    private Long id;
    private String calle;
    private Integer numero;
    private String apartamento;
    private String barrio;
    private boolean principal;

    // Campo helper para el frontend
    public String getDireccionCompleta() {
        StringBuilder sb = new StringBuilder();
        sb.append(calle).append(" ").append(numero);
        if (apartamento != null && !apartamento.isEmpty()) {
            sb.append(", Apto ").append(apartamento);
        }
        sb.append(", ").append(barrio);
        return sb.toString();
    }
}
