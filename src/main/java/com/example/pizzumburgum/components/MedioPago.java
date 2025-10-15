package com.example.pizzumburgum.components;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"cliente", "pago"}) // evita recursión en logs
@Entity
@Table(
        name = "medio_pago",
        indexes = {
                @Index(name = "idx_mediopago_cliente", columnList = "cliente_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_mediopago_token", columnNames = {"token_gateway"})
        }
)
public class MedioPago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    /** Ej.: "CREDITO", "DEBITO" */
    @Column(nullable = false, length = 20)
    private String tipo;

    /** Visa/Mastercard/Amex, etc. */
    @Column(nullable = false, length = 20)
    private String marca;

    /** Nombre del titular impreso en la tarjeta */
    @Column(nullable = false, length = 100)
    private String titular;

    /** Últimos 4 dígitos (no guardar el PAN completo) */
    @Column(name = "ultimos4", nullable = false, length = 4)
    private String ultimos4;

    /** Mes de vencimiento (1–12) */
    @Column(name = "mes_vto", nullable = false)
    private Integer mesVto;

    /** Año de vencimiento (YYYY) */
    @Column(name = "anio_vto", nullable = false)
    private Integer anioVto;

    /** Token seguro provisto por el gateway (único por medio almacenado) */
    @Column(name = "token_gateway", nullable = false, length = 120)
    private String tokenGateway;

    /** N:1 con Cliente (la FK vive acá) */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    /** 1:1 con Pago. Hacemos que Pago sea el dueño de la FK (pago.medioPago_id) */
    @OneToOne(mappedBy = "medioPago", fetch = FetchType.LAZY, optional = true)
    private Pago pago;
}

