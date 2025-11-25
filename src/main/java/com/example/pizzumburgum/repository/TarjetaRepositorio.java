package com.example.pizzumburgum.repository;

import com.example.pizzumburgum.entities.Tarjeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TarjetaRepositorio extends JpaRepository<Tarjeta, Long> {

    // Listar tarjetas de un usuario (principal primero)
    List<Tarjeta> findByUsuarioIdOrderByPrincipalDescIdAsc(Long usuarioId);

    // Buscar todas las tarjetas de un usuario
    List<Tarjeta> findByUsuarioId(Long usuarioId);

    // Buscar tarjeta específica de un usuario
    Optional<Tarjeta> findByIdAndUsuarioId(Long id, Long usuarioId);

    // Verificar si un número ya existe (opcional)
    boolean existsByToken(String numero);
}

