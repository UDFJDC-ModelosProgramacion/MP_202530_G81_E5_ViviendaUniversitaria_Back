package co.edu.udistrital.mdp.back.services;

import co.edu.udistrital.mdp.back.entities.SitioInteresEntity;
import co.edu.udistrital.mdp.back.repositories.SitioInteresRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Servicio que contiene la lógica de negocio para la entidad SitioInteres.
 */
@Slf4j
@Service
public class SitioInteresService {

    @Autowired
    private SitioInteresRepository sitioRepository;

    /** Crear un nuevo sitio de interés */
    public SitioInteresEntity createSitio(SitioInteresEntity sitio) throws Exception {
        log.info("Iniciando creación de sitio de interés...");

        if (sitio.getNombre() == null || sitio.getNombre().isBlank()) {
            throw new Exception("El nombre del sitio no puede ser nulo ni vacío.");
        }

        if (sitio.getUbicacion() == null || sitio.getUbicacion().isBlank()) {
            throw new Exception("La ubicación es obligatoria.");
        }

        if (sitio.getTiempoCaminando() == null || sitio.getTiempoCaminando() <= 0) {
            throw new Exception("El tiempo caminando debe ser un número positivo.");
        }

        log.info("Sitio de interés '{}' creado correctamente.", sitio.getNombre());
        return sitioRepository.save(sitio);
    }

    /** Consultar todos los sitios de interés */
    public List<SitioInteresEntity> getSitios() {
        log.info("Consultando todos los sitios de interés...");
        return sitioRepository.findAll();
    }

    /** Actualizar campos permitidos (foto y tiempoCaminando) */
    public SitioInteresEntity updateSitio(Long id, SitioInteresEntity nuevo) throws Exception {
        SitioInteresEntity existente = sitioRepository.findById(id)
                .orElseThrow(() -> new Exception("El sitio no existe."));

        existente.setFoto(nuevo.getFoto());
        existente.setTiempoCaminando(nuevo.getTiempoCaminando());

        log.info("Sitio de interés '{}' actualizado correctamente.", existente.getNombre());
        return sitioRepository.save(existente);
    }

    /** Eliminar un sitio de interés */
    public void deleteSitio(Long id) throws Exception {
        SitioInteresEntity sitio = sitioRepository.findById(id)
                .orElseThrow(() -> new Exception("El sitio no existe."));

        log.info("Eliminando sitio de interés '{}'", sitio.getNombre());
        sitioRepository.delete(sitio);
    }
}
