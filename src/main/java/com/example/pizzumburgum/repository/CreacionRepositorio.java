package com.example.pizzumburgum.repository;

import com.example.pizzumburgum.entities.Creacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CreacionRepositorio extends JpaRepository<Creacion, Long> {

    @Query("SELECT c FROM Creacion c LEFT JOIN FETCH c.productos LEFT JOIN FETCH c.usuario WHERE c.id = :id")
    Optional<Creacion> findByIdWithProductos(@Param("id") Long id);

}
