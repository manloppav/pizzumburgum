package com.example.pizzumburgum.service;

import com.example.pizzumburgum.dto.request.ProductoPatchDTO;
import com.example.pizzumburgum.entities.Producto;
import com.example.pizzumburgum.enums.CategoriaProducto;
import com.example.pizzumburgum.repository.ProductoRepositorio;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepositorio productoRepositorio;

    @InjectMocks
    private ProductoService productoService;

    @BeforeEach
    void setup() {
        lenient().when(productoRepositorio.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    /* =================== CREAR =================== */

    @Test
    void crearProducto_ok() {
        Producto p = baseProducto(null);
        when(productoRepositorio.save(any())).thenAnswer(inv -> {
            Producto saved = inv.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        Producto creado = productoService.crearProducto(p);

        assertThat(creado.getId()).isEqualTo(1L);
        assertThat(creado.getNombre()).isEqualTo("Masa Tradicional");
        verify(productoRepositorio).save(any(Producto.class));
    }

    @Test
    void crearProducto_precioInvalido_lanzaExcepcion() {
        Producto p = baseProducto(null);
        p.setPrecio(new BigDecimal("0.0001"));

        assertThatThrownBy(() -> productoService.crearProducto(p))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("precio");
        verify(productoRepositorio, never()).save(any());
    }

    @Test
    void crearProducto_sinCategoria_lanzaExcepcion() {
        Producto p = baseProducto(null);
        p.setCategoria(null);

        assertThatThrownBy(() -> productoService.crearProducto(p))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("categoría");
        verify(productoRepositorio, never()).save(any());
    }

    /* =================== PATCH =================== */

    @Test
    void actualizarAtributos_ok() {
        Producto existente = baseProducto(5L);
        when(productoRepositorio.findById(5L)).thenReturn(Optional.of(existente));

        ProductoPatchDTO patch = new ProductoPatchDTO();
        patch.setNombre("Masa Fina");
        patch.setImagenUrl("https://cdn/masa_fina.jpg");
        patch.setCategoria(CategoriaProducto.TIPO_MASA);

        Producto actualizado = productoService.actualizarAtributos(5L, patch);

        assertThat(actualizado.getNombre()).isEqualTo("Masa Fina");
        assertThat(actualizado.getImagenUrl()).isEqualTo("https://cdn/masa_fina.jpg");
        assertThat(actualizado.getCategoria()).isEqualTo(CategoriaProducto.TIPO_MASA);
        verify(productoRepositorio).save(any(Producto.class));
    }

    @Test
    void actualizarAtributos_productoNoExiste_lanzaEntityNotFound() {
        when(productoRepositorio.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productoService.actualizarAtributos(99L, new ProductoPatchDTO()))
                .isInstanceOf(EntityNotFoundException.class);
        verify(productoRepositorio, never()).save(any());
    }

    @Test
    void actualizarAtributos_nombreInvalido_lanzaExcepcion() {
        Producto existente = baseProducto(7L);
        when(productoRepositorio.findById(7L)).thenReturn(Optional.of(existente));

        ProductoPatchDTO patch = new ProductoPatchDTO();
        patch.setNombre("A");

        assertThatThrownBy(() -> productoService.actualizarAtributos(7L, patch))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nombre");
        verify(productoRepositorio, never()).save(any());
    }

    /* =================== PRECIO =================== */

    @Test
    void actualizarPrecio_ok() {
        Producto existente = baseProducto(10L);
        when(productoRepositorio.findById(10L)).thenReturn(Optional.of(existente));

        Producto actualizado = productoService.actualizarPrecio(10L, new BigDecimal("299.00"));

        assertThat(actualizado.getPrecio()).isEqualByComparingTo("299.00");
        verify(productoRepositorio).save(any(Producto.class));
    }

    @Test
    void actualizarPrecio_decimalesInvalidos_lanzaExcepcion() {
        Producto existente = baseProducto(11L);
        when(productoRepositorio.findById(11L)).thenReturn(Optional.of(existente));

        assertThatThrownBy(() -> productoService.actualizarPrecio(11L, new BigDecimal("10.999")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("2 decimales");
        verify(productoRepositorio, never()).save(any());
    }

    @Test
    void actualizarPrecio_productoNoExiste_lanzaEntityNotFound() {
        when(productoRepositorio.findById(123L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productoService.actualizarPrecio(123L, new BigDecimal("100.00")))
                .isInstanceOf(EntityNotFoundException.class);
        verify(productoRepositorio, never()).save(any());
    }

    /* =================== DESCRIPCIÓN =================== */

    @Test
    void actualizarDescripcion_ok() {
        Producto existente = baseProducto(20L);
        when(productoRepositorio.findById(20L)).thenReturn(Optional.of(existente));

        Producto actualizado = productoService.actualizarDescripcion(20L, "Nueva descripción");
        assertThat(actualizado.getDescripcion()).isEqualTo("Nueva descripción");
        verify(productoRepositorio).save(any(Producto.class));
    }

    @Test
    void actualizarDescripcion_demasiadoLarga_lanzaExcepcion() {
        Producto existente = baseProducto(21L);
        when(productoRepositorio.findById(21L)).thenReturn(Optional.of(existente));

        String muyLarga = "x".repeat(1001);
        assertThatThrownBy(() -> productoService.actualizarDescripcion(21L, muyLarga))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("1000");
        verify(productoRepositorio, never()).save(any());
    }

    @Test
    void actualizarDescripcion_productoNoExiste_lanzaEntityNotFound() {
        when(productoRepositorio.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productoService.actualizarDescripcion(404L, "desc"))
                .isInstanceOf(EntityNotFoundException.class);
        verify(productoRepositorio, never()).save(any());
    }

    /* =================== Helper =================== */
    private Producto baseProducto(Long id) {
        Producto p = new Producto();
        p.setId(id);
        p.setNombre("Masa Tradicional");
        p.setDescripcion("Masa clásica italiana");
        p.setPrecio(new BigDecimal("85.00"));
        p.setCategoria(CategoriaProducto.TIPO_MASA);
        p.setImagenUrl("https://cdn/masa_tradicional.jpg");
        return p;
    }
}
