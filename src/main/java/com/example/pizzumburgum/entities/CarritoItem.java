package com.example.pizzumburgum.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "carrito_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CarritoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "Mínimo debe haber un item")
    @Column(nullable = false)
    private Integer cantidad = 1;

    /** Precio unitario *congelado* al momento de agregar/actualizar el item */
    @NotNull(message = "El precio unitario es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio unitario debe ser mayor a 0")
    @Digits(integer = 8, fraction = 2, message = "Máximo 2 decimales")
    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    /** Subtotal = precioUnitario * cantidad (con 2 decimales, HALF_UP) */
    @NotNull(message = "El subtotal es obligatorio")
    @DecimalMin(value = "0.01", message = "El subtotal debe ser mayor a 0")
    @Digits(integer = 10, fraction = 2, message = "El subtotal debe tener máximo 2 decimales")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "carrito_id", nullable = false)
    @JsonBackReference
    private Carrito carrito;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creacion_id")
    private Creacion creacion;

    /** Invariante: exactamente uno de (producto, creacion) debe estar seteado */
    @AssertTrue(message = "Debe seleccionar un producto O una creación, pero no ambos")
    public boolean isProductoXorCreacion() {
        return (producto != null) ^ (creacion != null);
    }

    /** Helper para recalcular subtotal cuando cambian cantidad o precio unitario */
    private void recomputarSubtotal() {
        if (precioUnitario != null && cantidad != null) {
            this.subtotal = precioUnitario
                    .multiply(BigDecimal.valueOf(cantidad))
                    .setScale(2, RoundingMode.HALF_UP);
        }
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
        recomputarSubtotal();
    }

    public void setPrecioUnitario(BigDecimal precioUnitario) {
        if (precioUnitario != null) {
            this.precioUnitario = precioUnitario.setScale(2, RoundingMode.HALF_UP);
        } else {
            this.precioUnitario = null;
        }
        recomputarSubtotal();
    }

    /** Ya no leemos precio de Producto/Creacion aquí: el snapshot viene del Service */
    @PrePersist
    @PreUpdate
    public void validarSubtotalCongruente() {
        recomputarSubtotal();
    }
}
