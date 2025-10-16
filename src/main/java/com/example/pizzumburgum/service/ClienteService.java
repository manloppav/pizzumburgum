/*package com.example.pizzumburgum.service;

import com.example.pizzumburgum.component.actores.Cliente;
import com.example.pizzumburgum.component.actores.Direccion;
import com.example.pizzumburgum.component.pago.Tarjeta;
import com.example.pizzumburgum.dto.request.ClienteRequestDTO;
import com.example.pizzumburgum.dto.response.ClienteResponseDTO;
import com.example.pizzumburgum.exception.BadRequestException;
import com.example.pizzumburgum.exception.ResourceNotFoundException;
import com.example.pizzumburgum.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public ClienteResponseDTO crearCliente(ClienteRequestDTO requestDTO) {
        // Validar que el email no exista
        if (clienteRepository.existsByEmail(requestDTO.getEmail())) {
            throw new BadRequestException("Ya existe un cliente con el email: " + requestDTO.getEmail());
        }

        // Validar que el documento no exista (si se proporciona)
        if (requestDTO.getDocumentoIdentidad() != null &&
                clienteRepository.existsByDocumentoIdentidad(requestDTO.getDocumentoIdentidad())) {
            throw new BadRequestException("Ya existe un cliente con el documento: " + requestDTO.getDocumentoIdentidad());
        }

        // Validar que la tarjeta no esté vencida
        if (requestDTO.getTarjetaPrincipal().estaVencida()) {
            throw new BadRequestException("La tarjeta proporcionada está vencida");
        }

        Cliente cliente = Cliente.builder()
                .nombre(requestDTO.getNombre())
                .apellido(requestDTO.getApellido())
                .email(requestDTO.getEmail())
                .telefono(requestDTO.getTelefono())
                .password(passwordEncoder.encode(requestDTO.getPassword()))
                .direccionPrincipal(requestDTO.getDireccionPrincipal())
                .documentoIdentidad(requestDTO.getDocumentoIdentidad())
                .fechaNacimiento(requestDTO.getFechaNacimiento())
                .preferencias(requestDTO.getPreferencias())
                .tarjetaPrincipal(requestDTO.getTarjetaPrincipal())
                .build();

        Cliente clienteGuardado = clienteRepository.save(cliente);
        return convertirAResponseDTO(clienteGuardado);
    }

    @Transactional(readOnly = true)
    public ClienteResponseDTO obtenerClientePorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));
        return convertirAResponseDTO(cliente);
    }

    @Transactional(readOnly = true)
    public ClienteResponseDTO obtenerClientePorEmail(String email) {
        Cliente cliente = clienteRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "email", email));
        return convertirAResponseDTO(cliente);
    }

    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> obtenerTodosLosClientes() {
        return clienteRepository.findAll().stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ClienteResponseDTO> obtenerClientesActivos() {
        return clienteRepository.findByActivoTrue().stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ClienteResponseDTO actualizarCliente(Long id, ClienteRequestDTO requestDTO) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));

        // Validar email si cambió
        if (!cliente.getEmail().equals(requestDTO.getEmail()) &&
                clienteRepository.existsByEmail(requestDTO.getEmail())) {
            throw new BadRequestException("Ya existe un cliente con el email: " + requestDTO.getEmail());
        }

        cliente.setNombre(requestDTO.getNombre());
        cliente.setApellido(requestDTO.getApellido());
        cliente.setEmail(requestDTO.getEmail());
        cliente.setTelefono(requestDTO.getTelefono());
        cliente.setDireccionPrincipal(requestDTO.getDireccionPrincipal());
        cliente.setDocumentoIdentidad(requestDTO.getDocumentoIdentidad());
        cliente.setFechaNacimiento(requestDTO.getFechaNacimiento());
        cliente.setPreferencias(requestDTO.getPreferencias());

        if (requestDTO.getPassword() != null && !requestDTO.getPassword().isEmpty()) {
            cliente.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        }

        Cliente clienteActualizado = clienteRepository.save(cliente);
        return convertirAResponseDTO(clienteActualizado);
    }

    @Transactional
    public void agregarDireccionSecundaria(Long clienteId, Direccion direccion) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", clienteId));

        cliente.agregarDireccionSecundaria(direccion);
        clienteRepository.save(cliente);
    }

    @Transactional
    public void agregarTarjetaSecundaria(Long clienteId, Tarjeta tarjeta) {
        if (tarjeta.estaVencida()) {
            throw new BadRequestException("La tarjeta está vencida");
        }

        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", clienteId));

        cliente.agregarTarjetaSecundaria(tarjeta);
        clienteRepository.save(cliente);
    }

    @Transactional
    public void agregarPuntosLealtad(Long clienteId, Integer puntos) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", clienteId));

        cliente.agregarPuntosLealtad(puntos);
        clienteRepository.save(cliente);
    }

    @Transactional
    public void descontarPuntosLealtad(Long clienteId, Integer puntos) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", clienteId));

        if (cliente.getPuntosLealtad() < puntos) {
            throw new BadRequestException("El cliente no tiene suficientes puntos de lealtad");
        }

        cliente.descontarPuntosLealtad(puntos);
        clienteRepository.save(cliente);
    }

    @Transactional
    public void desactivarCliente(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));

        cliente.setActivo(false);
        clienteRepository.save(cliente);
    }

    @Transactional
    public void activarCliente(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", id));

        cliente.setActivo(true);
        clienteRepository.save(cliente);
    }

    @Transactional
    public void eliminarCliente(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cliente", "id", id);
        }
        clienteRepository.deleteById(id);
    }

    private ClienteResponseDTO convertirAResponseDTO(Cliente cliente) {
        return ClienteResponseDTO.builder()
                .id(cliente.getId())
                .nombre(cliente.getNombre())
                .apellido(cliente.getApellido())
                .email(cliente.getEmail())
                .telefono(cliente.getTelefono())
                .rol(cliente.getRol())
                .activo(cliente.getActivo())
                .direccionPrincipal(cliente.getDireccionPrincipal())
                .documentoIdentidad(cliente.getDocumentoIdentidad())
                .fechaNacimiento(cliente.getFechaNacimiento())
                .preferencias(cliente.getPreferencias())
                .puntosLealtad(cliente.getPuntosLealtad())
                .tarjetaEnmascarada(cliente.getTarjetaPrincipal() != null ?
                        cliente.getTarjetaPrincipal().getNumeroEnmascarado() : null)
                .fechaCreacion(cliente.getFechaCreacion())
                .fechaActualizacion(cliente.getFechaActualizacion())
                .build();
    }
}*/