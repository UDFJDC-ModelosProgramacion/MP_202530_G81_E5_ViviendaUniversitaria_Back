package co.edu.udistrital.mdp.back.dto;

import lombok.Data;

@Data
public class EstanciaDTO {
    private Long id;
    private Integer tiempoEstancia;
    // Las relaciones @ManyToOne van en el DTO base
    private EstudianteDTO estudianteArrendador;
}