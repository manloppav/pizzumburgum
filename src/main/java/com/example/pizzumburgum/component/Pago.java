package com.example.pizzumburgum.component;

import com.example.pizzumburgum.enums.EstadoPago;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"pedido"})
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String codigoTransaccion;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    @NotNull(message = "El pedido es obligatorio")
    private Pedido pedido;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPago estado;

    @Embedded
    @Valid
    private Tarjeta tarjetaUtilizada;

    @Column(length = 500)
    private String mensajeRespuesta;

    @Column(length = 100)
    private String codigoAutorizacion;

    private LocalDateTime fechaProcesamiento;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;

    @PrePersist
    public void prePersist() {
        if (estado == null) {
            estado = EstadoPago.PENDIENTE;
        }
        if (codigoTransaccion == null) {
            generarCodigoTransaccion();
        }
    }

    private void generarCodigoTransaccion() {
        this.codigoTransaccion = "TRX-" + System.currentTimeMillis();
    }

    public void aprobar(String codigoAutorizacion) {
        this.estado = EstadoPago.APROBADO;
        this.codigoAutorizacion = codigoAutorizacion;
        this.fechaProcesamiento = LocalDateTime.now();
    }

    public void rechazar(String mensaje) {
        this.estado = EstadoPago.RECHAZADO;
        this.mensajeRespuesta = mensaje;
        this.fechaProcesamiento = LocalDateTime.now();
    }

    public boolean fueAprobado() {
        return estado == EstadoPago.APROBADO;
    }
}