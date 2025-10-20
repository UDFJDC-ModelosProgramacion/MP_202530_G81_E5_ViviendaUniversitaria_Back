package co.edu.udistrital.mdp.back.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.udistrital.mdp.back.entities.PropietarioEntity;
import co.edu.udistrital.mdp.back.entities.ViviendaEntity;
import co.edu.udistrital.mdp.back.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.back.exceptions.ErrorMessage;
import co.edu.udistrital.mdp.back.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.back.repositories.PropietarioRepository;
import co.edu.udistrital.mdp.back.repositories.ViviendaRepository;
import lombok.extern.slf4j.Slf4j; 

@Slf4j
@Service
public class PropietarioViviendaService {

    @Autowired
    private PropietarioRepository propietarioRepository;

    @Autowired
    private ViviendaRepository viviendaRepository;

    @Transactional
    public ViviendaEntity addVivienda(Long propietarioId, Long viviendaId) throws EntityNotFoundException {
        log.info("Inicia proceso de asociar una vivienda al propietario con id = {}", propietarioId);
        Optional<PropietarioEntity> propietarioEntity = propietarioRepository.findById(propietarioId);  
        Optional<ViviendaEntity> viviendaEntity = viviendaRepository.findById(viviendaId);

        if (propietarioEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.PROPIETARIO_NOT_FOUND);

        if (viviendaEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.VIVIENDA_NOT_FOUND);

        viviendaEntity.get().setPropietario(propietarioEntity.get());
        log.info("Termina proceso de asociar una vivienda al propietario con id = {}", propietarioId);
        return viviendaEntity.get();
    }

    @Transactional
    public List<ViviendaEntity> getViviendas(Long propietarioId) throws EntityNotFoundException {
        log.info("Inicia proceso de consultar todas las viviendas del propietario con id = {}", propietarioId);
        Optional<PropietarioEntity> propietarioEntity = propietarioRepository.findById(propietarioId);
        if (propietarioEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.PROPIETARIO_NOT_FOUND);

        log.info("Termina proceso de consultar todas las viviendas del propietario con id = {}", propietarioId);
        return propietarioEntity.get().getViviendas();
    }

    @Transactional
    public ViviendaEntity getVivienda(Long propietarioId, Long viviendaId) throws EntityNotFoundException, IllegalOperationException {
        log.info("Inicia proceso de consultar la vivienda con id = {} del propietario con id = {}", viviendaId, propietarioId);
        Optional<PropietarioEntity> propietarioEntity = propietarioRepository.findById(propietarioId);
        Optional<ViviendaEntity> viviendaEntity = viviendaRepository.findById(viviendaId);

        if (propietarioEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.PROPIETARIO_NOT_FOUND);

        if (viviendaEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.VIVIENDA_NOT_FOUND);

        log.info("Termina proceso de consultar la vivienda con id = {} del propietario con id = {}", viviendaId, propietarioId);
        if (!viviendaEntity.get().getPropietario().equals(propietarioEntity.get()))
            throw new IllegalOperationException("La vivienda no pertenece al propietario");

        return viviendaEntity.get();
    }

    @Transactional
    public List<ViviendaEntity> replaceViviendas(Long propietarioId, List<ViviendaEntity> viviendas) throws EntityNotFoundException {
        log.info("Inicia proceso de reemplazar las viviendas del propietario con id = {}", propietarioId);
        Optional<PropietarioEntity> propietarioEntity = propietarioRepository.findById(propietarioId);
        if (propietarioEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.PROPIETARIO_NOT_FOUND);

        for (ViviendaEntity vivienda : viviendas) {
            Optional<ViviendaEntity> viviendaEntity = viviendaRepository.findById(vivienda.getId());
            if (viviendaEntity.isEmpty())
                throw new EntityNotFoundException(ErrorMessage.VIVIENDA_NOT_FOUND);
            viviendaEntity.get().setPropietario(propietarioEntity.get());
        }

        log.info("Finaliza proceso de reemplazar las viviendas del propietario con id = {}", propietarioId);
        return viviendas;
    }

    @Transactional
    public void removeVivienda(Long propietarioId, Long viviendaId) throws EntityNotFoundException, IllegalOperationException {
        log.info("Inicia proceso de borrar una vivienda del propietario con id = {}", propietarioId);
        Optional<PropietarioEntity> propietarioEntity = propietarioRepository.findById(propietarioId);
        if (propietarioEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.PROPIETARIO_NOT_FOUND);

        Optional<ViviendaEntity> viviendaEntity = viviendaRepository.findById(viviendaId);
        if (viviendaEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.VIVIENDA_NOT_FOUND);

        if (!viviendaEntity.get().getPropietario().equals(propietarioEntity.get()))
            throw new IllegalOperationException("La vivienda no pertenece al propietario");

        viviendaEntity.get().setPropietario(null);
        log.info("Finaliza proceso de borrar una vivienda del propietario con id = {}", propietarioId);
    }
}
