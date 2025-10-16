package com.example.pizzumburgum.actores;

import com.example.pizzumburgum.producto.Creacion;
import com.example.pizzumburgum.pedido_carrito_pago.MedioPago;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, exclude = {"mediosDePago", "creaciones"})
@Entity
@DiscriminatorValue("CLIENTE")
public class Cliente extends Usuario {

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<MedioPago> mediosDePago = new LinkedHashSet<>();

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Creacion> creaciones = new LinkedHashSet<>();

    // ==================== Métodos Helper ====================

    /**
     * Agrega un medio de pago al cliente y establece la relación bidireccional
     */
    public void addMedioPago(MedioPago medioPago) {
        mediosDePago.add(medioPago);
        medioPago.setCliente(this);
    }

    /**
     * Remueve un medio de pago del cliente y rompe la relación bidireccional
     */
    public void removeMedioPago(MedioPago medioPago) {
        mediosDePago.remove(medioPago);
        medioPago.setCliente(null);
    }

    /**
     * Agrega una creación al cliente y establece la relación bidireccional
     */
    public void addCreacion(Creacion creacion) {
        creaciones.add(creacion);
        creacion.setCliente(this);
    }

    /**
     * Remueve una creación del cliente y rompe la relación bidireccional
     */
    public void removeCreacion(Creacion creacion) {
        creaciones.remove(creacion);
        creacion.setCliente(null);
    }
}