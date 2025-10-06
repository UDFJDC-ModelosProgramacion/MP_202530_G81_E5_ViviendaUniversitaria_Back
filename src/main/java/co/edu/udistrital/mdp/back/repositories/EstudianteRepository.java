package co.edu.udistrital.mdp.back.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.udistrital.mdp.back.entities.EstudianteEntity;

import java.util.List;

@Repository
public interface EstudianteRepository extends JpaRepository<EstudianteEntity, Long> {

    // Buscar EstudianteEntity por correo
    EstudianteEntity findByCorreo(String correo);

    // Buscar EstudianteEntitys por universidad
    List<EstudianteEntity> findByUniversidad(String universidad);

    // Buscar EstudianteEntitys cuyo nombre contenga texto
    List<EstudianteEntity> findByNombreContaining(String nombre);
}
