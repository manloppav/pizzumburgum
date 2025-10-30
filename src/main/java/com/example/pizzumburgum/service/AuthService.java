package com.example.pizzumburgum.service;

import com.example.pizzumburgum.dto.request.LoginDTO;
import com.example.pizzumburgum.dto.request.RegistroUsuarioDTO;
import com.example.pizzumburgum.dto.request.TarjetaDTO;
import com.example.pizzumburgum.dto.response.AuthResponseDTO;
import com.example.pizzumburgum.entities.Direccion;
import com.example.pizzumburgum.entities.Tarjeta;
import com.example.pizzumburgum.entities.Usuario;
import com.example.pizzumburgum.enums.RolUsuario;
import com.example.pizzumburgum.exception.RegistroException;
import com.example.pizzumburgum.repository.UsuarioRepository;
import com.example.pizzumburgum.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponseDTO registrarUsuario(RegistroUsuarioDTO registroDTO) {
        return registrarUsuario(registroDTO, null);
    }

    @Transactional
    public AuthResponseDTO registrarUsuario(RegistroUsuarioDTO registroDTO, String rolSolicitante) {
        // Validaciones
        if (usuarioRepository.existsByEmail(registroDTO.getEmail())) {
            throw new RegistroException("El email ya está registrado");
        }

        if (usuarioRepository.existsByCedulaIdentidad(registroDTO.getCedulaIdentidad())) {
            throw new RegistroException("La cédula de identidad ya está registrada");
        }

        RolUsuario rolAsignado = determinarRol(registroDTO.getRol(), rolSolicitante);

        // Validar que los clientes tengan tarjeta
        if (rolAsignado == RolUsuario.CLIENTE && registroDTO.getTarjeta() == null) {
            throw new RegistroException("Los clientes deben registrar una tarjeta de crédito");
        }

        // Validar tarjeta si existe
        if (registroDTO.getTarjeta() != null) {
            validarTarjeta(registroDTO.getTarjeta());
        }

        // Crear usuario
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(registroDTO.getNombre());
        nuevoUsuario.setApellido(registroDTO.getApellido());
        nuevoUsuario.setCedulaIdentidad(registroDTO.getCedulaIdentidad());
        nuevoUsuario.setEmail(registroDTO.getEmail());
        nuevoUsuario.setPassword(passwordEncoder.encode(registroDTO.getPassword()));
        nuevoUsuario.setFechaNacimiento(registroDTO.getFechaNacimiento());
        nuevoUsuario.setTelefono(registroDTO.getTelefono());
        nuevoUsuario.setRol(rolAsignado);

        // Crear dirección
        Direccion direccion = new Direccion();
        direccion.setCalle(registroDTO.getDireccion().getCalle());
        direccion.setNumero(registroDTO.getDireccion().getNumero());
        direccion.setApartamento(registroDTO.getDireccion().getApartamento());
        direccion.setBarrio(registroDTO.getDireccion().getBarrio());
        direccion.setPrincipal(true);
        direccion.setUsuario(nuevoUsuario);
        nuevoUsuario.getDirecciones().add(direccion);

        // Crear tarjeta si corresponde (solo clientes)
        if (registroDTO.getTarjeta() != null) {
            Tarjeta tarjeta = new Tarjeta();

            // Generar token seguro (en producción usar pasarela de pago real)
            String tokenSeguro = generarTokenSeguro(registroDTO.getTarjeta().getNumeroTarjeta());
            tarjeta.setToken(tokenSeguro);

            // Guardar solo últimos 4 dígitos
            String numeroTarjeta = registroDTO.getTarjeta().getNumeroTarjeta();
            tarjeta.setUltimos4Digitos(numeroTarjeta.substring(numeroTarjeta.length() - 4));

            tarjeta.setFechaVencimiento(registroDTO.getTarjeta().getFechaVencimiento());
            tarjeta.setTitular(registroDTO.getTarjeta().getTitular());
            tarjeta.setTipo(registroDTO.getTarjeta().getTipo());
            tarjeta.setPrincipal(true);
            tarjeta.setUsuario(nuevoUsuario);

            nuevoUsuario.getTarjetas().add(tarjeta);
        }

        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);

        String token = jwtService.generarToken(
                usuarioGuardado.getEmail(),
                usuarioGuardado.getRol().name()
        );

        return new AuthResponseDTO(
                token,
                usuarioGuardado.getId(),
                usuarioGuardado.getNombre(),
                usuarioGuardado.getApellido(),
                usuarioGuardado.getEmail(),
                usuarioGuardado.getRol().name()
        );
    }

    private void validarTarjeta(TarjetaDTO tarjeta) {
        // Validar fecha de vencimiento no esté vencida
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
            YearMonth vencimiento = YearMonth.parse(tarjeta.getFechaVencimiento(), formatter);
            YearMonth ahora = YearMonth.now();

            if (vencimiento.isBefore(ahora)) {
                throw new RegistroException("La tarjeta está vencida");
            }
        } catch (Exception e) {
            throw new RegistroException("Fecha de vencimiento inválida");
        }

        // Validar algoritmo de Luhn (validación de número de tarjeta)
        if (!validarLuhn(tarjeta.getNumeroTarjeta())) {
            throw new RegistroException("Número de tarjeta inválido");
        }
    }

    private boolean validarLuhn(String numeroTarjeta) {
        // Algoritmo de Luhn para validar tarjetas de crédito
        int sum = 0;
        boolean alternate = false;

        for (int i = numeroTarjeta.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(numeroTarjeta.substring(i, i + 1));
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
        // IMPORTANTE: En producción, esto debe hacerse a través de una pasarela de pago
        // como Stripe, PayPal, MercadoPago, etc.
        // NUNCA guardes el número completo de tarjeta

        // Por ahora, generamos un hash simple (NO USAR EN PRODUCCIÓN)
        return passwordEncoder.encode(numeroTarjeta);
    }

    private RolUsuario determinarRol(String rolSolicitado, String rolSolicitante) {
        if (rolSolicitado == null || rolSolicitado.isEmpty()) {
            return RolUsuario.CLIENTE;
        }

        if ("ADMIN".equalsIgnoreCase(rolSolicitado)) {
            if (!"ADMIN".equals(rolSolicitante)) {
                throw new RegistroException("Solo un administrador puede crear otros administradores");
            }
            return RolUsuario.ADMIN;
        }

        try {
            return RolUsuario.valueOf(rolSolicitado.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RegistroException("Rol inválido: " + rolSolicitado);
        }
    }

    public AuthResponseDTO login(LoginDTO loginDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDTO.getEmail(),
                            loginDTO.getPassword()
                    )
            );

            Usuario usuario = usuarioRepository.findByEmail(loginDTO.getEmail())
                    .orElseThrow(() -> new RegistroException("Usuario no encontrado"));

            String token = jwtService.generarToken(
                    usuario.getEmail(),
                    usuario.getRol().name()
            );

            return new AuthResponseDTO(
                    token,
                    usuario.getId(),
                    usuario.getNombre(),
                    usuario.getApellido(),
                    usuario.getEmail(),
                    usuario.getRol().name()
            );

        } catch (AuthenticationException e) {
            throw new RegistroException("Credenciales inválidas");
        }
    }

}