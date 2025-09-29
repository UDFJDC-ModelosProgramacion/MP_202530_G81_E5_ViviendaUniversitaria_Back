package co.edu.udistrital.mdp.back.repositories;

import co.edu.udistrital.mdp.back.entities.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    // Encuentra todas las notificaciones para un usuario específico.
    List<Notificacion> findByUsuarioId(Long usuarioId);

    // Busca notificaciones no leídas de un usuario.
    List<Notificacion> findByUsuarioIdAndLeidaIsFalse(Long usuarioId);
}   