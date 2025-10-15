package com.example.pizzumburgum.components;

import jakarta.persistence.*;
import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(
        name = "detalle_carrito",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_detalle_carrito_carrito_creacion",
                columnNames = {"carrito_id", "creacion_id"}
        ),
        indexes = {
                @Index(name = "idx_detalle_carrito_carrito", columnList = "carrito_id"),
                @Index(name = "idx_detalle_carrito_creacion", columnList = "creacion_id")
        }
)
public class DetalleCarrito {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "carrito_id", nullable = false)
    private Carrito carrito;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "creacion_id", nullable = false)
    private Creacion creacion;

    @Column(nullable = false)
    private Integer cantidad;
}
