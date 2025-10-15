package com.example.pizzumburgum.pedido_carrito_pago;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"medioPago", "carrito", "pedido"})
@Entity
@Table(
        name = "pago",
        indexes = {
                @Index(name = "idx_pago_mediopago", columnList = "medio_pago_id"),
                @Index(name = "idx_pago_pedido", columnList = "pedido_id"),
                @Index(name = "idx_pago_carrito", columnList = "carrito_id")
        }
)
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    /** Monto del pago (usa BigDecimal para dinero). */
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    /** Fecha/hora de registro del pago. */
    @Column(nullable = false)
    private LocalDateTime fecha;

    /** Estado del pago (p.ej.: APROBADO, PENDIENTE, RECHAZADO). */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPago estado;

    /** 1:1 con MedioPago – FK vive en PAGO. */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medio_pago_id", nullable = false, unique = true)
    private MedioPago medioPago;

    /** 1:1 con Carrito – FK vive en PAGO. */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "carrito_id", nullable = false, unique = true)
    private Carrito carrito;

    /** FK a Pedido (muchos pagos podrían referir al mismo pedido). */
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;
}

