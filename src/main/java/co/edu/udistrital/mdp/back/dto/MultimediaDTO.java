package co.edu.udistrital.mdp.back.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MultimediaDTO {
    private Long id;
    private String titulo;
    private String descripcion;
    private String tipoArchivo;
    private String urlArchivo;
    private Integer ordenVisualizacion;
    private Boolean esPortada;
    private LocalDateTime fechaSubida;
}