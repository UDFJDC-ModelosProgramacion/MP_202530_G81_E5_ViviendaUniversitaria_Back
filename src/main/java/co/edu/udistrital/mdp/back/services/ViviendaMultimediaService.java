package co.edu.udistrital.mdp.back.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.back.entities.ViviendaEntity;
import co.edu.udistrital.mdp.back.entities.MultimediaEntity;
import co.edu.udistrital.mdp.back.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.back.exceptions.ErrorMessage;
import co.edu.udistrital.mdp.back.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.back.repositories.ViviendaRepository;
import co.edu.udistrital.mdp.back.repositories.MultimediaRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ViviendaMultimediaService {

    @Autowired
    private ViviendaRepository viviendaRepository;

    @Autowired
    private MultimediaRepository multimediaRepository;

    @Transactional
    public MultimediaEntity addMultimedia(Long viviendaId, Long multimediaId) throws EntityNotFoundException {
        log.info("Inicia proceso de asociar multimedia a la vivienda con id = {}", viviendaId);
        Optional<ViviendaEntity> viviendaEntity = viviendaRepository.findById(viviendaId);
        Optional<MultimediaEntity> multimediaEntity = multimediaRepository.findById(multimediaId);

        if (viviendaEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.VIVIENDA_NOT_FOUND);

        if (multimediaEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.MULTIMEDIA_NOT_FOUND);

        multimediaEntity.get().setVivienda(viviendaEntity.get());
        log.info("Termina proceso de asociar multimedia a la vivienda con id = {}", viviendaId);
        return multimediaEntity.get();
    }

    @Transactional
    public List<MultimediaEntity> getMultimedia(Long viviendaId) throws EntityNotFoundException {
        log.info("Inicia proceso de consultar todos los archivos multimedia de la vivienda con id = {}", viviendaId);
        Optional<ViviendaEntity> viviendaEntity = viviendaRepository.findById(viviendaId);
        if (viviendaEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.VIVIENDA_NOT_FOUND);

        log.info("Termina proceso de consultar todos los archivos multimedia de la vivienda con id = {}", viviendaId);
        return viviendaEntity.get().getMultimedia();
    }

    @Transactional
    public MultimediaEntity getMultimediaItem(Long viviendaId, Long multimediaId) throws EntityNotFoundException, IllegalOperationException {
        log.info("Inicia proceso de consultar el archivo multimedia con id = {} de la vivienda con id = {}", multimediaId, viviendaId);
        Optional<ViviendaEntity> viviendaEntity = viviendaRepository.findById(viviendaId);
        Optional<MultimediaEntity> multimediaEntity = multimediaRepository.findById(multimediaId);

        if (viviendaEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.VIVIENDA_NOT_FOUND);

        if (multimediaEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.MULTIMEDIA_NOT_FOUND);

        log.info("Termina proceso de consultar el archivo multimedia con id = {} de la vivienda con id = {}", multimediaId, viviendaId);
        if (!multimediaEntity.get().getVivienda().equals(viviendaEntity.get()))
            throw new IllegalOperationException("El archivo multimedia no pertenece a la vivienda");

        return multimediaEntity.get();
    }

    @Transactional
    public List<MultimediaEntity> replaceMultimedia(Long viviendaId, List<MultimediaEntity> multimedia) throws EntityNotFoundException {
        log.info("Inicia proceso de reemplazar los archivos multimedia de la vivienda con id = {}", viviendaId);
        Optional<ViviendaEntity> viviendaEntity = viviendaRepository.findById(viviendaId);
        if (viviendaEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.VIVIENDA_NOT_FOUND);

        for (MultimediaEntity item : multimedia) {
            Optional<MultimediaEntity> multimediaEntity = multimediaRepository.findById(item.getId());
            if (multimediaEntity.isEmpty())
                throw new EntityNotFoundException(ErrorMessage.MULTIMEDIA_NOT_FOUND);
            multimediaEntity.get().setVivienda(viviendaEntity.get());
        }

        log.info("Finaliza proceso de reemplazar los archivos multimedia de la vivienda con id = {}", viviendaId);
        return multimedia;
    }

    @Transactional
    public void removeMultimedia(Long viviendaId, Long multimediaId) throws EntityNotFoundException, IllegalOperationException {
        log.info("Inicia proceso de borrar un archivo multimedia de la vivienda con id = {}", viviendaId);
        Optional<ViviendaEntity> viviendaEntity = viviendaRepository.findById(viviendaId);
        if (viviendaEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.VIVIENDA_NOT_FOUND);

        Optional<MultimediaEntity> multimediaEntity = multimediaRepository.findById(multimediaId);
        if (multimediaEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.MULTIMEDIA_NOT_FOUND);

        if (!multimediaEntity.get().getVivienda().equals(viviendaEntity.get()))
            throw new IllegalOperationException("El archivo multimedia no pertenece a la vivienda");

        viviendaEntity.get().getMultimedia().remove(multimediaEntity.get());
        log.info("Finaliza proceso de borrar un archivo multimedia de la vivienda con id = {}", viviendaId);
    }
}