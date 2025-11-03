package com.example.pizzumburgum.service;

import com.example.pizzumburgum.entities.Creacion;
import com.example.pizzumburgum.entities.Producto;
import com.example.pizzumburgum.entities.Usuario;
import com.example.pizzumburgum.enums.CategoriaProducto;
import com.example.pizzumburgum.repositorio.CreacionRepositorio;
import com.example.pizzumburgum.repositorio.ProductoRepositorio;
import com.example.pizzumburgum.repositorio.UsuarioRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreacionServiceTest {

    @Mock private UsuarioRepositorio usuarioRepositorio;
    @Mock private ProductoRepositorio productoRepositorio;
    @Mock private CreacionRepositorio creacionRepositorio;

    @InjectMocks
    private CreacionService creacionService;

    private Usuario usuario;

    @BeforeEach
    void setup() {
        usuario = new Usuario();
        usuario.setId(10L);
    }

    /* ===================== Helpers ===================== */

    private Producto prod(long id, CategoriaProducto cat, String nombre, String precio) {
        Producto p = new Producto();
        p.setId(id);
        p.setCategoria(cat);
        p.setNombre(nombre);
        p.setPrecio(new BigDecimal(precio));
        return p;
    }

    /** Para casos OK (sí se invoca save) */
    private void stubOk(Long usuarioId, List<Long> ids, List<Producto> productos) {
        when(usuarioRepositorio.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(productoRepositorio.findAllById(ids)).thenReturn(productos);
        when(creacionRepositorio.save(any(Creacion.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    /** Para casos que deben fallar antes de save */
    private void stubError(Long usuarioId, List<Long> ids, List<Producto> productos) {
        when(usuarioRepositorio.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(productoRepositorio.findAllById(ids)).thenReturn(productos);
        // No stubbear save para evitar UnnecessaryStubbingException
    }

    /* ===================== Casos OK ===================== */

    @Test
    void crearCreacion_pizza_ok() {
        List<Long> ids = List.of(1L,2L,3L,4L,5L,6L,7L,8L);
        List<Producto> prods = List.of(
                prod(1, CategoriaProducto.PIZZA_BASE, "Base Pizza", "0"),
                prod(2, CategoriaProducto.TIPO_MASA, "Masa Fina", "0"),
                prod(3, CategoriaProducto.TAMANIO_PIZZA, "Mediana", "0"),
                prod(4, CategoriaProducto.SALSA_PIZZA, "Roja", "0"),
                prod(5, CategoriaProducto.SALSA_PIZZA, "Blanca", "0"),
                prod(6, CategoriaProducto.TOPPING_PIZZA, "Olivas", "0"),
                prod(7, CategoriaProducto.TOPPING_PIZZA, "Jamón", "0"),
                prod(8, CategoriaProducto.BEBIDA, "Coca", "120.00")
        );
        stubOk(10L, ids, prods);

        Creacion creada = creacionService.crearCreacion(10L, ids);

        assertNotNull(creada);
        assertEquals(usuario, creada.getUsuario());
        assertEquals(prods.size(), creada.getProductos().size());
        verify(creacionRepositorio, times(1)).save(any(Creacion.class));
    }

    @Test
    void crearCreacion_hamburguesa_ok() {
        List<Producto> prods = new ArrayList<>();
        long id = 1;
        prods.add(prod(id++, CategoriaProducto.HAMBURGUESA_BASE, "Base Hamb", "0"));
        prods.add(prod(id++, CategoriaProducto.TIPO_PAN, "Pan Brioche", "0"));
        prods.add(prod(id++, CategoriaProducto.TIPO_CARNE, "Carne 1", "0"));
        prods.add(prod(id++, CategoriaProducto.TIPO_CARNE, "Carne 2", "0"));
        prods.add(prod(id++, CategoriaProducto.TIPO_QUESO, "Queso", "0"));
        prods.add(prod(id++, CategoriaProducto.SALSA_HAMBURGUESA, "Mostaza", "0"));
        prods.add(prod(id++, CategoriaProducto.SALSA_HAMBURGUESA, "BBQ", "0"));
        for (int i = 0; i < 5; i++) prods.add(prod(id++, CategoriaProducto.TOPPING_HAMBURGUESA, "Top"+i, "0"));
        prods.add(prod(id++, CategoriaProducto.ACOMPANIAMIENTO, "Papas", "200.00"));

        List<Long> ids = prods.stream().map(Producto::getId).toList();
        stubOk(10L, ids, prods);

        Creacion creada = creacionService.crearCreacion(10L, ids);

        assertNotNull(creada);
        assertEquals(usuario, creada.getUsuario());
        assertEquals(prods.size(), creada.getProductos().size());
        verify(creacionRepositorio, times(1)).save(any(Creacion.class));
    }

    /* ===================== Casos de error ===================== */

    @Test
    void crearCreacion_falla_sinBase() {
        List<Long> ids = List.of(1L,2L);
        List<Producto> prods = List.of(
                prod(1, CategoriaProducto.TIPO_MASA, "Masa", "0"),
                prod(2, CategoriaProducto.TAMANIO_PIZZA, "Tamaño", "0")
        );
        stubError(10L, ids, prods);

        assertThrows(IllegalArgumentException.class,
                () -> creacionService.crearCreacion(10L, ids));
        verify(creacionRepositorio, never()).save(any());
    }

    @Test
    void crearCreacion_falla_multiplesBases() {
        List<Long> ids = List.of(1L,2L);
        List<Producto> prods = List.of(
                prod(1, CategoriaProducto.PIZZA_BASE, "Base Pizza", "0"),
                prod(2, CategoriaProducto.HAMBURGUESA_BASE, "Base Hamb", "0")
        );
        stubError(10L, ids, prods);

        assertThrows(IllegalArgumentException.class,
                () -> creacionService.crearCreacion(10L, ids));
    }

    @Test
    void crearCreacion_falla_categoriaNoPermitida_enPizza() {
        List<Long> ids = List.of(1L,2L,3L,4L);
        List<Producto> prods = List.of(
                prod(1, CategoriaProducto.PIZZA_BASE, "Base Pizza", "0"),
                prod(2, CategoriaProducto.TIPO_MASA, "Masa", "0"),
                prod(3, CategoriaProducto.TAMANIO_PIZZA, "Tamaño", "0"),
                prod(4, CategoriaProducto.TIPO_CARNE, "Carne", "0") // no permitido en pizza
        );
        stubError(10L, ids, prods);

        assertThrows(IllegalArgumentException.class,
                () -> creacionService.crearCreacion(10L, ids));
    }

    @Test
    void crearCreacion_falla_excesoCarnes_enHamburguesa() {
        List<Producto> prods = new ArrayList<>();
        long id = 1;
        prods.add(prod(id++, CategoriaProducto.HAMBURGUESA_BASE, "Base Hamb", "0"));
        prods.add(prod(id++, CategoriaProducto.TIPO_PAN, "Pan", "0"));
        for (int i = 0; i < 4; i++) prods.add(prod(id++, CategoriaProducto.TIPO_CARNE, "Carne"+i, "0")); // >3
        List<Long> ids = prods.stream().map(Producto::getId).toList();
        stubError(10L, ids, prods);

        assertThrows(IllegalArgumentException.class,
                () -> creacionService.crearCreacion(10L, ids));
    }

    @Test
    void crearCreacion_falla_excesoToppings_enPizza() {
        List<Producto> prods = new ArrayList<>();
        long id = 1;
        prods.add(prod(id++, CategoriaProducto.PIZZA_BASE, "Base", "0"));
        prods.add(prod(id++, CategoriaProducto.TIPO_MASA, "Masa", "0"));
        prods.add(prod(id++, CategoriaProducto.TAMANIO_PIZZA, "Tamaño", "0"));
        for (int i = 0; i < 6; i++) prods.add(prod(id++, CategoriaProducto.TOPPING_PIZZA, "Top"+i, "0")); // >5
        List<Long> ids = prods.stream().map(Producto::getId).toList();
        stubError(10L, ids, prods);

        assertThrows(IllegalArgumentException.class,
                () -> creacionService.crearCreacion(10L, ids));
    }

    @Test
    void crearCreacion_falla_excesoSalsas_enHamburguesa() {
        List<Producto> prods = new ArrayList<>();
        long id = 1;
        prods.add(prod(id++, CategoriaProducto.HAMBURGUESA_BASE, "Base", "0"));
        prods.add(prod(id++, CategoriaProducto.TIPO_PAN, "Pan", "0"));
        for (int i = 0; i < 3; i++) prods.add(prod(id++, CategoriaProducto.SALSA_HAMBURGUESA, "Salsa"+i, "0")); // >2
        List<Long> ids = prods.stream().map(Producto::getId).toList();
        stubError(10L, ids, prods);

        assertThrows(IllegalArgumentException.class,
                () -> creacionService.crearCreacion(10L, ids));
    }

    @Test
    void crearCreacion_falla_dosBebidas() {
        List<Long> ids = List.of(1L,2L,3L,4L,5L);
        List<Producto> prods = List.of(
                prod(1, CategoriaProducto.PIZZA_BASE, "Base Pizza", "0"),
                prod(2, CategoriaProducto.TIPO_MASA, "Masa", "0"),
                prod(3, CategoriaProducto.TAMANIO_PIZZA, "Tamaño", "0"),
                prod(4, CategoriaProducto.BEBIDA, "Cola", "0"),
                prod(5, CategoriaProducto.BEBIDA, "Naranja", "0") // 2 bebidas
        );
        stubError(10L, ids, prods);

        assertThrows(IllegalArgumentException.class,
                () -> creacionService.crearCreacion(10L, ids));
    }

    @Test
    void crearCreacion_falla_pizza_sinMasa() {
        List<Long> ids = List.of(1L,2L);
        List<Producto> prods = List.of(
                prod(1, CategoriaProducto.PIZZA_BASE, "Base", "0"),
                prod(2, CategoriaProducto.TAMANIO_PIZZA, "Tamaño", "0") // falta masa
        );
        stubError(10L, ids, prods);

        assertThrows(IllegalArgumentException.class,
                () -> creacionService.crearCreacion(10L, ids));
    }

    @Test
    void crearCreacion_falla_pizza_multiplesMasas() {
        List<Long> ids = List.of(1L,2L,3L);
        List<Producto> prods = List.of(
                prod(1, CategoriaProducto.PIZZA_BASE, "Base", "0"),
                prod(2, CategoriaProducto.TIPO_MASA, "Masa A", "0"),
                prod(3, CategoriaProducto.TIPO_MASA, "Masa B", "0") // 2 masas
        );
        stubError(10L, ids, prods);

        assertThrows(IllegalArgumentException.class,
                () -> creacionService.crearCreacion(10L, ids));
    }

    @Test
    void crearCreacion_falla_hamburguesa_sinPan() {
        List<Long> ids = List.of(1L,2L);
        List<Producto> prods = List.of(
                prod(1, CategoriaProducto.HAMBURGUESA_BASE, "Base", "0"),
                prod(2, CategoriaProducto.TIPO_CARNE, "Carne", "0") // falta pan
        );
        stubError(10L, ids, prods);

        assertThrows(IllegalArgumentException.class,
                () -> creacionService.crearCreacion(10L, ids));
    }
}
