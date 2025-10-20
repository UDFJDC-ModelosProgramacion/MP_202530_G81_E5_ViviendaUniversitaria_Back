package co.edu.udistrital.mdp.back.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "universidades")
public class UniversidadEntity extends BaseEntity {

    @Column(nullable = false, unique = true, length = 200)
    private String nombre;

    @Column(nullable = false, length = 200)
    private String direccion;

    @Column(nullable = false, length = 100)
    private String ciudad;

    @Column(length = 100)
    private String barrio;

    @Column(length = 15)
    private String telefono;

    @Column(length = 100)
    private String email;

    @Column(length = 100)
    private String rector;

    @Column(name = "fecha_fundacion")
    private LocalDate fechaFundacion;

    @Column(name = "tipo_institucion", length = 20)
    @Enumerated(EnumType.STRING)
    private TipoInstitucion tipoInstitucion;

    @Column(length = 500)
    private String descripcion;

    @Column(name = "url_logo", length = 500)
    private String urlLogo;

    private Double latitud;

    private Double longitud;

    public enum TipoInstitucion {
        PUBLICA,
        PRIVADA
    }
}