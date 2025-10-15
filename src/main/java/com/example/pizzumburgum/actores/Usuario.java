package com.example.pizzumburgum.actores;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA necesita ctor por defecto
@AllArgsConstructor
@SuperBuilder(toBuilder = true) // << usar SuperBuilder, NO Builder (xq es super clase)
@Entity
@Table(name = "usuario")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) //(todas las clases hijas en una unica tabla)
@DiscriminatorColumn(name = "tipo", length = 20) // la columna tipo indica de que subclase se trata
public abstract class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(nullable = false, length = 50)
    private String apellido;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 100)
    private String hashPassword;

    @Column(nullable = false, unique = true, length = 20)
    private String documento;

    @Column(name = "fecha_nac", nullable = false)
    private LocalDate fechaNacimiento;

    @Column(length = 15)
    private String celular;
}
