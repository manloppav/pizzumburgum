package com.example.pizzumburgum.components;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "producto",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_producto_nombre", columnNames = "nombre")
        }
)
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idProducto;

    /** Nombre del producto base — ej: “Pizza Margarita”, “Hamburguesa Clásica” */
    @Column(nullable = false, length = 100)
    private String nombre;

    /** Descripción breve del producto */
    @Column(length = 255)
    private String descripcion;

    /** Precio base del producto antes de las personalizaciones */
    @Column(name = "precio_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioBase;

    /** Indica si el producto está disponible en el catálogo */
    @Column(nullable = false)
    private boolean disponible;

    /** 1:N con Creacion — un producto base puede tener muchas creaciones derivadas */
    @OneToMany(mappedBy = "productoBase", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Creacion> creaciones = new LinkedHashSet<>();
}

