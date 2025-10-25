package co.edu.udistrital.mdp.back.controllers;

import co.edu.udistrital.mdp.back.dto.UniversidadCercaDTO;
import co.edu.udistrital.mdp.back.entities.UniversidadCercaEntity;
import co.edu.udistrital.mdp.back.services.UniversidadCercaService;
import co.edu.udistrital.mdp.back.exceptions.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/universidades")
public class UniversidadCercaController {

    @Autowired
    private UniversidadCercaService universidadService;

    @Autowired
    private ModelMapper modelMapper;

    // 🔹 Constante para el mensaje reutilizable
    private static final String MSG_UNIVERSIDAD_NO_ENCONTRADA = "UniversidadCerca no encontrada con id: ";

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<UniversidadCercaDTO> findAll() {
        List<UniversidadCercaEntity> list = universidadService.listarTodas();
        return modelMapper.map(list, new TypeToken<List<UniversidadCercaDTO>>() {}.getType());
    }

    @GetMapping("/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public UniversidadCercaDTO findOne(@PathVariable("id") Long id) throws EntityNotFoundException {
        try {
            UniversidadCercaEntity u = universidadService.obtenerPorId(id);
            return modelMapper.map(u, UniversidadCercaDTO.class);
        } catch (IllegalArgumentException ex) {
            throw new EntityNotFoundException(MSG_UNIVERSIDAD_NO_ENCONTRADA + id);
        }
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public UniversidadCercaDTO create(@RequestBody UniversidadCercaDTO dto) {
        UniversidadCercaEntity entidad = modelMapper.map(dto, UniversidadCercaEntity.class);
        UniversidadCercaEntity creado = universidadService.crearUniversidad(entidad);
        return modelMapper.map(creado, UniversidadCercaDTO.class);
    }

    @PutMapping("/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public UniversidadCercaDTO update(@PathVariable("id") Long id, @RequestBody UniversidadCercaDTO dto)
            throws EntityNotFoundException {
        try {
            UniversidadCercaEntity entidad = modelMapper.map(dto, UniversidadCercaEntity.class);
            UniversidadCercaEntity actualizado = universidadService.actualizar(id, entidad);
            return modelMapper.map(actualizado, UniversidadCercaDTO.class);
        } catch (IllegalArgumentException ex) {
            throw new EntityNotFoundException(MSG_UNIVERSIDAD_NO_ENCONTRADA + id);
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) throws EntityNotFoundException {
        try {
            universidadService.eliminar(id);
        } catch (IllegalArgumentException ex) {
            throw new EntityNotFoundException(MSG_UNIVERSIDAD_NO_ENCONTRADA + id);
        } catch (IllegalStateException ex) {
            throw new IllegalStateException("No se puede eliminar la universidad: " + ex.getMessage());
        }
    }
}
