package co.edu.udistrital.mdp.back.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.udistrital.mdp.back.entities.EstudianteEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstudianteRepository extends JpaRepository<EstudianteEntity, Long> {

    // Buscar estudiante por correo
    Optional<EstudianteEntity> findByCorreo(String correo);

    // Buscar estudiantes por universidad
    List<EstudianteEntity> findByUniversidad(String universidad);

    // Buscar estudiantes cuyo nombre contenga texto
    List<EstudianteEntity> findByNombreContaining(String nombre);
}
