package co.edu.udistrital.mdp.back.repositories;

import co.edu.udistrital.mdp.back.entities.ReservaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservaRepository extends JpaRepository<ReservaEntity, Long> {

        /**
         * Buscar reservas por estado exacto (ej: Pendiente, Confirmada, Cancelada)
         */
        List<ReservaEntity> findByEstadoIgnoreCase(String estado);

        /**
         * Buscar reservas asociadas a un estudiante por su ID
         */
        List<ReservaEntity> findByEstudiante_Id(Long estudianteId);

        /**
         * Buscar reservas asociadas a una vivienda por su ID
         */
        List<ReservaEntity> findByVivienda_Id(Long viviendaId);

        /**
         * Método simplificado para buscar por viviendaId (usado en estadísticas)
         */
        List<ReservaEntity> findByViviendaId(Long viviendaId);

        /**
         * Buscar reservas activas entre dos fechas (intersección de rango)
         */
        @Query("""
                        SELECT r FROM ReservaEntity r
                        WHERE r.fechaInicio <= :fechaFin
                          AND r.fechaFin >= :fechaInicio
                          AND LOWER(r.estado) = 'confirmada'
                        """)
        List<ReservaEntity> findReservasActivasEnRango(LocalDate fechaInicio, LocalDate fechaFin);

        /**
         * Buscar reservas activas (estado Confirmada y fecha actual entre inicio y fin)
         */
        @Query("""
                        SELECT r FROM ReservaEntity r
                        WHERE CURRENT_DATE BETWEEN r.fechaInicio AND r.fechaFin
                          AND LOWER(r.estado) = 'confirmada'
                        """)
        List<ReservaEntity> findReservasActivasHoy();

        /**
         * Buscar reservas pendientes (estado = Pendiente)
         */
        @Query("SELECT r FROM ReservaEntity r WHERE LOWER(r.estado) = 'pendiente'")
        List<ReservaEntity> findReservasPendientes();

        /**
         * Verificar si ya existe una reserva activa (confirmada o pendiente)
         * entre un estudiante y una vivienda en un rango de fechas.
         */
        @Query("""
                        SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END
                        FROM ReservaEntity r
                        WHERE r.estudiante.id = :estudianteId
                          AND r.vivienda.id = :viviendaId
                          AND r.fechaInicio <= :fechaFin
                          AND r.fechaFin >= :fechaInicio
                          AND LOWER(r.estado) IN ('pendiente', 'confirmada')
                        """)
        boolean existeReservaActiva(Long estudianteId, Long viviendaId, LocalDate fechaInicio, LocalDate fechaFin);

        /**
         * Buscar la última reserva (más reciente) de un estudiante
         */
        @Query("""
                        SELECT r FROM ReservaEntity r
                        WHERE r.estudiante.id = :estudianteId
                        ORDER BY r.fechaInicio DESC
                        """)
        List<ReservaEntity> findUltimasReservasPorEstudiante(Long estudianteId);

        /**
         * Buscar una reserva exacta por estudiante y vivienda
         */
        Optional<ReservaEntity> findByEstudiante_IdAndVivienda_Id(Long estudianteId, Long viviendaId);

        /**
         * Eliminar todas las reservas canceladas antes de una fecha dada
         */
        @Query("DELETE FROM ReservaEntity r WHERE LOWER(r.estado) = 'cancelada' AND r.fechaFin < :fechaCorte")
        void eliminarReservasCanceladasAntiguas(LocalDate fechaCorte);
}
