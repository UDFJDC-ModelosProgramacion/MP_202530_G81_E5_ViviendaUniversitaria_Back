package co.edu.udistrital.mdp.back.entities;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "estudiante")
public class Estudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String correo;
    private String universidad;

    @OneToMany(mappedBy = "estudiante", cascade = CascadeType.ALL)
    private List<Reserva> reservas;

    // --- Constructores ---
    public Estudiante() {}

    public Estudiante(String nombre, String correo, String universidad) {
        this.nombre = nombre;
        this.correo = correo;
        this.universidad = universidad;
    }

    // --- Getters y Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getUniversidad() { return universidad; }
    public void setUniversidad(String universidad) { this.universidad = universidad; }

    public List<Reserva> getReservas() { return reservas; }
    public void setReservas(List<Reserva> reservas) { this.reservas = reservas; }
}
