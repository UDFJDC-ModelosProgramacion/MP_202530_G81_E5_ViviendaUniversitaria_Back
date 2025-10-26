package co.edu.udistrital.mdp.back.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ContratoDTO {
    private Long id;
    private String codigo;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Double montoTotal;
    private Long estanciaId;
}
