package co.edu.udistrital.mdp.back.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.udistrital.mdp.back.entities.Estancia;

import java.util.List;

@Repository
public interface EstanciaRepository extends JpaRepository<Estancia, Long> {

    // Buscar estancias por duración
    List<Estancia> findByTiempoEstancia(Integer tiempoEstancia);

    // Buscar estancias por id de estudiante
    List<Estancia> findByEstudianteArrendador_Id(Long estudianteId);

    // Buscar estancias por id de la vivienda
    List<Estancia> findByViviendaArrendada_Id(Long viviendaId);
}
