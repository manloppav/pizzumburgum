package com.example.pizzumburgum.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "pedido_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PedidoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "Mínimo debe haber un item")
    @Column(nullable = false)
    private Integer cantidad = 1;

    @NotNull(message = "El subtotal es obligatorio")
    @DecimalMin(value = "0.01", message = "El subtotal debe ser mayor a 0")
    @Digits(integer = 8, fraction = 2, message = "El subtotal debe tener máximo 2 decimales")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pedido_id", nullable = false)
    @JsonBackReference
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creacion_id")
    private Creacion creacion;
}
