package com.example.pizzumburgum.repositorio;

import com.example.pizzumburgum.entities.Tarjeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TarjetaRepositorio extends JpaRepository<Tarjeta, Long> {

    // Buscar todas las tarjetas de un usuario
    List<Tarjeta> findByUsuarioId(Long usuarioId);

    // Verificar si un número ya existe (opcional)
    boolean existsByToken(String numero);
}

