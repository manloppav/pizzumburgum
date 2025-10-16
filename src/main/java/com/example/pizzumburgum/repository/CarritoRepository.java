package com.example.pizzumburgum.repository;

import com.example.pizzumburgum.component.carrito.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {

    @Query("SELECT c FROM Carrito c WHERE c.cliente.id = :clienteId AND c.activo = true")
    Optional<Carrito> findByClienteIdAndActivoTrue(@Param("clienteId") Long clienteId);

    List<Carrito> findByClienteId(Long clienteId);

    @Query("SELECT c FROM Carrito c LEFT JOIN FETCH c.items WHERE c.id = :id")
    Optional<Carrito> findByIdWithItems(@Param("id") Long id);

    @Query("SELECT c FROM Carrito c LEFT JOIN FETCH c.items WHERE c.cliente.id = :clienteId AND c.activo = true")
    Optional<Carrito> findByClienteIdAndActivoTrueWithItems(@Param("clienteId") Long clienteId);
}