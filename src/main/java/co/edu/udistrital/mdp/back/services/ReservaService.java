package co.edu.udistrital.mdp.back.services;

import co.edu.udistrital.mdp.back.entities.ReservaEntity;
import co.edu.udistrital.mdp.back.entities.EstudianteEntity;
import co.edu.udistrital.mdp.back.entities.ViviendaEntity;
import co.edu.udistrital.mdp.back.repositories.ReservaRepository;
import co.edu.udistrital.mdp.back.repositories.EstudianteRepository;
import co.edu.udistrital.mdp.back.repositories.ViviendaRepository;
import co.edu.udistrital.mdp.back.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.back.exceptions.IllegalOperationException;

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
    private final EstudianteRepository estudianteRepo;
    private final ViviendaRepository viviendaRepo;

    /**
     * CREATE - Crear reserva validando:
     * - Estudiante y Vivienda existen
     * - Vivienda disponible
     * - Fechas válidas
     * 
     * @throws IllegalOperationException
     */
    public ReservaEntity createReserva(ReservaEntity in) throws IllegalOperationException {
        if (in == null)
            throw new IllegalArgumentException("Entidad Reserva obligatoria");
        if (in.getEstudiante() == null || in.getEstudiante().getId() == null)
            throw new IllegalArgumentException("Debe asociarse un estudiante válido");
        if (in.getVivienda() == null || in.getVivienda().getId() == null)
            throw new IllegalArgumentException("Debe asociarse una vivienda válida");

        EstudianteEntity estudiante = estudianteRepo.findById(in.getEstudiante().getId())
                .orElseThrow(() -> new IllegalArgumentException("Estudiante no encontrado"));
        ViviendaEntity vivienda = viviendaRepo.findById(in.getVivienda().getId())
                .orElseThrow(() -> new IllegalArgumentException("Vivienda no encontrada"));

        if (!Boolean.TRUE.equals(vivienda.getDisponible())) {
            throw new IllegalOperationException("La vivienda no está disponible para reservar");
        }

        if (in.getFechaInicio() == null || in.getFechaFin() == null)
            throw new IllegalArgumentException("Las fechas de la reserva son obligatorias");
        if (!in.getFechaInicio().isBefore(in.getFechaFin()))
            throw new IllegalArgumentException("La fecha de inicio debe ser anterior a la fecha de fin");
        if (in.getFechaInicio().isBefore(LocalDate.now()))
            throw new IllegalArgumentException("No se puede reservar una fecha en el pasado");

        in.setEstado("Pendiente");
        ReservaEntity saved = reservaRepo.save(in);

        // Marcar vivienda como no disponible
        vivienda.setDisponible(false);
        viviendaRepo.save(vivienda);

        return saved;
    }

    /**
     * READ - Obtener todas las reservas
     */
    public List<ReservaEntity> getReservas() {
        return reservaRepo.findAll();
    }

    /**
     * READ - Obtener reserva por ID
     */
    public ReservaEntity getReserva(Long id) {
        return reservaRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada con ID: " + id));
    }

    /**
     * UPDATE - Cambiar estado de la reserva (Confirmada, Cancelada, Finalizada)
     */
    @Transactional
    public ReservaEntity updateReserva(Long id, ReservaEntity in)
            throws EntityNotFoundException, IllegalOperationException {
        ReservaEntity found = getReserva(id);

        if (in.getFechaInicio() != null)
            found.setFechaInicio(in.getFechaInicio());
        if (in.getFechaFin() != null)
            found.setFechaFin(in.getFechaFin());
        if (in.getEstado() != null)
            found.setEstado(in.getEstado());

        return reservaRepo.save(found);
    }

    /**
     * DELETE - Eliminar reserva solo si está cancelada
     * 
     * @throws IllegalOperationException
     */
    public void deleteReserva(Long id) throws IllegalOperationException {
        ReservaEntity found = getReserva(id);
        if (!"Cancelada".equals(found.getEstado())) {
            throw new IllegalOperationException("Solo se pueden eliminar reservas canceladas");
        }

        ViviendaEntity vivienda = found.getVivienda();
        vivienda.setDisponible(true);
        viviendaRepo.save(vivienda);

        reservaRepo.delete(found);
    }
}
