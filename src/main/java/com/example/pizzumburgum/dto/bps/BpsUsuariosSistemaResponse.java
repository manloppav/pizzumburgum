package com.example.pizzumburgum.dto.bps;

import java.time.LocalDate;

public record BpsUsuariosSistemaResponse(
        LocalDate fechaConsulta,
        long cantidadFuncionarios
) {}