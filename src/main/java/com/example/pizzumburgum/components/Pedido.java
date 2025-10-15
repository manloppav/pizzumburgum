package com.example.pizzumburgum.components;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "pedido",
        indexes = {
                @Index(name = "idx_pedido_cliente", columnList = "cliente_id"),
                @Index(name = "idx_pedido_direccion", columnList = "direccion_id")
        })
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long idPedido;

    @Column(nullable = false)
    private LocalDateTime fecha;          // fecha y hora del pedido

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoPedido estado;          // EN_COLA, EN_PREPARACION, ...

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;             // importe total

    // -------- Relaciones --------

    /** N:1 con Cliente (FK vive en pedido). */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    /** N:1 con Direccion (domicilio de entrega). Ajusta el nombre si tu clase es distinta. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "direccion_id", nullable = false)
    private Direccion direccion;

    /** 1:1 con Carrito (FK en Pedido). */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "carrito_id", nullable = false, unique = true)
    private Carrito carrito;

    /** 1:N con Pago (lado inverso; la FK vive en PAGO). */
    @OneToMany(mappedBy = "pedido", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Pago> pagos = new LinkedHashSet<>();
}


