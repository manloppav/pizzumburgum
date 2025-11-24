package com.example.pizzumburgum.dto.request;

import com.example.pizzumburgum.enums.CategoriaCreacion;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class CreacionRequestDTO {

    @NotNull(message = "El ID del usuario es obligatorio")
    private Long usuarioId;

    @NotNull(message = "La categoría de la creación es obligatoria (PIZZA o HAMBURGUESA)")
    private CategoriaCreacion categoriaCreacion;

    @NotEmpty(message = "Debe incluir al menos un producto")
    private List<Long> productoIds;

    // opcionales (tu entidad los soporta)
    private String nombre;       // si viene vacío, se autogenera
    private String descripcion;
    private String imagenUrl;
}
