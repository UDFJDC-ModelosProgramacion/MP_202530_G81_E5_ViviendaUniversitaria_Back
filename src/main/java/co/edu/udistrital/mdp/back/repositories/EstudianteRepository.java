package co.edu.udistrital.mdp.back.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    @Query("SELECT COUNT(r) FROM ReservaEntity r WHERE r.estudiante.id = :estudianteId AND LOWER(r.estado) = 'confirmada'") // Usar LOWER() para comparar strings
    long countReservasActivasByEstudianteId(@Param("estudianteId") Long estudianteId); // Asegúrate que el parámetro coincida

    /**
     * Cuenta estancias activas asociadas a un estudiante (ejemplo JPQL).
     * Ajusta la condición según tu modelo (aquí se toma fechaFin NULL como activo).
     */
    @Query("SELECT COUNT(e) FROM EstanciaEntity e WHERE e.estudianteArrendador.id = :estudianteId AND e.estado = co.edu.udistrital.mdp.back.entities.EstanciaEntity$EstadoEstancia.ACTIVA")
    long countEstanciasActivasByEstudianteId(@Param("estudianteId") Long estudianteId);
}
