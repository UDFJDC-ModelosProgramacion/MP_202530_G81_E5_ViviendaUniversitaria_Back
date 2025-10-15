package co.edu.udistrital.mdp.back.services;

import co.edu.udistrital.mdp.back.entities.ReservaEntity;
import co.edu.udistrital.mdp.back.entities.EstudianteEntity;
import co.edu.udistrital.mdp.back.entities.ViviendaEntity;
import co.edu.udistrital.mdp.back.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.back.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.back.repositories.ReservaRepository;
import co.edu.udistrital.mdp.back.repositories.EstudianteRepository;
import co.edu.udistrital.mdp.back.repositories.ViviendaRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Clase que implementa la lógica de negocio para la entidad Reserva.
 * Cumple con las reglas de negocio definidas en el dominio Vive Tu U.
 */
@Slf4j
@Service
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private EstudianteRepository estudianteRepository;

    @Autowired
    private ViviendaRepository viviendaRepository;

    /**
     * [1] Crea una nueva reserva aplicando las validaciones de negocio.
     */
    @Transactional
    public ReservaEntity createReserva(ReservaEntity reserva)
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Inicia proceso de creación de reserva.");

        if (reserva.getFechaInicio() == null || reserva.getFechaFin() == null) {
            throw new IllegalOperationException("Las fechas de inicio y fin son obligatorias.");
        }

        if (reserva.getFechaFin().isBefore(reserva.getFechaInicio())) {
            throw new IllegalOperationException("La fecha de fin debe ser posterior a la de inicio.");
        }

        EstudianteEntity estudiante = estudianteRepository.findById(reserva.getEstudiante().getId())
                .orElseThrow(() -> new EntityNotFoundException("El estudiante asociado no existe."));

        ViviendaEntity vivienda = viviendaRepository.findById(reserva.getVivienda().getId())
                .orElseThrow(() -> new EntityNotFoundException("La vivienda asociada no existe."));

        reserva.setEstudiante(estudiante);
        reserva.setVivienda(vivienda);
        reserva.setEstado("Pendiente");

        ReservaEntity nueva = reservaRepository.save(reserva);
        log.info("Reserva creada correctamente con id = {}", nueva.getId());
        return nueva;
    }

    /**
     * [2] Actualiza el estado de una reserva existente.
     */
    @Transactional
    public ReservaEntity updateEstado(Long id, String nuevoEstado)
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Inicia proceso de actualización de estado para la reserva {}", id);

        ReservaEntity reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("La reserva no existe."));

        if (!List.of("Pendiente", "Confirmada", "Cancelada").contains(nuevoEstado)) {
            throw new IllegalOperationException("El estado ingresado no es válido.");
        }

        reserva.setEstado(nuevoEstado);
        log.info("Estado actualizado correctamente a {}", nuevoEstado);
        return reservaRepository.save(reserva);
    }

    /**
     * [3] Elimina una reserva si está en estado Pendiente.
     */
    @Transactional
    public void deleteReserva(Long id)
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Inicia proceso de eliminación de reserva con id = {}", id);

        ReservaEntity reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("La reserva no existe."));

        if (!"Pendiente".equalsIgnoreCase(reserva.getEstado())) {
            throw new IllegalOperationException("Solo se pueden eliminar reservas pendientes.");
        }

        reservaRepository.delete(reserva);
        log.info("Reserva eliminada correctamente con id = {}", id);
    }
}
