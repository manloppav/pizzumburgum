package com.example.pizzumburgum.controller;

import com.example.pizzumburgum.dto.request.CrearPedidoDTO;
import com.example.pizzumburgum.dto.request.PedidoCrearRequestDTO;
import com.example.pizzumburgum.dto.request.PedidoDTO;
import com.example.pizzumburgum.entities.Pedido;
import com.example.pizzumburgum.enums.EstadoPedido;
import com.example.pizzumburgum.security.CustomUserDetails;
import com.example.pizzumburgum.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class PedidoController {

    private final PedidoService pedidoService;

    // ============= ENDPOINTS PARA ADMIN =============

    @GetMapping("/admin/todos")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PedidoDTO>> listarTodosPedidos() {
        List<PedidoDTO> pedidos = pedidoService.listarTodosPedidos();
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/admin/fecha/{fecha}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> listarPedidosPorFecha(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        List<PedidoDTO> pedidos = pedidoService.listarPedidosPorFecha(fecha);
        Long cantidad = pedidoService.contarPedidosPorFecha(fecha);

        return ResponseEntity.ok(Map.of(
                "fecha", fecha,
                "cantidad", cantidad,
                "pedidos", pedidos
        ));
    }

    @GetMapping("/admin/rango")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PedidoDTO>> listarPedidosPorRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        List<PedidoDTO> pedidos = pedidoService.listarPedidosPorRangoFechas(fechaInicio, fechaFin);
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/admin/estado/{estado}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PedidoDTO>> listarPedidosPorEstado(@PathVariable EstadoPedido estado) {
        List<PedidoDTO> pedidos = pedidoService.listarPedidosPorEstado(estado);
        return ResponseEntity.ok(pedidos);
    }

    @PatchMapping("/admin/{id}/estado")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PedidoDTO> cambiarEstadoPedido(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        EstadoPedido nuevoEstado = EstadoPedido.valueOf(body.get("estado").toUpperCase());
        PedidoDTO pedido = pedidoService.cambiarEstadoPedido(id, nuevoEstado);
        return ResponseEntity.ok(pedido);
    }

    // ============= ENDPOINTS PARA CLIENTES =============

    @GetMapping("/mis-pedidos")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    public ResponseEntity<List<PedidoDTO>> listarMisPedidos() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long usuarioId = userDetails.getId();

        List<PedidoDTO> pedidos = pedidoService.listarPedidosDeUsuario(usuarioId);
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    public ResponseEntity<PedidoDTO> obtenerPedido(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        PedidoDTO pedido = pedidoService.obtenerPedidoPorId(id, userDetails.getId(), userDetails.getRol());
        return ResponseEntity.ok(pedido);
    }

    @PostMapping("/crear")
    @PreAuthorize("hasAnyRole('CLIENTE', 'ADMIN')")
    public ResponseEntity<Pedido> crearPedido(@RequestBody @Valid CrearPedidoDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        Pedido pedido = pedidoService.crearPedido(
                userDetails.getId(),
                dto.getTarjetaId(),
                dto.getObservaciones(),
                dto.getDireccionEntrega()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(pedido);
    }

}
