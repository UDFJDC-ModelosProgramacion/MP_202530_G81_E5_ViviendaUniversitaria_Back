package co.edu.udistrital.mdp.back.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "reserva")

public class ReservaEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String estado; // Ej: "Pendiente", "Confirmada", "Cancelada"

    @ManyToOne
    @JoinColumn(name = "estudiante_id", nullable = false)
    private EstudianteEntity estudiante;

    @ManyToOne
    @JoinColumn(name = "vivienda_id", nullable = false)
    private ViviendaEntity vivienda;

    // --- Constructores ---
    public ReservaEntity() {
    }

    public ReservaEntity(LocalDate fechaInicio, LocalDate fechaFin, String estado, EstudianteEntity estudiante, ViviendaEntity vivienda) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = estado;
        this.estudiante = estudiante;
        // this.vivienda = vivienda;
    }

    // --- Getters y Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public EstudianteEntity getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(EstudianteEntity estudiante) {
        this.estudiante = estudiante;
    }

    public ViviendaEntity getVivienda() {
        return vivienda;
    }

    public void setVivienda(ViviendaEntity vivienda) {
        this.vivienda = vivienda;
    }
}
