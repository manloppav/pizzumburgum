package com.example.pizzumburgum.component;

import com.example.pizzumburgum.enums.CantidadCarnes;
import com.example.pizzumburgum.enums.TamanoPizza;
import com.example.pizzumburgum.enums.TamanoPizza;
import com.example.pizzumburgum.enums.TipoMasa;
import com.example.pizzumburgum.enums.TipoQueso;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "items_carrito")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"carrito", "producto"})
public class ItemCarrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrito_id", nullable = false)
    @NotNull(message = "El carrito es obligatorio")
    private Carrito carrito;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    @NotNull(message = "El producto es obligatorio")
    private Producto producto;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Column(nullable = false)
    private Integer cantidad;

    // Configuración para Pizzas
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TamanoPizza tamanioPizza;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TipoMasa tipoMasa;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TipoQueso tipoQueso;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "item_carrito_toppings_pizza",
            joinColumns = @JoinColumn(name = "item_carrito_id"),
            inverseJoinColumns = @JoinColumn(name = "topping_pizza_id")
    )
    @Builder.Default
    private List<ToppingPizza> toppingsPizza = new ArrayList<>();

    // Configuración para Hamburguesas
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private CantidadCarnes cantidadCarnes;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "item_carrito_toppings_hamburguesa",
            joinColumns = @JoinColumn(name = "item_carrito_id"),
            inverseJoinColumns = @JoinColumn(name = "topping_hamburguesa_id")
    )
    @Builder.Default
    private List<ToppingHamburguesa> toppingsHamburguesa = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "item_carrito_aderezos",
            joinColumns = @JoinColumn(name = "item_carrito_id"),
            inverseJoinColumns = @JoinColumn(name = "aderezo_id")
    )
    @Builder.Default
    private List<Aderezo> aderezos = new ArrayList<>();

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(length = 500)
    private String observaciones;

    public BigDecimal calcularSubtotal() {
        if (precioUnitario == null || cantidad == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal subtotalBase = precioUnitario.multiply(BigDecimal.valueOf(cantidad));

        // Agregar precio de toppings de pizza
        if (toppingsPizza != null && !toppingsPizza.isEmpty()) {
            BigDecimal totalToppingsPizza = toppingsPizza.stream()
                    .map(ToppingPizza::getPrecioAdicional)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .multiply(BigDecimal.valueOf(cantidad));
            subtotalBase = subtotalBase.add(totalToppingsPizza);
        }

        // Agregar precio de toppings de hamburguesa
        if (toppingsHamburguesa != null && !toppingsHamburguesa.isEmpty()) {
            BigDecimal totalToppingsHamburguesa = toppingsHamburguesa.stream()
                    .map(ToppingHamburguesa::getPrecioAdicional)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .multiply(BigDecimal.valueOf(cantidad));
            subtotalBase = subtotalBase.add(totalToppingsHamburguesa);
        }

        // Agregar precio de aderezos
        if (aderezos != null && !aderezos.isEmpty()) {
            BigDecimal totalAderezos = aderezos.stream()
                    .map(Aderezo::getPrecioAdicional)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .multiply(BigDecimal.valueOf(cantidad));
            subtotalBase = subtotalBase.add(totalAderezos);
        }

        return subtotalBase;
    }

    public void actualizarSubtotal() {
        this.subtotal = calcularSubtotal();
    }

    @PrePersist
    @PreUpdate
    public void calcularSubtotalAutomatico() {
        actualizarSubtotal();
    }
}