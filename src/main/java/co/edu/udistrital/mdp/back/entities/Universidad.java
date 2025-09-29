package co.edu.udistrital.mdp.back.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "viviendas")
public class Universidad extends BaseEntity {

    @Column(nullable = false, length = 200)
    private String direccion;

    @Column(nullable = false, length = 100)
    private String ciudad;

    @Column(length = 100)
    private String barrio;

    @Column(name = "numero_habitaciones")
    private Integer numeroHabitaciones;

    @Column(name = "numero_banos")
    private Integer numeroBanos;

    @Column(precision = 10, scale = 2)
    private BigDecimal precioMensual;

    @Column(length = 1000)
    private String descripcion;

    @Column(name = "area_metros")
    private Double areaMetros;

    private Boolean amoblada;

    @Column(name = "servicios_incluidos", length = 500)
    private String serviciosIncluidos;

    @Column(nullable = false)
    private Boolean disponible;

    @Column(name = "fecha_publicacion")
    private LocalDateTime fechaPublicacion;

    @ManyToOne
    @JoinColumn(name = "propietario_id", nullable = false)
    private Propietario propietario;

    @OneToMany(mappedBy = "vivienda", cascade = CascadeType.ALL)
    private List<Multimedia> multimedia = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.fechaPublicacion = LocalDateTime.now();
        if (this.disponible == null) {
            this.disponible = true;
        }
    }
}