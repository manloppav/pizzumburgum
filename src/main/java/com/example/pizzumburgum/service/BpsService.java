package com.example.pizzumburgum.service;

import com.example.pizzumburgum.dto.bps.BpsUsuariosSistemaResponse;
import com.example.pizzumburgum.enums.RolUsuario;
import com.example.pizzumburgum.repository.UsuarioRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class BpsService {

    private final UsuarioRepositorio usuarioRepositorio;

    @Transactional(readOnly = true)
    public BpsUsuariosSistemaResponse obtenerCantidadFuncionariosSistema() {
        long cantidad = usuarioRepositorio.countByRol(RolUsuario.ADMIN);
        return new BpsUsuariosSistemaResponse(LocalDate.now(), cantidad);
    }
}