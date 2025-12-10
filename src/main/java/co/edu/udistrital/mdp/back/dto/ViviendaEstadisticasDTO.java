package co.edu.udistrital.mdp.back.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ViviendaEstadisticasDTO {
    private Double ocupacionAnual; // Porcentaje de ocupación anual (0-100)
    private BigDecimal ingresosMesActual; // Ingresos del mes en curso
    private BigDecimal ingresosAnual; // Ingresos totales del año
}
