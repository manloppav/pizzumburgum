package com.example.pizzumburgum.repository;

import com.example.pizzumburgum.component.AuditoriaAccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditoriaAccionRepository extends JpaRepository<AuditoriaAccion, Long> {

    List<AuditoriaAccion> findByFuncionarioId(Long funcionarioId);

    List<AuditoriaAccion> findByAccion(String accion);

    List<AuditoriaAccion> findByEntidad(String entidad);

    @Query("SELECT a FROM AuditoriaAccion a WHERE a.entidad = :entidad AND a.entidadId = :entidadId ORDER BY a.fechaAccion DESC")
    List<AuditoriaAccion> findByEntidadAndEntidadId(@Param("entidad") String entidad,
                                                    @Param("entidadId") Long entidadId);

    @Query("SELECT a FROM AuditoriaAccion a WHERE a.fechaAccion BETWEEN :fechaInicio AND :fechaFin ORDER BY a.fechaAccion DESC")
    List<AuditoriaAccion> findByFechaAccionBetween(@Param("fechaInicio") LocalDateTime fechaInicio,
                                                   @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT a FROM AuditoriaAccion a WHERE a.funcionario.id = :funcionarioId AND a.fechaAccion BETWEEN :fechaInicio AND :fechaFin ORDER BY a.fechaAccion DESC")
    List<AuditoriaAccion> findByFuncionarioIdAndFechaAccionBetween(@Param("funcionarioId") Long funcionarioId,
                                                                   @Param("fechaInicio") LocalDateTime fechaInicio,
                                                                   @Param("fechaFin") LocalDateTime fechaFin);
}