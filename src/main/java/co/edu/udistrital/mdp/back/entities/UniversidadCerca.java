package co.edu.udistrital.mdp.back.entities;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Universidad cercana (1..N)*/
@Entity
@Table(name = "universidad_cerca")
public class UniversidadCerca extends BaseEntity {

    @Column(nullable = false, unique = true, length = 150)
    private String nombre;

    @Column(length = 120)
    private String ciudad;

    /** Unidireccional OneToMany. */ 
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "universidad_cerca_id") // crea/usa la FK en la tabla vivienda
    private List<Vivienda> viviendas = new ArrayList<>();

    public UniversidadCerca() {}

    public UniversidadCerca(String nombre, String ciudad) {
        this.nombre = nombre;
        this.ciudad = ciudad;
    }

    /** Añade una vivienda a la colección.*/
    public void addVivienda(Vivienda v) {
        if (v == null) return;
        viviendas.add(v);
    }

    /** Remueve una vivienda de la colección.*/
    public void removeVivienda(Vivienda v) {
        if (v == null) return;
        viviendas.remove(v);
    }

    // Getters / setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public List<Vivienda> getViviendas() { return viviendas; }
    public void setViviendas(List<Vivienda> viviendas) { this.viviendas = viviendas; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UniversidadCerca)) return false;
        UniversidadCerca that = (UniversidadCerca) o;
        return this.getId() != null && this.getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.getId());
    }
}
