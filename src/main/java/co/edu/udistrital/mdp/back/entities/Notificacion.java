package co.edu.udistrital.mdp.back.entities; 
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * Representa una notificación para un usuario dentro del sistema.
 */
@Entity
@Table(name = "notificaciones")
@Data
@EqualsAndHashCode(callSuper = true) 
public class Notificacion extends BaseEntity {

    // Contenido del mensaje que se mostrará al usuario.
    @Column(nullable = false, length = 255)
    private String mensaje;

    // Marca si la notificación ya fue vista por el usuario.
    @Column(nullable = false)
    private boolean leida = false;

    // Fecha y hora en que se generó la notificación.
    @Column(name = "fecha_envio", nullable = false)
    private LocalDateTime fechaEnvio;

    // Usuario que recibe la notificación.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
}