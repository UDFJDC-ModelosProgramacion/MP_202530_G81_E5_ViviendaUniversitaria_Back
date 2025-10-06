package co.edu.udistrital.mdp.back.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import uk.co.jemos.podam.common.PodamExclude;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa una vivienda disponible para arrendamiento
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
public class ViviendaEntity extends BaseEntity {

    @Column(nullable = false)
    private String direccion;

    @Column(nullable = false)
    private String ciudad;

    private String barrio;

    @Column(nullable = false)
    private BigDecimal precioMensual;

    @Column(length = 1000)
    private String descripcion;

    private int numeroHabitaciones;

    private int numeroBaños;

    private Double areaMetrosCuadrados;

    @Column(nullable = false)
    private Boolean disponible = true;

    @Enumerated(EnumType.STRING)
    private TipoVivienda tipo;

    // Relación con Servicios (una vivienda tiene múltiples servicios)
    @PodamExclude
    @ManyToMany
    @JoinTable(name = "vivienda_servicio", joinColumns = @JoinColumn(name = "vivienda_id"), inverseJoinColumns = @JoinColumn(name = "servicio_id"))
    private List<ServicioEntity> servicios = new ArrayList<>();

    // Relación con Comentarios (una vivienda puede tener múltiples comentarios)
    @PodamExclude
    @OneToMany(mappedBy = "vivienda", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ComentarioEntity> comentarios = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "propietario_id")
    private PropietarioEntity propietario;

    @ManyToOne
    @JoinColumn(name = "universidad_cerca_id")
    private UniversidadCercaEntity universidadCerca;

    public void setUniversidadCerca(UniversidadCercaEntity universidadCerca) {
        this.universidadCerca = universidadCerca;
    }

    public UniversidadCercaEntity getUniversidadCerca() {
        return universidadCerca;
    }

    public enum TipoVivienda {
        APARTAMENTO,
        CASA,
        HABITACION,
        ESTUDIO,
        COMPARTIDO
    }
}
