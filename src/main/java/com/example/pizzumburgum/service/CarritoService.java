package com.example.pizzumburgum.service;

import com.example.pizzumburgum.entities.*;
import com.example.pizzumburgum.repositorio.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class CarritoService {

    private final UsuarioRepositorio usuarioRepositorio;
    private final CarritoRepositorio carritoRepositorio;
    private final ProductoRepositorio productoRepositorio;
    private final CreacionRepositorio creacionRepositorio;

    /** ############ Helpers ############ */

    private Carrito obtenerOCrearCarrito(Long usuarioId) {
        var usuario = usuarioRepositorio.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + usuarioId));

        return carritoRepositorio.findByUsuarioId(usuarioId)
                .orElseGet(() -> {
                    Carrito nuevo = new Carrito();
                    nuevo.setUsuario(usuario);
                    nuevo.setItems(new ArrayList<>());
                    nuevo.setTotal(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
                    return carritoRepositorio.save(nuevo);
                });
    }

    private void recalcularTotalCarrito(Carrito carrito) {
        BigDecimal total = carrito.getItems().stream()
                .map(CarritoItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        carrito.setTotal(total);
    }

    /** ############ Precios vigentes (snapshot en el momento) ############ */

    private BigDecimal precioVigenteProducto(Long productoId) {
        var prod = productoRepositorio.findById(productoId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + productoId));
        // Si querés limitar a BEBIDA/ACOMPAÑAMIENTO, validalo aquí con prod.getCategoria()
        return prod.getPrecio().setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal precioVigenteCreacion(Long creacionId) {
        var creacion = creacionRepositorio.findById(creacionId)
                .orElseThrow(() -> new IllegalArgumentException("Creación no encontrada: " + creacionId));
        // Si querés revalidar reglas pizza/hamburguesa, este es el lugar.
        return creacion.getPrecioTotal().setScale(2, RoundingMode.HALF_UP);
    }

    /** ############ Operaciones públicas ############ */

    @Transactional
    public Carrito agregarProductoSuelto(Long usuarioId, Long productoId, int cantidad) {
        if (cantidad <= 0) throw new IllegalArgumentException("La cantidad debe ser mayor a 0");

        Carrito carrito = obtenerOCrearCarrito(usuarioId);
        BigDecimal unit = precioVigenteProducto(productoId);

        Producto prod = productoRepositorio.findById(productoId)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + productoId));

        CarritoItem item = new CarritoItem();
        item.setCarrito(carrito);
        item.setProducto(prod);
        item.setPrecioUnitario(unit);   // snapshot
        item.setCantidad(cantidad);     // recalcula subtotal

        carrito.getItems().add(item);
        recalcularTotalCarrito(carrito);

        return carritoRepositorio.save(carrito);
    }

    @Transactional
    public Carrito agregarCreacion(Long usuarioId, Long creacionId, int cantidad) {
        if (cantidad <= 0) throw new IllegalArgumentException("La cantidad debe ser mayor a 0");

        Carrito carrito = obtenerOCrearCarrito(usuarioId);
        BigDecimal unit = precioVigenteCreacion(creacionId);

        Creacion creacion = creacionRepositorio.findById(creacionId)
                .orElseThrow(() -> new IllegalArgumentException("Creación no encontrada: " + creacionId));

        CarritoItem item = new CarritoItem();
        item.setCarrito(carrito);
        item.setCreacion(creacion);
        item.setPrecioUnitario(unit);   // snapshot
        item.setCantidad(cantidad);     // recalcula subtotal

        carrito.getItems().add(item);
        recalcularTotalCarrito(carrito);

        return carritoRepositorio.save(carrito);
    }

    @Transactional
    public Carrito actualizarCantidad(Long usuarioId, Long carritoItemId, int nuevaCantidad) {
        if (nuevaCantidad <= 0) throw new IllegalArgumentException("La cantidad debe ser mayor a 0");

        Carrito carrito = carritoRepositorio.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("El usuario no tiene carrito activo"));

        var item = carrito.getItems().stream()
                .filter(ci -> ci.getId().equals(carritoItemId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Item no encontrado: " + carritoItemId));

        // NO se toca el precioUnitario (snapshot). Solo cambia cantidad → subtotal
        item.setCantidad(nuevaCantidad);

        recalcularTotalCarrito(carrito);
        return carritoRepositorio.save(carrito);
    }
}

