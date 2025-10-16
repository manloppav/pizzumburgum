package com.example.pizzumburgum.repository;

import com.example.pizzumburgum.component.ToppingPizza;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ToppingPizzaRepository extends JpaRepository<ToppingPizza, Long> {

    Optional<ToppingPizza> findByNombre(String nombre);

    boolean existsByNombre(String nombre);

    List<ToppingPizza> findByDisponibleTrue();

    List<ToppingPizza> findAllByOrderByNombreAsc();
}