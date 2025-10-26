package co.edu.udistrital.mdp.back.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import co.edu.udistrital.mdp.back.entities.EstudianteEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstudianteRepository extends JpaRepository<EstudianteEntity, Long> {

    /**
     * Verifica si existe un estudiante con el mismo correo (sin distinción de
     * mayúsculas/minúsculas)
     * --> método derivado soportado por Spring Data JPA
     */
    boolean existsByCorreoIgnoreCase(String correo);

    /**
     * Buscar estudiante por correo exacto
     */
    Optional<EstudianteEntity> findByCorreo(String correo);

    /**
     * Buscar estudiantes por universidad
     */
    List<EstudianteEntity> findByUniversidad(String universidad);

    /**
     * Buscar estudiantes cuyo nombre contenga cierto texto
     */
    List<EstudianteEntity> findByNombreContaining(String nombre);

    /**
     * Cuenta reservas activas asociadas a un estudiante (ejemplo JPQL).
     * Ajusta el nombre de las entidades/columnas a tu modelo real si es necesario.
     */
    @Query("SELECT COUNT(r) FROM ReservaEntity r WHERE r.estudiante.id = :id AND r.estado = 'ACTIVA'")
    long countReservasActivasByEstudianteId(Long id);

    /**
     * Cuenta estancias activas asociadas a un estudiante (ejemplo JPQL).
     * Ajusta la condición según tu modelo (aquí se toma fechaFin NULL como activo).
     */
    @Query("SELECT COUNT(e) FROM EstanciaEntity e WHERE e.estudiante.id = :id AND e.fechaFin IS NULL")
    long countEstanciasActivasByEstudianteId(Long id);
}
