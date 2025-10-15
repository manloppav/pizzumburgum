package com.example.pizzumburgum.components;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "direcciones")
@NoArgsConstructor
@Getter
@Setter
public class Direccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDireccion;

    @Column
    private String calle;

    @Column
    private String numero;

    @Column
    private String apto;

    @Column
    private String departamento;

    @Column
    private String ciudad;

    @Column
    private String etiqueta;

    @Column
    private boolean es_principal;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;
}
