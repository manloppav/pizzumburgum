package com.example.pizzumburgum.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarritoRequestDTO {

    @NotNull(message = "El ID del cliente es obligatorio")
    private Long clienteId;
}