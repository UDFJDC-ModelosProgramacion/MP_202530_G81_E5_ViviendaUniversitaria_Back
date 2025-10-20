package co.edu.udistrital.mdp.back.controllers;

import co.edu.udistrital.mdp.back.dto.PreferenciaEstudianteDTO;
import co.edu.udistrital.mdp.back.dto.PreferenciaEstudianteDetailDTO;
import co.edu.udistrital.mdp.back.entities.PreferenciaEstudianteEntity;
import co.edu.udistrital.mdp.back.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.back.services.PreferenciaEstudianteService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/preferenciasEstudiante")
public class PreferenciaEstudianteController {

    /*
    @Autowired
    private PreferenciaEstudianteService preferenciaService;
    */
    
    @Autowired
    private ModelMapper modelMapper; // 

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<PreferenciaEstudianteDTO> findAll() {
        // List<PreferenciaEstudianteEntity> prefs = preferenciaService.getPreferencias();
        // return modelMapper.map(prefs, new TypeToken<List<PreferenciaEstudianteDTO>>() {}.getType());
        System.out.println("findAll() (sin servicio) - Placeholder");
        return List.of(); // Placeholder
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public PreferenciaEstudianteDetailDTO findOne(@PathVariable("id") Long id) throws EntityNotFoundException {
        // PreferenciaEstudianteEntity pref = preferenciaService.getPreferencia(id);
        // return modelMapper.map(pref, PreferenciaEstudianteDetailDTO.class);
        System.out.println("findOne(" + id + ") (sin servicio) - Placeholder");
        if (id == 1) return new PreferenciaEstudianteDetailDTO(); // Placeholder
        throw new EntityNotFoundException("Preferencia no encontrada con id: " + id);
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public PreferenciaEstudianteDTO create(@RequestBody PreferenciaEstudianteDTO dto) {
        // PreferenciaEstudianteEntity prefEntity = modelMapper.map(dto, PreferenciaEstudianteEntity.class);
        // PreferenciaEstudianteEntity nuevaPref = preferenciaService.createPreferencia(prefEntity);
        // return modelMapper.map(nuevaPref, PreferenciaEstudianteDTO.class);
        System.out.println("create() (sin servicio) - Placeholder");
        dto.setId(Long.valueOf(1L));
        return dto;
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public PreferenciaEstudianteDTO update(@PathVariable("id") Long id, @RequestBody PreferenciaEstudianteDTO dto) throws EntityNotFoundException {
        // PreferenciaEstudianteEntity prefEntity = modelMapper.map(dto, PreferenciaEstudianteEntity.class);
        // PreferenciaEstudianteEntity prefActualizada = preferenciaService.updatePreferencia(id, prefEntity);
        // return modelMapper.map(prefActualizada, PreferenciaEstudianteDTO.class);
        System.out.println("update(" + id + ") (sin servicio) - Placeholder");
        return dto;
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) throws EntityNotFoundException {
        // preferenciaService.deletePreferencia(id);
        System.out.println("delete(" + id + ") (sin servicio) - Placeholder");
    }
}