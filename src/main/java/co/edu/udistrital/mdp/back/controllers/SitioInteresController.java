package co.edu.udistrital.mdp.back.controllers;

import co.edu.udistrital.mdp.back.dto.SitioInteresDTO;
import co.edu.udistrital.mdp.back.entities.SitioInteresEntity;
import co.edu.udistrital.mdp.back.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.back.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.back.services.SitioInteresService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/sitios-interes")
public class SitioInteresController {

    @Autowired
    private SitioInteresService sitioService;

    @Autowired
    private ModelMapper modelMapper;

    /** GET /sitios-interes */
    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<SitioInteresDTO> findAll() {
        List<SitioInteresEntity> sitios = sitioService.getAllSitios();
        return modelMapper.map(sitios, new TypeToken<List<SitioInteresDTO>>() {
        }.getType());
    }

    /** GET /sitios-interes/{id} */
    @GetMapping("/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public SitioInteresDTO findOne(@PathVariable("id") Long id) throws EntityNotFoundException {
        SitioInteresEntity sitio = sitioService.getSitioInteres(id);
        return modelMapper.map(sitio, SitioInteresDTO.class);
    }

    /** POST /sitios-interes */
    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public SitioInteresDTO create(@RequestBody SitioInteresDTO dto) throws IllegalOperationException {
        SitioInteresEntity entity = modelMapper.map(dto, SitioInteresEntity.class);
        SitioInteresEntity created = sitioService.createSitioInteres(entity);
        return modelMapper.map(created, SitioInteresDTO.class);
    }

    /** PUT /sitios-interes/{id} — actualización completa */
    @PutMapping("/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public SitioInteresDTO update(@PathVariable("id") Long id, @RequestBody SitioInteresDTO dto)
            throws EntityNotFoundException, IllegalOperationException {
        SitioInteresEntity entity = modelMapper.map(dto, SitioInteresEntity.class);
        SitioInteresEntity updated = sitioService.updateSitioInteres(id, entity);
        return modelMapper.map(updated, SitioInteresDTO.class);
    }

    /** DELETE /sitios-interes/{id} */
    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id)
            throws EntityNotFoundException, IllegalOperationException {
        sitioService.deleteSitioInteres(id);
    }
}
