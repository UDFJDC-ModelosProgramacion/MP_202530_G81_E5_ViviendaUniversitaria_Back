package co.edu.udistrital.mdp.back.controllers;

import co.edu.udistrital.mdp.back.dto.ContratoDTO;
import co.edu.udistrital.mdp.back.entities.ContratoEntity;
import co.edu.udistrital.mdp.back.services.ContratoService;
import co.edu.udistrital.mdp.back.exceptions.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/contratos")
public class ContratoController {

    @Autowired
    private ContratoService contratoService;

    @Autowired
    private ModelMapper modelMapper;

    // 🔹 Constante para evitar repetir el mismo mensaje
    private static final String MSG_CONTRATO_NO_ENCONTRADO = "Contrato no encontrado con id: ";

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<ContratoDTO> findAll() {
        List<ContratoEntity> contratos = contratoService.obtenerTodos();
        return contratos.stream()
                .map(c -> modelMapper.map(c, ContratoDTO.class))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public ContratoDTO findOne(@PathVariable("id") Long id) throws EntityNotFoundException {
        try {
            ContratoEntity c = contratoService.obtenerPorId(id);
            return modelMapper.map(c, ContratoDTO.class);
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException(MSG_CONTRATO_NO_ENCONTRADO + id);
        }
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public ContratoDTO create(@RequestBody ContratoDTO dto) {
        ContratoEntity entidad = modelMapper.map(dto, ContratoEntity.class);
        ContratoEntity creado = contratoService.crearContrato(entidad);
        return modelMapper.map(creado, ContratoDTO.class);
    }

    @PutMapping("/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public ContratoDTO update(@PathVariable("id") Long id, @RequestBody ContratoDTO dto) throws EntityNotFoundException {
        try {
            ContratoEntity entidad = modelMapper.map(dto, ContratoEntity.class);
            ContratoEntity actualizado = contratoService.actualizar(id, entidad);
            return modelMapper.map(actualizado, ContratoDTO.class);
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException(MSG_CONTRATO_NO_ENCONTRADO + id);
        }
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) throws EntityNotFoundException {
        try {
            contratoService.eliminar(id);
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException(MSG_CONTRATO_NO_ENCONTRADO + id);
        } catch (IllegalStateException e) {
            throw new IllegalStateException("No se puede eliminar el contrato: " + e.getMessage());
        }
    }
}
