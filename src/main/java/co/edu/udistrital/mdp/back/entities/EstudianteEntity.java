package co.edu.udistrital.mdp.back.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "estudiantes")
public class EstudianteEntity extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, unique = true, length = 100)
    private String correo;

    @Column(length = 150)
    private String universidad;

    @OneToMany(mappedBy = "estudiante", cascade = CascadeType.ALL)
    private List<ReservaEntity> reservas = new ArrayList<>();

    @OneToMany(mappedBy = "estudiante", cascade = CascadeType.ALL)
    private List<NotificacionEntity> notificaciones = new ArrayList<>();
}