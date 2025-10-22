package co.edu.udistrital.mdp.back.controllers;

import co.edu.udistrital.mdp.back.dto.ViviendaDTO;
import co.edu.udistrital.mdp.back.dto.ViviendaDetailDTO;
import co.edu.udistrital.mdp.back.entities.ViviendaEntity;
import co.edu.udistrital.mdp.back.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.back.services.ViviendaService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gestionar las operaciones CRUD de Vivienda
 * Endpoints: /viviendas
 */
@RestController
@RequestMapping("/viviendas")
public class ViviendaController {

    @Autowired
    private ViviendaService viviendaService;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * GET /viviendas
     * Obtiene todas las viviendas
     */
    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<ViviendaDTO> findAll() {
        List<ViviendaEntity> viviendas = viviendaService.obtenerTodasLasViviendas();
        return modelMapper.map(viviendas, new TypeToken<List<ViviendaDTO>>() {
        }.getType());
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public ViviendaDetailDTO findOne(@PathVariable("id") Long id) throws EntityNotFoundException {
        try {
            ViviendaEntity vivienda = viviendaService.obtenerViviendaPorId(id);
            return modelMapper.map(vivienda, ViviendaDetailDTO.class);
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException("Vivienda no encontrada con id: " + id);
        }
    }

    /**
     * GET /viviendas/ciudad/{ciudad}
     * Obtiene viviendas disponibles por ciudad
     */
    @GetMapping(value = "/ciudad/{ciudad}")
    @ResponseStatus(code = HttpStatus.OK)
    public List<ViviendaDTO> findByCiudadDisponible(@PathVariable("ciudad") String ciudad) {
        List<ViviendaEntity> viviendas = viviendaService.obtenerViviendasDisponiblesPorCiudad(ciudad);
        return modelMapper.map(viviendas, new TypeToken<List<ViviendaDTO>>() {
        }.getType());
    }

    /**
     * POST /viviendas
     * Crea una nueva vivienda
     */
    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public ViviendaDTO create(@RequestBody ViviendaDTO dto) {
        ViviendaEntity viviendaEntity = modelMapper.map(dto, ViviendaEntity.class);
        ViviendaEntity nuevaVivienda = viviendaService.crearVivienda(viviendaEntity);
        return modelMapper.map(nuevaVivienda, ViviendaDTO.class);
    }

    /**
     * PUT /viviendas/{id}
     * Actualiza una vivienda existente
     */
    @PutMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public ViviendaDTO update(@PathVariable("id") Long id, @RequestBody ViviendaDTO dto)
            throws EntityNotFoundException {
        try {
            ViviendaEntity viviendaEntity = modelMapper.map(dto, ViviendaEntity.class);
            ViviendaEntity viviendaActualizada = viviendaService.actualizarVivienda(id, viviendaEntity);
            return modelMapper.map(viviendaActualizada, ViviendaDTO.class);
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException("Vivienda no encontrada con id: " + id);
        }
    }

    /**
     * PATCH /viviendas/{id}/disponibilidad
     * Marca una vivienda como disponible o no disponible
     */
    @PatchMapping(value = "/{id}/disponibilidad")
    @ResponseStatus(code = HttpStatus.OK)
    public ViviendaDTO cambiarDisponibilidad(
            @PathVariable("id") Long id,
            @RequestParam("disponible") Boolean disponible) throws EntityNotFoundException {
        try {
            ViviendaEntity vivienda;
            if (disponible) {
                vivienda = viviendaService.marcarComoDisponible(id);
            } else {
                vivienda = viviendaService.marcarComoNoDisponible(id);
            }
            return modelMapper.map(vivienda, ViviendaDTO.class);
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException("Vivienda no encontrada con id: " + id);
        }
    }

    /**
     * DELETE /viviendas/{id}
     * Elimina una vivienda (solo si está disponible)
     */
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) throws EntityNotFoundException {
        try {
            viviendaService.eliminarVivienda(id);
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException("Vivienda no encontrada con id: " + id);
        } catch (IllegalStateException e) {
            throw new IllegalStateException("No se puede eliminar la vivienda: " + e.getMessage());
        }
    }
}