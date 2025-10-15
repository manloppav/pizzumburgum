package com.example.pizzumburgum.components;

import jakarta.persistence.*;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "seleccion",
        indexes = {
                @Index(name = "idx_seleccion_creacion", columnList = "creacion_id"),
                @Index(name = "idx_seleccion_caracteristica", columnList = "caracteristica_id")
        },
        uniqueConstraints = @UniqueConstraint(
                name = "uk_seleccion_creacion_caracteristica",
                columnNames = {"creacion_id", "caracteristica_id"}
        )
)
public class Seleccion {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** N:1 con Creacion (dueño). */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creacion_id", nullable = false)
    private Creacion creacion;

    /** N:1 con Caracteristica (opción elegida). */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "caracteristica_id", nullable = false)
    private Caracteristica caracteristica;

    /** N:1 con Opcion (reemplaza a Caracteristica en tu versión anterior) */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "opcion_id", nullable = false)
    private Opcion opcion;
}

