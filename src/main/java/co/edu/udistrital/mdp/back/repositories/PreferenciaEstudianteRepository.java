package co.edu.udistrital.mdp.back.repositories;

import co.edu.udistrital.mdp.back.entities.PreferenciaEstudianteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PreferenciaEstudianteRepository extends JpaRepository<PreferenciaEstudianteEntity, Long> {
    
    // Encuentra las preferencias de un estudiante por el ID del estudiante.
    Optional<PreferenciaEstudianteEntity> findByEstudianteId(Long estudianteId);
}