package com.example.pizzumburgum.service;

import com.example.pizzumburgum.dto.request.CreacionDTO;
import com.example.pizzumburgum.dto.request.ProductoSimpleDTO;
import com.example.pizzumburgum.entities.Creacion;
import com.example.pizzumburgum.entities.Producto;
import com.example.pizzumburgum.entities.Usuario;
import com.example.pizzumburgum.enums.CategoriaCreacion;
import com.example.pizzumburgum.enums.CategoriaProducto;
import com.example.pizzumburgum.exception.RegistroException;
import com.example.pizzumburgum.repository.*;
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
    public Creacion crearCreacion(Long usuarioId,
                                  CategoriaCreacion categoriaCreacion,
                                  List<Long> productoIds) {

        if (categoriaCreacion == null) {
            throw new IllegalArgumentException("Debe especificarse la categoría de creación (PIZZA o HAMBURGUESA)");
        }

        Usuario usuario = usuarioRepositorio.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + usuarioId));

        List<Producto> productos = productoRepositorio.findAllById(productoIds);
        if (productos.isEmpty()) {
            throw new IllegalArgumentException("Debe incluir al menos un producto");
        }

        // Categorías permitidas según tipo de creación (no hay 'base' en productos)
        Set<CategoriaProducto> permitidas = (categoriaCreacion == CategoriaCreacion.PIZZA_BASE)
                ? Set.of(
                CategoriaProducto.TIPO_MASA,
                CategoriaProducto.TAMANIO_PIZZA,
                CategoriaProducto.SALSA_PIZZA,
                CategoriaProducto.TOPPING_PIZZA,
                CategoriaProducto.BEBIDA,
                CategoriaProducto.ACOMPANIAMIENTO
        )
                : Set.of(
                CategoriaProducto.TIPO_PAN,
                CategoriaProducto.TIPO_CARNE,
                CategoriaProducto.TIPO_QUESO,
                CategoriaProducto.SALSA_HAMBURGUESA,
                CategoriaProducto.TOPPING_HAMBURGUESA,
                CategoriaProducto.BEBIDA,
                CategoriaProducto.ACOMPANIAMIENTO
        );

        // Validar categorías no permitidas
        for (Producto p : productos) {
            if (!permitidas.contains(p.getCategoria())) {
                throw new IllegalArgumentException(
                        "El producto '" + p.getNombre() + "' tiene una categoría no permitida para " + categoriaCreacion);
            }
        }

        // Conteo por categoría
        Map<CategoriaProducto, Long> conteo = productos.stream()
                .collect(Collectors.groupingBy(Producto::getCategoria, Collectors.counting()));

        // Reglas específicas
        if (categoriaCreacion == CategoriaCreacion.PIZZA_BASE) {
            if (conteo.getOrDefault(CategoriaProducto.TIPO_MASA, 0L) != 1)
                throw new IllegalArgumentException("Debe haber exactamente 1 tipo de masa");
            if (conteo.getOrDefault(CategoriaProducto.TAMANIO_PIZZA, 0L) != 1)
                throw new IllegalArgumentException("Debe haber exactamente 1 tamaño de pizza");
            if (conteo.getOrDefault(CategoriaProducto.TOPPING_PIZZA, 0L) > 5)
                throw new IllegalArgumentException("Máximo 5 toppings para pizza");
        } else { // HAMBURGUESA
            if (conteo.getOrDefault(CategoriaProducto.TIPO_PAN, 0L) != 1)
                throw new IllegalArgumentException("Debe haber exactamente 1 tipo de pan");
            if (conteo.getOrDefault(CategoriaProducto.TIPO_CARNE, 0L) > 3)
                throw new IllegalArgumentException("Máximo 3 tipos de carne");
            if (conteo.getOrDefault(CategoriaProducto.SALSA_HAMBURGUESA, 0L) > 2)
                throw new IllegalArgumentException("Máximo 2 salsas para hamburguesa");
            if (conteo.getOrDefault(CategoriaProducto.TOPPING_HAMBURGUESA, 0L) > 5)
                throw new IllegalArgumentException("Máximo 5 toppings para hamburguesa");
        }

        if (conteo.getOrDefault(CategoriaProducto.BEBIDA, 0L) > 1)
            throw new IllegalArgumentException("Máximo una bebida por creación");

        // Construcción y guardado
        Creacion creacion = new Creacion();
        creacion.setUsuario(usuario);
        creacion.setProductos(productos);
        creacion.setCategoriaCreacion(categoriaCreacion);

        // nombre es obligatorio en la entidad -> autogenerar si no viene por otro flujo
        String nombreDef = (categoriaCreacion == CategoriaCreacion.PIZZA_BASE)
                ? "Pizza personalizada"
                : "Hamburguesa personalizada";
        creacion.setNombre(nombreDef);

        return creacionRepositorio.save(creacion);
    }

    @Transactional(readOnly = true)
    public CreacionDTO obtenerCreacionConDetalles(Long id) {
        Creacion creacion = creacionRepositorio.findByIdWithProductos(id)
                .orElseThrow(() -> new RegistroException("Creación no encontrada"));

        return convertirACreacionDTO(creacion);
    }

    private CreacionDTO convertirACreacionDTO(Creacion creacion) {
        CreacionDTO dto = new CreacionDTO();
        dto.setId(creacion.getId());
        dto.setNombre(creacion.getNombre());
        dto.setDescripcion(creacion.getDescripcion());
        dto.setImagenUrl(creacion.getImagenUrl());
        dto.setCategoriaCreacion(creacion.getCategoriaCreacion());
        dto.setPrecioTotal(creacion.getPrecioTotal());

        if (creacion.getUsuario() != null) {
            dto.setNombreUsuario(creacion.getUsuario().getNombre() + " " + creacion.getUsuario().getApellido());
        }

        if (creacion.getProductos() != null && !creacion.getProductos().isEmpty()) {
            dto.setProductos(
                    creacion.getProductos().stream()
                            .map(this::convertirAProductoSimpleDTO)
                            .collect(Collectors.toList())
            );
        }

        return dto;
    }

    private ProductoSimpleDTO convertirAProductoSimpleDTO(Producto producto) {
        ProductoSimpleDTO dto = new ProductoSimpleDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setPrecio(producto.getPrecio());
        dto.setCategoria(producto.getCategoria());
        return dto;
    }
}
