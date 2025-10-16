package com.example.pizzumburgum.component;

import com.example.pizzumburgum.enums.RolUsuario;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clientes")
@PrimaryKeyJoinColumn(name = "usuario_id")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, exclude = {"pedidos", "carritos"})
public class Cliente extends Usuario {

    @Embedded
    @Valid
    private Direccion direccionPrincipal;

    @ElementCollection
    @CollectionTable(name = "cliente_direcciones", joinColumns = @JoinColumn(name = "cliente_id"))
    @Builder.Default
    private List<Direccion> direccionesSecundarias = new ArrayList<>();

    @Column(length = 50)
    private String documentoIdentidad;

    private LocalDate fechaNacimiento;

    @Column(length = 500)
    private String preferencias;

    @Builder.Default
    @Column(nullable = false)
    private Integer puntosLealtad = 0;

    @Embedded
    @Valid
    private Tarjeta tarjetaPrincipal;

    @ElementCollection
    @CollectionTable(name = "cliente_tarjetas", joinColumns = @JoinColumn(name = "cliente_id"))
    @Builder.Default
    private List<Tarjeta> tarjetasSecundarias = new ArrayList<>();

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Pedido> pedidos = new ArrayList<>();

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Carrito> carritos = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (getRol() == null) {
            setRol(RolUsuario.USUARIO);
        }
    }

    public void agregarDireccionSecundaria(Direccion direccion) {
        if (direccion != null) {
            this.direccionesSecundarias.add(direccion);
        }
    }

    public void agregarTarjetaSecundaria(Tarjeta tarjeta) {
        if (tarjeta != null && !tarjeta.estaVencida()) {
            this.tarjetasSecundarias.add(tarjeta);
        }
    }

    public void agregarPuntosLealtad(Integer puntos) {
        if (puntos != null && puntos > 0) {
            this.puntosLealtad += puntos;
        }
    }

    public void descontarPuntosLealtad(Integer puntos) {
        if (puntos != null && puntos > 0 && this.puntosLealtad >= puntos) {
            this.puntosLealtad -= puntos;
        }
    }
}