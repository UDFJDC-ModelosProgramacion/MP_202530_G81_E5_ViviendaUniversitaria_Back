package co.edu.udistrital.mdp.back.dto;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class UniversidadCercaDTO {
    private Long id;
    private String nombre;
    private String ciudad;
    private List<Long> viviendaIds = new ArrayList<>();
}
