package co.edu.udistrital.mdp.back.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.ManyToOne;

@Data
public class TransaccionDTO {
    private Long id;
    private BigDecimal monto;
    private LocalDateTime fechaTransaccion;
    private String metodoPago;
    private String estado;
    
    @ManyToOne
    private EstanciaDTO estancia;
}