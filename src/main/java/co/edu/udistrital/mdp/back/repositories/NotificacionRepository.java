package co.edu.udistrital.mdp.back.repositories;

import co.edu.udistrital.mdp.back.entities.NotificacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<NotificacionEntity, Long> {

    // Encuentra todas las NotificacionEntityes para un estudiante específico (por id).
    List<NotificacionEntity> findByEstudiante_Id(Long estudianteId);

    // Encuentra todas las NotificacionEntityes no leídas para un estudiante específico (por id).
    List<NotificacionEntity> findByEstudiante_IdAndLeidaIsFalse(Long estudianteId);
}   