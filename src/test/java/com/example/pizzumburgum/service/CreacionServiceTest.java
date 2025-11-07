package com.example.pizzumburgum.service;

import com.example.pizzumburgum.entities.Creacion;
import com.example.pizzumburgum.entities.Producto;
import com.example.pizzumburgum.entities.Usuario;
import com.example.pizzumburgum.enums.CategoriaCreacion;
import com.example.pizzumburgum.enums.CategoriaProducto;
import com.example.pizzumburgum.repository.CreacionRepositorio;
import com.example.pizzumburgum.repository.ProductoRepositorio;
import com.example.pizzumburgum.repository.UsuarioRepositorio;
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
        lenient().when(creacionRepositorio.save(any(Creacion.class)))
                .thenAnswer(inv -> inv.getArgument(0));
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

    private void stubUsuarioYProductos(Long usuarioId, List<Long> ids, List<Producto> productos) {
        when(usuarioRepositorio.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(productoRepositorio.findAllById(ids)).thenReturn(productos);
    }

    /* ===================== Casos OK ===================== */

    @Test
    void crearCreacion_PIZZA_BASE_ok() {
        // Reglas PIZZA_BASE: 1 TIPO_MASA, 1 TAMANIO_PIZZA_BASE, toppings <=5, bebida <=1, categorías solo de PIZZA_BASE
        List<Producto> prods = new ArrayList<>();
        long id = 1;
        prods.add(prod(id++, CategoriaProducto.TIPO_MASA, "Masa Fina", "80.00"));
        prods.add(prod(id++, CategoriaProducto.TAMANIO_PIZZA, "Mediana", "120.00"));
        prods.add(prod(id++, CategoriaProducto.SALSA_PIZZA, "Tomate", "60.00"));
        prods.add(prod(id++, CategoriaProducto.TOPPING_PIZZA, "Mozzarella", "90.00"));
        prods.add(prod(id++, CategoriaProducto.TOPPING_PIZZA, "Aceitunas", "70.00"));
        prods.add(prod(id++, CategoriaProducto.BEBIDA, "Cola 500ml", "90.00")); // opcional, máx 1
        List<Long> ids = prods.stream().map(Producto::getId).toList();

        stubUsuarioYProductos(10L, ids, prods);

        Creacion creada = creacionService.crearCreacion(10L, CategoriaCreacion.PIZZA_BASE, ids);

        assertNotNull(creada);
        assertEquals(usuario, creada.getUsuario());
        assertEquals(prods.size(), creada.getProductos().size());
        verify(creacionRepositorio, times(1)).save(any(Creacion.class));
    }

    @Test
    void crearCreacion_HAMBURGUESA_BASE_ok() {
        // Reglas HAMBURGUESA_BASE: 1 TIPO_PAN, carnes <=3, salsas <=2, toppings <=5, bebida <=1
        List<Producto> prods = new ArrayList<>();
        long id = 1;
        prods.add(prod(id++, CategoriaProducto.TIPO_PAN, "Brioche", "70.00"));           // obligatorio 1
        prods.add(prod(id++, CategoriaProducto.TIPO_CARNE, "Carne vacuna", "150.00"));  // <=3
        prods.add(prod(id++, CategoriaProducto.TIPO_QUESO, "Cheddar", "60.00"));
        prods.add(prod(id++, CategoriaProducto.SALSA_HAMBURGUESA, "BBQ", "50.00"));     // <=2
        prods.add(prod(id++, CategoriaProducto.TOPPING_HAMBURGUESA, "Lechuga", "25.00"));
        prods.add(prod(id++, CategoriaProducto.BEBIDA, "Agua 500ml", "70.00"));         // opcional, máx 1
        List<Long> ids = prods.stream().map(Producto::getId).toList();

        stubUsuarioYProductos(10L, ids, prods);

        Creacion creada = creacionService.crearCreacion(10L, CategoriaCreacion.HAMBURGUESA_BASE, ids);

        assertNotNull(creada);
        assertEquals(usuario, creada.getUsuario());
        assertEquals(prods.size(), creada.getProductos().size());
        verify(creacionRepositorio, times(1)).save(any(Creacion.class));
    }

    /* ===================== Casos de error (PIZZA_BASE) ===================== */

    @Test
    void crearCreacion_PIZZA_BASE_falla_categoriaNoPermitida() {
        // Meter una categoría de HAMBURGUESA_BASE en una creación de PIZZA_BASE
        List<Producto> prods = List.of(
                prod(1, CategoriaProducto.TIPO_MASA, "Masa", "80.00"),
                prod(2, CategoriaProducto.TAMANIO_PIZZA, "Mediana", "120.00"),
                prod(3, CategoriaProducto.TIPO_CARNE, "Carne", "150.00") // NO permitido en PIZZA_BASE
        );
        List<Long> ids = prods.stream().map(Producto::getId).toList();
        stubUsuarioYProductos(10L, ids, prods);

        assertThrows(IllegalArgumentException.class,
                () -> creacionService.crearCreacion(10L, CategoriaCreacion.PIZZA_BASE, ids));
        verify(creacionRepositorio, never()).save(any());
    }

    @Test
    void crearCreacion_PIZZA_BASE_falla_sinMasa() {
        List<Producto> prods = List.of(
                prod(1, CategoriaProducto.TAMANIO_PIZZA, "Mediana", "120.00"),
                prod(2, CategoriaProducto.SALSA_PIZZA, "Tomate", "60.00")
        );
        List<Long> ids = prods.stream().map(Producto::getId).toList();
        stubUsuarioYProductos(10L, ids, prods);

        assertThrows(IllegalArgumentException.class,
                () -> creacionService.crearCreacion(10L, CategoriaCreacion.PIZZA_BASE, ids));
    }

    @Test
    void crearCreacion_PIZZA_BASE_falla_multiplesMasas() {
        List<Producto> prods = List.of(
                prod(1, CategoriaProducto.TIPO_MASA, "Masa A", "80.00"),
                prod(2, CategoriaProducto.TIPO_MASA, "Masa B", "85.00"), // 2 masas
                prod(3, CategoriaProducto.TAMANIO_PIZZA, "Mediana", "120.00")
        );
        List<Long> ids = prods.stream().map(Producto::getId).toList();
        stubUsuarioYProductos(10L, ids, prods);

        assertThrows(IllegalArgumentException.class,
                () -> creacionService.crearCreacion(10L, CategoriaCreacion.PIZZA_BASE, ids));
    }

    @Test
    void crearCreacion_PIZZA_BASE_falla_sinTamanio() {
        List<Producto> prods = List.of(
                prod(1, CategoriaProducto.TIPO_MASA, "Masa", "80.00"),
                prod(2, CategoriaProducto.SALSA_PIZZA, "Tomate", "60.00")
        );
        List<Long> ids = prods.stream().map(Producto::getId).toList();
        stubUsuarioYProductos(10L, ids, prods);

        assertThrows(IllegalArgumentException.class,
                () -> creacionService.crearCreacion(10L, CategoriaCreacion.PIZZA_BASE, ids));
    }

    @Test
    void crearCreacion_PIZZA_BASE_falla_excesoToppings() {
        List<Producto> prods = new ArrayList<>();
        long id = 1;
        prods.add(prod(id++, CategoriaProducto.TIPO_MASA, "Masa", "80.00"));
        prods.add(prod(id++, CategoriaProducto.TAMANIO_PIZZA, "Mediana", "120.00"));
        for (int i = 0; i < 6; i++) { // >5
            prods.add(prod(id++, CategoriaProducto.TOPPING_PIZZA, "Top" + i, "40.00"));
        }
        List<Long> ids = prods.stream().map(Producto::getId).toList();
        stubUsuarioYProductos(10L, ids, prods);

        assertThrows(IllegalArgumentException.class,
                () -> creacionService.crearCreacion(10L, CategoriaCreacion.PIZZA_BASE, ids));
    }

    /* ===================== Casos de error (HAMBURGUESA_BASE) ===================== */

    @Test
    void crearCreacion_HAMBURGUESA_BASE_falla_categoriaNoPermitida() {
        // Meter una categoría de PIZZA_BASE en una creación de HAMBURGUESA_BASE
        List<Producto> prods = List.of(
                prod(1, CategoriaProducto.TIPO_PAN, "Brioche", "70.00"),
                prod(2, CategoriaProducto.SALSA_PIZZA, "Salsa roja", "60.00") // NO permitido en HAMBURGUESA_BASE
        );
        List<Long> ids = prods.stream().map(Producto::getId).toList();
        stubUsuarioYProductos(10L, ids, prods);

        assertThrows(IllegalArgumentException.class,
                () -> creacionService.crearCreacion(10L, CategoriaCreacion.HAMBURGUESA_BASE, ids));
    }

    @Test
    void crearCreacion_HAMBURGUESA_BASE_falla_sinPan() {
        List<Producto> prods = List.of(
                prod(1, CategoriaProducto.TIPO_CARNE, "Carne", "150.00")
        );
        List<Long> ids = prods.stream().map(Producto::getId).toList();
        stubUsuarioYProductos(10L, ids, prods);

        assertThrows(IllegalArgumentException.class,
                () -> creacionService.crearCreacion(10L, CategoriaCreacion.HAMBURGUESA_BASE, ids));
    }

    @Test
    void crearCreacion_HAMBURGUESA_BASE_falla_excesoCarnes() {
        List<Producto> prods = new ArrayList<>();
        long id = 1;
        prods.add(prod(id++, CategoriaProducto.TIPO_PAN, "Pan", "70.00"));
        for (int i = 0; i < 4; i++) { // >3
            prods.add(prod(id++, CategoriaProducto.TIPO_CARNE, "Carne" + i, "150.00"));
        }
        List<Long> ids = prods.stream().map(Producto::getId).toList();
        stubUsuarioYProductos(10L, ids, prods);

        assertThrows(IllegalArgumentException.class,
                () -> creacionService.crearCreacion(10L, CategoriaCreacion.HAMBURGUESA_BASE, ids));
    }

    @Test
    void crearCreacion_HAMBURGUESA_BASE_falla_excesoSalsas() {
        List<Producto> prods = new ArrayList<>();
        long id = 1;
        prods.add(prod(id++, CategoriaProducto.TIPO_PAN, "Pan", "70.00"));
        for (int i = 0; i < 3; i++) { // >2
            prods.add(prod(id++, CategoriaProducto.SALSA_HAMBURGUESA, "Salsa" + i, "40.00"));
        }
        List<Long> ids = prods.stream().map(Producto::getId).toList();
        stubUsuarioYProductos(10L, ids, prods);

        assertThrows(IllegalArgumentException.class,
                () -> creacionService.crearCreacion(10L, CategoriaCreacion.HAMBURGUESA_BASE, ids));
    }

    /* ===================== Casos comunes ===================== */

    @Test
    void crearCreacion_falla_dosBebidas() {
        // Aplica a ambos tipos; probamos con PIZZA_BASE
        List<Producto> prods = List.of(
                prod(1, CategoriaProducto.TIPO_MASA, "Masa", "80.00"),
                prod(2, CategoriaProducto.TAMANIO_PIZZA, "Mediana", "120.00"),
                prod(3, CategoriaProducto.BEBIDA, "Cola", "90.00"),
                prod(4, CategoriaProducto.BEBIDA, "Naranja", "90.00") // 2 bebidas
        );
        List<Long> ids = prods.stream().map(Producto::getId).toList();
        stubUsuarioYProductos(10L, ids, prods);

        assertThrows(IllegalArgumentException.class,
                () -> creacionService.crearCreacion(10L, CategoriaCreacion.PIZZA_BASE, ids));
    }

    @Test
    void crearCreacion_falla_sinProductos() {
        List<Long> ids = List.of();
        stubUsuarioYProductos(10L, ids, List.of());

        assertThrows(IllegalArgumentException.class,
                () -> creacionService.crearCreacion(10L, CategoriaCreacion.PIZZA_BASE, ids));
    }

    @Test
    void crearCreacion_falla_usuarioNoExiste() {
        when(usuarioRepositorio.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> creacionService.crearCreacion(999L, CategoriaCreacion.PIZZA_BASE, List.of(1L, 2L)));
        verify(productoRepositorio, never()).findAllById(any());
        verify(creacionRepositorio, never()).save(any());
    }
}
