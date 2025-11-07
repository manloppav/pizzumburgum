package com.example.pizzumburgum.service;

import com.example.pizzumburgum.entities.*;
import com.example.pizzumburgum.enums.EstadoPedido;
import com.example.pizzumburgum.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock private UsuarioRepositorio usuarioRepositorio;
    @Mock private CarritoRepositorio carritoRepositorio;
    @Mock private PedidoRepositorio pedidoRepositorio;
    @Mock private TarjetaRepositorio tarjetaRepositorio;
    @Mock private PagoRepositorio pagoRepositorio;

    @InjectMocks
    private PedidoService pedidoService;

    private Usuario usuario;
    private Tarjeta tarjeta;
    private Carrito carrito;

    @BeforeEach
    void setup() {
        usuario = new Usuario();
        usuario.setId(10L);

        tarjeta = new Tarjeta();
        tarjeta.setId(7L);
        tarjeta.setUsuario(usuario);

        carrito = new Carrito();
        carrito.setUsuario(usuario);

        // Item del carrito con subtotal 240.00
        CarritoItem item = new CarritoItem();
        item.setCantidad(2);
        item.setSubtotal(new BigDecimal("240.00"));
        carrito.getItems().add(item);
        carrito.recalcularTotal(); // total = 240.00
    }

    @Test
    void crearPedido_ok() {
        when(usuarioRepositorio.findById(10L)).thenReturn(Optional.of(usuario));
        when(carritoRepositorio.findByUsuarioId(10L)).thenReturn(Optional.of(carrito));
        when(tarjetaRepositorio.findById(7L)).thenReturn(Optional.of(tarjeta));
        when(pedidoRepositorio.save(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));
        when(pagoRepositorio.save(any(Pago.class))).thenAnswer(inv -> inv.getArgument(0));

        // 🔹 snapshot del total ANTES de crear el pedido
        BigDecimal totalAntes = carrito.getTotal(); // 240.00

        Pedido pedido = pedidoService.crearPedido(10L, 7L, "Sin cebolla", "Av. Rivera 1234");

        // El pedido conserva el total original del carrito
        assertEquals(totalAntes, pedido.getPrecioTotal());

        // El carrito fue vaciado y su total quedó en 0.00
        assertEquals(0, carrito.getItems().size());
        assertEquals(new BigDecimal("0.00"), carrito.getTotal());

        // Chequeos extra
        assertEquals(usuario, pedido.getUsuario());
        assertEquals(EstadoPedido.PENDIENTE, pedido.getEstado());
        assertEquals(1, pedido.getItems().size());
        verify(pagoRepositorio, times(1)).save(any(Pago.class));
        verify(carritoRepositorio, times(1)).save(any(Carrito.class));
    }

    @Test
    void crearPedido_falla_usuarioNoExiste() {
        when(usuarioRepositorio.findById(10L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> pedidoService.crearPedido(10L, 7L, "nota", "dir"));
    }

    @Test
    void crearPedido_falla_carritoVacio() {
        carrito.getItems().clear();
        when(usuarioRepositorio.findById(10L)).thenReturn(Optional.of(usuario));
        when(carritoRepositorio.findByUsuarioId(10L)).thenReturn(Optional.of(carrito));
        assertThrows(IllegalArgumentException.class,
                () -> pedidoService.crearPedido(10L, 7L, "nota", "dir"));
    }

    @Test
    void crearPedido_falla_tarjetaNoExiste() {
        when(usuarioRepositorio.findById(10L)).thenReturn(Optional.of(usuario));
        when(carritoRepositorio.findByUsuarioId(10L)).thenReturn(Optional.of(carrito));
        when(tarjetaRepositorio.findById(7L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> pedidoService.crearPedido(10L, 7L, "nota", "dir"));
    }

    @Test
    void crearPedido_falla_tarjetaNoPertenece() {
        Usuario otroUsuario = new Usuario();
        otroUsuario.setId(99L);
        tarjeta.setUsuario(otroUsuario);

        when(usuarioRepositorio.findById(10L)).thenReturn(Optional.of(usuario));
        when(carritoRepositorio.findByUsuarioId(10L)).thenReturn(Optional.of(carrito));
        when(tarjetaRepositorio.findById(7L)).thenReturn(Optional.of(tarjeta));

        assertThrows(IllegalArgumentException.class,
                () -> pedidoService.crearPedido(10L, 7L, "nota", "dir"));
    }

    @Test
    void editarPedido_ok() {
        Usuario usuario = new Usuario();
        usuario.setId(10L);

        Pedido pedido = new Pedido();
        pedido.setId(5L);
        pedido.setUsuario(usuario);
        pedido.setEstado(EstadoPedido.PENDIENTE);
        pedido.setObservaciones("vieja");
        pedido.setDireccionEntrega("viejaDir");

        when(pedidoRepositorio.findById(5L)).thenReturn(Optional.of(pedido));
        when(pedidoRepositorio.save(any(Pedido.class))).thenAnswer(inv -> inv.getArgument(0));

        Pedido result = pedidoService.editarPedido(10L, 5L, "nueva", "nuevaDir");

        assertEquals("nueva", result.getObservaciones());
        assertEquals("nuevaDir", result.getDireccionEntrega());
        verify(pedidoRepositorio, times(1)).save(any(Pedido.class));
    }

    @Test
    void editarPedido_falla_usuarioNoEsPropietario() {
        Usuario dueno = new Usuario();
        dueno.setId(99L);

        Pedido pedido = new Pedido();
        pedido.setId(6L);
        pedido.setUsuario(dueno);
        pedido.setEstado(EstadoPedido.PENDIENTE);

        when(pedidoRepositorio.findById(6L)).thenReturn(Optional.of(pedido));

        assertThrows(IllegalArgumentException.class,
                () -> pedidoService.editarPedido(10L, 6L, "nota", "dir"));
    }

    @Test
    void editarPedido_falla_noEditableEstado() {
        Usuario usuario = new Usuario();
        usuario.setId(10L);

        Pedido pedido = new Pedido();
        pedido.setId(7L);
        pedido.setUsuario(usuario);
        pedido.setEstado(EstadoPedido.ENTREGADO); // estado no editable

        when(pedidoRepositorio.findById(7L)).thenReturn(Optional.of(pedido));

        assertThrows(IllegalStateException.class,
                () -> pedidoService.editarPedido(10L, 7L, "nota", "dir"));
    }

}