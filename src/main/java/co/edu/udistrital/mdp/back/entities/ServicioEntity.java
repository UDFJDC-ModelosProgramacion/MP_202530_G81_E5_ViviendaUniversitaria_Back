package co.edu.udistrital.mdp.back.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.jemos.podam.common.PodamExclude;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa los servicios disponibles en una vivienda
 * Relación muchos a muchos con Vivienda
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
public class ServicioEntity extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    private String icono; // Para guardar referencia a un ícono visual

    @Enumerated(EnumType.STRING)
    private CategoriaServicio categoria;

    // Relación inversa con Vivienda
    @PodamExclude
    @ManyToMany(mappedBy = "servicios")
    private List<ViviendaEntity> viviendas = new ArrayList<>();

    public enum CategoriaServicio {
        BASICO, // Agua, luz, gas
        CONECTIVIDAD, // Internet, TV cable
        MOBILIARIO, // Amoblado, cocina equipada
        SEGURIDAD, // Portería, alarma, cámaras
        RECREACION, // Piscina, gimnasio, zona BBQ
        ADICIONAL // Parqueadero, lavandería, etc.
    }
}