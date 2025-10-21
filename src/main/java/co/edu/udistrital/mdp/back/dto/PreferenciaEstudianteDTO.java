package co.edu.udistrital.mdp.back.dto;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Data
public class PreferenciaEstudianteDTO {
    private Long id;
    private Integer precioMaximo;
    private String zonaPreferida;
    private Boolean aceptaMascotas;
    private String tipoVivienda;
    
    @OneToOne
    private EstudianteDTO estudiante;
}
