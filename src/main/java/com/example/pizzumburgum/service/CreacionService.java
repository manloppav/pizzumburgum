// src/main/java/com/example/pizzumburgum/service/CreacionService.java
package com.example.pizzumburgum.service;

import com.example.pizzumburgum.entities.Creacion;
import com.example.pizzumburgum.repositorio.*;
import com.example.pizzumburgum.entities.Producto;
import com.example.pizzumburgum.entities.Usuario;
import com.example.pizzumburgum.enums.CategoriaProducto;
import com.example.pizzumburgum.rules.ReglasMinimas;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class CreacionService {

    private final CreacionRepositorio creacionRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;
    private final ProductoRepositorio productoRepositorio;

    public Optional<Creacion> buscarPorId(Long id) {
        return creacionRepositorio.findById(id);
    }

    @Transactional
    public Creacion crearCreacion(Long usuarioId, List<Long> productoIds) {
        Usuario usuario = usuarioRepositorio.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + usuarioId));

        List<Producto> productos = productoRepositorio.findAllById(productoIds);
        if (productos.isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos un producto");
        }

        Producto productoBase = productos.stream()
                .filter(p -> p.getCategoria() == CategoriaProducto.PIZZA_BASE
                        || p.getCategoria() == CategoriaProducto.HAMBURGUESA_BASE)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Debe incluir un producto base (pizza o hamburguesa)"));

        CategoriaProducto categoriaBase = productoBase.getCategoria();
        Map<CategoriaProducto, ReglasMinimas.Regla> reglas = ReglasMinimas.reglasCardinalidad(categoriaBase);

        Map<CategoriaProducto, Long> conteo = productos.stream()
                .collect(Collectors.groupingBy(Producto::getCategoria, Collectors.counting()));

        for (var entry : reglas.entrySet()) {
            CategoriaProducto categoria = entry.getKey();
            ReglasMinimas.Regla regla = entry.getValue();
            long cantidad = conteo.getOrDefault(categoria, 0L);
            if (cantidad < regla.min() || cantidad > regla.max()) {
                throw new IllegalArgumentException("Cantidad inválida para " + categoria +
                        ": " + cantidad + " (esperado " + regla.min() + ".." + regla.max() + ")");
            }
        }

        Creacion creacion = new Creacion();
        creacion.setUsuario(usuario);
        creacion.setProductos(productos);

        return creacionRepositorio.save(creacion);
    }
}