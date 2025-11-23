package com.example.pizzumburgum.repositorio;

import com.example.pizzumburgum.entities.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PedidoRepositorio extends JpaRepository<Pedido, Long> {
    // IDs de pedidos con pago en un rango de fecha/hora (día completo)
    @Query("""
           select p.id
           from Pedido p
           where p.fechaHora >= :start and p.fechaHora < :end
             and p.pago is not null
           """)
    List<Long> findIdsWithPagoByDateRange(@Param("start") LocalDateTime start,
                                          @Param("end") LocalDateTime end);

    // Carga el pedido con sus items y pago en una sola pasada
    @Query("""
           select distinct p
           from Pedido p
           left join fetch p.items i
           left join fetch p.pago pg
           where p.id in :ids
           """)
    List<Pedido> findAllWithItemsAndPagoByIds(@Param("ids") List<Long> ids);
}

