package co.edu.udistrital.mdp.back.entities; 
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Almacena las preferencias de búsqueda de un estudiante para una vivienda.
 */
@Entity
@Table(name = "preferencias_estudiante")
@Data
@EqualsAndHashCode(callSuper = true)
public class PreferenciaEstudianteEntity extends BaseEntity {

    // Presupuesto máximo que el estudiante está dispuesto a pagar.
    @Column(name = "precio_maximo")
    private Integer precioMaximo;

    // Zona o barrio de interés para el estudiante.
    @Column(name = "zona_preferida", length = 100)
    private String zonaPreferida;

    // Indica si el estudiante necesita que se permitan mascotas.
    @Column(name = "acepta_mascotas")
    private Boolean aceptaMascotas;

    // Tipo de vivienda buscada (ej: "Apartamento", "Habitación").
    @Column(name = "tipo_vivienda", length = 50)
    private String tipoVivienda;

    // El estudiante al que pertenecen estas preferencias.
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false, unique = true)
    private EstudianteEntity estudiante;
}
