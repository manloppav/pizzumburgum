package com.example.pizzumburgum.components;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, exclude = {
        "caracteristicasAgregadas", "caracteristicasBorradas",
        "atributosAgregados", "atributosBorrados"
})
@Entity
@DiscriminatorValue("FUNCIONARIO")   // usa la misma tabla "usuario" (SINGLE_TABLE)
public class Funcionario extends Usuario {

    // Inversas 1→N (opcionales, útiles para navegar desde Funcionario)

    @OneToMany(mappedBy = "funcionarioAlta")
    @Builder.Default
    private Set<Caracteristica> caracteristicasAgregadas = new LinkedHashSet<>();

    @OneToMany(mappedBy = "funcionarioBaja")
    @Builder.Default
    private Set<Caracteristica> caracteristicasBorradas = new LinkedHashSet<>();

    @OneToMany(mappedBy = "funcionarioAlta")
    @Builder.Default
    private Set<Atributo> atributosAgregados = new LinkedHashSet<>();

    @OneToMany(mappedBy = "funcionarioBaja")
    @Builder.Default
    private Set<Atributo> atributosBorrados = new LinkedHashSet<>();
}
