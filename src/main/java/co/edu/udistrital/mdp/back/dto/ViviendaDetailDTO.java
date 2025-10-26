package co.edu.udistrital.mdp.back.dto;

import lombok.Data;
import java.util.List;

@Data
public class ViviendaDetailDTO extends ViviendaDTO {

    private PropietarioDTO propietario;
    private List<ServicioDTO> servicios;
    private List<ComentarioDTO> comentarios;
    private Double calicacionPromedio;

}