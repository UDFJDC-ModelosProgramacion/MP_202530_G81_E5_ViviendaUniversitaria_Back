package co.edu.udistrital.mdp.back.repositories;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.udistrital.mdp.back.entities.SitioInteresEntity;

import java.util.List;

@Repository
public interface SitioInteresRepository extends JpaRepository<SitioInteresEntity, Long> {

    // Buscar sitios cuyo nombre contenga una cadena
    List<SitioInteresEntity> findByNombreContaining(String nombre);

    // Buscar sitios de interés de una vivienda específica
    @Query("SELECT s FROM SitioInteresEntity s JOIN s.viviendas v WHERE v.id = :viviendaId")
    List<SitioInteresEntity> findByViviendaId(@Param("viviendaId") Long viviendaId);

    // Buscar sitios con tiempo caminando <= minutos
    List<SitioInteresEntity> findByTiempoCaminandoLessThanEqual(int minutos);
}
