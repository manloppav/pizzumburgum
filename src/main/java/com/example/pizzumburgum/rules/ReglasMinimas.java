package com.example.pizzumburgum.rules;

import com.example.pizzumburgum.enums.CategoriaProducto;

import java.util.*;

/**
 * Reglas de qué categorías están habilitadas para cada base (pizza o hamburguesa)
 * y cuántos productos de cada tipo se pueden elegir.
 */
public final class ReglasMinimas {

    private ReglasMinimas() {}

    // Conjuntos de categorías habilitadas
    private static final EnumSet<CategoriaProducto> PERMITIDAS_PIZZA = EnumSet.of(
            CategoriaProducto.PIZZA_BASE,
            CategoriaProducto.TIPO_MASA,
            CategoriaProducto.SALSA_PIZZA,
            CategoriaProducto.TAMANIO_PIZZA,
            CategoriaProducto.TOPPING_PIZZA,
            CategoriaProducto.ACOMPANIAMIENTO,
            CategoriaProducto.BEBIDA
    );

    private static final EnumSet<CategoriaProducto> PERMITIDAS_HAMBURGUESA = EnumSet.of(
            CategoriaProducto.HAMBURGUESA_BASE,
            CategoriaProducto.TIPO_PAN,
            CategoriaProducto.TIPO_CARNE,
            CategoriaProducto.TIPO_QUESO,
            CategoriaProducto.SALSA_HAMBURGUESA,
            CategoriaProducto.TOPPING_HAMBURGUESA,
            CategoriaProducto.ACOMPANIAMIENTO,
            CategoriaProducto.BEBIDA
    );

    /** Estructura simple para definir min y max permitidos */
    public record Regla(int min, int max) {}

    // Cardinalidades por categoría de producto para pizza
    private static final Map<CategoriaProducto, Regla> CARDINALIDAD_PIZZA = Map.of(
            CategoriaProducto.TIPO_MASA, new Regla(1, 1),        // una sola masa
            CategoriaProducto.SALSA_PIZZA, new Regla(1, 1),      // una sola salsa
            CategoriaProducto.TAMANIO_PIZZA, new Regla(0, 1),    // opcional
            CategoriaProducto.TOPPING_PIZZA, new Regla(0, 6)     // hasta 6 toppings
    );

    // Cardinalidades para hamburguesa (ejemplo)
    private static final Map<CategoriaProducto, Regla> CARDINALIDAD_HAMBURGUESA = Map.of(
            CategoriaProducto.TIPO_PAN, new Regla(1, 1),
            CategoriaProducto.TIPO_CARNE, new Regla(1, 2),
            CategoriaProducto.TIPO_QUESO, new Regla(0, 1),
            CategoriaProducto.SALSA_HAMBURGUESA, new Regla(0, 2),
            CategoriaProducto.TOPPING_HAMBURGUESA, new Regla(0, 6)
    );

    /** Devuelve el set de categorías habilitadas para esa base. */
    public static Set<CategoriaProducto> categoriasHabilitadas(CategoriaProducto categoriaBase) {
        return switch (categoriaBase) {
            case PIZZA_BASE -> PERMITIDAS_PIZZA;
            case HAMBURGUESA_BASE -> PERMITIDAS_HAMBURGUESA;
            default -> throw new IllegalArgumentException(
                    "Se esperaba una categoría BASE (PIZZA_BASE o HAMBURGUESA_BASE)");
        };
    }

    /** Devuelve las reglas de cardinalidad para cada categoría según la base */
    public static Map<CategoriaProducto, Regla> reglasCardinalidad(CategoriaProducto categoriaBase) {
        return switch (categoriaBase) {
            case PIZZA_BASE -> CARDINALIDAD_PIZZA;
            case HAMBURGUESA_BASE -> CARDINALIDAD_HAMBURGUESA;
            default -> throw new IllegalArgumentException(
                    "Se esperaba una categoría BASE (PIZZA_BASE o HAMBURGUESA_BASE)");
        };
    }
}
