package com.example.pizzumburgum.actores;

import com.example.pizzumburgum.producto.Atributo;
import com.example.pizzumburgum.producto.Caracteristica;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true, exclude = {
        "caracteristicasAgregadas", "caracteristicasBorradas",
        "atributosAgregados", "atributosBorrados"
})
@Entity
@DiscriminatorValue("FUNCIONARIO")
public class Funcionario extends Usuario {

    // ==================== Auditoría de Características ====================

    @OneToMany(mappedBy = "funcionarioAlta", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Builder.Default
    @JsonManagedReference("func-carac-alta")
    private Set<Caracteristica> caracteristicasAgregadas = new LinkedHashSet<>();

    @OneToMany(mappedBy = "funcionarioBaja", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Builder.Default
    @JsonManagedReference("func-carac-baja")
    private Set<Caracteristica> caracteristicasBorradas = new LinkedHashSet<>();

    // ==================== Auditoría de Atributos ====================

    @OneToMany(mappedBy = "funcionarioAlta", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Builder.Default
    @JsonManagedReference("func-attr-alta")
    private Set<Atributo> atributosAgregados = new LinkedHashSet<>();

    @OneToMany(mappedBy = "funcionarioBaja", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Builder.Default
    @JsonManagedReference("func-attr-baja")
    private Set<Atributo> atributosBorrados = new LinkedHashSet<>();

    // ==================== Métodos Helper - Características ====================

    /**
     * Registra que este funcionario agregó una característica
     */
    public void registrarCaracteristicaAgregada(Caracteristica caracteristica) {
        caracteristicasAgregadas.add(caracteristica);
        caracteristica.setFuncionarioAlta(this);
    }

    /**
     * Registra que este funcionario borró una característica
     */
    public void registrarCaracteristicaBorrada(Caracteristica caracteristica) {
        caracteristicasBorradas.add(caracteristica);
        caracteristica.setFuncionarioBaja(this);
    }

    // ==================== Métodos Helper - Atributos ====================

    /**
     * Registra que este funcionario agregó un atributo
     */
    public void registrarAtributoAgregado(Atributo atributo) {
        atributosAgregados.add(atributo);
        atributo.setFuncionarioAlta(this);
    }

    /**
     * Registra que este funcionario borró un atributo
     */
    public void registrarAtributoBorrado(Atributo atributo) {
        atributosBorrados.add(atributo);
        atributo.setFuncionarioBaja(this);
    }

    // ==================== Métodos de Consulta ====================

    /**
     * Verifica si este funcionario ha agregado alguna característica
     */
    public boolean haAgregadoCaracteristicas() {
        return caracteristicasAgregadas != null && !caracteristicasAgregadas.isEmpty();
    }

    /**
     * Verifica si este funcionario ha agregado algún atributo
     */
    public boolean haAgregadoAtributos() {
        return atributosAgregados != null && !atributosAgregados.isEmpty();
    }

    /**
     * Obtiene el total de elementos agregados (características + atributos)
     */
    public int getTotalElementosAgregados() {
        int caracteristicas = caracteristicasAgregadas != null ? caracteristicasAgregadas.size() : 0;
        int atributos = atributosAgregados != null ? atributosAgregados.size() : 0;
        return caracteristicas + atributos;
    }

    /**
     * Obtiene el total de elementos borrados (características + atributos)
     */
    public int getTotalElementosBorrados() {
        int caracteristicas = caracteristicasBorradas != null ? caracteristicasBorradas.size() : 0;
        int atributos = atributosBorrados != null ? atributosBorrados.size() : 0;
        return caracteristicas + atributos;
    }
}
