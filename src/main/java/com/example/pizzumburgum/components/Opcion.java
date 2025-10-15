package com.example.pizzumburgum.components;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
@Entity @Table(name = "opcion",
        uniqueConstraints = @UniqueConstraint(
                name="uk_opcion_caracteristica_nombre",
                columnNames = {"caracteristica_id", "nombre"}
        ),
        indexes = @Index(name="idx_opcion_caracteristica", columnList = "caracteristica_id"))
public class Opcion {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idOpcion;

    @Column(nullable = false, length = 100)
    private String nombre;          // ej.: "Fina", "Gruesa", "Integral", "Extra queso"

    @Column(name = "precio_extra", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioExtra; // 0.00 si no suma

    /** N:1 con Caracteristica */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "caracteristica_id", nullable = false)
    private Caracteristica caracteristica;
}
