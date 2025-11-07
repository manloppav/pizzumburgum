package com.example.pizzumburgum.controller;

import com.example.pizzumburgum.entities.Tarjeta;
import com.example.pizzumburgum.repositorio.TarjetaRepositorio;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tarjetas")
public class TarjetaController {

    private final TarjetaRepositorio tarjetaRepositorio;

    public TarjetaController(TarjetaRepositorio tarjetaRepositorio) {
        this.tarjetaRepositorio = tarjetaRepositorio;
    }

    // Obtener todas las tarjetas
    @GetMapping
    public List<Tarjeta> listar() {
        return tarjetaRepositorio.findAll();
    }

    // Crear una nueva tarjeta
    @PostMapping
    public ResponseEntity<Tarjeta> crear(@RequestBody Tarjeta tarjeta) {
        Tarjeta guardada = tarjetaRepositorio.save(tarjeta);
        return ResponseEntity.ok(guardada);
    }

    // Buscar tarjetas por usuario
    @GetMapping("/usuario/{usuarioId}")
    public List<Tarjeta> buscarPorUsuario(@PathVariable Long usuarioId) {
        return tarjetaRepositorio.findByUsuarioId(usuarioId);
    }

    // Verificar si existe por token
    @GetMapping("/existe/{token}")
    public ResponseEntity<Boolean> existePorToken(@PathVariable String token) {
        boolean existe = tarjetaRepositorio.existsByToken(token);
        return ResponseEntity.ok(existe);
    }
}
