package com.example.pizzumburgum.component;

import com.example.pizzumburgum.component.actores.Funcionario;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria_acciones")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"funcionario"})
public class AuditoriaAccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "funcionario_id", nullable = false)
    @NotNull(message = "El funcionario es obligatorio")
    private Funcionario funcionario;

    @NotBlank(message = "La acción es obligatoria")
    @Column(nullable = false, length = 100)
    private String accion;

    @NotBlank(message = "La entidad es obligatoria")
    @Column(nullable = false, length = 100)
    private String entidad;

    @Column(name = "entidad_id")
    private Long entidadId;

    @Column(length = 1000)
    private String descripcion;

    @Column(length = 50)
    private String ipAddress;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaAccion;

    public static AuditoriaAccion crear(Funcionario funcionario, String accion, String entidad,
                                        Long entidadId, String descripcion) {
        return AuditoriaAccion.builder()
                .funcionario(funcionario)
                .accion(accion)
                .entidad(entidad)
                .entidadId(entidadId)
                .descripcion(descripcion)
                .build();
    }
}