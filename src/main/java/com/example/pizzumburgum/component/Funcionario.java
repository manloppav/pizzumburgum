package com.example.pizzumburgum.component;

import com.example.pizzumburgum.enums.RolUsuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "funcionarios")
@PrimaryKeyJoinColumn(name = "usuario_id")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, exclude = {"auditorias"})
public class Funcionario extends Usuario {

    @NotBlank(message = "El código de empleado es obligatorio")
    @Column(nullable = false, unique = true, length = 20)
    private String codigoEmpleado;

    @NotNull(message = "La fecha de contratación es obligatoria")
    @Column(nullable = false)
    private LocalDate fechaContratacion;

    @Column(precision = 10, scale = 2)
    private BigDecimal salario;

    @Column(length = 100)
    private String cargo;

    @Builder.Default
    @Column(nullable = false)
    private Boolean disponible = true;

    @Column(length = 50)
    private String turno;

    @OneToMany(mappedBy = "funcionario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<AuditoriaAccion> auditorias = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (getRol() == null) {
            setRol(RolUsuario.FUNCIONARIO);
        }
    }
}