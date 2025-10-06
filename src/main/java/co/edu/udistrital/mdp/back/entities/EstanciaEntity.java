package co.edu.udistrital.mdp.back.entities;

import jakarta.persistence.*;
import java.util.Objects;

/**
 * Estancia: relaciona Estudiante con Vivienda y tiene un Contrato 1..1.
 */
@Entity
@Table(name = "estancia")
public class EstanciaEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private EstudianteEntity estudianteArrendador;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "vivienda_id", nullable = false)
    private ViviendaEntity viviendaArrendada;

    @Column(nullable = false)
    private Integer tiempoEstancia;

    @OneToOne(mappedBy = "estancia", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private ContratoEntity contrato;

    public EstanciaEntity() {}
    public EstanciaEntity(EstudianteEntity est, ViviendaEntity viv, Integer meses) {
        this.estudianteArrendador = est; this.viviendaArrendada = viv; this.tiempoEstancia = meses;
    }

    public Long getId() { return id; }
    public EstudianteEntity getEstudianteArrendador() { return estudianteArrendador; }
    public void setEstudianteArrendador(EstudianteEntity e) { this.estudianteArrendador = e; }
    public ViviendaEntity getViviendaArrendada() { return viviendaArrendada; }
    public void setViviendaArrendada(ViviendaEntity v) { this.viviendaArrendada = v; }
    public Integer getTiempoEstancia() { return tiempoEstancia; }
    public void setTiempoEstancia(Integer t) { this.tiempoEstancia = t; }
    public ContratoEntity getContrato() { return contrato; }

    @Override public boolean equals(Object o){ return o instanceof EstanciaEntity e && id!=null && id.equals(e.id); }
    @Override public int hashCode(){ return Objects.hashCode(id); }
}
