package com.example.pizzumburgum.repository;

import com.example.pizzumburgum.component.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    Optional<Categoria> findByNombre(String nombre);

    boolean existsByNombre(String nombre);

    List<Categoria> findByActivoTrue();

    @Query("SELECT c FROM Categoria c LEFT JOIN FETCH c.productos WHERE c.id = :id")
    Optional<Categoria> findByIdWithProductos(@Param("id") Long id);
}