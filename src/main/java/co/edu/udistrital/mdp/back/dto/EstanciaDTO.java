package co.edu.udistrital.mdp.back.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class EstanciaDTO {
    private Long id;
    private Long estudianteArrendadorId;
    private Long viviendaArrendadaId;
    private String estado; 
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Integer tiempoEstancia;
    private Long contratoId;
}
