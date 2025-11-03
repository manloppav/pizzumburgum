// src/main/java/com/example/pizzumburgum/service/CreacionService.java
package com.example.pizzumburgum.service;

import com.example.pizzumburgum.entities.Creacion;
import com.example.pizzumburgum.repositorio.*;
import com.example.pizzumburgum.entities.Producto;
import com.example.pizzumburgum.entities.Usuario;
import com.example.pizzumburgum.enums.CategoriaProducto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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

        // --- 1. Validar base obligatoria ---
        List<Producto> bases = productos.stream()
                .filter(p -> p.getCategoria() == CategoriaProducto.PIZZA_BASE
                        || p.getCategoria() == CategoriaProducto.HAMBURGUESA_BASE)
                .toList();

        if (bases.isEmpty()) {
            throw new IllegalArgumentException("Debe incluir una base (pizza o hamburguesa)");
        }
        if (bases.size() > 1) {
            throw new IllegalArgumentException("Solo puede haber una base");
        }

        CategoriaProducto base = bases.get(0).getCategoria();

        // --- 2. Categorías permitidas ---
        Set<CategoriaProducto> categoriasPizza = Set.of(
                CategoriaProducto.PIZZA_BASE, CategoriaProducto.TIPO_MASA, CategoriaProducto.TAMANIO_PIZZA,
                CategoriaProducto.SALSA_PIZZA, CategoriaProducto.TOPPING_PIZZA,
                CategoriaProducto.BEBIDA, CategoriaProducto.ACOMPANIAMIENTO
        );

        Set<CategoriaProducto> categoriasHamb = Set.of(
                CategoriaProducto.HAMBURGUESA_BASE, CategoriaProducto.TIPO_PAN, CategoriaProducto.TIPO_CARNE,
                CategoriaProducto.TIPO_QUESO, CategoriaProducto.SALSA_HAMBURGUESA, CategoriaProducto.TOPPING_HAMBURGUESA,
                CategoriaProducto.BEBIDA, CategoriaProducto.ACOMPANIAMIENTO
        );

        Set<CategoriaProducto> permitidas = (base == CategoriaProducto.PIZZA_BASE)
                ? categoriasPizza : categoriasHamb;

        // --- 3. Validar categorías prohibidas ---
        for (Producto p : productos) {
            if (!permitidas.contains(p.getCategoria())) {
                throw new IllegalArgumentException("Categoría no permitida: " + p.getCategoria());
            }
        }

        // --- 4. Conteo de categorías ---
        Map<CategoriaProducto, Long> conteo = productos.stream()
                .collect(Collectors.groupingBy(Producto::getCategoria, Collectors.counting()));

        // --- 5. Validaciones específicas ---
        if (base == CategoriaProducto.PIZZA_BASE) {
            long masas = conteo.getOrDefault(CategoriaProducto.TIPO_MASA, 0L);
            if (masas != 1)
                throw new IllegalArgumentException("Debe haber exactamente 1 tipo de masa");

            long tamanios = conteo.getOrDefault(CategoriaProducto.TAMANIO_PIZZA, 0L);
            if (tamanios != 1)
                throw new IllegalArgumentException("Debe haber exactamente 1 tamaño de pizza");

            long toppings = conteo.getOrDefault(CategoriaProducto.TOPPING_PIZZA, 0L);
            if (toppings > 5)
                throw new IllegalArgumentException("Máximo 5 toppings para pizza");
        }

        if (base == CategoriaProducto.HAMBURGUESA_BASE) {
            long panes = conteo.getOrDefault(CategoriaProducto.TIPO_PAN, 0L);
            if (panes != 1)
                throw new IllegalArgumentException("Debe haber exactamente 1 tipo de pan");

            long carnes = conteo.getOrDefault(CategoriaProducto.TIPO_CARNE, 0L);
            if (carnes > 3)
                throw new IllegalArgumentException("Máximo 3 tipos de carne");

            long salsas = conteo.getOrDefault(CategoriaProducto.SALSA_HAMBURGUESA, 0L);
            if (salsas > 2)
                throw new IllegalArgumentException("Máximo 2 salsas para hamburguesa");

            long toppings = conteo.getOrDefault(CategoriaProducto.TOPPING_HAMBURGUESA, 0L);
            if (toppings > 5)
                throw new IllegalArgumentException("Máximo 5 toppings para hamburguesa");
        }

        long bebidas = conteo.getOrDefault(CategoriaProducto.BEBIDA, 0L);
        if (bebidas > 1)
            throw new IllegalArgumentException("Máximo una bebida por creación");

        // --- 6. Guardar creación ---
        Creacion creacion = new Creacion();
        creacion.setUsuario(usuario);
        creacion.setProductos(productos);

        return creacionRepositorio.save(creacion);
    }
}

