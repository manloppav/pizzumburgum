package com.example.pizzumburgum.components;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder // << heredado desde Usuario
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@DiscriminatorValue("CLIENTE") // << SINGLE_TABLE: discrimina acá
public class Cliente extends Usuario {

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MedioPago> mediosDePago = new HashSet<>();

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Creacion> creaciones = new HashSet<>();
}
