package com.example.pizzumburgum.dto.request;

import com.example.pizzumburgum.enums.CategoriaProducto;
import lombok.Data;

@Data
public class ProductoPatchDTO {
    private String nombre;            // 2..150
    private String imagenUrl;         // <=500
    private CategoriaProducto categoria;
    // (No incluye precio ni descripción: se actualizan con métodos dedicados)
}

