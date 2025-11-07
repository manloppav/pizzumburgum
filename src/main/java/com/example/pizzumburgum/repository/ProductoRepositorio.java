package com.example.pizzumburgum.repository;

import com.example.pizzumburgum.entities.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepositorio extends JpaRepository<Producto, Long> {
}
