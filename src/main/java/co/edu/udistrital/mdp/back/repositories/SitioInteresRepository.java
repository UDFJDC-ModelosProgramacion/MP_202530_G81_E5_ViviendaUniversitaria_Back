package co.edu.udistrital.mdp.back.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.udistrital.mdp.back.entities.SitioInteresEntity;

import java.util.List;

@Repository
public interface SitioInteresRepository extends JpaRepository<SitioInteresEntity, Long> {

    // Buscar sitios de interés por nombre (contiene texto)
    List<SitioInteresEntity> findByNombreContaining(String nombre);

    // Buscar sitios de interés de una vivienda específica
    List<SitioInteresEntity> findByViviendaId(Long viviendaId);

    // Buscar sitios de interés por tiempo máximo caminando
    List<SitioInteresEntity> findByTiempoCaminandoLessThanEqual(int minutos);
}
