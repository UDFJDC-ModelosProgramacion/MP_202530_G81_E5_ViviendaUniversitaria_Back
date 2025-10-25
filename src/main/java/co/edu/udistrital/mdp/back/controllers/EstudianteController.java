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
    @ResponseStatus(HttpStatus.OK)
    public List<EstudianteDTO> findAll() {
        List<EstudianteEntity> list = estudianteService.listarTodos();
        return modelMapper.map(list, new TypeToken<List<EstudianteDTO>>() {
        }.getType());
    }

    /** GET /estudiantes/{id} */
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EstudianteDetailDTO findOne(@PathVariable Long id) throws EntityNotFoundException {
        try {
            EstudianteEntity entity = estudianteService.obtenerPorId(id);
            return modelMapper.map(entity, EstudianteDetailDTO.class);
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException(e.getMessage());
        }
    }

    /** POST /estudiantes */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EstudianteDTO create(@RequestBody EstudianteDTO dto) throws IllegalOperationException {
        EstudianteEntity entity = modelMapper.map(dto, EstudianteEntity.class);
        try {
            EstudianteEntity created = estudianteService.crear(entity);
            return modelMapper.map(created, EstudianteDTO.class);
        } catch (IllegalArgumentException e) {
            throw new IllegalOperationException(e.getMessage());
        }
    }

    /** PUT /estudiantes/{id} */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EstudianteDTO update(@PathVariable Long id, @RequestBody EstudianteDTO dto)
            throws EntityNotFoundException, IllegalOperationException {
        try {
            EstudianteEntity entity = modelMapper.map(dto, EstudianteEntity.class);
            EstudianteEntity updated = estudianteService.actualizar(id, entity);
            return modelMapper.map(updated, EstudianteDTO.class);
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException(e.getMessage());
        }
    }

    /** DELETE /estudiantes/{id} */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id)
            throws EntityNotFoundException, IllegalOperationException {
        try {
            estudianteService.eliminar(id);
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException(e.getMessage());
        }
    }
}
