package com.example.pizzumburgum.component.pedido;

import com.example.pizzumburgum.component.actores.Direccion;
import com.example.pizzumburgum.component.pago.Pago;
import com.example.pizzumburgum.component.actores.Cliente;
import com.example.pizzumburgum.enums.EstadoPedido;
import com.example.pizzumburgum.component.pedido.ItemPedido;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"cliente", "items", "pago"})
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String numeroPedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    @NotNull(message = "El cliente es obligatorio")
    private Cliente cliente;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ItemPedido> items = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPedido estado;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Embedded
    private Direccion direccionEntrega;

    @Column(length = 1000)
    private String observaciones;

    @OneToOne(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Pago pago;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;

    private LocalDateTime fechaEntrega;

    @PrePersist
    public void prePersist() {
        if (estado == null) {
            estado = EstadoPedido.EN_COLA;
        }
        if (numeroPedido == null) {
            generarNumeroPedido();
        }
    }

    private void generarNumeroPedido() {
        this.numeroPedido = "PED-" + System.currentTimeMillis();
    }

    public void agregarItem(ItemPedido item) {
        if (item != null) {
            item.setPedido(this);
            this.items.add(item);
            recalcularTotal();
        }
    }

    public void recalcularTotal() {
        this.total = items.stream()
                .map(ItemPedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean puedeTransicionarA(EstadoPedido nuevoEstado) {
        return this.estado.puedeTransicionarA(nuevoEstado);
    }

    public void cambiarEstado(EstadoPedido nuevoEstado) {
        if (puedeTransicionarA(nuevoEstado)) {
            this.estado = nuevoEstado;
            if (nuevoEstado == EstadoPedido.ENTREGADO) {
                this.fechaEntrega = LocalDateTime.now();
            }
        } else {
            throw new IllegalStateException(
                    "No se puede cambiar el estado de " + this.estado + " a " + nuevoEstado
            );
        }
    }

    public boolean estaFinalizado() {
        return estado.esFinal();
    }
}