package co.edu.udistrital.mdp.back.repositories;

import co.edu.udistrital.mdp.back.entities.ComentarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ComentarioRepository extends JpaRepository<ComentarioEntity, Long> {

    // Busca ComentarioEntitys de una vivienda
    List<ComentarioEntity> findByViviendaId(Long viviendaId);

    // Busca ComentarioEntitys de un estudiante
    List<ComentarioEntity> findByAutorId(Long autorId);

    // Verifica si un estudiante ya comentó una vivienda
    boolean existsByViviendaIdAndAutorId(Long viviendaId, Long autorId);

    // Calcula promedio de calificaciones
    @Query("SELECT AVG(c.calificacion) FROM ComentarioEntity c WHERE c.vivienda.id = :viviendaId")
    Double calcularPromedioCalificacion(@Param("viviendaId") Long viviendaId);
}