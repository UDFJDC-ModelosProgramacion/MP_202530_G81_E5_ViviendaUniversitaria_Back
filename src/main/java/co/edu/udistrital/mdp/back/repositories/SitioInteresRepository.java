package co.edu.udistrital.mdp.back.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.udistrital.mdp.back.entities.SitioInteres;

import java.util.List;

@Repository
public interface SitioInteresRepository extends JpaRepository<SitioInteres, Long> {

    // Buscar sitios de interés por nombre (contiene texto)
    List<SitioInteres> findByNombreContaining(String nombre);

    // Buscar sitios de interés de una vivienda específica
    List<SitioInteres> findByViviendaId(Long viviendaId);

    // Buscar sitios de interés por tiempo máximo caminando
    List<SitioInteres> findByTiempoCaminandoLessThanEqual(int minutos);
}
