package co.edu.udistrital.mdp.back.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.udistrital.mdp.back.entities.SitioInteresEntity;

import java.util.List;

@Repository
public interface SitioInteresRepository extends JpaRepository<SitioInteresEntity, Long> {

    // Buscar sitios cuyo nombre contenga una cadena
    List<SitioInteresEntity> findByNombreContaining(String nombre);

    // Buscar sitios por vivienda (relación many-to-many)
    List<SitioInteresEntity> findByViviendas_Id(Long viviendaId);

    // Buscar sitios con tiempo caminando <= minutos
    List<SitioInteresEntity> findByTiempoCaminandoLessThanEqual(int minutos);
}
