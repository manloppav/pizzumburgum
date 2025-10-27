package com.example.pizzumburgum.service;

import com.example.pizzumburgum.entities.*;
import com.example.pizzumburgum.repositorio.CarritoRepositorio;
import com.example.pizzumburgum.repositorio.CreacionRepositorio;
import com.example.pizzumburgum.repositorio.ProductoRepositorio;
import com.example.pizzumburgum.repositorio.UsuarioRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        when(usuarioRepositorio.findById(1L)).thenReturn(Optional.of(usuario));
    }

    // ---------- Helpers simples ----------
    private Producto producto(Long id, String nombre, String precio) {
        Producto p = new Producto();
        p.setId(id);
        p.setNombre(nombre);
        p.setPrecio(new BigDecimal(precio));
        return p;
    }

    private Creacion creacion(Long id, String precioTotal) {
        Creacion c = mock(Creacion.class);              // Mockito
        when(c.getId()).thenReturn(id);
        when(c.getPrecioTotal()).thenReturn(new BigDecimal(precioTotal));
        return c;
    }

    private Carrito carritoVacioPersistido() {
        Carrito c = new Carrito();
        c.setId(100L);
        c.setUsuario(usuario);
        c.setItems(new ArrayList<>());
        c.setTotal(BigDecimal.ZERO);
        return c;
    }

    // ============================================================
    // 1) Crea carrito si no existe y persiste al agregar producto
    // ============================================================
    @Test
    void agregarProductoSuelto_creaCarritoSiNoExiste_yCalculaTotales() {
        when(carritoRepositorio.findByUsuarioId(1L)).thenReturn(Optional.empty());
        when(carritoRepositorio.save(any(Carrito.class))).thenAnswer(inv -> inv.getArgument(0));

        Producto p = producto(10L, "Coca", "123.456"); // se redondeará a 123.46 en el service
        when(productoRepositorio.findById(10L)).thenReturn(Optional.of(p));

        Carrito carrito = carritoService.agregarProductoSuelto(1L, 10L, 2);

        assertNotNull(carrito);
        assertEquals(1, carrito.getItems().size());

        CarritoItem item = carrito.getItems().get(0);
        assertEquals(new BigDecimal("123.46"), item.getPrecioUnitario()); // snapshot
        assertEquals(2, item.getCantidad());
        assertEquals(new BigDecimal("246.92"), item.getSubtotal());
        assertEquals(new BigDecimal("246.92"), carrito.getTotal());

        verify(carritoRepositorio, atLeastOnce()).save(any(Carrito.class));
    }

    // ============================================
    // 2) Agregar creación: snapshot y totales ok
    // ============================================
    @Test
    void agregarCreacion_usaPrecioTotalDeCreacion_yCalculaTotales() {
        Carrito existente = carritoVacioPersistido();
        when(carritoRepositorio.findByUsuarioId(1L)).thenReturn(Optional.of(existente));
        when(carritoRepositorio.save(any(Carrito.class))).thenAnswer(inv -> inv.getArgument(0));

        Creacion c = creacion(20L, "100.00");
        when(creacionRepositorio.findById(20L)).thenReturn(Optional.of(c));

        Carrito carrito = carritoService.agregarCreacion(1L, 20L, 3);

        assertEquals(1, carrito.getItems().size());
        CarritoItem item = carrito.getItems().get(0);
        assertEquals(new BigDecimal("100.00"), item.getPrecioUnitario());
        assertEquals(3, item.getCantidad());
        assertEquals(new BigDecimal("300.00"), item.getSubtotal());
        assertEquals(new BigDecimal("300.00"), carrito.getTotal());
    }

    // ==============================================================
    // 3) Actualizar cantidad NO cambia precioUnitario (solo subtotal)
    // ==============================================================
    @Test
    void actualizarCantidad_noCambiaPrecioUnitario_recalculaSubtotalYTotal() {
        Carrito carrito = carritoVacioPersistido();
        CarritoItem item = new CarritoItem();
        item.setId(555L);
        item.setCarrito(carrito);
        item.setPrecioUnitario(new BigDecimal("50.00"));
        item.setCantidad(1); // subtotal = 50.00
        carrito.getItems().add(item);
        carrito.setTotal(new BigDecimal("50.00"));

        when(carritoRepositorio.findByUsuarioId(1L)).thenReturn(Optional.of(carrito));
        when(carritoRepositorio.save(any(Carrito.class))).thenAnswer(inv -> inv.getArgument(0));

        Carrito actualizado = carritoService.actualizarCantidad(1L, 555L, 4);

        CarritoItem it = actualizado.getItems().get(0);
        assertEquals(new BigDecimal("50.00"), it.getPrecioUnitario()); // no cambia
        assertEquals(4, it.getCantidad());
        assertEquals(new BigDecimal("200.00"), it.getSubtotal());
        assertEquals(new BigDecimal("200.00"), actualizado.getTotal());
    }

    // ===============================================
    // 4) Múltiples ítems se suman correctamente
    // ===============================================
    @Test
    void agregarVariosItems_totalEsLaSumaDeSubtotales() {
        Carrito carrito = carritoVacioPersistido();
        when(carritoRepositorio.findByUsuarioId(1L)).thenReturn(Optional.of(carrito));
        when(carritoRepositorio.save(any(Carrito.class))).thenAnswer(inv -> inv.getArgument(0));

        Producto p1 = producto(11L, "Agua", "50.00");
        Producto p2 = producto(12L, "Jugo", "25.123"); // → 25.12
        when(productoRepositorio.findById(11L)).thenReturn(Optional.of(p1));
        when(productoRepositorio.findById(12L)).thenReturn(Optional.of(p2));

        carrito = carritoService.agregarProductoSuelto(1L, 11L, 2); // 2 * 50.00 = 100.00
        carrito = carritoService.agregarProductoSuelto(1L, 12L, 3); // 3 * 25.12 = 75.36

        assertEquals(2, carrito.getItems().size());
        assertEquals(new BigDecimal("175.36"), carrito.getTotal());
    }

    // ===========================================
    // 5) Validaciones básicas de errores
    // ===========================================
    @Test
    void agregarProductoSuelto_fallaSiCantidadInvalida() {
        when(carritoRepositorio.findByUsuarioId(1L)).thenReturn(Optional.empty());
        when(carritoRepositorio.save(any(Carrito.class))).thenAnswer(inv -> inv.getArgument(0));
        when(productoRepositorio.findById(10L)).thenReturn(Optional.of(producto(10L, "Coca", "10.00")));

        assertThrows(IllegalArgumentException.class,
                () -> carritoService.agregarProductoSuelto(1L, 10L, 0));
    }

    @Test
    void agregarProductoSuelto_fallaSiProductoNoExiste() {
        when(carritoRepositorio.findByUsuarioId(1L)).thenReturn(Optional.empty());
        when(productoRepositorio.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> carritoService.agregarProductoSuelto(1L, 999L, 1));
    }

    @Test
    void agregarCreacion_fallaSiCreacionNoExiste() {
        when(carritoRepositorio.findByUsuarioId(1L)).thenReturn(Optional.of(carritoVacioPersistido()));
        when(creacionRepositorio.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> carritoService.agregarCreacion(1L, 999L, 1));
    }

    @Test
    void actualizarCantidad_fallaSiNoHayCarritoActivo() {
        when(carritoRepositorio.findByUsuarioId(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> carritoService.actualizarCantidad(1L, 123L, 2));
    }

    @Test
    void actualizarCantidad_fallaSiItemNoExiste() {
        Carrito carrito = carritoVacioPersistido();
        when(carritoRepositorio.findByUsuarioId(1L)).thenReturn(Optional.of(carrito));

        assertThrows(IllegalArgumentException.class,
                () -> carritoService.actualizarCantidad(1L, 999L, 2));
    }
}
