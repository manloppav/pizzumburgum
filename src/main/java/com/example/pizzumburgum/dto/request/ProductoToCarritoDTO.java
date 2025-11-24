package com.example.pizzumburgum.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductoToCarritoDTO {
    @NotNull
    private Long usuarioId;

    @NotNull
    @Min(1)
    private Integer cantidad;
}
