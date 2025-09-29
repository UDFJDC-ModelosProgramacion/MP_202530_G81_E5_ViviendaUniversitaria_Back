package co.edu.udistrital.mdp.back.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "sitio_interes")
public class SitioInteres {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String ubicacion;
    private int tiempoCaminando; // en minutos

    @ManyToOne
    @JoinColumn(name = "vivienda_id", nullable = false)
    private Vivienda vivienda;

    // --- Constructores ---
    public SitioInteres() {
    }

    public SitioInteres(String nombre, String ubicacion, int tiempoCaminando, Vivienda vivienda) {
        this.nombre = nombre;
        this.ubicacion = ubicacion;
        this.tiempoCaminando = tiempoCaminando;
        this.vivienda = vivienda;
    }

    // --- Getters y Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public int getTiempoCaminando() {
        return tiempoCaminando;
    }

    public void setTiempoCaminando(int tiempoCaminando) {
        this.tiempoCaminando = tiempoCaminando;
    }

    public Vivienda getVivienda() {
        return vivienda;
    }

    public void setVivienda(Vivienda vivienda) {
        this.vivienda = vivienda;
    }
}
