package co.edu.udistrital.mdp.back.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "notificaciones")
public class NotificacionEntity extends BaseEntity {

    @Column(nullable = false, length = 500)
    private String mensaje;

    @Column(nullable = false)
    private Boolean leida;

    @Column(name = "fecha_envio", nullable = false)
    private LocalDateTime fechaEnvio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private EstudianteEntity estudiante;

    @PrePersist
    protected void onCreate() {
        this.fechaEnvio = LocalDateTime.now();
        if (this.leida == null) {
            this.leida = false;
        }
    }
}