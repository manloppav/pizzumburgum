package com.example.pizzumburgum.producto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AtributoRepository extends JpaRepository<Atributo, Long> {

    Optional<Atributo> findByNombre(String nombre);

    boolean existsByNombre(String nombre);

    List<Atributo> findByAplicaA(String aplicaA);

    @Query("SELECT a FROM Atributo a WHERE a.funcionarioAlta.idUsuario = :funcionarioId")
    List<Atributo> findByFuncionarioAltaId(@Param("funcionarioId") Long funcionarioId);

    @Query("SELECT a FROM Atributo a WHERE a.funcionarioBaja IS NULL")
    List<Atributo> findActivos();

    @Query("SELECT a FROM Atributo a WHERE a.funcionarioBaja IS NOT NULL")
    List<Atributo> findBorrados();
}
