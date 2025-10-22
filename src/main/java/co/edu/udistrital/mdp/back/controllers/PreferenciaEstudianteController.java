package co.edu.udistrital.mdp.back.controllers;

import co.edu.udistrital.mdp.back.dto.PreferenciaEstudianteDTO;
import co.edu.udistrital.mdp.back.dto.PreferenciaEstudianteDetailDTO;
import co.edu.udistrital.mdp.back.entities.PreferenciaEstudianteEntity;
import co.edu.udistrital.mdp.back.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.back.services.PreferenciaEstudianteService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/preferenciasEstudiante")
public class PreferenciaEstudianteController {

    @Autowired
    private PreferenciaEstudianteService preferenciaService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<PreferenciaEstudianteDTO> findAll() {
        System.out.println("findAll() requiere implementación en PreferenciaEstudianteService.");
        return List.of(); 
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public PreferenciaEstudianteDetailDTO findOne(@PathVariable("id") Long id) throws EntityNotFoundException {
        try {
            PreferenciaEstudianteEntity pref = preferenciaService.obtenerPreferenciasPorId(id);
            return modelMapper.map(pref, PreferenciaEstudianteDetailDTO.class);
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException("Preferencia no encontrada con id: " + id);
        }
    }

    @GetMapping(value = "/estudiante/{estudianteId}")
    @ResponseStatus(code = HttpStatus.OK)
    public PreferenciaEstudianteDetailDTO findByEstudianteId(@PathVariable("estudianteId") Long estudianteId) throws EntityNotFoundException {
        try {
            PreferenciaEstudianteEntity pref = preferenciaService.obtenerPreferenciasPorEstudianteId(estudianteId);
            return modelMapper.map(pref, PreferenciaEstudianteDetailDTO.class);
        } catch (IllegalArgumentException e) {
             throw new EntityNotFoundException("Preferencias no encontradas para el estudiante con id: " + estudianteId);
        }
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public PreferenciaEstudianteDTO create(@RequestBody PreferenciaEstudianteDTO dto) {
        PreferenciaEstudianteEntity prefEntity = modelMapper.map(dto, PreferenciaEstudianteEntity.class);
        PreferenciaEstudianteEntity nuevaPref = preferenciaService.crearPreferencias(prefEntity);
        return modelMapper.map(nuevaPref, PreferenciaEstudianteDTO.class);
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public PreferenciaEstudianteDTO update(@PathVariable("id") Long id, @RequestBody PreferenciaEstudianteDTO dto) throws EntityNotFoundException {
         try {
            PreferenciaEstudianteEntity prefEntity = modelMapper.map(dto, PreferenciaEstudianteEntity.class);
            PreferenciaEstudianteEntity prefActualizada = preferenciaService.actualizarPreferencias(id, prefEntity);
            return modelMapper.map(prefActualizada, PreferenciaEstudianteDTO.class);
        } catch (IllegalArgumentException e) {
             throw new EntityNotFoundException("Preferencia no encontrada con id: " + id);
        } catch (IllegalStateException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }

    
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) throws EntityNotFoundException {
        System.out.println("delete(" + id + ") requiere implementación en PreferenciaEstudianteService.");
        throw new UnsupportedOperationException("La eliminación de preferencias no está implementada.");
    }
}