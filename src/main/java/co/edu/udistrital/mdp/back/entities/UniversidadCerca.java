package co.edu.udistrital.mdp.back.entities;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Universidad cercana a la que se asocian varias Viviendas (1..N). */
@Entity
@Table(name = "universidad_cerca")
public class UniversidadCerca {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 150)
    private String nombre;

    @Column(length = 120)
    private String ciudad;

    /** Lado inverso: la FK vive en Vivienda. */
    @OneToMany(mappedBy = "universidadCerca", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vivienda> viviendas = new ArrayList<>();

    public UniversidadCerca() {}
    public UniversidadCerca(String nombre, String ciudad) {
        this.nombre = nombre; this.ciudad = ciudad;
    }

    public void addVivienda(Vivienda v){ viviendas.add(v); v.setUniversidadCerca(this); }
    public void removeVivienda(Vivienda v){ viviendas.remove(v); v.setUniversidadCerca(null); }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    public List<Vivienda> getViviendas() { return viviendas; }

    @Override public boolean equals(Object o){ return o instanceof UniversidadCerca u && id!=null && id.equals(u.id); }
    @Override public int hashCode(){ return Objects.hashCode(id); }
}
