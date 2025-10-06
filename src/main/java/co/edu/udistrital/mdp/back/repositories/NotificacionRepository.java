package co.edu.udistrital.mdp.back.repositories;

import co.edu.udistrital.mdp.back.entities.NotificacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<NotificacionEntity, Long> {

    // Encuentra todas las NotificacionEntityes para un usuario específico.
    List<NotificacionEntity> findByUsuarioId(Long usuarioId);

    // Busca NotificacionEntityes no leídas de un usuario.
    List<NotificacionEntity> findByUsuarioIdAndLeidaIsFalse(Long usuarioId);
}   