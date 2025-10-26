package co.edu.udistrital.mdp.back.controllers;

import co.edu.udistrital.mdp.back.dto.EstanciaDTO;
import co.edu.udistrital.mdp.back.entities.EstanciaEntity;
import co.edu.udistrital.mdp.back.services.EstanciaService;
import co.edu.udistrital.mdp.back.exceptions.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/estancias")
public class EstanciaController {

    @Autowired
    private EstanciaService estanciaService;

    @Autowired
    private ModelMapper modelMapper;

    // 🔹 Constante para mensaje reutilizable
    private static final String MSG_ESTANCIA_NO_ENCONTRADA = "Estancia no encontrada con id: ";

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<EstanciaDTO> findAll() {
        List<EstanciaEntity> list = estanciaService.obtenerTodas();
        return list.stream()
                .map(e -> modelMapper.map(e, EstanciaDTO.class))
                .toList(); 
    }

    @GetMapping("/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public EstanciaDTO findOne(@PathVariable("id") Long id) throws EntityNotFoundException {
        try {
            EstanciaEntity e = estanciaService.obtenerPorId(id);
            return modelMapper.map(e, EstanciaDTO.class);
        } catch (IllegalArgumentException ex) {
            throw new EntityNotFoundException(MSG_ESTANCIA_NO_ENCONTRADA + id);
        }
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public EstanciaDTO create(@RequestBody EstanciaDTO dto) {
        EstanciaEntity entidad = modelMapper.map(dto, EstanciaEntity.class);
        EstanciaEntity creado = estanciaService.crearEstancia(entidad);
        return modelMapper.map(creado, EstanciaDTO.class);
    }

    @PutMapping("/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public EstanciaDTO update(@PathVariable("id") Long id, @RequestBody EstanciaDTO dto) throws EntityNotFoundException {
        try {
            EstanciaEntity entidad = modelMapper.map(dto, EstanciaEntity.class);
            EstanciaEntity actualizado = estanciaService.actualizar(id, entidad);
            return modelMapper.map(actualizado, EstanciaDTO.class);
        } catch (IllegalArgumentException ex) {
            throw new EntityNotFoundException(MSG_ESTANCIA_NO_ENCONTRADA + id);
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) throws EntityNotFoundException {
        try {
            estanciaService.eliminar(id);
        } catch (IllegalArgumentException ex) {
            throw new EntityNotFoundException(MSG_ESTANCIA_NO_ENCONTRADA + id);
        } catch (IllegalStateException ex) {
            throw new IllegalStateException("No se puede eliminar la estancia: " + ex.getMessage());
        }
    }
}
