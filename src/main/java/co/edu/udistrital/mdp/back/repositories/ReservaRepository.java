package co.edu.udistrital.mdp.back.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.udistrital.mdp.back.entities.ReservaEntity;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<ReservaEntity, Long> {

    // Buscar ReservaEntitys por estado (Pendiente, Confirmada, Cancelada)
    List<ReservaEntity> findByEstado(String estado);

    // Buscar ReservaEntitys de un estudiante
    List<ReservaEntity> findByEstudianteId(Long estudianteId);

    // Buscar ReservaEntitys de una vivienda
    List<ReservaEntity> findByViviendaId(Long viviendaId);

    // Buscar ReservaEntitys activas en una fecha
    List<ReservaEntity> findByFechaInicioBeforeAndFechaFinAfter(LocalDate fecha, LocalDate fecha2);
}
