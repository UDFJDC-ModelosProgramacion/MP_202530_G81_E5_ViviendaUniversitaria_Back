package co.edu.udistrital.mdp.back.repositories;

import co.edu.udistrital.mdp.back.entities.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ComentarioRepository extends JpaRepository<Comentario, Long> {

    // Busca comentarios de una vivienda
    List<Comentario> findByViviendaId(Long viviendaId);

    // Busca comentarios de un estudiante
    List<Comentario> findByAutorId(Long autorId);

    // Verifica si un estudiante ya comentó una vivienda
    boolean existsByViviendaIdAndAutorId(Long viviendaId, Long autorId);

    // Calcula promedio de calificaciones
    @Query("SELECT AVG(c.calificacion) FROM Comentario c WHERE c.vivienda.id = :viviendaId")
    Double calcularPromedioCalificacion(@Param("viviendaId") Long viviendaId);
}