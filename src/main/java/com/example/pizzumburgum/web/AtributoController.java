package com.example.pizzumburgum.web;

import com.example.pizzumburgum.producto.Atributo;
import com.example.pizzumburgum.producto.AtributoDTO;
import com.example.pizzumburgum.producto.AtributoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/atributos")
@RequiredArgsConstructor
@Slf4j
public class AtributoController {

    private final AtributoService atributoService;

    @PostMapping
    public ResponseEntity<Atributo> crearAtributo(
            @Valid @RequestBody AtributoDTO dto,
            @RequestParam Long funcionarioId) {

        log.info("Request POST /api/atributos - funcionario: {}, nombre: {}",
                funcionarioId, dto.getNombre());

        try {
            Atributo atributo = atributoService.crearAtributo(dto, funcionarioId);
            return ResponseEntity.status(HttpStatus.CREATED).body(atributo);
        } catch (IllegalArgumentException e) {
            log.error("Error al crear atributo: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarAtributo(
            @PathVariable Long id,
            @RequestParam Long funcionarioId) {

        log.info("Request DELETE /api/atributos/{} - funcionario: {}", id, funcionarioId);

        try {
            atributoService.eliminarAtributo(id, funcionarioId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("Error al eliminar atributo: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/activos")
    public ResponseEntity<List<Atributo>> obtenerActivos() {
        log.info("Request GET /api/atributos/activos");
        List<Atributo> atributos = atributoService.obtenerAtributosActivos();
        return ResponseEntity.ok(atributos);
    }

    @GetMapping("/funcionario/{funcionarioId}")
    public ResponseEntity<List<Atributo>> obtenerPorFuncionario(
            @PathVariable Long funcionarioId) {

        log.info("Request GET /api/atributos/funcionario/{}", funcionarioId);
        List<Atributo> atributos = atributoService.obtenerAtributosPorFuncionario(funcionarioId);
        return ResponseEntity.ok(atributos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Atributo> obtenerPorId(@PathVariable Long id) {
        log.info("Request GET /api/atributos/{}", id);

        try {
            Atributo atributo = atributoService.obtenerPorId(id);
            return ResponseEntity.ok(atributo);
        } catch (IllegalArgumentException e) {
            log.error("Atributo no encontrado: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Atributo>> obtenerTodos() {
        log.info("Request GET /api/atributos");
        List<Atributo> atributos = atributoService.obtenerTodos();
        return ResponseEntity.ok(atributos);
    }
}