package co.edu.udistrital.mdp.back.dto;
import lombok.Data;
import java.time.LocalDateTime;

import jakarta.persistence.ManyToOne;

@Data
public class NotificacionDTO {
    private Long id;
    private String mensaje;
    private Boolean leida;
    private LocalDateTime fechaEnvio;
    
    @ManyToOne
    private EstudianteDTO estudiante;
}
