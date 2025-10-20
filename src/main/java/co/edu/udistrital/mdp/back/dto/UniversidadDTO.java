package co.edu.udistrital.mdp.back.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UniversidadDTO {
    private Long id;
    private String nombre;
    private String direccion;
    private String ciudad;
    private String barrio;
    private String telefono;
    private String email;
    private String rector;
    private LocalDate fechaFundacion;
    private String tipoInstitucion;
    private String descripcion;
    private String urlLogo;
    private Double latitud;
    private Double longitud;
}