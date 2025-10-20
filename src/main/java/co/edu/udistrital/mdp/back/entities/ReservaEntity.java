package co.edu.udistrital.mdp.back.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.jemos.podam.common.PodamExclude;
import java.time.LocalDate;

/**
 * Entidad que representa una reserva realizada por un estudiante
 * para una vivienda universitaria.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "reserva")
public class ReservaEntity extends BaseEntity {

    @Column(nullable = false)
    private LocalDate fechaInicio;

    @Column(nullable = false)
    private LocalDate fechaFin;

    @Column(nullable = false)
    private String estado; // Ej: Pendiente, Confirmada, Cancelada

    /** Estudiante que realiza la reserva */
    @PodamExclude
    @ManyToOne
    @JoinColumn(name = "estudiante_id", nullable = false)
    private EstudianteEntity estudiante;

    /** Vivienda que se reserva */
    @PodamExclude
    @ManyToOne
    @JoinColumn(name = "vivienda_id", nullable = false)
    private ViviendaEntity vivienda;
}
