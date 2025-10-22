package co.edu.udistrital.mdp.back.controllers;

import co.edu.udistrital.mdp.back.dto.MultimediaDTO;
import co.edu.udistrital.mdp.back.entities.MultimediaEntity;
import co.edu.udistrital.mdp.back.services.ViviendaMultimediaService;
import co.edu.udistrital.mdp.back.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.back.exceptions.IllegalOperationException;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/viviendas/{viviendaId}/multimedia")
@RequiredArgsConstructor
public class ViviendaMultimediaController {

    private final ViviendaMultimediaService viviendaMultimediaService;
    private final ModelMapper modelMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MultimediaDTO createMultimedia(@PathVariable("viviendaId") Long viviendaId, @RequestBody MultimediaDTO multimediaDTO) throws EntityNotFoundException {
        MultimediaEntity multimediaEntity = modelMapper.map(multimediaDTO, MultimediaEntity.class);
        MultimediaEntity savedMultimedia = viviendaMultimediaService.addMultimedia(viviendaId, multimediaEntity.getId());
        return modelMapper.map(savedMultimedia, MultimediaDTO.class);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<MultimediaDTO> getMultimedia(@PathVariable("viviendaId") Long viviendaId) throws EntityNotFoundException {
        List<MultimediaEntity> multimediaList = viviendaMultimediaService.getMultimedia(viviendaId);
        return multimediaList.stream()
                .map(multimedia -> modelMapper.map(multimedia, MultimediaDTO.class))
                .collect(Collectors.toList());
    }

    @GetMapping("/{multimediaId}")
    @ResponseStatus(HttpStatus.OK)
    public MultimediaDTO getMultimediaItem(@PathVariable("viviendaId") Long viviendaId, @PathVariable("multimediaId") Long multimediaId) throws EntityNotFoundException, IllegalOperationException {
        MultimediaEntity multimedia = viviendaMultimediaService.getMultimediaItem(viviendaId, multimediaId);
        return modelMapper.map(multimedia, MultimediaDTO.class);
    }

    @DeleteMapping("/{multimediaId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeMultimedia(@PathVariable("viviendaId") Long viviendaId, @PathVariable("multimediaId") Long multimediaId) throws EntityNotFoundException, IllegalOperationException {
        viviendaMultimediaService.removeMultimedia(viviendaId, multimediaId);
    }
}