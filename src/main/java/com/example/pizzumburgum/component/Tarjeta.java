package com.example.pizzumburgum.component;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tarjeta {

    @NotBlank(message = "El número de tarjeta es obligatorio")
    @Pattern(regexp = "\\d{16}", message = "El número de tarjeta debe tener 16 dígitos")
    @Column(name = "tarjeta_numero", nullable = false, length = 16)
    private String numero;

    @NotBlank(message = "La fecha de vencimiento es obligatoria")
    @Pattern(regexp = "(0[1-9]|1[0-2])/\\d{2}", message = "La fecha debe estar en formato MM/YY")
    @Column(name = "tarjeta_vencimiento", nullable = false, length = 5)
    private String fechaVencimiento;

    @NotBlank(message = "El nombre del titular es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre del titular debe tener entre 2 y 100 caracteres")
    @Column(name = "tarjeta_titular_nombre", nullable = false, length = 100)
    private String nombreTitular;

    @NotBlank(message = "El apellido del titular es obligatorio")
    @Size(min = 2, max = 100, message = "El apellido del titular debe tener entre 2 y 100 caracteres")
    @Column(name = "tarjeta_titular_apellido", nullable = false, length = 100)
    private String apellidoTitular;

    public String getNumeroEnmascarado() {
        if (numero == null || numero.length() != 16) {
            return "****";
        }
        return "**** **** **** " + numero.substring(12);
    }

    public String getTitularCompleto() {
        return nombreTitular + " " + apellidoTitular;
    }

    public boolean estaVencida() {
        if (fechaVencimiento == null || !fechaVencimiento.matches("(0[1-9]|1[0-2])/\\d{2}")) {
            return true;
        }

        String[] partes = fechaVencimiento.split("/");
        int mes = Integer.parseInt(partes[0]);
        int anio = 2000 + Integer.parseInt(partes[1]);

        java.time.LocalDate hoy = java.time.LocalDate.now();
        java.time.LocalDate vencimiento = java.time.LocalDate.of(anio, mes, 1)
                .plusMonths(1).minusDays(1);

        return vencimiento.isBefore(hoy);
    }
}