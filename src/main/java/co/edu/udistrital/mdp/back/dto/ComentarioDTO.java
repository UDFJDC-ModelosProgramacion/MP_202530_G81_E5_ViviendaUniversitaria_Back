package co.edu.udistrital.mdp.back.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ComentarioDTO {
    private Long id;
    private String contenido;
    private Integer calificacion;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;
    private Long viviendaId;
    private Long autorId;
}