package com.example.pizzumburgum.components;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@Table(name = "cliente")
@PrimaryKeyJoinColumn(name = "id_usuario")  // usa el mismo PK que Usuario
public class Cliente extends Usuario {

    // Cliente "tiene_registrado" muchos medios de pago (1 a N)
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private Set<MedioPago> mediosDePago = new HashSet<>();

    // Cliente "crea" muchas creaciones (1 a N)
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private Set<Creacion> creaciones = new HashSet<>();
}
