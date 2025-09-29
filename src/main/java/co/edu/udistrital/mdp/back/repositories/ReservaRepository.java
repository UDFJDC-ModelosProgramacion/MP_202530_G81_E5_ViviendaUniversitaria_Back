package co.edu.udistrital.mdp.back.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.udistrital.mdp.back.entities.Reserva;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    // Buscar reservas por estado (Pendiente, Confirmada, Cancelada)
    List<Reserva> findByEstado(String estado);

    // Buscar reservas de un estudiante
    List<Reserva> findByEstudianteId(Long estudianteId);

    // Buscar reservas de una vivienda
    List<Reserva> findByViviendaId(Long viviendaId);

    // Buscar reservas activas en una fecha
    List<Reserva> findByFechaInicioBeforeAndFechaFinAfter(LocalDate fecha, LocalDate fecha2);
}
