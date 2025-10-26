package com.example.pizzumburgum.service;

import com.example.pizzumburgum.entities.*;
import com.example.pizzumburgum.repositorio.CarritoRepositorio;
import com.example.pizzumburgum.repositorio.CreacionRepositorio;
import com.example.pizzumburgum.repositorio.UsuarioRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarritoServiceTest {

    @Mock private CarritoRepositorio carritoRepositorio;
    @Mock private UsuarioRepositorio usuarioRepositorio;
    @Mock private CreacionRepositorio creacionRepositorio;

    @InjectMocks
    private CarritoService carritoService;

    private Usuario usuario;
    private Creacion creacion;

    @BeforeEach
    void setup() {
        usuario = new Usuario();
        usuario.setId(10L);

        creacion = new Creacion();
        creacion.setId(77L);
        creacion.setProductos(new ArrayList<>());
    }

    @Test
    void crearCarrito_creaNuevoSiNoExiste() {
        // Preparar mocks
        when(usuarioRepositorio.findById(10L)).thenReturn(Optional.of(usuario));
        when(creacionRepositorio.findById(77L)).thenReturn(Optional.of(creacion));
        when(carritoRepositorio.findByUsuarioId(10L)).thenReturn(Optional.empty());

        when(carritoRepositorio.save(any(Carrito.class)))
                .thenAnswer(inv -> {
                    Carrito c = inv.getArgument(0);
                    c.setId(200L);
                    return c;
                });

        // Ejecutar
        Carrito carrito = carritoService.crearCarrito(10L, 77L);

        // Verificar resultados
        assertNotNull(carrito.getId());
        assertEquals(10L, carrito.getUsuario().getId());
        assertEquals(1, carrito.getItems().size());
        assertEquals(77L, carrito.getItems().get(0).getCreacion().getId());

        ArgumentCaptor<Carrito> captor = ArgumentCaptor.forClass(Carrito.class);
        verify(carritoRepositorio).save(captor.capture());
        assertEquals(1, captor.getValue().getItems().size());
    }

    @Test
    void crearCarrito_reutilizaExistenteYAgragaItem() {
        // Preparar mocks
        when(usuarioRepositorio.findById(10L)).thenReturn(Optional.of(usuario));
        when(creacionRepositorio.findById(77L)).thenReturn(Optional.of(creacion));

        Carrito existente = new Carrito();
        existente.setId(123L);
        existente.setUsuario(usuario);
        existente.setItems(new ArrayList<>(List.of(new CarritoItem())));

        when(carritoRepositorio.findByUsuarioId(10L)).thenReturn(Optional.of(existente));
        when(carritoRepositorio.save(any(Carrito.class))).thenAnswer(inv -> inv.getArgument(0));

        // Ejecutar
        Carrito carrito = carritoService.crearCarrito(10L, 77L);

        // Verificar resultados
        assertEquals(123L, carrito.getId());
        assertEquals(2, carrito.getItems().size());
        verify(carritoRepositorio).save(carrito);
    }

    @Test
    void crearCarrito_falla_siUsuarioNoExiste() {
        when(usuarioRepositorio.findById(999L)).thenReturn(Optional.empty());

        var ex = assertThrows(IllegalArgumentException.class,
                () -> carritoService.crearCarrito(999L, 77L));

        assertTrue(ex.getMessage().contains("Usuario no encontrado"));
        verifyNoInteractions(creacionRepositorio, carritoRepositorio);
    }

    @Test
    void crearCarrito_falla_siCreacionNoExiste() {
        when(usuarioRepositorio.findById(10L)).thenReturn(Optional.of(usuario));
        when(creacionRepositorio.findById(555L)).thenReturn(Optional.empty());

        var ex = assertThrows(IllegalArgumentException.class,
                () -> carritoService.crearCarrito(10L, 555L));

        assertTrue(ex.getMessage().contains("Creación no encontrada"));
        verify(carritoRepositorio, never()).save(any());
    }
}
