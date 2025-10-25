package co.edu.udistrital.mdp.back.services;

import co.edu.udistrital.mdp.back.entities.ReservaEntity;
import co.edu.udistrital.mdp.back.entities.ViviendaEntity;
import co.edu.udistrital.mdp.back.entities.EstudianteEntity;
import co.edu.udistrital.mdp.back.repositories.ReservaRepository;
import co.edu.udistrital.mdp.back.repositories.ViviendaRepository;
import co.edu.udistrital.mdp.back.repositories.EstudianteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReservaService {

    private final ReservaRepository reservaRepo;
    private final ViviendaRepository viviendaRepo;
    private final EstudianteRepository estudianteRepo;

    /**
     * CREATE - crea una reserva validando reglas:
     * - estudiante y vivienda existen
     * - fechas válidas (inicio antes de fin, no nulas)
     * - no se solapan con reservas activas existentes
     */
    public ReservaEntity createReserva(ReservaEntity in) {
        if (in == null)
            throw new IllegalArgumentException("Entidad Reserva es obligatoria");

        // Validar estudiante
        if (in.getEstudiante() == null || in.getEstudiante().getId() == null)
            throw new IllegalArgumentException("Debe indicar el estudiante que realiza la reserva");

        EstudianteEntity estudiante = estudianteRepo.findById(in.getEstudiante().getId())
                .orElseThrow(() -> new IllegalArgumentException("Estudiante no encontrado"));

        // Validar vivienda
        if (in.getVivienda() == null || in.getVivienda().getId() == null)
            throw new IllegalArgumentException("Debe indicar la vivienda a reservar");

        ViviendaEntity vivienda = viviendaRepo.findById(in.getVivienda().getId())
                .orElseThrow(() -> new IllegalArgumentException("Vivienda no encontrada"));

        // Validar fechas
        if (in.getFechaInicio() == null || in.getFechaFin() == null)
            throw new IllegalArgumentException("Las fechas de inicio y fin son obligatorias");
        if (in.getFechaInicio().isAfter(in.getFechaFin()))
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");

        // Validar solapamiento con otras reservas activas (pendiente o confirmada)
        boolean existeActiva = reservaRepo.existeReservaActiva(
                estudiante.getId(),
                vivienda.getId(),
                in.getFechaInicio(),
                in.getFechaFin());

        if (existeActiva)
            throw new IllegalArgumentException(
                    "Ya existe una reserva activa para este estudiante y vivienda en el rango de fechas indicado");

        // Validar estado
        String estado = safeTrim(in.getEstado());
        if (estado.isBlank())
            estado = "Pendiente";
        in.setEstado(estado);

        // Asignar relaciones seguras
        in.setEstudiante(estudiante);
        in.setVivienda(vivienda);

        return reservaRepo.save(in);
    }

    /**
     * READ - obtener todas las reservas
     */
    public List<ReservaEntity> getAllReservas() {
        return reservaRepo.findAll();
    }

    /**
     * READ - obtener reserva por id
     */
    public ReservaEntity getReserva(Long id) {
        return reservaRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada con ID: " + id));
    }

    /**
     * UPDATE - actualiza estado o fechas de una reserva existente
     */
    public ReservaEntity updateReserva(Long id, ReservaEntity updates) {
        ReservaEntity found = getReserva(id);

        // Actualizar fechas si vienen y son válidas
        if (updates.getFechaInicio() != null && updates.getFechaFin() != null) {
            if (updates.getFechaInicio().isAfter(updates.getFechaFin()))
                throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");
            found.setFechaInicio(updates.getFechaInicio());
            found.setFechaFin(updates.getFechaFin());
        }

        // Actualizar estado si viene y es válido
        if (updates.getEstado() != null) {
            String nuevoEstado = safeTrim(updates.getEstado());
            if (nuevoEstado.isBlank())
                throw new IllegalArgumentException("El estado no puede estar vacío");
            found.setEstado(nuevoEstado);
        }

        return reservaRepo.save(found);
    }

    /**
     * DELETE - elimina una reserva si ya está cancelada o finalizada.
     */
    public void deleteReserva(Long id) {
        ReservaEntity found = getReserva(id);

        String estado = safeTrim(found.getEstado()).toLowerCase();
        if (estado.equals("confirmada") || estado.equals("pendiente")) {
            throw new IllegalStateException("No se puede eliminar una reserva activa (confirmada o pendiente)");
        }

        reservaRepo.delete(found);
    }

    /**
     * Obtener reservas activas actualmente (confirmadas)
     */
    public List<ReservaEntity> getReservasActivasHoy() {
        return reservaRepo.findReservasActivasHoy();
    }

    /**
     * Obtener reservas pendientes
     */
    public List<ReservaEntity> getReservasPendientes() {
        return reservaRepo.findReservasPendientes();
    }

    private String safeTrim(String s) {
        return s == null ? "" : s.trim();
    }
}
