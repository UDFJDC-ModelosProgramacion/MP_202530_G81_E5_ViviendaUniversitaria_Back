package co.edu.udistrital.mdp.back.dto;

import java.time.LocalDate;
import co.edu.udistrital.mdp.back.entities.ReservaEntity;
import lombok.Data;

/**
 * DTO básico que representa una reserva realizada por un estudiante.
 */
@Data
public class ReservaDTO {

    private Long id;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String estado;

    public ReservaDTO() {
    }

    public ReservaDTO(ReservaEntity entity) {
        if (entity != null) {
            this.id = entity.getId();
            this.fechaInicio = entity.getFechaInicio();
            this.fechaFin = entity.getFechaFin();
            this.estado = entity.getEstado();
        }
    }
}
