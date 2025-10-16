package com.example.pizzumburgum.repository;

import com.example.pizzumburgum.component.pedido.Pedido;
import com.example.pizzumburgum.enums.EstadoPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    Optional<Pedido> findByNumeroPedido(String numeroPedido);

    List<Pedido> findByClienteId(Long clienteId);

    List<Pedido> findByEstado(EstadoPedido estado);

    @Query("SELECT p FROM Pedido p WHERE p.estado = :estado ORDER BY p.fechaCreacion ASC")
    List<Pedido> findByEstadoOrderByFechaCreacionAsc(@Param("estado") EstadoPedido estado);

    @Query("SELECT p FROM Pedido p WHERE p.cliente.id = :clienteId AND p.estado = :estado")
    List<Pedido> findByClienteIdAndEstado(@Param("clienteId") Long clienteId, @Param("estado") EstadoPedido estado);

    @Query("SELECT p FROM Pedido p WHERE p.fechaCreacion BETWEEN :fechaInicio AND :fechaFin ORDER BY p.fechaCreacion DESC")
    List<Pedido> findByFechaCreacionBetween(@Param("fechaInicio") LocalDateTime fechaInicio,
                                            @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT p FROM Pedido p LEFT JOIN FETCH p.items WHERE p.id = :id")
    Optional<Pedido> findByIdWithItems(@Param("id") Long id);

    @Query("SELECT p FROM Pedido p LEFT JOIN FETCH p.pago WHERE p.id = :id")
    Optional<Pedido> findByIdWithPago(@Param("id") Long id);

    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.cliente.id = :clienteId AND p.estado = :estado")
    Long countByClienteIdAndEstado(@Param("clienteId") Long clienteId, @Param("estado") EstadoPedido estado);
}