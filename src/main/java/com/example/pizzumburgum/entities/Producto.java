package com.example.pizzumburgum.entities;

import com.example.pizzumburgum.enums.CategoriaProducto;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "productos")
@Getter
@Setter
@NoArgsConstructor
public class Producto {

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

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    @Digits(integer = 8, fraction = 2, message = "El precio debe tener máximo 2 decimales")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(length = 500)
    private String imagenUrl;

    @NotNull(message = "La categoría del producto es obligatoria")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaProducto categoria;

}