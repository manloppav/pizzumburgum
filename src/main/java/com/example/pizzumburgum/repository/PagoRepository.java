package com.example.pizzumburgum.repository;

import com.example.pizzumburgum.component.Pago;
import com.example.pizzumburgum.enums.EstadoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    Optional<Pago> findByCodigoTransaccion(String codigoTransaccion);

    Optional<Pago> findByPedidoId(Long pedidoId);

    List<Pago> findByEstado(EstadoPago estado);

    @Query("SELECT p FROM Pago p WHERE p.fechaProcesamiento BETWEEN :fechaInicio AND :fechaFin")
    List<Pago> findByFechaProcesamientoBetween(@Param("fechaInicio") LocalDateTime fechaInicio,
                                                @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT p FROM Pago p WHERE p.estado = :estado AND p.fechaCreacion < :fecha")
    List<Pago> findByEstadoAndFechaCreacionBefore(@Param("estado") EstadoPago estado,
                                                  @Param("fecha") LocalDateTime fecha);
}