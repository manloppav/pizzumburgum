package com.example.pizzumburgum.repository;

import com.example.pizzumburgum.entities.Pedido;
import com.example.pizzumburgum.enums.EstadoPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PedidoRepositorio extends JpaRepository<Pedido, Long> {

    // Buscar pedidos por fecha exacta
    @Query("SELECT p FROM Pedido p WHERE DATE(p.fechaHora) = :fecha ORDER BY p.fechaHora DESC")
    List<Pedido> findByFecha(@Param("fecha") LocalDate fecha);

    // Buscar pedidos por rango de fechas
    @Query("SELECT p FROM Pedido p WHERE p.fechaHora BETWEEN :inicio AND :fin ORDER BY p.fechaHora DESC")
    List<Pedido> findByFechaRange(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    // Buscar pedidos de un usuario
    List<Pedido> findByUsuarioIdOrderByFechaHoraDesc(Long usuarioId);

    // Buscar por estado
    List<Pedido> findByEstadoOrderByFechaHoraDesc(EstadoPedido estado);

    // Contar pedidos por fecha
    @Query("SELECT COUNT(p) FROM Pedido p WHERE DATE(p.fechaHora) = :fecha")
    Long countByFecha(@Param("fecha") LocalDate fecha);

    // Buscar todos ordenados por fecha
    List<Pedido> findAllByOrderByFechaHoraDesc();

}