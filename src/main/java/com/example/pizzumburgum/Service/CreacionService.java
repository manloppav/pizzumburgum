// src/main/java/com/example/pizzumburgum/service/CreacionService.java
package com.example.pizzumburgum.Service;

import com.example.pizzumburgum.entities.Creacion;
import com.example.pizzumburgum.Repositorio.CreacionRepositorio;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CreacionService {

    private final CreacionRepositorio creacionRepositorio;

    public CreacionService(CreacionRepositorio creacionRepositorio) {
        this.creacionRepositorio = creacionRepositorio;
    }

    public Optional<Creacion> buscarPorId(Long id) {
        return creacionRepositorio.findById(id);
    }
}