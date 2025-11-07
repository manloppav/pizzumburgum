package com.example.pizzumburgum.repository;

import com.example.pizzumburgum.entities.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepositorio extends JpaRepository<Pedido, Long> {
}