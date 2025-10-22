package com.example.pizzumburgum.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "tarjetas")
@Getter
@Setter
@NoArgsConstructor
public class Tarjeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El token de la tarjeta es obligatorio")
    @JsonIgnore
    @Column(name = "token", nullable = false, length = 100)
    private String token;

    @NotBlank(message = "Últimos 4 dígitos son obligatorios")
    @Pattern(regexp = "\\d{4}", message = "Debe tener exactamente 4 dígitos")
    @Column(name = "ultimos_digitos", nullable = false, length = 4)
    private String ultimos4Digitos;

    @NotBlank(message = "La fecha de vencimiento es obligatoria")
    @Pattern(regexp = "^(0[1-9]|1[0-2])/\\d{2}$", message = "Formato inválido, debe ser MM/YY")
    @Column(name = "fecha_vencimiento", nullable = false, length = 5)
    private String fechaVencimiento;

    @NotBlank(message = "El titular de la tarjeta es obligatorio")
    @Size(min = 2, max = 100, message = "El titular debe tener entre 2 y 100 caracteres")
    @Column(name = "titular", nullable = false, length = 100)
    private String titular;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonBackReference
    private Usuario usuario;

}