package co.edu.udistrital.mdp.back.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.udistrital.mdp.back.entities.EstanciaEntity;
import co.edu.udistrital.mdp.back.entities.EstanciaEntity.EstadoEstancia;

import java.util.List;

@Repository
public interface EstanciaRepository extends JpaRepository<EstanciaEntity, Long> {

    // Buscar estancias por estado
    List<EstanciaEntity> findByEstado(EstadoEstancia estado);

    // Buscar EstanciaEntitys por duración
    List<EstanciaEntity> findByTiempoEstancia(Integer tiempoEstancia);

    // Buscar EstanciaEntitys por id de estudiante
    List<EstanciaEntity> findByEstudianteArrendador_IdAndEstado(Long estudianteId, EstadoEstancia estado);

    // Buscar EstanciaEntitys por id de la vivienda
    List<EstanciaEntity> findByViviendaArrendada_IdAndEstado(Long viviendaId, EstadoEstancia estado);

    Boolean existsByEstudianteArrendador_IdAndViviendaArrendada_IdAndEstado(
            Long estudianteId,
            Long viviendaId,
            EstadoEstancia estado);
}
