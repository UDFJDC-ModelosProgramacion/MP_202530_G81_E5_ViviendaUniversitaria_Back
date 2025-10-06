package co.edu.udistrital.mdp.back.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

/** Contrato 1..1 con Estancia. Este lado es el dueño y guarda la FK única. */
@Entity
@Table(name = "contrato")
public class ContratoEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String codigo;

    @Column(nullable = false) private LocalDate fechaInicio;
    @Column(nullable = false) private LocalDate fechaFin;
    @Column(nullable = false) private Double montoTotal;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "estancia_id", nullable = false, unique = true)
    private EstanciaEntity estancia;

    public ContratoEntity() {}
    public ContratoEntity(String codigo, LocalDate inicio, LocalDate fin, Double montoTotal, EstanciaEntity estancia) {
        this.codigo = codigo; this.fechaInicio = inicio; this.fechaFin = fin; this.montoTotal = montoTotal; this.estancia = estancia;
    }

    public Long getId() { return id; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }
    public Double getMontoTotal() { return montoTotal; }
    public void setMontoTotal(Double montoTotal) { this.montoTotal = montoTotal; }
    public EstanciaEntity getEstancia() { return estancia; }
    public void setEstancia(EstanciaEntity estancia) { this.estancia = estancia; }

    @Override public boolean equals(Object o){ return o instanceof ContratoEntity c && id!=null && id.equals(c.id); }
    @Override public int hashCode(){ return Objects.hashCode(id); }
}
