package com.example.pizzumburgum.dto.request;

import com.example.pizzumburgum.enums.CantidadCarnes;
import com.example.pizzumburgum.enums.TamanoPizza;
import com.example.pizzumburgum.enums.TipoMasa;
import com.example.pizzumburgum.enums.TipoQueso;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemCarritoRequestDTO {

    @NotNull(message = "El ID del producto es obligatorio")
    private Long productoId;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;

    // Configuración para Pizzas
    private TamanoPizza tamanioPizza;
    private TipoMasa tipoMasa;
    private TipoQueso tipoQueso;
    private List<Long> toppingsPizzaIds;

    // Configuración para Hamburguesas
    private CantidadCarnes cantidadCarnes;
    private List<Long> toppingsHamburguesaIds;
    private List<Long> aderezosIds;

    @Size(max = 500)
    private String observaciones;
}