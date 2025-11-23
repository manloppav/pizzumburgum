package com.example.pizzumburgum.repositorio;

import com.example.pizzumburgum.entities.Usuario;
import com.example.pizzumburgum.enums.RolUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {

    long countByRol(RolUsuario rol);
}