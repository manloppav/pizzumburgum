package com.example.pizzumburgum.entities;

import com.example.pizzumburgum.enums.EstadoPedido;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
@Getter
@Setter
@NoArgsConstructor
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El precio total es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio total debe ser mayor que 0")
    @Digits(integer = 8, fraction = 2, message = "El precio debe tener máximo 2 decimales")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precioTotal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoPedido estado = EstadoPedido.PENDIENTE;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaHora;

    @Column(length = 1000)
    private String observaciones;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonBackReference
    private Usuario usuario;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<PedidoItem> items = new ArrayList<>();

    @OneToOne(mappedBy = "pedido", cascade = CascadeType.ALL)
    private Pago pago;

}