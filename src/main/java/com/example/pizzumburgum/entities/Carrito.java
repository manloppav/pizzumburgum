package com.example.pizzumburgum.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carritos")
@Getter
@Setter
@NoArgsConstructor
public class Carrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<CarritoItem> items = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @Transient
    public BigDecimal getPrecioTotal() {
        return items.stream()
                .map(CarritoItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}