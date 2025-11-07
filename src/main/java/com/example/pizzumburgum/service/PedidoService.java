package com.example.pizzumburgum.service;

import com.example.pizzumburgum.entities.*;
import com.example.pizzumburgum.enums.EstadoPedido;
import com.example.pizzumburgum.repositorio.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final UsuarioRepositorio usuarioRepositorio;
    private final CarritoRepositorio carritoRepositorio;
    private final PedidoRepositorio pedidoRepositorio;
    private final TarjetaRepositorio tarjetaRepositorio;
    private final PagoRepositorio pagoRepositorio;

    @Transactional
    public Pedido crearPedido(Long usuarioId, Long tarjetaId, String nota, String direccionEntrega) {
        // Usuario
        Usuario usuario = usuarioRepositorio.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + usuarioId));

        // Carrito
        Carrito carrito = carritoRepositorio.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("El usuario no tiene carrito activo"));
        if (carrito.getItems() == null || carrito.getItems().isEmpty()) {
            throw new IllegalArgumentException("El carrito está vacío");
        }

        // Tarjeta
        Tarjeta tarjeta = tarjetaRepositorio.findById(tarjetaId)
                .orElseThrow(() -> new IllegalArgumentException("Tarjeta no encontrada: " + tarjetaId));
        if (!tarjeta.getUsuario().getId().equals(usuarioId)) {
            throw new IllegalArgumentException("La tarjeta no pertenece al usuario");
        }

        // Total (snapshot)
        BigDecimal total = carrito.getItems().stream()
                .map(CarritoItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        if (total.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El total del pedido debe ser mayor a 0");
        }

        // Pago simplificado: siempre aprobado (sin beans, sin pasarelas)
        String codigoAutorizacion = "AUTH-LOCAL";

        // Construir Pedido en PENDIENTE
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setEstado(EstadoPedido.PENDIENTE);
        pedido.setObservaciones(nota);
        pedido.setDireccionEntrega(direccionEntrega);
        pedido.setPrecioTotal(total);

        // Items snapshot
        for (CarritoItem ci : carrito.getItems()) {
            PedidoItem pi = new PedidoItem();
            pi.setPedido(pedido);
            pi.setCantidad(ci.getCantidad());
            // Snapshot directo del carrito:
            pi.setSubtotal(ci.getSubtotal());

            if (ci.getProducto() != null) pi.setProducto(ci.getProducto());
            if (ci.getCreacion() != null) pi.setCreacion(ci.getCreacion());

            pedido.getItems().add(pi);
        }

        pedido = pedidoRepositorio.save(pedido);

        // Registrar pago
        Pago pago = new Pago();
        pago.setPedido(pedido);
        pago.setTarjeta(tarjeta);
        pago.setMonto(total);
        pago.setCodigoAutorizacion(codigoAutorizacion);
        pagoRepositorio.save(pago);

        // Vaciar carrito
        carrito.getItems().clear();
        carrito.setTotal(new BigDecimal("0.00"));
        carritoRepositorio.save(carrito);

        return pedido;
    }

    @Transactional
    public Pedido editarPedido(Long usuarioId, Long pedidoId, String nuevaNota, String nuevaDireccion) {
        Pedido pedido = pedidoRepositorio.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado: " + pedidoId));

        if (!pedido.getUsuario().getId().equals(usuarioId)) {
            throw new IllegalArgumentException("El pedido no pertenece al usuario");
        }
        if (pedido.getEstado() == EstadoPedido.ENTREGADO || pedido.getEstado() == EstadoPedido.EN_CAMINO ||  pedido.getEstado() == EstadoPedido.PREPARACION) {
            throw new IllegalStateException("El pedido no es editable en estado " + pedido.getEstado());
        }

        if (nuevaNota != null) pedido.setObservaciones(nuevaNota);
        if (nuevaDireccion != null) pedido.setDireccionEntrega(nuevaDireccion);

        return pedidoRepositorio.save(pedido);
    }
}