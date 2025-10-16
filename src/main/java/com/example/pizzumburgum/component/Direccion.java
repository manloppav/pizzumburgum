package com.example.pizzumburgum.component;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Direccion {
    @NotBlank(message = "La calle es obligatoria")
    @Size(max = 200, message = "La calle no puede exceder 200 caracteres")
    private String calle;

    @NotBlank(message = "El número es obligatorio")
    @Size(max = 10, message = "El número no puede exceder 10 caracteres")
    private String numero;

    @Size(max = 100, message = "El apartamento no puede exceder 100 caracteres")
    private String apartamento;

    @NotBlank(message = "El barrio es obligatorio")
    @Size(max = 100, message = "El barrio no puede exceder 100 caracteres")
    private String barrio;

    @NotBlank(message = "La ciudad es obligatoria")
    @Size(max = 100, message = "La ciudad no puede exceder 100 caracteres")
    private String ciudad;

    @Size(max = 500, message = "Las referencias no pueden exceder 500 caracteres")
    private String referencias;

    private Double latitud;
    private Double longitud;

    public String getDireccionCompleta() {
        StringBuilder sb = new StringBuilder();
        sb.append(calle).append(" ").append(numero);
        if (apartamento != null && !apartamento.isEmpty()) {
            sb.append(", Apt. ").append(apartamento);
        }
        sb.append(", ").append(barrio);
        sb.append(", ").append(ciudad);
        return sb.toString();
    }
}
