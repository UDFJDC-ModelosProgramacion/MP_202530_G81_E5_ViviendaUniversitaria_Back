package co.edu.udistrital.mdp.back.services;

import co.edu.udistrital.mdp.back.entities.ReservaEntity;
import co.edu.udistrital.mdp.back.entities.EstudianteEntity;
import co.edu.udistrital.mdp.back.repositories.ReservaRepository;
import co.edu.udistrital.mdp.back.repositories.EstudianteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

/**
 * Servicio que contiene la lógica de negocio para la entidad Reserva.
 */
@Slf4j
@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private EstudianteRepository estudianteRepository;

    /** Crear una nueva reserva */
    public ReservaEntity createReserva(ReservaEntity reserva) throws Exception {
        log.info("Iniciando creación de reserva...");

        if (reserva.getFechaInicio() == null || reserva.getFechaFin() == null) {
            throw new Exception("Las fechas de inicio y fin son obligatorias.");
        }

        if (reserva.getFechaFin().isBefore(reserva.getFechaInicio())) {
            throw new Exception("La fecha de fin debe ser posterior a la de inicio.");
        }

        if (reserva.getEstudiante() == null || reserva.getEstudiante().getId() == null) {
            throw new Exception("La reserva debe estar asociada a un estudiante existente.");
        }

        EstudianteEntity estudiante = estudianteRepository.findById(reserva.getEstudiante().getId())
                .orElseThrow(() -> new Exception("El estudiante asociado no existe."));

        reserva.setEstudiante(estudiante);
        reserva.setEstado("Pendiente");

        log.info("Reserva creada correctamente para el estudiante {}", estudiante.getNombre());
        return reservaRepository.save(reserva);
    }

    /** Consultar todas las reservas */
    public List<ReservaEntity> getReservas() {
        log.info("Consultando todas las reservas...");
        return reservaRepository.findAll();
    }

    /** Actualizar el estado de una reserva */
    public ReservaEntity updateReserva(Long id, ReservaEntity actualizada) throws Exception {
        ReservaEntity existente = reservaRepository.findById(id)
                .orElseThrow(() -> new Exception("La reserva no existe."));

        String nuevoEstado = actualizada.getEstado();
        if (nuevoEstado != null && !List.of("Pendiente", "Confirmada", "Cancelada").contains(nuevoEstado)) {
            throw new Exception("Estado de reserva inválido.");
        }

        existente.setEstado(nuevoEstado);
        log.info("Reserva con ID {} actualizada a estado {}", id, nuevoEstado);
        return reservaRepository.save(existente);
    }

    /** Eliminar solo reservas pendientes */
    public void deleteReserva(Long id) throws Exception {
        ReservaEntity reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new Exception("La reserva no existe."));

        if (!"Pendiente".equalsIgnoreCase(reserva.getEstado())) {
            throw new Exception("Solo se pueden eliminar reservas pendientes.");
        }

        log.info("Eliminando reserva pendiente con ID {}", id);
        reservaRepository.delete(reserva);
    }
}
