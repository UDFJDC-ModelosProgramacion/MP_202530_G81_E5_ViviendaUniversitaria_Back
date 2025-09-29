package co.edu.udistrital.mdp.back.entities; 
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Modela una transacción financiera, como el pago de un alquiler.
 */
@Entity
@Table(name = "transacciones")
@Data
@EqualsAndHashCode(callSuper = true)
public class Transaccion extends BaseEntity {

    // Valor monetario de la transacción. Se usa BigDecimal para precisión.
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    // Momento exacto en que se realizó la operación.
    @Column(name = "fecha_transaccion", nullable = false)
    private LocalDateTime fechaTransaccion;

    // Describe el método utilizado para el pago (ej: "Tarjeta de Crédito", "PSE").
    @Column(name = "metodo_pago", nullable = false, length = 50)
    private String metodoPago;

    // Estado actual de la transacción (ej: "Completada", "Pendiente", "Fallida").
    @Column(nullable = false, length = 30)
    private String estado;
    
    // Estancia a la que corresponde este pago.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estancia_id", nullable = false)
    private Estancia estancia;
}