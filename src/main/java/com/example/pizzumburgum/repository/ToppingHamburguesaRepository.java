package com.example.pizzumburgum.repository;

import com.example.pizzumburgum.component.ToppingHamburguesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ToppingHamburguesaRepository extends JpaRepository<ToppingHamburguesa, Long> {

    Optional<ToppingHamburguesa> findByNombre(String nombre);

    boolean existsByNombre(String nombre);

    List<ToppingHamburguesa> findByDisponibleTrue();

    List<ToppingHamburguesa> findAllByOrderByNombreAsc();
}