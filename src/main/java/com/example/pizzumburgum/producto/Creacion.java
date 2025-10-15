package com.example.pizzumburgum.producto;

import com.example.pizzumburgum.actores.Cliente;
import com.example.pizzumburgum.pedido_carrito_pago.DetalleCarrito;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "creacion",
        indexes = {
                @Index(name = "idx_creacion_cliente", columnList = "cliente_id"),
                @Index(name = "idx_creacion_producto_base", columnList = "producto_base_id")
        })
public class Creacion {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCreacion;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(nullable = false)
    private boolean favorito;

    @Column(name = "precio_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioTotal;

    /** N:1 con Cliente (quien la crea). */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    /** N:1 con Producto (producto base). */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto_base_id", nullable = false)
    private Producto productoBase;

    /** 1:N con Seleccion (creación selecciona muchas características). */
    @OneToMany(mappedBy = "creacion", orphanRemoval = true, cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Seleccion> selecciones = new LinkedHashSet<>();

    /** 1:N con DetalleCarrito (si la creación está en carritos). */
    @OneToMany(mappedBy = "creacion", orphanRemoval = true)
    @Builder.Default
    private Set<DetalleCarrito> detalles = new LinkedHashSet<>();
}


