package co.edu.udistrital.mdp.back.controllers;

import co.edu.udistrital.mdp.back.dto.ViviendaDTO;
import co.edu.udistrital.mdp.back.dto.ViviendaDetailDTO;
import co.edu.udistrital.mdp.back.dto.ViviendaEstadisticasDTO;
import co.edu.udistrital.mdp.back.dto.MultimediaDTO;
import co.edu.udistrital.mdp.back.entities.ViviendaEntity;
import co.edu.udistrital.mdp.back.entities.MultimediaEntity;
import co.edu.udistrital.mdp.back.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.back.services.ViviendaService;
import co.edu.udistrital.mdp.back.services.MultimediaService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Controller REST para gestionar las operaciones CRUD de Vivienda
 * Endpoints: /viviendas
 */
@RestController
@RequestMapping("/viviendas")
@CrossOrigin(origins = "*")
public class ViviendaController {

    private static final String VIVIENDA_NOT_FOUND_MSG = "Vivienda no encontrada con id: ";

    @Autowired
    private ViviendaService viviendaService;

    @Autowired
    private MultimediaService multimediaService;

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
            throw new EntityNotFoundException(VIVIENDA_NOT_FOUND_MSG + id);
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
     * GET /viviendas/{id}/estadist icas
     * Obtiene las estadísticas de ocupación e ingresos de la vivienda
     */
    @GetMapping("/{id}/estadisticas")
    @ResponseStatus(code = HttpStatus.OK)
    public ViviendaEstadisticasDTO getEstadisticas(@PathVariable("id") Long id) throws EntityNotFoundException {
        try {
            return viviendaService.obtenerEstadisticas(id);
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException(VIVIENDA_NOT_FOUND_MSG + id);
        }
    }

    /**
     * GET /viviendas/propietario/{propietarioId}
     * Obtiene todas las viviendas de un propietario específico
     */
    @GetMapping("/propietario/{propietarioId}")
    @ResponseStatus(code = HttpStatus.OK)
    public List<ViviendaDTO> findByPropietario(@PathVariable("propietarioId") Long propietarioId) {
        List<ViviendaEntity> viviendas = viviendaService.obtenerViviendasPorPropietario(propietarioId);
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
            throw new EntityNotFoundException(VIVIENDA_NOT_FOUND_MSG + id);
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
            @RequestParam("disponible") boolean disponible) throws EntityNotFoundException {
        try {
            ViviendaEntity vivienda;
            if (disponible) {
                vivienda = viviendaService.marcarComoDisponible(id);
            } else {
                vivienda = viviendaService.marcarComoNoDisponible(id);
            }
            return modelMapper.map(vivienda, ViviendaDTO.class);
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException(VIVIENDA_NOT_FOUND_MSG + id);
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
            throw new EntityNotFoundException(VIVIENDA_NOT_FOUND_MSG + id);
        } catch (IllegalStateException e) {
            throw new IllegalStateException("No se puede eliminar la vivienda: " + e.getMessage());
        }
    }

    /**
     * POST /viviendas/{id}/multimedia
     * Sube una imagen para una vivienda
     */
    @PostMapping(value = "/{id}/multimedia", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(code = HttpStatus.CREATED)
    public MultimediaDTO uploadImage(
            @PathVariable("id") Long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "titulo", required = false) String titulo,
            @RequestParam(value = "descripcion", required = false) String descripcion,
            @RequestParam(value = "esPortada", required = false, defaultValue = "false") Boolean esPortada)
            throws IOException {
        MultimediaEntity multimedia = multimediaService.subirImagenVivienda(
                id, file, titulo, descripcion, esPortada);
        return modelMapper.map(multimedia, MultimediaDTO.class);
    }

    /**
     * GET /viviendas/{id}/multimedia
     * Obtiene todas las imágenes de una vivienda
     */
    @GetMapping("/{id}/multimedia")
    @ResponseStatus(code = HttpStatus.OK)
    public List<MultimediaDTO> getImages(@PathVariable("id") Long id) {
        try {
            List<MultimediaEntity> multimedia = multimediaService.obtenerImagenesVivienda(id);
            return modelMapper.map(multimedia, new TypeToken<List<MultimediaDTO>>() {
            }.getType());
        } catch (Exception e) {
            // Si falla, retornar lista vacía en vez de error 500
            return new java.util.ArrayList<>();
        }
    }

    /**
     * DELETE /viviendas/multimedia/{multimediaId}
     * Elimina una imagen
     */
    @DeleteMapping("/multimedia/{multimediaId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteImage(@PathVariable("multimediaId") Long multimediaId) throws IOException {
        multimediaService.eliminarImagen(multimediaId);
    }

    /**
     * PATCH /viviendas/multimedia/{multimediaId}/portada
     * Marca una imagen como portada
     */
    @PatchMapping("/multimedia/{multimediaId}/portada")
    @ResponseStatus(code = HttpStatus.OK)
    public MultimediaDTO setAsPortada(@PathVariable("multimediaId") Long multimediaId) {
        MultimediaEntity multimedia = multimediaService.marcarComoPortada(multimediaId);
        return modelMapper.map(multimedia, MultimediaDTO.class);
    }
}
