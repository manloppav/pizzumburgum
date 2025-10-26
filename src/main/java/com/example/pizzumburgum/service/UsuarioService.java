package com.example.pizzumburgum.service;

import com.example.pizzumburgum.entities.Usuario;
import com.example.pizzumburgum.repositorio.UsuarioRepositorio;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepositorio usuarioRepositorio;

    public UsuarioService(UsuarioRepositorio usuarioRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepositorio.findById(id);
    }
}