package com.example.pizzumburgum.repository;

import com.example.pizzumburgum.component.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, Long> {

    Optional<Funcionario> findByEmail(String email);

    Optional<Funcionario> findByCodigoEmpleado(String codigoEmpleado);

    boolean existsByEmail(String email);

    boolean existsByCodigoEmpleado(String codigoEmpleado);

    List<Funcionario> findByActivoTrue();

    List<Funcionario> findByDisponibleTrue();

    @Query("SELECT f FROM Funcionario f WHERE f.turno = :turno AND f.disponible = true")
    List<Funcionario> findByTurnoAndDisponibleTrue(@Param("turno") String turno);

    @Query("SELECT f FROM Funcionario f LEFT JOIN FETCH f.auditorias WHERE f.id = :id")
    Optional<Funcionario> findByIdWithAuditorias(@Param("id") Long id);
}