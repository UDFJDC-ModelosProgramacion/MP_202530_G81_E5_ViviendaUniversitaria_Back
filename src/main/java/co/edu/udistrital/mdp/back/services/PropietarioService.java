package co.edu.udistrital.mdp.back.services;

import co.edu.udistrital.mdp.back.dto.PropietarioDTO;
import co.edu.udistrital.mdp.back.entities.PropietarioEntity;
import co.edu.udistrital.mdp.back.repositories.PropietarioRepository;
import co.edu.udistrital.mdp.back.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.back.exceptions.ErrorMessage;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PropietarioService {

    private final PropietarioRepository propietarioRepository;
    private final ModelMapper modelMapper;

    public PropietarioDTO crearPropietario(PropietarioDTO propietarioDTO) {
        if (propietarioRepository.existsByDocumento(propietarioDTO.getDocumento())) {
            throw new IllegalArgumentException("Ya existe un propietario con el documento: " + propietarioDTO.getDocumento());
        }
        PropietarioEntity propietarioEntity = modelMapper.map(propietarioDTO, PropietarioEntity.class);
        PropietarioEntity nuevoPropietario = propietarioRepository.save(propietarioEntity);
        return modelMapper.map(nuevoPropietario, PropietarioDTO.class);
    }

    public PropietarioDTO getPropietario(Long id) throws EntityNotFoundException {
        PropietarioEntity propietario = propietarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.PROPIETARIO_NOT_FOUND));
        return modelMapper.map(propietario, PropietarioDTO.class);
    }

    public List<PropietarioDTO> getPropietarios() {
        List<PropietarioEntity> propietarios = propietarioRepository.findAll();
        return propietarios.stream()
            .map(propietario -> modelMapper.map(propietario, PropietarioDTO.class))
            .toList();
    }

    public PropietarioDTO updatePropietario(Long id, PropietarioDTO propietarioDTO) throws EntityNotFoundException {
        PropietarioEntity propietario = propietarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.PROPIETARIO_NOT_FOUND));

        propietario.setNombre(propietarioDTO.getNombre());
        propietario.setApellido(propietarioDTO.getApellido());
        propietario.setTelefono(propietarioDTO.getTelefono());
        propietario.setEmail(propietarioDTO.getEmail());
        propietario.setTipoDocumento(propietarioDTO.getTipoDocumento());
        
        PropietarioEntity updatedPropietario = propietarioRepository.save(propietario);
        return modelMapper.map(updatedPropietario, PropietarioDTO.class);
    }

    public void deletePropietario(Long id) throws EntityNotFoundException {
        if (!propietarioRepository.existsById(id)) {
            throw new EntityNotFoundException(ErrorMessage.PROPIETARIO_NOT_FOUND);
        }
        propietarioRepository.deleteById(id);
    }
}