package com.example.pizzumburgum.Service;

import com.example.pizzumburgum.entities.Producto;
import com.example.pizzumburgum.Repositorio.ProductoRepositorio;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductoService {

    private final ProductoRepositorio productoRepositorio;

    public ProductoService(ProductoRepositorio productoRepositorio) {
        this.productoRepositorio = productoRepositorio;
    }

    public Optional<Producto> buscarPorId(Long id) {
        return productoRepositorio.findById(id);
    }
}