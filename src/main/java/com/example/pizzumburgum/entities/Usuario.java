package com.example.pizzumburgum.components;

import jakarta.persistence.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "usuario")
public class Usuario {

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
    private java.time.LocalDate fechaNacimiento;

    @Column(length = 15)
    private String celular;
}