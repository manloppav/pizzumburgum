package com.example.pizzumburgum.service;

import com.example.pizzumburgum.dto.response.CarritoDTO;
import com.example.pizzumburgum.dto.response.CarritoItemDTO;
import com.example.pizzumburgum.entities.*;
import com.example.pizzumburgum.repository.CarritoRepositorio;
import com.example.pizzumburgum.repository.CreacionRepositorio;
import com.example.pizzumburgum.repository.ProductoRepositorio;
import com.example.pizzumburgum.repository.UsuarioRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarritoService {

    private final UsuarioRepositorio usuarioRepositorio;
    private final CarritoRepositorio carritoRepositorio;
    private final ProductoRepositorio productoRepositorio;
    private final CreacionRepositorio creacionRepositorio;

    /**
     * ############ Helpers ############
     */

    public Carrito obtenerOCrearCarrito(Long usuarioId) {
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

    /**
     * ############ Precios vigentes (snapshot en el momento) ############
     */

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

    /**
     * ############ Operaciones públicas ############
     */

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

    @Transactional(readOnly = true)
    public Carrito obtenerCarritoConDetalles(Long usuarioId) {
        return obtenerOCrearCarrito(usuarioId);
    }

    @Transactional
    public Carrito eliminarItem(Long usuarioId, Long carritoItemId) {
        Carrito carrito = carritoRepositorio.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("El usuario no tiene carrito activo"));

        carrito.getItems().removeIf(item -> item.getId().equals(carritoItemId));
        recalcularTotalCarrito(carrito);

        return carritoRepositorio.save(carrito);
    }

    @Transactional
    public void vaciarCarrito(Long usuarioId) {
        Carrito carrito = carritoRepositorio.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("El usuario no tiene carrito activo"));

        carrito.getItems().clear();
        carrito.setTotal(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        carritoRepositorio.save(carrito);
    }

    public CarritoDTO convertirADTO(Carrito carrito) {
        CarritoDTO dto = new CarritoDTO();
        dto.setId(carrito.getId());
        dto.setTotal(carrito.getTotal());

        List<CarritoItemDTO> itemsDTO = carrito.getItems().stream()
                .map(this::convertirItemADTO)
                .collect(Collectors.toList());
        dto.setItems(itemsDTO);

        return dto;
    }

    private CarritoItemDTO convertirItemADTO(CarritoItem item) {
        CarritoItemDTO dto = new CarritoItemDTO();
        dto.setId(item.getId());
        dto.setCantidad(item.getCantidad());
        dto.setPrecioUnitario(item.getPrecioUnitario());
        dto.setSubtotal(item.getSubtotal());

        if (item.getProducto() != null) {
            dto.setProductoId(item.getProducto().getId());
            dto.setProductoNombre(item.getProducto().getNombre());
        }

        if (item.getCreacion() != null) {
            dto.setCreacionId(item.getCreacion().getId());
            dto.setCreacionNombre(item.getCreacion().getNombre());
        }

        return dto;
    }
}