package co.edu.udistrial.mdp.back.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "multimedia")
public class Multimedia extends BaseEntity {

    @Column(nullable = false, length = 200)
    private String titulo;

    @Column(length = 500)
    private String descripcion;

    @Column(name = "tipo_archivo", nullable = false, length = 50)
    private String tipoArchivo;

    @Column(name = "url_archivo", nullable = false, length = 500)
    private String urlArchivo;

    @Column(name = "orden_visualizacion")
    private Integer ordenVisualizacion;

    @Column(name = "es_portada")
    private Boolean esPortada;

    @Column(name = "fecha_subida")
    private LocalDateTime fechaSubida;

    @ManyToOne
    @JoinColumn(name = "vivienda_id", nullable = false)
    private Universidad vivienda;

    @PrePersist
    protected void onCreate() {
        this.fechaSubida = LocalDateTime.now();
        if (this.esPortada == null) {
            this.esPortada = false;
        }
    }
}