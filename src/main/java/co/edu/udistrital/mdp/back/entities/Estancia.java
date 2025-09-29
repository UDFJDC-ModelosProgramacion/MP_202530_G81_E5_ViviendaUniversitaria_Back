package co.edu.udistrital.mdp.back.entities;

import jakarta.persistence.*;
import java.util.Objects;

/**
 * Estancia: relaciona Estudiante con Vivienda y tiene un Contrato 1..1.
 */
@Entity
@Table(name = "estancia")
public class Estancia {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudianteArrendador;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "vivienda_id", nullable = false)
    private Vivienda viviendaArrendada;

    @Column(nullable = false)
    private Integer tiempoEstancia;

    @OneToOne(mappedBy = "estancia", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Contrato contrato;

    public Estancia() {}
    public Estancia(Estudiante est, Vivienda viv, Integer meses) {
        this.estudianteArrendador = est; this.viviendaArrendada = viv; this.tiempoEstancia = meses;
    }

    public Long getId() { return id; }
    public Estudiante getEstudianteArrendador() { return estudianteArrendador; }
    public void setEstudianteArrendador(Estudiante e) { this.estudianteArrendador = e; }
    public Vivienda getViviendaArrendada() { return viviendaArrendada; }
    public void setViviendaArrendada(Vivienda v) { this.viviendaArrendada = v; }
    public Integer getTiempoEstancia() { return tiempoEstancia; }
    public void setTiempoEstancia(Integer t) { this.tiempoEstancia = t; }
    public Contrato getContrato() { return contrato; }

    @Override public boolean equals(Object o){ return o instanceof Estancia e && id!=null && id.equals(e.id); }
    @Override public int hashCode(){ return Objects.hashCode(id); }
}
