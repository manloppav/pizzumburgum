package com.example.pizzumburgum.Service;

import com.example.pizzumburgum.Repositorio.PedidoRepositorio;
import com.example.pizzumburgum.entities.*;
import com.example.pizzumburgum.enums.EstadoPedido;
import com.example.pizzumburgum.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PedidoServiceTest {

    @Mock
    private PedidoRepositorio pedidoRepositorio;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private ProductoService productoService;

    @Mock
    private CreacionService creacionService;

    @InjectMocks
    private PedidoService pedidoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCrearPedido() {
        // Datos de prueba
        Long usuarioId = 1L;
        Usuario usuario = new Usuario();
        usuario.setId(usuarioId);

        Producto producto = new Producto();
        producto.setId(1L);
        producto.setPrecio(new BigDecimal("100.00"));

        PedidoItem item = new PedidoItem();
        item.setProducto(producto);
        item.setCantidad(2);

        List<PedidoItem> items = List.of(item);

        Pedido pedidoGuardado = new Pedido();
        pedidoGuardado.setId(1L);
        pedidoGuardado.setUsuario(usuario);
        pedidoGuardado.setEstado(EstadoPedido.PENDIENTE);
        pedidoGuardado.setPrecioTotal(new BigDecimal("200.00"));
        pedidoGuardado.setItems(items);

        // Mock de servicios
        when(usuarioService.buscarPorId(usuarioId)).thenReturn(Optional.of(usuario));
        when(productoService.buscarPorId(producto.getId())).thenReturn(Optional.of(producto));
        when(pedidoRepositorio.save(any(Pedido.class))).thenReturn(pedidoGuardado);

        // Ejecución
        Pedido pedido = pedidoService.crearPedido(usuarioId, items, "Sin observaciones");

        // Verificaciones
        assertNotNull(pedido);
        assertEquals(usuarioId, pedido.getUsuario().getId());
        assertEquals(EstadoPedido.PENDIENTE, pedido.getEstado());
        assertEquals(new BigDecimal("200.00"), pedido.getPrecioTotal());
        assertEquals(1, pedido.getItems().size());

        verify(usuarioService, times(1)).buscarPorId(usuarioId);
        verify(productoService, times(1)).buscarPorId(producto.getId());
        verify(pedidoRepositorio, times(1)).save(any(Pedido.class));
    }

    @Test
    void testCrearPedidoUsuarioNoEncontrado() {
        Long usuarioId = 1L;
        List<PedidoItem> items = List.of();

        when(usuarioService.buscarPorId(usuarioId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            pedidoService.crearPedido(usuarioId, items, "Sin observaciones");
        });

        verify(usuarioService, times(1)).buscarPorId(usuarioId);
        verifyNoInteractions(productoService, pedidoRepositorio);
    }
}