package com.example.pizzumburgum.dto.request;

import com.example.pizzumburgum.enums.CategoriaProducto;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductoSimpleDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private CategoriaProducto categoria;

}
