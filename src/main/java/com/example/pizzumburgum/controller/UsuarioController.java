package com.example.pizzumburgum.controller;

import com.example.pizzumburgum.dto.request.DireccionDTO;
import com.example.pizzumburgum.dto.request.TarjetaDTO;
import com.example.pizzumburgum.entities.Usuario;
import com.example.pizzumburgum.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PutMapping("/{id}/direccion")
    public ResponseEntity<Usuario> cambiarDireccion(@PathVariable Long id,
                                                    @RequestBody @Valid DireccionDTO body) {
        Usuario u = usuarioService.cambiarDireccion(id, body);
        return ResponseEntity.ok(u);
    }

    @PutMapping("/{id}/tarjeta")
    public ResponseEntity<Usuario> cambiarTarjeta(@PathVariable Long id,
                                                  @RequestBody @Valid TarjetaDTO body) {
        Usuario u = usuarioService.cambiarTarjeta(id, body);
        return ResponseEntity.ok(u);
    }
}