package com.example.pizzumburgum.repository;

import com.example.pizzumburgum.entities.Direccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DireccionRepositorio extends JpaRepository<Direccion, Long> {

    // Listar direcciones de un usuario (principal primero)
    List<Direccion> findByUsuarioIdOrderByPrincipalDescIdAsc(Long usuarioId);

    // Buscar dirección específica de un usuario
    Optional<Direccion> findByIdAndUsuarioId(Long id, Long usuarioId);
}
