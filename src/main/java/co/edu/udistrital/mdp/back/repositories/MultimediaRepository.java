package co.edu.udistrital.mdp.back.repositories;

import co.edu.udistrital.mdp.back.entities.MultimediaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface MultimediaRepository extends JpaRepository<MultimediaEntity, Long> {

    // Busca todos los archivos MultimediaEntity de una vivienda
    List<MultimediaEntity> findByViviendaId(Long viviendaId);

    // Busca MultimediaEntity de una vivienda ordenado por orden de visualización
    List<MultimediaEntity> findByViviendaIdOrderByOrdenVisualizacionAsc(Long viviendaId);

    // Busca la imagen de portada de una vivienda
    Optional<MultimediaEntity> findByViviendaIdAndEsPortada(Long viviendaId, Boolean esPortada);

    // Busca MultimediaEntity por tipo de archivo (ej: "imagen", "video")
    List<MultimediaEntity> findByTipoArchivo(String tipoArchivo);

    // Busca MultimediaEntity de una vivienda por tipo
    List<MultimediaEntity> findByViviendaIdAndTipoArchivo(Long viviendaId, String tipoArchivo);

    // Cuenta cuántos archivos multimedia tiene una vivienda
    Long countByViviendaId(Long viviendaId);

    // Verifica si una vivienda tiene portada asignada
    boolean existsByViviendaIdAndEsPortada(Long viviendaId, Boolean esPortada);
}