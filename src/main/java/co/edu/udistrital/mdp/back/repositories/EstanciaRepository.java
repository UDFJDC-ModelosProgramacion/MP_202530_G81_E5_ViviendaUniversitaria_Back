package co.edu.udistrital.mdp.back.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.udistrital.mdp.back.entities.EstanciaEntity;

import java.util.List;

@Repository
public interface EstanciaRepository extends JpaRepository<EstanciaEntity, Long> {

    // Buscar EstanciaEntitys por duración
    List<EstanciaEntity> findByTiempoEstancia(Integer tiempoEstancia);

    // Buscar EstanciaEntitys por id de estudiante
    List<EstanciaEntity> findByEstudianteArrendador_Id(Long estudianteId);

    // Buscar EstanciaEntitys por id de la vivienda
    List<EstanciaEntity> findByViviendaArrendada_Id(Long viviendaId);

    Boolean existsByEstudianteIdAndViviendaIdAndEstadoCompletada(Long estudianteId, Long viviendaId);
}
