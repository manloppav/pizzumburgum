package com.example.pizzumburgum.entities;

import com.example.pizzumburgum.enums.CategoriaCreacion;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "creaciones")
@Getter
@Setter
@NoArgsConstructor
public class Creacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 150, message = "El nombre debe tener entre 2 y 150 caracteres")
    @Column(nullable = false, length = 150)
    private String nombre;

    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    @Column(length = 1000)
    private String descripcion;

    @Column(length = 500)
    private String imagenUrl;

    @NotNull(message = "La categoría de la creación es obligatoria")
    @Enumerated(EnumType.STRING)
    @Column(name = "categoria_creacion", nullable = false, length = 20)
    private CategoriaCreacion categoriaCreacion;

    @ManyToMany
    @JoinTable(
            name = "creacion_productos",
            joinColumns = @JoinColumn(name = "creacion_id"),
            inverseJoinColumns = @JoinColumn(name = "producto_id")
    )
    private List<Producto> productos = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    @JsonBackReference
    private Usuario usuario;

    @Transient
    private BigDecimal precioTotal;

    @Transient
    public BigDecimal getPrecioTotal() {
        if (precioTotal != null)
            return precioTotal.setScale(2, RoundingMode.HALF_UP);

        return (productos == null ? BigDecimal.ZERO : productos.stream()
                .map(Producto::getPrecio)
                .reduce(BigDecimal.ZERO, BigDecimal::add))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
