package co.edu.udistrital.mdp.back.entities;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.co.jemos.podam.common.PodamExclude;

/** Estancia: relaciona Estudiante con Vivienda y tiene un Contrato 1..1. */
@Data
@NoArgsConstructor
@Entity
@Table(name = "estancia")
public class EstanciaEntity extends BaseEntity {

    // Muchos registros de Estancia pueden pertenecer al mismo Estudiante
    @PodamExclude
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private EstudianteEntity estudianteArrendador;

    // Muchas Estancias pueden referirse a la misma Vivienda
    @PodamExclude
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "vivienda_id", nullable = false)
    private ViviendaEntity viviendaArrendada;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoEstancia estado = EstadoEstancia.ACTIVA;

    @Column(name = "fecha_inicio")
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;

    @Column(nullable = false)
    private Integer tiempoEstancia;

    // Relación 1..1 inversa: el dueño está en Contrato
    @PodamExclude
    @OneToOne(mappedBy = "estancia", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private ContratoEntity contrato;

    public EstanciaEntity(EstudianteEntity est, ViviendaEntity viv, Integer meses) {
        this.estudianteArrendador = est;
        this.viviendaArrendada = viv;
        this.tiempoEstancia = meses;
        this.estado = EstadoEstancia.ACTIVA;
        this.fechaInicio = LocalDateTime.now();
    }

    public enum EstadoEstancia {
        PENDIENTE,
        ACTIVA,
        COMPLETADA,
        CANCELADA
    }
}
