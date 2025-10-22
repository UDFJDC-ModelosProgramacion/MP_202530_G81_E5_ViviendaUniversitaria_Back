package co.edu.udistrital.mdp.back.dto;

import lombok.Data;

/**
 * DTO básico que representa un sitio de interés cercano a una vivienda.
 */
@Data
public class SitioInteresDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private String ubicacion;
    private String tiempoCaminata;
    private String imagenUrl;

}