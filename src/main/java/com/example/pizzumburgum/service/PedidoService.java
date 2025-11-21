package com.example.pizzumburgum.service;

import com.example.pizzumburgum.dto.request.PedidoItemDTO;
import com.example.pizzumburgum.dto.request.PedidoDTO;
import com.example.pizzumburgum.entities.*;
import com.example.pizzumburgum.enums.EstadoPedido;
import com.example.pizzumburgum.exception.RegistroException;
import com.example.pizzumburgum.repository.*;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

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
        if (pedido.getEstado() == EstadoPedido.ENTREGADO || pedido.getEstado() == EstadoPedido.EN_CAMINO || pedido.getEstado() == EstadoPedido.PREPARACION) {
            throw new IllegalStateException("El pedido no es editable en estado " + pedido.getEstado());
        }

        if (nuevaNota != null) pedido.setObservaciones(nuevaNota);
        if (nuevaDireccion != null) pedido.setDireccionEntrega(nuevaDireccion);

        return pedidoRepositorio.save(pedido);
    }

    // ============= MÉTODOS PARA ADMIN =============

    @Transactional(readOnly = true)
    public List<PedidoDTO> listarPedidosPorFecha(LocalDate fecha) {
        List<Pedido> pedidos = pedidoRepositorio.findByFecha(fecha);
        return pedidos.stream()
                .map(this::convertirAPedidoDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PedidoDTO> listarPedidosPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(LocalTime.MAX);

        List<Pedido> pedidos = pedidoRepositorio.findByFechaRange(inicio, fin);
        return pedidos.stream()
                .map(this::convertirAPedidoDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PedidoDTO> listarTodosPedidos() {
        List<Pedido> pedidos = pedidoRepositorio.findAllByOrderByFechaHoraDesc();
        return pedidos.stream()
                .map(this::convertirAPedidoDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PedidoDTO obtenerPedidoPorId(Long id, Long usuarioId, String rol) {
        Pedido pedido = pedidoRepositorio.findById(id)
                .orElseThrow(() -> new RegistroException("Pedido no encontrado"));

        // Si no es admin, verificar que el pedido pertenezca al usuario
        if (!"ADMIN".equals(rol) && !pedido.getUsuario().getId().equals(usuarioId)) {
            throw new RegistroException("No tienes permiso para ver este pedido");
        }

        return convertirAPedidoDTO(pedido);
    }

    @Transactional
    public PedidoDTO cambiarEstadoPedido(Long id, EstadoPedido nuevoEstado) {
        Pedido pedido = pedidoRepositorio.findById(id)
                .orElseThrow(() -> new RegistroException("Pedido no encontrado"));

        pedido.setEstado(nuevoEstado);
        Pedido pedidoActualizado = pedidoRepositorio.save(pedido);

        return convertirAPedidoDTO(pedidoActualizado);
    }

    @Transactional(readOnly = true)
    public Long contarPedidosPorFecha(LocalDate fecha) {
        return pedidoRepositorio.countByFecha(fecha);
    }

    @Transactional(readOnly = true)
    public List<PedidoDTO> listarPedidosPorEstado(EstadoPedido estado) {
        List<Pedido> pedidos = pedidoRepositorio.findByEstadoOrderByFechaHoraDesc(estado);
        return pedidos.stream()
                .map(this::convertirAPedidoDTO)
                .collect(Collectors.toList());
    }

    // ============= MÉTODOS PARA CLIENTES =============

    @Transactional(readOnly = true)
    public List<PedidoDTO> listarPedidosDeUsuario(Long usuarioId) {
        List<Pedido> pedidos = pedidoRepositorio.findByUsuarioIdOrderByFechaHoraDesc(usuarioId);
        return pedidos.stream()
                .map(this::convertirAPedidoDTO)
                .collect(Collectors.toList());
    }

    // ============= METODO AUXILIAR =============

    private PedidoDTO convertirAPedidoDTO(Pedido pedido) {
        PedidoDTO dto = new PedidoDTO();
        dto.setId(pedido.getId());
        dto.setFechaHora(pedido.getFechaHora());
        dto.setEstado(pedido.getEstado());
        dto.setPrecioTotal(pedido.getPrecioTotal());
        dto.setObservaciones(pedido.getObservaciones());
        dto.setDireccionEntrega(pedido.getDireccionEntrega());
        dto.setNombreCliente(pedido.getUsuario().getNombre() + " " + pedido.getUsuario().getApellido());
        dto.setEmailCliente(pedido.getUsuario().getEmail());

        // Convertir items
        if (pedido.getItems() != null && !pedido.getItems().isEmpty()) {
            List<PedidoItemDTO> itemsDTO = pedido.getItems().stream()
                    .map(this::convertirAItemDTO)
                    .collect(Collectors.toList());
            dto.setItems(itemsDTO);
        }

        return dto;
    }

    private PedidoItemDTO convertirAItemDTO(PedidoItem item) {
        PedidoItemDTO dto = new PedidoItemDTO();
        dto.setId(item.getId());
        dto.setCantidad(item.getCantidad());
        dto.setSubtotal(item.getSubtotal());

        if (item.getProducto() != null) {
            dto.setNombreItem(item.getProducto().getNombre());
            dto.setTipo("PRODUCTO");
            dto.setProductoId(item.getProducto().getId());
        } else if (item.getCreacion() != null) {
            dto.setNombreItem(item.getCreacion().getNombre());
            dto.setTipo("CREACIÓN");
            dto.setCreacionId(item.getCreacion().getId());
        } else {
            dto.setNombreItem("Sin nombre");
            dto.setTipo("DESCONOCIDO");
        }

        return dto;
    }
}