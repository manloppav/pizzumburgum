package com.example.pizzumburgum.entities;

import com.example.pizzumburgum.enums.EstadoPago;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
@Getter
@Setter
@NoArgsConstructor
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    @Digits(integer = 8, fraction = 2, message = "El precio debe tener máximo 2 decimales")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @NotNull(message = "La fecha y hora de pago es obligatoria")
    @Column(name = "fecha_pago", nullable = false)
    private LocalDateTime fechaPago;

    @NotNull(message = "El estado del pago es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPago estado;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pedido_id", nullable = false, unique = true)
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tarjeta_id")
    private Tarjeta tarjeta;

    @PrePersist
    protected void onCreate() {
        fechaPago = LocalDateTime.now();
    }

}