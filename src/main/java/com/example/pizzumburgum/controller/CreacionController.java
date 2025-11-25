package com.example.pizzumburgum.controller;

import com.example.pizzumburgum.dto.request.CreacionDTO;
import com.example.pizzumburgum.dto.request.CreacionRequestDTO;
import com.example.pizzumburgum.entities.Creacion;
import com.example.pizzumburgum.security.CustomUserDetails;
import com.example.pizzumburgum.service.CreacionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.*;

@RestController
@RequestMapping("/api/creaciones")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CreacionController {

    private final CreacionService creacionService;

    public CreacionController(CreacionService creacionService) {
        this.creacionService = creacionService;
    }

    /**
     * ================== Crear una creación ==================
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    public ResponseEntity<Creacion> crear(@RequestBody @Valid CreacionRequestDTO dto) {
        Creacion creada = creacionService.crearCreacion(
                dto.getUsuarioId(),
                dto.getNombre(),
                dto.getDescripcion(),
                dto.getImagenUrl(),
                dto.getCategoriaCreacion(),
                dto.getProductoIds()
        );

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creada.getId())
                .toUri();

        return ResponseEntity.created(location).body(creada);
    }

    /**
     * ================== Obtener una creación por ID ==================
     */
    @GetMapping("/{id}")
    public ResponseEntity<Creacion> obtenerPorId(@PathVariable Long id) {
        return creacionService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /* ================== Manejo simple de errores ================== */

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", 400);
        body.put("error", "Bad Request");
        body.put("message", ex.getMessage());
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", 400);
        body.put("error", "Validation Failed");

        List<Map<String, String>> details = new ArrayList<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            Map<String, String> d = new HashMap<>();
            d.put("field", fe.getField());
            d.put("message", fe.getDefaultMessage());
            details.add(d);
        }
        body.put("details", details);

        return new ResponseEntity<>(body, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleUnreadable(HttpMessageNotReadableException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", 400);
        body.put("error", "Bad Request");
        body.put("message", "JSON inválido o malformado");
        return ResponseEntity.badRequest().body(body);
    }

    @GetMapping("/{id}/detallada")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    public ResponseEntity<CreacionDTO> obtenerCreacion(@PathVariable Long id) {
        CreacionDTO creacion = creacionService.obtenerCreacionConDetalles(id);
        return ResponseEntity.ok(creacion);
    }

    @GetMapping("/mis-creaciones")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    public ResponseEntity<List<CreacionDTO>> listarMisCreaciones() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        List<CreacionDTO> creaciones = creacionService.listarCreacionesDeUsuario(userDetails.getId());
        return ResponseEntity.ok(creaciones);
    }

    @PatchMapping("/{id}/favorita")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    public ResponseEntity<CreacionDTO> actualizarFavorita(
            @PathVariable Long id,
            @RequestParam boolean favorita
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        CreacionDTO dto = creacionService.actualizarFavorita(id, userDetails.getId(), favorita);
        return ResponseEntity.ok(dto);
    }

}
