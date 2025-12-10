package co.edu.udistrital.mdp.back.controllers;

import co.edu.udistrital.mdp.back.dto.PropietarioDTO;
import co.edu.udistrital.mdp.back.entities.ViviendaEntity;
import co.edu.udistrital.mdp.back.services.PropietarioService;
import co.edu.udistrital.mdp.back.services.PropietarioViviendaService;
import co.edu.udistrital.mdp.back.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.back.exceptions.IllegalOperationException;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/propietarios")
@RequiredArgsConstructor
public class PropietarioController {

    private final PropietarioService propietarioService;
    private final PropietarioViviendaService propietarioViviendaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PropietarioDTO createPropietario(@RequestBody PropietarioDTO propietarioDTO) {
        try {
            return propietarioService.crearPropietario(propietarioDTO);
        } catch (IllegalArgumentException e) {
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<PropietarioDTO> getPropietarios() {
        return propietarioService.getPropietarios();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PropietarioDTO getPropietario(@PathVariable("id") Long id) throws EntityNotFoundException {
        return propietarioService.getPropietario(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public PropietarioDTO updatePropietario(@PathVariable("id") Long id, @RequestBody PropietarioDTO propietarioDTO)
            throws EntityNotFoundException {
        return propietarioService.updatePropietario(id, propietarioDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePropietario(@PathVariable("id") Long id) throws EntityNotFoundException {
        propietarioService.deletePropietario(id);
    }

    @GetMapping("/{propietarioId}/viviendas")
    @ResponseStatus(HttpStatus.OK)
    public List<ViviendaEntity> getViviendas(@PathVariable("propietarioId") Long propietarioId)
            throws EntityNotFoundException {
        return propietarioViviendaService.getViviendas(propietarioId);
    }

    @GetMapping("/{propietarioId}/viviendas/{viviendaId}")
    @ResponseStatus(HttpStatus.OK)
    public ViviendaEntity getVivienda(@PathVariable("propietarioId") Long propietarioId,
            @PathVariable("viviendaId") Long viviendaId) throws EntityNotFoundException, IllegalOperationException {
        return propietarioViviendaService.getVivienda(propietarioId, viviendaId);
    }

    @DeleteMapping("/{propietarioId}/viviendas/{viviendaId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeVivienda(@PathVariable("propietarioId") Long propietarioId,
            @PathVariable("viviendaId") Long viviendaId) throws EntityNotFoundException, IllegalOperationException {
        propietarioViviendaService.removeVivienda(propietarioId, viviendaId);
    }
}