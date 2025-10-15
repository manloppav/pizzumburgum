package com.example.pizzumburgum.components;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "carrito", indexes = @Index(name = "idx_carrito_cliente", columnList = "cliente_id"))
public class Carrito {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCarrito;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private boolean activo;

    /** N:1 con Cliente (dueño FK en carrito). */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    /** 1:N con DetalleCarrito (FK vive en detalle). */
    @OneToMany(mappedBy = "carrito", orphanRemoval = true, cascade = CascadeType.ALL)
    @Builder.Default
    private Set<DetalleCarrito> detalles = new LinkedHashSet<>();

    /** 1:1 con Pago (FK en pago). */
    @OneToOne(mappedBy = "carrito", fetch = FetchType.LAZY)
    private Pago pago;

    /** 1:1 con Pedido (FK en pedido). */
    @OneToOne(mappedBy = "carrito", fetch = FetchType.LAZY)
    private Pedido pedido;
}

