package co.edu.udistrital.mdp.back.dto;
import lombok.Data;

@Data
public class EstudianteDTO {
    private Long id;
    private String nombre;
    private String correo;
    // No se agregan listas (como 'reservas') según la regla.
}