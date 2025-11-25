package com.example.pizzumburgum.service;

import com.example.pizzumburgum.dto.request.TarjetaDTO;
import com.example.pizzumburgum.dto.response.TarjetaResponseDTO;
import com.example.pizzumburgum.entities.Tarjeta;
import com.example.pizzumburgum.entities.Usuario;
import com.example.pizzumburgum.repository.TarjetaRepositorio;
import com.example.pizzumburgum.repository.UsuarioRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TarjetaService {

    private final TarjetaRepositorio tarjetaRepositorio;
    private final UsuarioRepositorio usuarioRepositorio;
    private final PasswordEncoder passwordEncoder; // Para tokenizar

    /**
     * Listar todas las tarjetas de un usuario (principal primero)
     */
    @Transactional(readOnly = true)
    public List<TarjetaResponseDTO> listarTarjetasDeUsuario(Long usuarioId) {
        if (!usuarioRepositorio.existsById(usuarioId)) {
            throw new IllegalArgumentException("Usuario no encontrado: " + usuarioId);
        }

        List<Tarjeta> tarjetas = tarjetaRepositorio
                .findByUsuarioIdOrderByPrincipalDescIdAsc(usuarioId);

        return tarjetas.stream()
                .map(this::convertirATarjetaResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Crear nueva tarjeta
     */
    @Transactional
    public TarjetaResponseDTO crearTarjeta(Long usuarioId, TarjetaDTO dto) {
        // Validar tarjeta
        validarTarjeta(dto);

        Usuario usuario = usuarioRepositorio.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + usuarioId));

        // Si se marca como principal O es la primera tarjeta, desmarcar las demás
        List<Tarjeta> tarjetasExistentes = tarjetaRepositorio.findByUsuarioId(usuarioId);

        if (dto.isPrincipal() || tarjetasExistentes.isEmpty()) {
            tarjetasExistentes.forEach(t -> t.setPrincipal(false));
            tarjetaRepositorio.saveAll(tarjetasExistentes);
            dto.setPrincipal(true);
        }

        // Crear nueva tarjeta
        Tarjeta tarjeta = new Tarjeta();
        tarjeta.setUsuario(usuario);

        // Generar token seguro (NO guardar número completo)
        String token = generarTokenSeguro(dto.getNumeroTarjeta());
        tarjeta.setToken(token);

        // Guardar solo últimos 4 dígitos
        String numeroTarjeta = dto.getNumeroTarjeta();
        tarjeta.setUltimos4Digitos(numeroTarjeta.substring(numeroTarjeta.length() - 4));

        tarjeta.setFechaVencimiento(dto.getFechaVencimiento());
        tarjeta.setTitular(dto.getTitular());
        tarjeta.setTipo(dto.getTipo());
        tarjeta.setPrincipal(dto.isPrincipal());

        Tarjeta tarjetaGuardada = tarjetaRepositorio.save(tarjeta);
        return convertirATarjetaResponseDTO(tarjetaGuardada);
    }

    /**
     * Marcar tarjeta como principal
     */
    @Transactional
    public TarjetaResponseDTO marcarTarjetaComoPrincipal(Long usuarioId, Long tarjetaId) {
        // Verificar que el usuario existe
        Usuario usuario = usuarioRepositorio.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + usuarioId));

        // Buscar la tarjeta y verificar que pertenece al usuario
        Tarjeta tarjeta = tarjetaRepositorio.findByIdAndUsuarioId(tarjetaId, usuarioId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Tarjeta no encontrada o no pertenece al usuario"));

        // Desmarcar todas las tarjetas del usuario
        List<Tarjeta> tarjetas = tarjetaRepositorio.findByUsuarioId(usuarioId);
        tarjetas.forEach(t -> t.setPrincipal(false));
        tarjetaRepositorio.saveAll(tarjetas);

        // Marcar esta como principal
        tarjeta.setPrincipal(true);
        Tarjeta tarjetaActualizada = tarjetaRepositorio.save(tarjeta);

        return convertirATarjetaResponseDTO(tarjetaActualizada);
    }

    /* ===== HELPERS ===== */

    private void validarTarjeta(TarjetaDTO tarjeta) {
        // Validar fecha de vencimiento no esté vencida
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
            YearMonth vencimiento = YearMonth.parse(tarjeta.getFechaVencimiento(), formatter);
            YearMonth ahora = YearMonth.now();

            if (vencimiento.isBefore(ahora)) {
                throw new IllegalArgumentException("La tarjeta está vencida");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Fecha de vencimiento inválida");
        }

        // Validar algoritmo de Luhn (validación de número de tarjeta)
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
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }

        return (sum % 10 == 0);
    }

    private String generarTokenSeguro(String numeroTarjeta) {
        // IMPORTANTE: En producción usar pasarela de pago (Stripe, PayPal, MercadoPago)
        // NUNCA guardes el número completo de tarjeta

        // Opción 1: Hash del número (más seguro)
        // return passwordEncoder.encode(numeroTarjeta);

        // Opción 2: UUID único (más simple para desarrollo)
        return UUID.randomUUID().toString();
    }

    private TarjetaResponseDTO convertirATarjetaResponseDTO(Tarjeta tarjeta) {
        TarjetaResponseDTO dto = new TarjetaResponseDTO();
        dto.setId(tarjeta.getId());
        dto.setUltimos4Digitos(tarjeta.getUltimos4Digitos());
        dto.setFechaVencimiento(tarjeta.getFechaVencimiento());
        dto.setTitular(tarjeta.getTitular());
        dto.setTipo(tarjeta.getTipo());
        dto.setPrincipal(tarjeta.isPrincipal());
        return dto;
    }

}
