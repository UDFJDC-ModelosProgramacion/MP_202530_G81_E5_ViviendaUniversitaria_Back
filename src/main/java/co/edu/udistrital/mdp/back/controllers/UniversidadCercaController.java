package co.edu.udistrital.mdp.back.controllers;

import co.edu.udistrital.mdp.back.entities.UniversidadCercaEntity;
import co.edu.udistrital.mdp.back.entities.ViviendaEntity;
import co.edu.udistrital.mdp.back.services.UniversidadCercaService;
import co.edu.udistrital.mdp.back.services.UniversidadCercaViviendaService;
import co.edu.udistrital.mdp.back.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.back.exceptions.IllegalOperationException;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/universidadesCerca")
@RequiredArgsConstructor
public class UniversidadCercaController {

    private final UniversidadCercaService universidadCercaService;
    private final UniversidadCercaViviendaService universidadCercaViviendaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UniversidadCercaEntity createUniversidadCerca(@RequestBody UniversidadCercaEntity universidad) {
        return universidadCercaService.crearUniversidad(universidad);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UniversidadCercaEntity> getUniversidadesCerca() {
        return universidadCercaService.listarTodas();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UniversidadCercaEntity getUniversidadCerca(@PathVariable("id") Long id) {
        return universidadCercaService.obtenerPorId(id);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UniversidadCercaEntity updateUniversidadCerca(@PathVariable("id") Long id, @RequestBody UniversidadCercaEntity universidad) {
        return universidadCercaService.actualizar(id, universidad);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUniversidadCerca(@PathVariable("id") Long id) {
        universidadCercaService.eliminar(id);
    }

    @GetMapping("/{universidadId}/viviendas")
    @ResponseStatus(HttpStatus.OK)
    public List<ViviendaEntity> getViviendas(@PathVariable("universidadId") Long universidadId) throws EntityNotFoundException {
        return universidadCercaViviendaService.getViviendas(universidadId);
    }

    @GetMapping("/{universidadId}/viviendas/{viviendaId}")
    @ResponseStatus(HttpStatus.OK)
    public ViviendaEntity getVivienda(@PathVariable("universidadId") Long universidadId, @PathVariable("viviendaId") Long viviendaId) throws EntityNotFoundException, IllegalOperationException {
        return universidadCercaViviendaService.getVivienda(universidadId, viviendaId);
    }
}