package com.example.pizzumburgum.controller;

import com.example.pizzumburgum.dto.request.ProductoPatchDTO; // movelo si cambias el paquete
import com.example.pizzumburgum.entities.Producto;
import com.example.pizzumburgum.enums.CategoriaProducto;
import com.example.pizzumburgum.service.ProductoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.*;

/**
 * Controlador de productos con soporte de alta individual y masiva (bulk).
 * - Crea 1: POST /api/productos
 * - Crea varios: POST /api/productos/bulk
 * - GET por id: GET /api/productos/{id}
 * - PATCH parcial (nombre, imagenUrl, categoria): PATCH /api/productos/{id}
 * - PUT precio: PUT /api/productos/{id}/precio
 * - PUT descripcion: PUT /api/productos/{id}/descripcion
 */
@RestController
@RequestMapping("/api/productos")
@Validated
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    /* ============== Crear uno ============== */
    @PostMapping
    public ResponseEntity<Producto> crear(@RequestBody @Valid Producto body) {
        Producto creado = productoService.crearProducto(body);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creado.getId())
                .toUri();

        return ResponseEntity.created(location).body(creado); // 201 Created
    }

    /* ============== Crear varios (bulk) ============== */
    @PostMapping("/bulk")
    public ResponseEntity<?> crearVarios(@RequestBody @NotEmpty List<@Valid Producto> productos) {
        // Si querés éxitos parciales, podés usar la versión "segura" (comentada abajo).
        List<Producto> creados = productoService.crearProductos(productos);

        // Podríamos devolver 201 sin Location (porque son varios)
        return ResponseEntity.status(HttpStatus.CREATED).body(creados);
        /*
        // === Versión con éxitos parciales (usa crearProductosSeguro en el service) ===
        var res = productoService.crearProductosSeguro(productos);
        if (res.errores().isEmpty()) return ResponseEntity.status(HttpStatus.CREATED).body(res.creados());
        if (res.creados().isEmpty())  return ResponseEntity.badRequest().body(res);
        return ResponseEntity.status(207).body(res); // MULTI_STATUS
        */
    }

    /* ============== GET por id ============== */
    @GetMapping("/{id}")
    public ResponseEntity<Producto> porId(@PathVariable Long id) {
        return productoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /* ============== PATCH parcial (nombre/imagenUrl/categoria) ============== */
    @PatchMapping("/{id}")
    public ResponseEntity<Producto> patchAtributos(@PathVariable Long id,
                                                   @RequestBody Map<String, Object> body) {
        ProductoPatchDTO patch = new ProductoPatchDTO();

        if (body.containsKey("nombre")) {
            patch.setNombre(Objects.toString(body.get("nombre"), null));
        }
        if (body.containsKey("imagenUrl")) {
            patch.setImagenUrl(Objects.toString(body.get("imagenUrl"), null));
        }
        if (body.containsKey("categoria")) {
            String raw = Objects.toString(body.get("categoria"), null);
            if (raw != null) {
                try {
                    patch.setCategoria(CategoriaProducto.valueOf(raw.toUpperCase(Locale.ROOT)));
                } catch (IllegalArgumentException ex) {
                    throw new IllegalArgumentException("Categoria inválida: " + raw +
                            ". Valores permitidos: " + Arrays.toString(CategoriaProducto.values()));
                }
            }
        }

        Producto actualizado = productoService.actualizarAtributos(id, patch);
        return ResponseEntity.ok(actualizado);
    }

    /* ============== PUTs específicos ============== */
    @PutMapping("/{id}/precio")
    public ResponseEntity<Producto> actualizarPrecio(@PathVariable Long id,
                                                     @RequestBody Map<String, Object> body) {
        Object precioObj = body.get("precio");
        if (precioObj == null) {
            throw new IllegalArgumentException("Campo 'precio' es obligatorio");
        }
        BigDecimal precio;
        try {
            precio = new BigDecimal(String.valueOf(precioObj));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Formato de 'precio' inválido: " + precioObj);
        }

        Producto actualizado = productoService.actualizarPrecio(id, precio);
        return ResponseEntity.ok(actualizado);
    }

    @PutMapping("/{id}/descripcion")
    public ResponseEntity<Producto> actualizarDescripcion(@PathVariable Long id,
                                                          @RequestBody Map<String, Object> body) {
        String descripcion = Objects.toString(body.get("descripcion"), null);
        if (descripcion == null) {
            throw new IllegalArgumentException("Campo 'descripcion' es obligatorio");
        }
        Producto actualizado = productoService.actualizarDescripcion(id, descripcion);
        return ResponseEntity.ok(actualizado);
    }

    /* ============== Manejo de errores legibles ============== */

    @ExceptionHandler({
            IllegalArgumentException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<Map<String, Object>> handleBadRequest(Exception ex) {
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("status", 400);
        resp.put("error", "Bad Request");
        resp.put("message", ex.getMessage());
        return ResponseEntity.badRequest().body(resp);
    }

    @ExceptionHandler({ MethodArgumentNotValidException.class, BindException.class })
    public ResponseEntity<Map<String, Object>> handleValidation(Exception ex) {
        Map<String, Object> resp = new LinkedHashMap<>();
        resp.put("status", 400);
        resp.put("error", "Validation Failed");

        List<Map<String, String>> details = new ArrayList<>();
        if (ex instanceof MethodArgumentNotValidException manv) {
            manv.getBindingResult().getFieldErrors().forEach(fe -> {
                Map<String, String> d = new HashMap<>();
                d.put("field", fe.getField());
                d.put("message", fe.getDefaultMessage());
                details.add(d);
            });
        } else if (ex instanceof BindException be) {
            be.getBindingResult().getFieldErrors().forEach(fe -> {
                Map<String, String> d = new HashMap<>();
                d.put("field", fe.getField());
                d.put("message", fe.getDefaultMessage());
                details.add(d);
            });
        }
        resp.put("details", details);
        return new ResponseEntity<>(resp, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }
}
