package co.edu.udistrital.mdp.back.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.back.entities.UniversidadCercaEntity;
import co.edu.udistrital.mdp.back.entities.ViviendaEntity;
import co.edu.udistrital.mdp.back.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.back.exceptions.ErrorMessage;
import co.edu.udistrital.mdp.back.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.back.repositories.UniversidadCercaRepository;
import co.edu.udistrital.mdp.back.repositories.ViviendaRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UniversidadCercaViviendaService {

    @Autowired
    private UniversidadCercaRepository universidadCercaRepository;

    @Autowired
    private ViviendaRepository viviendaRepository;

    @Transactional
    public ViviendaEntity addVivienda(Long universidadCercaId, Long viviendaId) throws EntityNotFoundException {
        log.info("Inicia proceso de asociar vivienda a universidad cerca con id = {}", universidadCercaId);
        Optional<UniversidadCercaEntity> universidadCercaEntity = universidadCercaRepository.findById(universidadCercaId);
        Optional<ViviendaEntity> viviendaEntity = viviendaRepository.findById(viviendaId);

        if (universidadCercaEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.UNIVERSIDAD_CERCA_NOT_FOUND);

        if (viviendaEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.VIVIENDA_NOT_FOUND);

        universidadCercaEntity.get().addVivienda(viviendaEntity.get());
        log.info("Termina proceso de asociar vivienda a universidad cerca con id = {}", universidadCercaId);
        return viviendaEntity.get();
    }

    @Transactional
    public List<ViviendaEntity> getViviendas(Long universidadCercaId) throws EntityNotFoundException {
        log.info("Inicia proceso de consultar viviendas de universidad cerca con id = {}", universidadCercaId);
        Optional<UniversidadCercaEntity> universidadCercaEntity = universidadCercaRepository.findById(universidadCercaId);
        if (universidadCercaEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.UNIVERSIDAD_CERCA_NOT_FOUND);

        log.info("Termina proceso de consultar viviendas de universidad cerca con id = {}", universidadCercaId);
        return universidadCercaEntity.get().getViviendas();
    }

    @Transactional
    public ViviendaEntity getVivienda(Long universidadCercaId, Long viviendaId) throws EntityNotFoundException, IllegalOperationException {
        log.info("Inicia proceso de consultar vivienda con id = {} de universidad cerca con id = {}", viviendaId, universidadCercaId);
        Optional<UniversidadCercaEntity> universidadCercaEntity = universidadCercaRepository.findById(universidadCercaId);
        Optional<ViviendaEntity> viviendaEntity = viviendaRepository.findById(viviendaId);

        if (universidadCercaEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.UNIVERSIDAD_CERCA_NOT_FOUND);

        if (viviendaEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.VIVIENDA_NOT_FOUND);

        log.info("Termina proceso de consultar vivienda con id = {} de universidad cerca con id = {}", viviendaId, universidadCercaId);
        if (!viviendaEntity.get().getUniversidadCerca().equals(universidadCercaEntity.get()))
            throw new IllegalOperationException("La vivienda no está asociada a esta universidad");

        return viviendaEntity.get();
    }

    @Transactional
    public void removeVivienda(Long universidadCercaId, Long viviendaId) throws EntityNotFoundException, IllegalOperationException {
        log.info("Inicia proceso de desasociar vivienda de universidad cerca con id = {}", universidadCercaId);
        Optional<UniversidadCercaEntity> universidadCercaEntity = universidadCercaRepository.findById(universidadCercaId);
        if (universidadCercaEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.UNIVERSIDAD_CERCA_NOT_FOUND);

        Optional<ViviendaEntity> viviendaEntity = viviendaRepository.findById(viviendaId);
        if (viviendaEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.VIVIENDA_NOT_FOUND);

        if (!viviendaEntity.get().getUniversidadCerca().equals(universidadCercaEntity.get()))
            throw new IllegalOperationException("La vivienda no está asociada a esta universidad");

        universidadCercaEntity.get().removeVivienda(viviendaEntity.get());
        log.info("Finaliza proceso de desasociar vivienda de universidad cerca con id = {}", universidadCercaId);
    }
}