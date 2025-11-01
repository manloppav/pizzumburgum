package com.example.pizzumburgum.service;

import com.example.pizzumburgum.entities.*;
import com.example.pizzumburgum.repositorio.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarritoServiceTest {

    @Mock private UsuarioRepositorio usuarioRepositorio;
    @Mock private CarritoRepositorio carritoRepositorio;
    @Mock private ProductoRepositorio productoRepositorio;
    @Mock private CreacionRepositorio creacionRepositorio;

    @InjectMocks
    private CarritoService carritoService;

    private Usuario usuario;
    private Carrito carrito;
    private Producto producto;
    private Creacion creacion;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(10L);

        carrito = new Carrito();
        carrito.setId(1L);
        carrito.setUsuario(usuario);
        carrito.setItems(new ArrayList<>());
        carrito.setTotal(BigDecimal.ZERO);

        producto = new Producto();
        producto.setId(5L);
        producto.setNombre("Coca Cola");
        producto.setPrecio(new BigDecimal("120.00"));

        creacion = new Creacion();
        creacion.setId(7L);
        creacion.setPrecioTotal(new BigDecimal("450.00"));
    }

    /** ------------------ agregarProductoSuelto() ------------------ */
    @Test
    void agregarProductoSuelto_ok() {
        // Fuerza rama “crear carrito nuevo”: primer save dentro de obtenerOCrearCarrito
        when(usuarioRepositorio.findById(10L)).thenReturn(Optional.of(usuario));
        when(carritoRepositorio.findByUsuarioId(10L)).thenReturn(Optional.empty());
        when(productoRepositorio.findById(5L)).thenReturn(Optional.of(producto));
        // devolvés el mismo objeto que te pasan (patrón habitual en unit)
        when(carritoRepositorio.save(any(Carrito.class))).thenAnswer(inv -> inv.getArgument(0));

        Carrito resultado = carritoService.agregarProductoSuelto(10L, 5L, 2);

        // Estado del objeto retornado
        assertNotNull(resultado);
        assertEquals(1, resultado.getItems().size());
        CarritoItem item = resultado.getItems().get(0);
        assertEquals(producto, item.getProducto());
        assertEquals(new BigDecimal("120.00"), item.getPrecioUnitario());
        assertEquals(2, item.getCantidad());
        assertEquals(new BigDecimal("240.00"), resultado.getTotal());

        // Verificá 2 saves y validá SOLO el último (el primero muta por referencia)
        ArgumentCaptor<Carrito> captor = ArgumentCaptor.forClass(Carrito.class);
        verify(carritoRepositorio, times(2)).save(captor.capture());

        Carrito ultimoSave = captor.getAllValues().get(captor.getAllValues().size() - 1);
        assertEquals(1, ultimoSave.getItems().size());
        assertEquals(new BigDecimal("240.00"), ultimoSave.getTotal());
    }


    /** ------------------ agregarCreacion() ------------------ */
    @Test
    void agregarCreacion_ok() {
        when(usuarioRepositorio.findById(10L)).thenReturn(Optional.of(usuario));
        when(carritoRepositorio.findByUsuarioId(10L)).thenReturn(Optional.of(carrito));
        when(creacionRepositorio.findById(7L)).thenReturn(Optional.of(creacion));
        when(carritoRepositorio.save(any(Carrito.class))).thenAnswer(inv -> inv.getArgument(0));

        Carrito resultado = carritoService.agregarCreacion(10L, 7L, 1);

        assertEquals(1, resultado.getItems().size());
        CarritoItem item = resultado.getItems().get(0);
        assertEquals(creacion, item.getCreacion());
        assertEquals(new BigDecimal("450.00"), item.getPrecioUnitario());
        assertEquals(1, item.getCantidad());
        assertEquals(new BigDecimal("450.00"), resultado.getTotal());
        verify(carritoRepositorio, times(1)).save(any(Carrito.class));
    }

    /** ------------------ actualizarCantidad() ------------------ */
    @Test
    void actualizarCantidad_ok() {
        CarritoItem item = new CarritoItem();
        item.setId(3L);
        item.setCantidad(1);
        item.setPrecioUnitario(new BigDecimal("100.00"));
        carrito.getItems().add(item);
        carrito.setTotal(new BigDecimal("100.00"));

        when(carritoRepositorio.findByUsuarioId(10L)).thenReturn(Optional.of(carrito));
        when(carritoRepositorio.save(any(Carrito.class))).thenAnswer(inv -> inv.getArgument(0));

        Carrito actualizado = carritoService.actualizarCantidad(10L, 3L, 4);

        assertEquals(4, actualizado.getItems().get(0).getCantidad());
        assertEquals(new BigDecimal("400.00"), actualizado.getTotal());
        verify(carritoRepositorio).save(carrito);
    }

    /** ------------------ Casos de error ------------------ */
    @Test
    void agregarProductoSuelto_cantidadInvalida() {
        assertThrows(IllegalArgumentException.class, () -> carritoService.agregarProductoSuelto(10L, 5L, 0));
    }

    @Test
    void agregarCreacion_noExiste() {
        when(creacionRepositorio.findById(99L)).thenReturn(Optional.empty());
        when(usuarioRepositorio.findById(10L)).thenReturn(Optional.of(usuario));
        when(carritoRepositorio.findByUsuarioId(10L)).thenReturn(Optional.of(carrito));
        assertThrows(IllegalArgumentException.class, () -> carritoService.agregarCreacion(10L, 99L, 1));
    }

    @Test
    void actualizarCantidad_itemNoEncontrado() {
        when(carritoRepositorio.findByUsuarioId(10L)).thenReturn(Optional.of(carrito));
        assertThrows(IllegalArgumentException.class, () -> carritoService.actualizarCantidad(10L, 123L, 2));
    }
}
