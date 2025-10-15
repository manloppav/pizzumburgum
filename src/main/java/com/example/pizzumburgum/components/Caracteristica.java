package com.example.pizzumburgum.components;

import jakarta.persistence.*;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
@Entity @Table(name = "caracteristica",
        uniqueConstraints = @UniqueConstraint(name="uk_caracteristica_nombre", columnNames = "nombre"))
public class Caracteristica {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCaracteristica;

    @Column(nullable = false, length = 100)
    private String nombre;    // ej.: "Grande", "Mediana", "Chica" si fuera por tamaño (o "Tipo de masa")

    /** N:1 con Atributo */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "atributo_id", nullable = false)
    private Atributo atributo;

    /** 1:N con Opcion */
    @OneToMany(mappedBy = "caracteristica", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Opcion> opciones = new LinkedHashSet<>();

    /** N:1 – Funcionario que AGREGÓ la característica */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "funcionario_alta_id", nullable = false)
    private Funcionario funcionarioAlta;

    /** N:1 – Funcionario que BORRÓ (o dio de baja) la característica */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "funcionario_baja_id")
    private Funcionario funcionarioBaja;
}


