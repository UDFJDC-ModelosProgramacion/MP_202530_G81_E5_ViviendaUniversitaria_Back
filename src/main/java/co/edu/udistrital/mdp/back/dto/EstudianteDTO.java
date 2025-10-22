package co.edu.udistrital.mdp.back.dto;

import lombok.Data;

/**
 * DTO básico que representa un Estudiante.
 */
@Data
public class EstudianteDTO {

    private Long id;
    private String nombre;
    private String correo;
    private String universidad;
    private String telefono;

}