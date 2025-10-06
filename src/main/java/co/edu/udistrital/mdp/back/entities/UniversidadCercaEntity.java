package co.edu.udistrital.mdp.back.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "universidad_cerca")
public class UniversidadCercaEntity extends BaseEntity {

    @Column(nullable = false, unique = true, length = 150)
    private String nombre;

    @Column(length = 120)
    private String ciudad;

    @OneToMany(mappedBy = "universidadCerca", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ViviendaEntity> viviendas = new ArrayList<>();

    public void addVivienda(ViviendaEntity v) {
        viviendas.add(v);
        v.setUniversidadCerca(this);
    }

    public void removeVivienda(ViviendaEntity v) {
        viviendas.remove(v);
        v.setUniversidadCerca(null);
    }
}
