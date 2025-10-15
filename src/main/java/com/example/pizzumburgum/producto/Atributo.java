package com.example.pizzumburgum.producto;

import com.example.pizzumburgum.actores.Funcionario;
import jakarta.persistence.*;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "atributo",
        uniqueConstraints = @UniqueConstraint(name="UK_atributo_nombre", columnNames = "nombre")) //la columna nombre no puede repetirse(atributo único)
public class Atributo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAtributo;

    @Column(nullable = false, length = 100)
    private String nombre;        // ej.: "Tamaño", "Tipo de masa", "Salsa"

    @Column(name = "aplica_a", length = 100)
    private String aplicaA;       // opcional: a qué producto/área aplica

    /** 1:N con Caracteristica */
    @OneToMany(mappedBy = "atributo", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Caracteristica> caracteristicas = new LinkedHashSet<>();

    /** N:1 – Funcionario que AGREGÓ el atributo */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "funcionario_alta_id", nullable = false)
    private Funcionario funcionarioAlta;

    /** N:1 – Funcionario que BORRÓ (o dio de baja) el atributo */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "funcionario_baja_id")
    private Funcionario funcionarioBaja;
}

