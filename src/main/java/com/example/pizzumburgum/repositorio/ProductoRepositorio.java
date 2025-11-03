package com.example.pizzumburgum.repositorio;

import com.example.pizzumburgum.entities.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepositorio extends JpaRepository<Producto, Long> {
}
