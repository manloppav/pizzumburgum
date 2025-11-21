package com.example.pizzumburgum.dto.request;

import com.example.pizzumburgum.enums.CategoriaCreacion;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CreacionDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private String imagenUrl;
    private CategoriaCreacion categoriaCreacion;
    private BigDecimal precioTotal;
    private List<ProductoSimpleDTO> productos;
    private String nombreUsuario;

}
