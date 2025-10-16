package com.example.pizzumburgum.actores;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "usuario",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_usuario_email", columnNames = "email"),
                @UniqueConstraint(name = "uk_usuario_documento", columnNames = "documento")
        },
        indexes = {
                @Index(name = "idx_usuario_email", columnList = "email"),
                @Index(name = "idx_usuario_documento", columnList = "documento")
        }
)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo", discriminatorType = DiscriminatorType.STRING, length = 20)
public abstract class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long idUsuario;

    @Column(nullable = false, length = 50)
    @ToString.Include
    private String nombre;

    @Column(nullable = false, length = 50)
    @ToString.Include
    private String apellido;

    @Column(nullable = false, length = 100)
    @EqualsAndHashCode.Include
    @ToString.Include
    private String email;

    @Column(nullable = false, length = 100)
    private String hashPassword;

    @Column(nullable = false, length = 20)
    private String documento;

    @Column(name = "fecha_nac", nullable = false)
    private LocalDate fechaNacimiento;

    @Column(length = 15)
    private String celular;

    // ==================== Auditoría ====================

    /*@Column(name = "fecha_registro", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    @Column(name = "ultima_modificacion")
    private LocalDateTime ultimaModificacion;

    @Column(name = "activo", nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @Column(name = "fecha_baja")
    private LocalDateTime fechaBaja;
*/
    // ==================== Métodos de Negocio ====================

    /**
     * Obtiene el nombre completo del usuario
     */
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    /**
     * Calcula la edad del usuario basado en la fecha de nacimiento
     */
    public int getEdad() {
        return LocalDate.now().getYear() - fechaNacimiento.getYear();
    }

    /**
     * Verifica si el usuario es mayor de edad (18 años)
     */
    public boolean esMayorDeEdad() {
        return getEdad() >= 18;
    }
/*
    /**
     * Desactiva el usuario (soft delete)
     */
    /*public void desactivar() {
        this.activo = false;
        this.fechaBaja = LocalDateTime.now();
    }
    /**
     * Reactiva un usuario previamente desactivado

    public void reactivar() {
        this.activo = true;
        this.fechaBaja = null;
    }

    /**
     * Actualiza la marca de tiempo de última modificación

    @PreUpdate
    protected void onUpdate() {
        this.ultimaModificacion = LocalDateTime.now();
    }

    /**
     * Inicializa la fecha de registro al persistir

    @PrePersist
    protected void onCreate() {
        if (fechaRegistro == null) {
            fechaRegistro = LocalDateTime.now();
        }
    }*/

    /**
     * Verifica si el usuario tiene un celular registrado
     */
    public boolean tieneCelular() {
        return celular != null && !celular.isBlank();
    }

    /**
     * Ofusca parcialmente el email para mostrar en logs o UI
     * Ejemplo: john.doe@example.com -> j***e@example.com
     */
    public String getEmailOfuscado() {
        if (email == null || email.length() < 3) {
            return "***";
        }
        String[] parts = email.split("@");
        if (parts.length != 2) {
            return "***";
        }
        String local = parts[0];
        String domain = parts[1];

        if (local.length() <= 2) {
            return local.charAt(0) + "***@" + domain;
        }
        return local.charAt(0) + "***" + local.charAt(local.length() - 1) + "@" + domain;
    }
}