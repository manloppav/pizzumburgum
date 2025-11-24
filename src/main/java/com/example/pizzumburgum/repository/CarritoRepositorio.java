package com.example.pizzumburgum.repository;

import com.example.pizzumburgum.entities.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarritoRepositorio extends JpaRepository<Carrito, Long> {

    // (buscar por el id del usuario):
    Optional<Carrito> findByUsuarioId(Long usuarioId);
}
