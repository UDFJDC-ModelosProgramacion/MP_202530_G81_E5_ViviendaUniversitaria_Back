package co.edu.udistrital.mdp.back.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.jemos.podam.common.PodamExclude;

/**
 * Entidad que representa comentarios/reseñas sobre una vivienda
 * Relacionada con la entidad Vivienda (muchos comentarios para una vivienda)
 * y con la entidad Usuario/Estudiante (muchos comentarios por usuario)
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
public class ComentarioEntity extends BaseEntity {

    @Column(nullable = false, length = 2000)
    private String contenido;

    @Column(nullable = false)
    private Integer calificacion; // Por ejemplo, de 1 a 5 estrellas

    @Column(nullable = false)
    private java.time.LocalDateTime fechaCreacion;

    private java.time.LocalDateTime fechaModificacion;

    // Relación con Vivienda (muchos comentarios pertenecen a una vivienda)
    @PodamExclude
    @ManyToOne
    @JoinColumn(name = "vivienda_id", nullable = false)
    private ViviendaEntity vivienda;

    // Relación con Usuario/Estudiante que hizo el comentario
    @PodamExclude
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private EstudianteEntity autor;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = java.time.LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaModificacion = java.time.LocalDateTime.now();
    }
}
