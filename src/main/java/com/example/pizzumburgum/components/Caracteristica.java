package com.example.pizzumburgum.components;

import jakarta.persistence.*;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "caracteristica",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_caracteristica_nombre", columnNames = "nombre")
        }
)
public class Caracteristica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCaracteristica;

    /** Ejemplo: "Tipo de masa", "Tamaño", "Salsa", "Extra queso" */
    @Column(nullable = false, length = 100)
    private String nombre;

    /** Descripción o aclaración opcional (ej: “masa integral con semillas”) */
    @Column(length = 255)
    private String descripcion;

    /** 1:N con Seleccion — una característica puede ser elegida en muchas selecciones */
    @OneToMany(mappedBy = "caracteristica", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Seleccion> selecciones = new LinkedHashSet<>();
}

