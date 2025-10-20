package co.edu.udistrital.mdp.back.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PropietarioDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String documento;
    private String tipoDocumento;
    private String telefono;
    private String email;
    private LocalDateTime fechaRegistro;
}