package co.edu.udistrital.mdp.back.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "vivienda")
public class Vivienda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String direccion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "universidad_cerca_id")
    private UniversidadCerca universidadCerca;

    public Vivienda() {}
    public Vivienda(String direccion) { this.direccion = direccion; }

    public Long getId() { return id; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public UniversidadCerca getUniversidadCerca() { return universidadCerca; }
    public void setUniversidadCerca(UniversidadCerca universidadCerca) {
        this.universidadCerca = universidadCerca;
    }
}

