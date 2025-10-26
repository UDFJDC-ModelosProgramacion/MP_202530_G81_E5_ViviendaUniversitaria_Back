package co.edu.udistrital.mdp.back.controllers;

import co.edu.udistrital.mdp.back.dto.ServicioDTO;
import co.edu.udistrital.mdp.back.dto.ServicioDetailDTO;
import co.edu.udistrital.mdp.back.entities.ServicioEntity;
import co.edu.udistrital.mdp.back.entities.ServicioEntity.CategoriaServicio;
import co.edu.udistrital.mdp.back.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.back.services.ServicioService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gestionar las operaciones CRUD de Servicio
 * Endpoints: /servicios
 */
@RestController
@RequestMapping("/servicios")
public class ServicioController {

    @Autowired
    private ServicioService servicioService;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * GET /servicios
     * Obtiene todos los servicios
     */
    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<ServicioDTO> findAll() {
        List<ServicioEntity> servicios = servicioService.obtenerTodosLosServicios();
        return modelMapper.map(servicios, new TypeToken<List<ServicioDTO>>() {
        }.getType());
    }

    /**
     * GET /servicios/{id}
     * Obtiene un servicio por ID
     */
    @GetMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public ServicioDetailDTO findOne(@PathVariable("id") Long id) throws EntityNotFoundException {
        try {
            ServicioEntity servicio = servicioService.obtenerServicioPorId(id);
            return modelMapper.map(servicio, ServicioDetailDTO.class);
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException("Servicio no encontrado con id: " + id);
        }
    }

    /**
     * GET /servicios/nombre/{nombre}
     * Busca un servicio por nombre
     */
    @GetMapping(value = "/nombre/{nombre}")
    @ResponseStatus(code = HttpStatus.OK)
    public ServicioDTO findByNombre(@PathVariable("nombre") String nombre) throws EntityNotFoundException {
        try {
            ServicioEntity servicio = servicioService.obtenerServicioPorNombre(nombre);
            return modelMapper.map(servicio, ServicioDTO.class);
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException("Servicio no encontrado con nombre: " + nombre);
        }
    }

    /**
     * GET /servicios/categoria/{categoria}
     * Obtiene servicios por categoría
     */
    @GetMapping(value = "/categoria/{categoria}")
    @ResponseStatus(code = HttpStatus.OK)
    public List<ServicioDTO> findByCategoria(@PathVariable("categoria") String categoria) {
        try {
            CategoriaServicio cat = CategoriaServicio.valueOf(categoria.toUpperCase());
            List<ServicioEntity> servicios = servicioService.obtenerServiciosPorCategoria(cat);
            return modelMapper.map(servicios, new TypeToken<List<ServicioDTO>>() {
            }.getType());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Categoría no válida: " + categoria +
                    ". Categorías válidas: BASICO, CONECTIVIDAD, MOBILIARIO, SEGURIDAD, RECREACION, ADICIONAL");
        }
    }

    /**
     * POST /servicios
     * Crea un nuevo servicio
     */
    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public ServicioDTO create(@RequestBody ServicioDTO dto) {
        ServicioEntity servicioEntity = modelMapper.map(dto, ServicioEntity.class);
        ServicioEntity nuevoServicio = servicioService.crearServicio(servicioEntity);
        return modelMapper.map(nuevoServicio, ServicioDTO.class);
    }

    /**
     * PUT /servicios/{id}
     * Actualiza un servicio existente
     */
    @PutMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public ServicioDTO update(@PathVariable("id") Long id, @RequestBody ServicioDTO dto)
            throws EntityNotFoundException {
        try {
            ServicioEntity servicioEntity = modelMapper.map(dto, ServicioEntity.class);
            ServicioEntity servicioActualizado = servicioService.actualizarServicio(id, servicioEntity);
            return modelMapper.map(servicioActualizado, ServicioDTO.class);
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException("Servicio no encontrado con id: " + id);
        }
    }

    /**
     * PATCH /servicios/{id}/descripcion
     * Actualiza solo la descripción de un servicio
     */
    @PatchMapping(value = "/{id}/descripcion")
    @ResponseStatus(code = HttpStatus.OK)
    public ServicioDTO updateDescripcion(
            @PathVariable("id") Long id,
            @RequestBody String nuevaDescripcion) throws EntityNotFoundException {
        try {
            ServicioEntity servicio = servicioService.actualizarDescripcion(id, nuevaDescripcion);
            return modelMapper.map(servicio, ServicioDTO.class);
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException("Servicio no encontrado con id: " + id);
        }
    }

    /**
     * PATCH /servicios/{id}/icono
     * Actualiza solo el icono de un servicio
     */
    @PatchMapping(value = "/{id}/icono")
    @ResponseStatus(code = HttpStatus.OK)
    public ServicioDTO updateIcono(
            @PathVariable("id") Long id,
            @RequestBody String nuevoIcono) throws EntityNotFoundException {
        try {
            ServicioEntity servicio = servicioService.actualizarIcono(id, nuevoIcono);
            return modelMapper.map(servicio, ServicioDTO.class);
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException("Servicio no encontrado con id: " + id);
        }
    }

    /**
     * GET /servicios/{id}/puede-eliminar
     * Verifica si un servicio puede ser eliminado
     */
    @GetMapping(value = "/{id}/puede-eliminar")
    @ResponseStatus(code = HttpStatus.OK)
    public boolean puedeEliminar(@PathVariable("id") Long id) throws EntityNotFoundException {
        try {
            return servicioService.puedeEliminarServicio(id);
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException("Servicio no encontrado con id: " + id);
        }
    }

    /**
     * GET /servicios/{id}/viviendas-count
     * Cuenta cuántas viviendas están usando un servicio
     */
    @GetMapping(value = "/{id}/viviendas-count")
    @ResponseStatus(code = HttpStatus.OK)
    public int contarViviendas(@PathVariable("id") Long id) throws EntityNotFoundException {
        try {
            return servicioService.contarViviendasAsociadas(id);
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException("Servicio no encontrado con id: " + id);
        }
    }

    /**
     * DELETE /servicios/{id}
     * Elimina un servicio (solo si no tiene viviendas asociadas)
     */
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) throws EntityNotFoundException {
        try {
            servicioService.eliminarServicio(id);
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException("Servicio no encontrado con id: " + id);
        } catch (IllegalStateException e) {
            throw new IllegalStateException("No se puede eliminar el servicio: " + e.getMessage());
        }
    }
}