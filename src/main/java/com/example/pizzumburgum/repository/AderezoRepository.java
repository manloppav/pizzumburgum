package com.example.pizzumburgum.repository;

import com.example.pizzumburgum.component.creacion.Aderezo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AderezoRepository extends JpaRepository<Aderezo, Long> {

    Optional<Aderezo> findByNombre(String nombre);

    boolean existsByNombre(String nombre);

    List<Aderezo> findByDisponibleTrue();

    List<Aderezo> findAllByOrderByNombreAsc();
}