package co.edu.udistrital.mdp.back.services;

import co.edu.udistrital.mdp.back.entities.SitioInteresEntity;
import co.edu.udistrital.mdp.back.entities.ViviendaEntity;
import co.edu.udistrital.mdp.back.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.back.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.back.repositories.SitioInteresRepository;
import co.edu.udistrital.mdp.back.repositories.ViviendaRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Clase que implementa la lógica de negocio para la entidad SitioInteres.
 * Gestiona la relación ManyToMany con Vivienda según las reglas definidas.
 */
@Slf4j
@Service
public class SitioInteresService {

    @Autowired
    private SitioInteresRepository sitioInteresRepository;

    @Autowired
    private ViviendaRepository viviendaRepository;

    /**
     * [1] Crea un nuevo sitio de interés validando sus atributos.
     */
    @Transactional
    public SitioInteresEntity createSitioInteres(SitioInteresEntity sitio) throws IllegalOperationException {
        log.info("Inicia proceso de creación del sitio de interés '{}'", sitio.getNombre());

        if (sitio.getNombre() == null || sitio.getNombre().isBlank()) {
            throw new IllegalOperationException("El nombre del sitio no puede ser nulo ni vacío.");
        }

        if (sitio.getUbicacion() == null || sitio.getUbicacion().isBlank()) {
            throw new IllegalOperationException("La ubicación es obligatoria.");
        }

        if (sitio.getTiempoCaminando() == null || sitio.getTiempoCaminando() <= 0) {
            throw new IllegalOperationException("El tiempo caminando debe ser positivo.");
        }

        SitioInteresEntity nuevo = sitioInteresRepository.save(sitio);
        log.info("Sitio de interés '{}' creado correctamente con id = {}", nuevo.getNombre(), nuevo.getId());
        return nuevo;
    }

    /**
     * [2] Asocia un sitio de interés a una vivienda (ManyToMany).
     */
    @Transactional
    public SitioInteresEntity addVivienda(Long sitioId, Long viviendaId)
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Inicia proceso de asociación Vivienda {} → SitioInteres {}", viviendaId, sitioId);

        SitioInteresEntity sitio = sitioInteresRepository.findById(sitioId)
                .orElseThrow(() -> new EntityNotFoundException("Sitio de interés no encontrado."));

        ViviendaEntity vivienda = viviendaRepository.findById(viviendaId)
                .orElseThrow(() -> new EntityNotFoundException("Vivienda no encontrada."));

        if (sitio.getViviendas().contains(vivienda)) {
            throw new IllegalOperationException("La vivienda ya está asociada a este sitio de interés.");
        }

        sitio.getViviendas().add(vivienda);
        vivienda.getSitiosInteres().add(sitio);

        log.info("Vivienda {} asociada correctamente al sitio de interés {}", viviendaId, sitioId);
        return sitioInteresRepository.save(sitio);
    }

    /**
     * [3] Desasocia una vivienda de un sitio de interés.
     */
    @Transactional
    public SitioInteresEntity removeVivienda(Long sitioId, Long viviendaId)
            throws EntityNotFoundException, IllegalOperationException {
        log.info("Inicia proceso de eliminación de asociación Vivienda {} → SitioInteres {}", viviendaId, sitioId);

        SitioInteresEntity sitio = sitioInteresRepository.findById(sitioId)
                .orElseThrow(() -> new EntityNotFoundException("Sitio de interés no encontrado."));

        ViviendaEntity vivienda = viviendaRepository.findById(viviendaId)
                .orElseThrow(() -> new EntityNotFoundException("Vivienda no encontrada."));

        if (!sitio.getViviendas().contains(vivienda)) {
            throw new IllegalOperationException("La vivienda no está asociada a este sitio.");
        }

        sitio.getViviendas().remove(vivienda);
        vivienda.getSitiosInteres().remove(sitio);

        log.info("Asociación Vivienda {} → SitioInteres {} eliminada correctamente", viviendaId, sitioId);
        return sitioInteresRepository.save(sitio);
    }

    /**
     * [4] Consulta todos los sitios registrados.
     */
    @Transactional
    public List<SitioInteresEntity> getSitios() {
        log.info("Inicia proceso de consulta de todos los sitios de interés");
        return sitioInteresRepository.findAll();
    }
}
