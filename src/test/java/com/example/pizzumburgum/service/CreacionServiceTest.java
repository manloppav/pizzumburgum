package com.example.pizzumburgum.service;

import com.example.pizzumburgum.entities.Creacion;
import com.example.pizzumburgum.entities.Producto;
import com.example.pizzumburgum.entities.Usuario;
import com.example.pizzumburgum.enums.CategoriaProducto;
import com.example.pizzumburgum.repositorio.CreacionRepositorio;
import com.example.pizzumburgum.repositorio.ProductoRepositorio;
import com.example.pizzumburgum.repositorio.UsuarioRepositorio;
import com.example.pizzumburgum.rules.ReglasMinimas;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreacionServiceTest {

    @Mock private CreacionRepositorio creacionRepositorio;
    @Mock private UsuarioRepositorio usuarioRepositorio;
    @Mock private ProductoRepositorio productoRepositorio;

    @InjectMocks
    private CreacionService creacionService;

    private Usuario usuario;
    private Producto basePizza;
    private Producto masaFina;
    private Producto salsaRoja;

    @BeforeEach
    void setup() {
        usuario = new Usuario();
        usuario.setId(10L);

        basePizza = producto(1L, "Pizza base", CategoriaProducto.PIZZA_BASE, new BigDecimal("500.00"));
        masaFina  = producto(2L, "Masa fina",   CategoriaProducto.TIPO_MASA, new BigDecimal("0.00"));
        salsaRoja = producto(3L, "Salsa roja",  CategoriaProducto.SALSA_PIZZA, new BigDecimal("0.00"));
    }

    @Test
    void crearCreacion_ok() {
        when(usuarioRepositorio.findById(10L)).thenReturn(Optional.of(usuario));
        when(productoRepositorio.findAllById(List.of(1L,2L,3L)))
                .thenReturn(List.of(basePizza, masaFina, salsaRoja));

        // simular save devolviendo la misma entidad con id
        when(creacionRepositorio.save(any(Creacion.class))).thenAnswer(inv -> {
            Creacion c = inv.getArgument(0);
            c.setId(100L);
            return c;
        });

        Creacion creada = creacionService.crearCreacion(10L, List.of(1L,2L,3L));

        assertNotNull(creada.getId());
        assertEquals(usuario.getId(), creada.getUsuario().getId());
        assertEquals(3, creada.getProductos().size());

        // opcional: capturar el objeto que se guardó
        ArgumentCaptor<Creacion> captor = ArgumentCaptor.forClass(Creacion.class);
        verify(creacionRepositorio).save(captor.capture());
        assertEquals(3, captor.getValue().getProductos().size());
    }

    @Test
    void crearCreacion_falla_siUsuarioNoExiste() {
        when(usuarioRepositorio.findById(999L)).thenReturn(Optional.empty());

        var ex = assertThrows(IllegalArgumentException.class,
                () -> creacionService.crearCreacion(999L, List.of(1L)));
        assertTrue(ex.getMessage().contains("Usuario no encontrado"));
        verifyNoInteractions(productoRepositorio, creacionRepositorio);
    }

    @Test
    void crearCreacion_falla_siListaProductosVacia() {
        when(usuarioRepositorio.findById(10L)).thenReturn(Optional.of(usuario));
        when(productoRepositorio.findAllById(List.of())).thenReturn(List.of());

        var ex = assertThrows(IllegalArgumentException.class,
                () -> creacionService.crearCreacion(10L, List.of()));
        assertTrue(ex.getMessage().contains("al menos un producto"));
        verify(creacionRepositorio, never()).save(any());
    }

    @Test
    void crearCreacion_falla_siNoHayProductoBase() {
        when(usuarioRepositorio.findById(10L)).thenReturn(Optional.of(usuario));
        // productos sin PIZZA_BASE / HAMBURGUESA_BASE
        when(productoRepositorio.findAllById(List.of(2L,3L))).thenReturn(List.of(masaFina, salsaRoja));

        var ex = assertThrows(IllegalArgumentException.class,
                () -> creacionService.crearCreacion(10L, List.of(2L,3L)));
        assertTrue(ex.getMessage().toLowerCase().contains("producto base"));
        verify(creacionRepositorio, never()).save(any());
    }

    // helper
    private static Producto producto(Long id, String nombre, CategoriaProducto cat, BigDecimal precio) {
        Producto p = new Producto();
        p.setId(id);
        p.setNombre(nombre);
        p.setCategoria(cat);
        p.setPrecio(precio);
        return p;
    }
}
