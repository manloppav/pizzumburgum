package com.example.pizzumburgum.entities;

import com.example.pizzumburgum.model.BaseEntity;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "usuarios")
@Inheritance(strategy = InheritanceType.JOINED)
public class Usuario extends BaseEntity {

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(name = "fecha_nac")
    private String fecha_nac;

    @Column
    private String documento;

    @Column
    private String email;

    @Column
    private String password;

    @Column
    private String celular;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Direccion> direcciones;

}