package com.example.pizzumburgum.service;

import com.example.pizzumburgum.entities.Carrito;
import com.example.pizzumburgum.entities.Creacion;
import com.example.pizzumburgum.repositorio.*;
import com.example.pizzumburgum.entities.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;


@Service
@AllArgsConstructor
public class CarritoService {

    private final UsuarioRepositorio usuarioRepositorio;
    private final CreacionRepositorio creacionRepositorio;
    private final CarritoRepositorio carritoRepositorio;

    @Transactional
    public Carrito crearCarrito(Long usuarioId, Long creacionId) {
        // 1) Traer usuario y creación
        Usuario usuario = usuarioRepositorio.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + usuarioId));

        Creacion creacion = creacionRepositorio.findById(creacionId)
                .orElseThrow(() -> new IllegalArgumentException("Creación no encontrada: " + creacionId));

        // 2) Obtener (o crear) el carrito 1:1 del usuario
        Carrito carrito = carritoRepositorio.findByUsuarioId(usuarioId)
                .orElseGet(() -> {
                    Carrito nuevo = new Carrito();
                    nuevo.setUsuario(usuario);
                    // si usás estado (ABIERTO/TEMPORAL), setealo acá
                    // nuevo.setEstado(EstadoCarrito.ABIERTO);
                    return nuevo;
                });

        // 3) Crear el ítem que referencia la Creación
        CarritoItem item = new CarritoItem();
        item.setCarrito(carrito);
        item.setCreacion(creacion);

        // 4) Agregar el ítem a la colección del carrito (inicializar si hace falta)
        if (carrito.getItems() == null) {
            carrito.setItems(new ArrayList<>());
        }
        carrito.getItems().add(item);

        // 5) Guardar (asegurate que Carrito.items tenga cascade = CascadeType.ALL, orphanRemoval = true)
        return carritoRepositorio.save(carrito);
    }

}
