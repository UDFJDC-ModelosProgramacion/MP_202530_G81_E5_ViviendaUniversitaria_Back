package co.edu.udistrital.mdp.back.controllers;

import co.edu.udistrital.mdp.back.dto.EstudianteDTO;
import co.edu.udistrital.mdp.back.dto.EstudianteDetailDTO;
import co.edu.udistrital.mdp.back.entities.EstudianteEntity;
import co.edu.udistrital.mdp.back.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.back.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.back.services.EstudianteService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/estudiantes")
public class EstudianteController {

    @Autowired
    private EstudianteService estudianteService;

    @Autowired
    private ModelMapper modelMapper;

    /** GET /estudiantes */
    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<EstudianteDTO> findAll() {
        List<EstudianteEntity> list = estudianteService.getEstudiantes();
        return modelMapper.map(list, new TypeToken<List<EstudianteDTO>>() {
        }.getType());
    }

    /** GET /estudiantes/{id} */
    @GetMapping("/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public EstudianteDetailDTO findOne(@PathVariable("id") Long id) throws EntityNotFoundException {
        EstudianteEntity entity = estudianteService.getEstudiante(id);
        return modelMapper.map(entity, EstudianteDetailDTO.class);
    }

    /** POST /estudiantes */
    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public EstudianteDTO create(@RequestBody EstudianteDTO dto) throws IllegalOperationException {
        EstudianteEntity entity = modelMapper.map(dto, EstudianteEntity.class);
        EstudianteEntity created = estudianteService.createEstudiante(entity);
        return modelMapper.map(created, EstudianteDTO.class);
    }

    /** PUT /estudiantes/{id} */
    @PutMapping("/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public EstudianteDTO update(@PathVariable("id") Long id, @RequestBody EstudianteDTO dto)
            throws EntityNotFoundException, IllegalOperationException {
        EstudianteEntity entity = modelMapper.map(dto, EstudianteEntity.class);
        EstudianteEntity updated = estudianteService.updateEstudiante(id, entity);
        return modelMapper.map(updated, EstudianteDTO.class);
    }

    /** DELETE /estudiantes/{id} */
    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id)
            throws EntityNotFoundException, IllegalOperationException {
        estudianteService.deleteEstudiante(id);
    }
}
