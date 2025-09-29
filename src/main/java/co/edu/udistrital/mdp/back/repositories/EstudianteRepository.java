package co.edu.udistrital.mdp.back.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.udistrital.mdp.back.entities.Estudiante;

import java.util.List;

@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {

    // Buscar estudiante por correo
    Estudiante findByCorreo(String correo);

    // Buscar estudiantes por universidad
    List<Estudiante> findByUniversidad(String universidad);

    // Buscar estudiantes cuyo nombre contenga texto
    List<Estudiante> findByNombreContaining(String nombre);
}
