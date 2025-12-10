package co.edu.udistrital.mdp.back.services;

import co.edu.udistrital.mdp.back.dto.UniversidadDTO;
import co.edu.udistrital.mdp.back.entities.UniversidadEntity;
import co.edu.udistrital.mdp.back.repositories.UniversidadRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UniversidadService {

    @Autowired
    private UniversidadRepository universidadRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Transactional(readOnly = true)
    public List<UniversidadDTO> getAll() {
        return universidadRepository.findAll().stream()
                .map(entity -> modelMapper.map(entity, UniversidadDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UniversidadDTO getById(Long id) {
        UniversidadEntity entity = universidadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Universidad no encontrada con id: " + id));
        return modelMapper.map(entity, UniversidadDTO.class);
    }

    @Transactional
    public UniversidadDTO create(UniversidadDTO universidadDTO) {
        UniversidadEntity entity = modelMapper.map(universidadDTO, UniversidadEntity.class);
        UniversidadEntity savedEntity = universidadRepository.save(entity);
        return modelMapper.map(savedEntity, UniversidadDTO.class);
    }

    @Transactional
    public UniversidadDTO update(Long id, UniversidadDTO universidadDTO) {
        UniversidadEntity entity = universidadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Universidad no encontrada con id: " + id));

        entity.setNombre(universidadDTO.getNombre());
        entity.setDireccion(universidadDTO.getDireccion());
        entity.setCiudad(universidadDTO.getCiudad());
        entity.setBarrio(universidadDTO.getBarrio());
        entity.setTelefono(universidadDTO.getTelefono());
        entity.setEmail(universidadDTO.getEmail());
        entity.setRector(universidadDTO.getRector());
        entity.setFechaFundacion(universidadDTO.getFechaFundacion());
        entity.setDescripcion(universidadDTO.getDescripcion());
        entity.setUrlLogo(universidadDTO.getUrlLogo());
        entity.setLatitud(universidadDTO.getLatitud());
        entity.setLongitud(universidadDTO.getLongitud());

        UniversidadEntity updatedEntity = universidadRepository.save(entity);
        return modelMapper.map(updatedEntity, UniversidadDTO.class);
    }

    @Transactional
    public void delete(Long id) {
        if (!universidadRepository.existsById(id)) {
            throw new RuntimeException("Universidad no encontrada con id: " + id);
        }
        universidadRepository.deleteById(id);
    }
}
