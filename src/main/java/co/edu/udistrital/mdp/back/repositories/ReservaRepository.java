package co.edu.udistrital.mdp.back.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.udistrital.mdp.back.entities.ReservaEntity;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<ReservaEntity, Long> {

    // Buscar reservas por estado (Pendiente, Confirmada, Cancelada)
    List<ReservaEntity> findByEstado(String estado);

    // Buscar reservas de un estudiante
    List<ReservaEntity> findByEstudianteId(Long estudianteId);

    // Buscar reservas de una vivienda
    List<ReservaEntity> findByVivienda_Id(Long viviendaId);

    // Buscar reservas activas en una fecha
    List<ReservaEntity> findByFechaInicioBeforeAndFechaFinAfter(LocalDate fecha, LocalDate fecha2);
}
