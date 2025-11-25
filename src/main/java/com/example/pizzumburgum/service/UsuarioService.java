package com.example.pizzumburgum.service;

import com.example.pizzumburgum.dto.request.DireccionDTO;
import com.example.pizzumburgum.dto.request.TarjetaDTO;
import com.example.pizzumburgum.dto.response.DireccionResponseDTO;
import com.example.pizzumburgum.entities.Direccion;
import com.example.pizzumburgum.entities.Tarjeta;
import com.example.pizzumburgum.entities.Usuario;
import com.example.pizzumburgum.repository.DireccionRepositorio;
import com.example.pizzumburgum.repository.UsuarioRepositorio;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    private final UsuarioRepositorio usuarioRepositorio;
    private final DireccionRepositorio direccionRepositorio;

    public UsuarioService(UsuarioRepositorio usuarioRepositorio,
                          DireccionRepositorio direccionRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.direccionRepositorio = direccionRepositorio;
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepositorio.findById(id);
    }

    @Transactional
    public DireccionResponseDTO cambiarDireccion(Long usuarioId, DireccionDTO dto) {
        Usuario u = usuarioRepositorio.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + usuarioId));

        // Si se marca como principal O es la primera dirección, desmarcar las demás
        if (dto.isPrincipal() || u.getDirecciones().isEmpty()) {
            u.getDirecciones().forEach(d -> d.setPrincipal(false));
            dto.setPrincipal(true);
        }

        // Crear nueva dirección
        Direccion d = new Direccion();
        d.setCalle(dto.getCalle());
        d.setNumero(dto.getNumero());
        d.setApartamento(dto.getApartamento());
        d.setBarrio(dto.getBarrio());
        d.setPrincipal(dto.isPrincipal());
        d.setUsuario(u);

        u.getDirecciones().add(d);
        Usuario usuarioGuardado = usuarioRepositorio.save(u);

        // Retornar la dirección recién creada
        Direccion direccionCreada = usuarioGuardado.getDirecciones()
                .stream()
                .filter(dir -> dir.getCalle().equals(dto.getCalle())
                        && dir.getNumero().equals(dto.getNumero()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Error al crear dirección"));

        return convertirADireccionResponseDTO(direccionCreada);
    }

    @Transactional
    public DireccionResponseDTO marcarDireccionComoPrincipal(Long usuarioId, Long direccionId) {
        // Verificar que el usuario existe
        Usuario usuario = usuarioRepositorio.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + usuarioId));

        // Buscar la dirección y verificar que pertenece al usuario
        Direccion direccion = direccionRepositorio.findByIdAndUsuarioId(direccionId, usuarioId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Dirección no encontrada o no pertenece al usuario"));

        // Desmarcar todas las direcciones del usuario
        usuario.getDirecciones().forEach(d -> d.setPrincipal(false));

        // Marcar esta como principal
        direccion.setPrincipal(true);

        usuarioRepositorio.save(usuario);

        return convertirADireccionResponseDTO(direccion);
    }

    @Transactional(readOnly = true)
    public List<DireccionResponseDTO> listarDirecciones(Long usuarioId) {
        // Verificar que el usuario existe
        if (!usuarioRepositorio.existsById(usuarioId)) {
            throw new IllegalArgumentException("Usuario no encontrado: " + usuarioId);
        }

        List<Direccion> direcciones = direccionRepositorio
                .findByUsuarioIdOrderByPrincipalDescIdAsc(usuarioId);

        return direcciones.stream()
                .map(this::convertirADireccionResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void eliminarDireccion(Long usuarioId, Long direccionId) {
        // Verificar que el usuario existe
        Usuario usuario = usuarioRepositorio.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + usuarioId));

        // Buscar la dirección y verificar que pertenece al usuario
        Direccion direccion = direccionRepositorio.findByIdAndUsuarioId(direccionId, usuarioId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Dirección no encontrada o no pertenece al usuario"));

        // Validar que no sea la única dirección (opcional - puedes quitar esto si quieres permitirlo)
        if (usuario.getDirecciones().size() == 1) {
            throw new IllegalStateException("No puedes eliminar tu única dirección. Debes tener al menos una.");
        }

        boolean eraPrincipal = direccion.isPrincipal();

        // Eliminar la dirección
        usuario.getDirecciones().remove(direccion);
        direccionRepositorio.delete(direccion);

        // Si era principal, marcar otra como principal automáticamente
        if (eraPrincipal && !usuario.getDirecciones().isEmpty()) {
            Direccion nuevaPrincipal = usuario.getDirecciones().get(0);
            nuevaPrincipal.setPrincipal(true);
            direccionRepositorio.save(nuevaPrincipal);
        }
    }

    @Transactional
    public Usuario cambiarTarjeta(Long usuarioId, TarjetaDTO dto) {
        validarTarjeta(dto);

        Usuario u = usuarioRepositorio.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + usuarioId));

        if (u.getTarjetas() != null) {
            u.getTarjetas().forEach(t -> t.setPrincipal(false));
        }

        Tarjeta t = new Tarjeta();
        // Token seguro (placeholder); en producción usar pasarela de pagos
        t.setToken(generarTokenSeguro());
        String num = dto.getNumeroTarjeta();
        t.setUltimos4Digitos(num.substring(num.length() - 4));
        t.setFechaVencimiento(dto.getFechaVencimiento());
        t.setTitular(dto.getTitular());
        t.setTipo(dto.getTipo());
        t.setPrincipal(true);
        t.setUsuario(u);

        u.getTarjetas().add(t);
        return usuarioRepositorio.save(u);
    }


    /* ===== Helpers de validación de tarjeta ===== */

    private void validarTarjeta(TarjetaDTO tarjeta) {
        // Fecha MM/yy no vencida
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
            YearMonth vencimiento = YearMonth.parse(tarjeta.getFechaVencimiento(), formatter);
            if (vencimiento.isBefore(YearMonth.now())) {
                throw new IllegalArgumentException("La tarjeta está vencida");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Fecha de vencimiento inválida");
        }

        // Luhn
        if (!validarLuhn(tarjeta.getNumeroTarjeta())) {
            throw new IllegalArgumentException("Número de tarjeta inválido");
        }
    }

    private boolean validarLuhn(String numeroTarjeta) {
        int sum = 0;
        boolean alternate = false;
        for (int i = numeroTarjeta.length() - 1; i >= 0; i--) {
            int n = numeroTarjeta.charAt(i) - '0';
            if (alternate) {
                n *= 2;
                if (n > 9) n = (n % 10) + 1;
            }
            sum += n;
            alternate = !alternate;
        }
        return sum % 10 == 0;
    }

    private String generarTokenSeguro() {
        return UUID.randomUUID().toString();
    }

    private DireccionResponseDTO convertirADireccionResponseDTO(Direccion direccion) {
        DireccionResponseDTO dto = new DireccionResponseDTO();
        dto.setId(direccion.getId());
        dto.setCalle(direccion.getCalle());
        dto.setNumero(direccion.getNumero());
        dto.setApartamento(direccion.getApartamento());
        dto.setBarrio(direccion.getBarrio());
        dto.setPrincipal(direccion.isPrincipal());
        return dto;
    }

}