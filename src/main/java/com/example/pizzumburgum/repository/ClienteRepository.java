package com.example.pizzumburgum.repository;

import com.example.pizzumburgum.component.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByEmail(String email);

    Optional<Cliente> findByDocumentoIdentidad(String documentoIdentidad);

    boolean existsByEmail(String email);

    boolean existsByDocumentoIdentidad(String documentoIdentidad);

    List<Cliente> findByActivoTrue();

    @Query("SELECT c FROM Cliente c WHERE c.puntosLealtad >= :puntos")
    List<Cliente> findByPuntosLealtadGreaterThanEqual(@Param("puntos") Integer puntos);

    @Query("SELECT c FROM Cliente c LEFT JOIN FETCH c.pedidos WHERE c.id = :id")
    Optional<Cliente> findByIdWithPedidos(@Param("id") Long id);
}