package com.example.pizzumburgum.dto.request;

import com.example.pizzumburgum.enums.TipoTarjeta;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class TarjetaDTO {

    @NotBlank(message = "El número de tarjeta es obligatorio")
    @Pattern(regexp = "\\d{13,19}", message = "El número de tarjeta debe tener entre 13 y 19 dígitos")
    private String numeroTarjeta;

    @NotBlank(message = "La fecha de vencimiento es obligatoria")
    @Pattern(regexp = "^(0[1-9]|1[0-2])/\\d{2}$", message = "Formato inválido, debe ser MM/YY")
    private String fechaVencimiento;

    @NotBlank(message = "El CVV es obligatorio")
    @Pattern(regexp = "\\d{3,4}", message = "El CVV debe tener 3 o 4 dígitos")
    private String cvv; // No se guarda, solo para validación

    @NotBlank(message = "El titular de la tarjeta es obligatorio")
    @Size(min = 2, max = 100)
    private String titular;

    @NotNull(message = "El tipo de tarjeta es obligatorio")
    private TipoTarjeta tipo;

    private boolean principal = true;

}
