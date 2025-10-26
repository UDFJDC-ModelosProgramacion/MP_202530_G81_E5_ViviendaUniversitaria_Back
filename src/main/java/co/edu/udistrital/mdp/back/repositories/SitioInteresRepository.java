package co.edu.udistrital.mdp.back.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import co.edu.udistrital.mdp.back.entities.SitioInteresEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SitioInteresRepository extends JpaRepository<SitioInteresEntity, Long> {

    /**
     * Verifica si existe un sitio con el mismo nombre (ignorando
     * mayúsculas/minúsculas)
     */
    boolean existsByNombreIgnoreCase(String nombre);

    /**
     * Buscar sitio de interés por nombre exacto
     */
    Optional<SitioInteresEntity> findByNombre(String nombre);

    /**
     * Buscar sitios cuyo nombre contenga una palabra o fragmento (búsqueda parcial)
     */
    List<SitioInteresEntity> findByNombreContaining(String nombre);

    /**
     * Buscar sitios por ubicación exacta
     */
    List<SitioInteresEntity> findByUbicacion(String ubicacion);

    /**
     * Buscar sitios por rango de tiempo caminando (entre dos valores, inclusive)
     */
    List<SitioInteresEntity> findByTiempoCaminandoBetween(Integer min, Integer max);

    /**
     * Buscar sitios con tiempo caminando menor o igual a cierto valor
     */
    List<SitioInteresEntity> findByTiempoCaminandoLessThanEqual(Integer max);

    /**
     * Contar cuántas viviendas están asociadas a un sitio
     */
    @Query("SELECT COUNT(v) FROM SitioInteresEntity s JOIN s.viviendas v WHERE s.id = :sitioId")
    long countViviendasAsociadas(Long sitioId);

    /**
     * Buscar sitios que no tengan viviendas asociadas
     */
    @Query("SELECT s FROM SitioInteresEntity s WHERE s.viviendas IS EMPTY")
    List<SitioInteresEntity> findSitiosSinViviendas();

    /**
     * Buscar sitios con al menos una vivienda asociada
     */
    @Query("SELECT DISTINCT s FROM SitioInteresEntity s JOIN s.viviendas v")
    List<SitioInteresEntity> findSitiosConViviendas();

    /**
     * Buscar sitios con un número mínimo de viviendas asociadas
     */
    @Query("SELECT s FROM SitioInteresEntity s WHERE SIZE(s.viviendas) >= :minimo")
    List<SitioInteresEntity> findSitiosConMinimoViviendas(int minimo);
}
