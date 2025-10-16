package com.example.pizzumburgum.producto;

import com.example.pizzumburgum.actores.Funcionario;
import com.example.pizzumburgum.actores.FuncionarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AtributoService {

    private final AtributoRepository atributoRepository;
    private final FuncionarioRepository funcionarioRepository;

    @Transactional
    public Atributo crearAtributo(AtributoDTO dto, Long funcionarioId) {
        log.info("Creando atributo '{}' por funcionario ID: {}", dto.getNombre(), funcionarioId);

        if (atributoRepository.existsByNombre(dto.getNombre())) {
            throw new IllegalArgumentException(
                    "Ya existe un atributo con el nombre: " + dto.getNombre()
            );
        }

        Funcionario funcionario = funcionarioRepository.findById(funcionarioId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Funcionario no encontrado con ID: " + funcionarioId
                ));

        Atributo atributo = Atributo.builder()
                .nombre(dto.getNombre())
                .aplicaA(dto.getAplicaA())
                .build();

        funcionario.registrarAtributoAgregado(atributo);

        Atributo saved = atributoRepository.save(atributo);

        log.info("Atributo creado exitosamente con ID: {}", saved.getIdAtributo());
        return saved;
    }

    @Transactional
    public void eliminarAtributo(Long atributoId, Long funcionarioId) {
        log.info("Eliminando atributo ID: {} por funcionario ID: {}", atributoId, funcionarioId);

        Atributo atributo = atributoRepository.findById(atributoId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Atributo no encontrado con ID: " + atributoId
                ));

        if (atributo.getFuncionarioBaja() != null) {
            throw new IllegalStateException(
                    "El atributo ya fue eliminado anteriormente"
            );
        }

        Funcionario funcionario = funcionarioRepository.findById(funcionarioId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Funcionario no encontrado con ID: " + funcionarioId
                ));

        funcionario.registrarAtributoBorrado(atributo);

        atributoRepository.save(atributo);

        log.info("Atributo eliminado exitosamente");
    }

    @Transactional(readOnly = true)
    public List<Atributo> obtenerAtributosActivos() {
        return atributoRepository.findActivos();
    }

    @Transactional(readOnly = true)
    public List<Atributo> obtenerAtributosPorFuncionario(Long funcionarioId) {
        return atributoRepository.findByFuncionarioAltaId(funcionarioId);
    }

    @Transactional(readOnly = true)
    public Atributo obtenerPorId(Long id) {
        return atributoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Atributo no encontrado con ID: " + id
                ));
    }

    @Transactional(readOnly = true)
    public List<Atributo> obtenerTodos() {
        return atributoRepository.findAll();
    }
}