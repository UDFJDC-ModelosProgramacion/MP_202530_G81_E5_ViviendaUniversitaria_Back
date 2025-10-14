package co.edu.udistrital.mdp.back.services;

import co.edu.udistrital.mdp.back.entities.NotificacionEntity;
import co.edu.udistrital.mdp.back.repositories.NotificacionRepository;
import co.edu.udistrital.mdp.back.repositories.EstudianteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final EstudianteRepository estudianteRepository;

    /**
     * CREATE - Envía una nueva notificación a un estudiante.
     *
     * Reglas aplicadas:
     * - Debe estar asociada a un Estudiante (destinatario) válido y existente.
     * - El mensaje no puede ser vacío.
     * - El estado 'leida' se inicializa en false.
     * - La 'fechaEnvio' se registra automáticamente.
     */
    public NotificacionEntity enviarNotificacion(NotificacionEntity notificacion) {
        validarDestinatario(notificacion);
        validarMensaje(notificacion);

        notificacion.setLeida(false);
        notificacion.setFechaEnvio(LocalDateTime.now());

        return notificacionRepository.save(notificacion);
    }
    
    /**
     * READ - Obtiene una notificación por su ID.
     */
    public NotificacionEntity obtenerNotificacionPorId(Long id) {
        return notificacionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Notificación no encontrada con ID: " + id));
    }

    /**
     * READ - Obtiene todas las notificaciones de un estudiante.
     */
    public List<NotificacionEntity> obtenerNotificacionesPorEstudiante(Long estudianteId) {
        return notificacionRepository.findByEstudiante_Id(estudianteId);
    }

    /**
     * UPDATE - Marca una notificación como leída.
     *
     * Reglas aplicadas:
     * - Solo se permite modificar el estado 'leida' de false a true.
     */
    public NotificacionEntity marcarComoLeida(Long id) {
        NotificacionEntity notificacion = obtenerNotificacionPorId(id);
        
        if (notificacion.getLeida()) {
             throw new IllegalStateException("La notificación ya está marcada como leída.");
        }

        notificacion.setLeida(true);
        return notificacionRepository.save(notificacion);
    }

    /**
     * DELETE - Elimina una notificación.
     * Un usuario puede limpiar sus notificaciones.
     */
    public void eliminarNotificacion(Long id) {
        if (!notificacionRepository.existsById(id)) {
            throw new IllegalArgumentException("No se puede eliminar la notificación con ID " + id + " porque no existe.");
        }
        notificacionRepository.deleteById(id);
    }


    private void validarDestinatario(NotificacionEntity notificacion) {
        if (notificacion.getEstudiante() == null || notificacion.getEstudiante().getId() == null) {
            throw new IllegalArgumentException("La notificación debe tener un destinatario.");
        }
        if (!estudianteRepository.existsById(notificacion.getEstudiante().getId())) {
            throw new IllegalArgumentException("El estudiante destinatario con ID " + notificacion.getEstudiante().getId() + " no existe.");
        }
    }

    private void validarMensaje(NotificacionEntity notificacion) {
        if (notificacion.getMensaje() == null || notificacion.getMensaje().trim().isEmpty()) {
            throw new IllegalArgumentException("El mensaje de la notificación no puede ser vacío.");
        }
    }
}