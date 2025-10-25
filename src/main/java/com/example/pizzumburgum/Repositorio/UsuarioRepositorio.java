package com.example.pizzumburgum.Repositorio;

import com.example.pizzumburgum.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {
}