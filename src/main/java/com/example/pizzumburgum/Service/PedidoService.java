package com.example.pizzumburgum.Service;

import com.example.pizzumburgum.entities.*;
import com.example.pizzumburgum.enums.EstadoPedido;
import com.example.pizzumburgum.exception.ResourceNotFoundException;
import com.example.pizzumburgum.Repositorio.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepositorio pedidoRepositorio;
    private final UsuarioService usuarioService;
    private final ProductoService productoService;
    private final CreacionService creacionService;

    public PedidoService(PedidoRepositorio pedidoRepositorio, UsuarioService usuarioService,
                        ProductoService productoService, CreacionService creacionService) {
        this.pedidoRepositorio = pedidoRepositorio;
        this.usuarioService = usuarioService;
        this.productoService = productoService;
        this.creacionService = creacionService;
    }

    @Transactional
    public Pedido crearPedido(Long usuarioId, List<PedidoItem> items, String observaciones) {
        Usuario usuario = usuarioService.buscarPorId(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", usuarioId));

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setEstado(EstadoPedido.PENDIENTE);
        pedido.setObservaciones(observaciones);

        BigDecimal total = BigDecimal.ZERO;
        for (PedidoItem item : items) {
            if (item.getProducto() != null) {
                Producto producto = productoService.buscarPorId(item.getProducto().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Producto", "id", item.getProducto().getId()));
                item.setProducto(producto);
                item.setSubtotal(producto.getPrecio().multiply(BigDecimal.valueOf(item.getCantidad())));
            } else if (item.getCreacion() != null) {
                Creacion creacion = creacionService.buscarPorId(item.getCreacion().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Creacion", "id", item.getCreacion().getId()));
                item.setCreacion(creacion);
                item.setSubtotal(creacion.getPrecioTotal().multiply(BigDecimal.valueOf(item.getCantidad())));
            } else {
                throw new IllegalArgumentException("El item debe tener producto o creacion");
            }
            item.setPedido(pedido);
            total = total.add(item.getSubtotal());
        }
        pedido.setPrecioTotal(total);
        pedido.setItems(items);

        return pedidoRepositorio.save(pedido);
    }
}