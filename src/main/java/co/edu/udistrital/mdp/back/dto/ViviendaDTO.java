package co.edu.udistrital.mdp.back.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ViviendaDTO {
    private String direccion;
    private String ciudad;
    private String barrio;
    private BigDecimal precioMensual;
    private String descripcion;
    private int numeroHabitaciones;
    private int numeroBanos;
    private Double areaMetrosCuadrados;
    private Boolean disponible;
    private String tipo;
    private Long propietarioId;
}