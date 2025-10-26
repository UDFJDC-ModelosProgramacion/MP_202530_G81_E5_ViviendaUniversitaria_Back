package co.edu.udistrital.mdp.back.repositories;

import co.edu.udistrital.mdp.back.entities.EstudianteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstudianteRepository extends JpaRepository<EstudianteEntity, Long> {

    // Buscar estudiante por correo exacto
    Optional<EstudianteEntity> findByCorreo(String correo);

    // Buscar estudiantes por universidad
    List<EstudianteEntity> findByUniversidad(String universidad);

    // Buscar estudiantes cuyo nombre contenga texto
    List<EstudianteEntity> findByNombreContaining(String nombre);

    // Verificar si ya existe un estudiante con el mismo correo (ignorando
    // mayúsculas/minúsculas)
    boolean existsByCorreoIgnoreCase(String correo);

    /**
     * Contar cuántas estancias activas tiene un estudiante (fechaFin nula)
     * usando el nombre correcto del campo: estudianteArrendador.
     */
    @Query("""
                SELECT COUNT(e)
                FROM EstanciaEntity e
                WHERE e.estudianteArrendador.id = :id
                  AND e.fechaFin IS NULL
            """)
    long countEstanciasActivasByEstudianteId(Long id);

    /**
     * Contar cuántas reservas activas (Pendiente o Confirmada)
     * tiene un estudiante actualmente.
     */
    @Query("""
                SELECT COUNT(r)
                FROM ReservaEntity r
                WHERE r.estudiante.id = :id
                  AND (r.estado = 'Pendiente' OR r.estado = 'Confirmada')
            """)
    long countReservasActivasByEstudianteId(Long id);
}
