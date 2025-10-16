package com.example.pizzumburgum.repository;

import org.springframework.stereotype.Repository;

@Repository
public interface AderezoRepository extends JpaRepository<Aderezo, Long> {

    Optional<Aderezo> findByNombre(String nombre);

    boolean existsByNombre(String nombre);

    List<Aderezo> findByDisponibleTrue();

    List<Aderezo> findAllByOrderByNombreAsc();
}