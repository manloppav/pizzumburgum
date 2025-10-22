package com.example.pizzumburgum.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "direcciones")
@Getter
@Setter
@NoArgsConstructor
public class Direccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "La calle es obligatoria")
    @Size(max = 200, message = "La calle no puede exceder 200 caracteres")
    @Column(nullable = false, length = 200)
    private String calle;

    @NotNull(message = "El número es obligatorio")
    @Min(value = 1, message = "El número de puerta debe ser mayor que 0")
    @Column(nullable = false)
    private Integer numero;

    @Size(max = 100, message = "El apartamento no puede exceder 100 caracteres")
    @Column(length = 100)
    private String apartamento;

    @NotBlank(message = "El barrio es obligatorio")
    @Size(max = 100, message = "El barrio no puede exceder 100 caracteres")
    @Column(nullable = false, length = 100)
    private String barrio;

    @Column(nullable = false)
    private boolean principal = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonBackReference
    private Usuario usuario;

}
