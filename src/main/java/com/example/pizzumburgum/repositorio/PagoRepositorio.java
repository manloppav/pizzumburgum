package com.example.pizzumburgum.repositorio;

import com.example.pizzumburgum.entities.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PagoRepositorio extends JpaRepository<Pago, Long> {

    // Buscar pago por pedido
    Pago findByPedidoId(Long pedidoId);
}
