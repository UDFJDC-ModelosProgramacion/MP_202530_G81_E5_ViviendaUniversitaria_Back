package co.edu.udistrital.mdp.back.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.jemos.podam.common.PodamExclude;

/**
 * Entidad que representa un sitio de interés cercano a una vivienda.
 * Contiene información de ubicación, imagen y tiempo de llegada a pie.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sitio_interes")
public class SitioInteresEntity extends BaseEntity {

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String ubicacion;

    private String foto;

    @Column(nullable = false)
    private Integer tiempoCaminando; // en minutos

    @PodamExclude
    @ManyToOne
    @JoinColumn(name = "vivienda_id")
    private ViviendaEntity vivienda;
}
