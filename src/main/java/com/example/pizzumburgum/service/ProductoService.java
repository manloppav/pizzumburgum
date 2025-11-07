package com.example.pizzumburgum.service;

import com.example.pizzumburgum.dto.request.ProductoPatchDTO;
import com.example.pizzumburgum.entities.Producto;
import com.example.pizzumburgum.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

@Service
public class ProductoService {

    private final ProductoRepositorio productoRepositorio;

    public ProductoService(ProductoRepositorio productoRepositorio) {
        this.productoRepositorio = productoRepositorio;
    }

    /* ============ QUERIES ============ */
    public java.util.Optional<Producto> buscarPorId(Long id) {
        return productoRepositorio.findById(id);
    }

    /* ============ CREAR (uno) ============ */
    @Transactional
    public Producto crearProducto(Producto nuevo) {
        if (nuevo == null) throw new IllegalArgumentException("Producto nulo");
        validarProductoBasico(nuevo);
        normalizarProducto(nuevo);
        return productoRepositorio.save(nuevo);
    }

    /* ============ CREAR (varios / bulk) ============ */
    @Transactional
    public List<Producto> crearProductos(List<Producto> productos) {
        if (productos == null || productos.isEmpty()) {
            throw new IllegalArgumentException("La lista de productos no puede estar vacía");
        }
        // Validación + normalización por ítem
        for (Producto p : productos) {
            if (p == null) throw new IllegalArgumentException("Hay un producto nulo en el batch");
            validarProductoBasico(p);
            normalizarProducto(p);
        }
        return productoRepositorio.saveAll(productos);
    }

    /* ============ PATCH (nombre, imagenUrl, categoria) ============ */
    @Transactional
    public Producto actualizarAtributos(Long id, ProductoPatchDTO patch) {
        Producto p = productoRepositorio.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado: " + id));

        if (patch == null) return p;

        if (patch.getNombre() != null) {
            validarNombre(patch.getNombre());
            p.setNombre(patch.getNombre().trim());
        }
        if (patch.getImagenUrl() != null) {
            validarImagenUrl(patch.getImagenUrl());
            p.setImagenUrl(patch.getImagenUrl().trim());
        }
        if (patch.getCategoria() != null) {
            p.setCategoria(patch.getCategoria());
        }

        normalizarProducto(p);
        return productoRepositorio.save(p);
    }

    /* ============ ACTUALIZAR PRECIO ============ */
    @Transactional
    public Producto actualizarPrecio(Long id, BigDecimal nuevoPrecio) {
        Producto p = productoRepositorio.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado: " + id));

        validarPrecio(nuevoPrecio);
        // Normalizamos a 2 decimales por consistencia con la columna (scale = 2)
        p.setPrecio(nuevoPrecio.setScale(2, RoundingMode.HALF_UP));
        return productoRepositorio.save(p);
    }

    /* ============ ACTUALIZAR DESCRIPCIÓN ============ */
    @Transactional
    public Producto actualizarDescripcion(Long id, String nuevaDescripcion) {
        Producto p = productoRepositorio.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado: " + id));

        validarDescripcion(nuevaDescripcion);
        p.setDescripcion(nuevaDescripcion == null ? null : nuevaDescripcion.trim());
        normalizarProducto(p);
        return productoRepositorio.save(p);
    }

    /* ============ Helpers de validación ============ */

    /** Valida campos obligatorios y coherencia base antes de persistir. */
    private void validarProductoBasico(Producto p) {
        validarPrecio(p.getPrecio());
        validarNombre(p.getNombre());
        validarDescripcion(p.getDescripcion());
        validarImagenUrl(p.getImagenUrl());
        if (p.getCategoria() == null) {
            throw new IllegalArgumentException("La categoría es obligatoria");
        }
    }

    private void validarPrecio(BigDecimal precio) {
        if (precio == null) throw new IllegalArgumentException("El precio es obligatorio");
        // Permitimos más de 2 decimales en input y normalizamos luego, pero validamos límites
        if (precio.compareTo(new BigDecimal("0.01")) < 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0");
        }
        // Límite de enteros: precision 10, scale 2 -> hasta 8 enteros
        BigDecimal abs = precio.abs();
        int enteros = abs.precision() - abs.scale();
        if (enteros > 8) {
            throw new IllegalArgumentException("El precio excede el tamaño permitido (máx 8 enteros)");
        }
    }

    private void validarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        String n = nombre.trim();
        if (n.length() < 2 || n.length() > 150) {
            throw new IllegalArgumentException("El nombre debe tener entre 2 y 150 caracteres");
        }
    }

    private void validarDescripcion(String descripcion) {
        if (descripcion == null) return; // opcional
        String d = descripcion.trim();
        if (d.length() > 1000) {
            throw new IllegalArgumentException("La descripción no puede exceder 1000 caracteres");
        }
    }

    private void validarImagenUrl(String url) {
        if (url == null) return; // opcional
        String u = url.trim();
        if (u.length() > 500) {
            throw new IllegalArgumentException("La URL de imagen no puede exceder 500 caracteres");
        }
    }

    /* ============ Normalización consistente antes de guardar ============ */

    /** Aplica trims y asegura escala 2 en precio. */
    private void normalizarProducto(Producto p) {
        if (p.getNombre() != null)      p.setNombre(p.getNombre().trim());
        if (p.getDescripcion() != null) p.setDescripcion(trimOrNull(p.getDescripcion()));
        if (p.getImagenUrl() != null)   p.setImagenUrl(trimOrNull(p.getImagenUrl()));
        if (p.getPrecio() != null)      p.setPrecio(p.getPrecio().setScale(2, RoundingMode.HALF_UP));
    }

    private String trimOrNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
