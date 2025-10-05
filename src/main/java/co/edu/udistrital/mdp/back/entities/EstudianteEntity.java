package co.edu.udistrital.mdp.back.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.jemos.podam.common.PodamExclude;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un estudiante registrado en el sistema.
 * Cada estudiante puede tener múltiples reservas de vivienda.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "estudiante")
public class EstudianteEntity extends BaseEntity {

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String correo;

    @Column(nullable = false)
    private String universidad;

    /** Reservas asociadas al estudiante */
    @PodamExclude
    @OneToMany(mappedBy = "estudiante", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservaEntity> reservas = new ArrayList<>();
}
