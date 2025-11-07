package com.example.pizzumburgum.service;

import com.example.pizzumburgum.dto.request.DireccionDTO;
import com.example.pizzumburgum.dto.request.TarjetaDTO;
import com.example.pizzumburgum.entities.Direccion;
import com.example.pizzumburgum.entities.Tarjeta;
import com.example.pizzumburgum.entities.Usuario;
import com.example.pizzumburgum.repository.UsuarioRepositorio;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepositorio usuarioRepositorio;

    public UsuarioService(UsuarioRepositorio usuarioRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepositorio.findById(id);
    }

    @Transactional
    public Usuario cambiarDireccion(Long usuarioId, DireccionDTO dto) {
        Usuario u = usuarioRepositorio.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + usuarioId));

        // Desmarcar anteriores y agregar nueva como principal
        if (u.getDirecciones() != null) {
            u.getDirecciones().forEach(d -> d.setPrincipal(false));
        }
        Direccion d = new Direccion();
        d.setCalle(dto.getCalle());
        d.setNumero(dto.getNumero());
        d.setApartamento(dto.getApartamento());
        d.setBarrio(dto.getBarrio());
        d.setPrincipal(true);
        d.setUsuario(u);

        u.getDirecciones().add(d);
        return usuarioRepositorio.save(u);
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

}