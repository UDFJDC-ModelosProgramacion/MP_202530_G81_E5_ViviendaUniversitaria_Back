package co.edu.udistrital.mdp.back.controllers;

import co.edu.udistrital.mdp.back.dto.TransaccionDTO;
import co.edu.udistrital.mdp.back.dto.TransaccionDetailDTO;
import co.edu.udistrital.mdp.back.entities.TransaccionEntity;
import co.edu.udistrital.mdp.back.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.back.services.TransaccionService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Level; // Importar Level
import java.util.logging.Logger;

@RestController
@RequestMapping("/transacciones")
public class TransaccionController {

    // Constante para el mensaje de error repetido
    private static final String MSG_TRANSACCION_NO_ENCONTRADA = "Transacción no encontrada con id: ";
    private static final Logger LOGGER = Logger.getLogger(TransaccionController.class.getName()); // Logger como constante

    @Autowired
    private TransaccionService transaccionService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<TransaccionDTO> findAll() {
        LOGGER.log(Level.INFO, "findAll() requiere implementación en TransaccionService.");
        return List.of(); // Devuelve vacío mientras no exista en servicio.
    }

    @GetMapping("/estancia/{estanciaId}")
    @ResponseStatus(code = HttpStatus.OK)
    public List<TransaccionDTO> findByEstancia(@PathVariable("estanciaId") Long estanciaId) {
        List<TransaccionEntity> trans = transaccionService.obtenerTransaccionesPorEstancia(estanciaId);
        return modelMapper.map(trans, new TypeToken<List<TransaccionDTO>>() {}.getType());
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public TransaccionDetailDTO findOne(@PathVariable("id") Long id) throws EntityNotFoundException {
        try {
            TransaccionEntity tran = transaccionService.obtenerTransaccionPorId(id);
            return modelMapper.map(tran, TransaccionDetailDTO.class);
        } catch (IllegalArgumentException e) {
            // Usamos la constante
            throw new EntityNotFoundException(MSG_TRANSACCION_NO_ENCONTRADA + id);
        }
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public TransaccionDTO create(@RequestBody TransaccionDTO dto) {
        TransaccionEntity tranEntity = modelMapper.map(dto, TransaccionEntity.class);
        TransaccionEntity nuevaTran = transaccionService.crearTransaccion(tranEntity);
        return modelMapper.map(nuevaTran, TransaccionDTO.class);
    }

    @PutMapping(value = "/{id}/actualizarEstado")
    @ResponseStatus(code = HttpStatus.OK)
    public TransaccionDTO updateEstado(@PathVariable("id") Long id, @RequestParam("estado") String nuevoEstado) throws EntityNotFoundException {
        try {
            TransaccionEntity tranActualizada = transaccionService.actualizarEstadoTransaccion(id, nuevoEstado);
            return modelMapper.map(tranActualizada, TransaccionDTO.class);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("no encontrada")) {
                // Usamos la constante
                throw new EntityNotFoundException(MSG_TRANSACCION_NO_ENCONTRADA + id);
            } else {
                throw new IllegalArgumentException("Estado proporcionado inválido: " + nuevoEstado);
            }
        }
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public TransaccionDTO update(@PathVariable("id") Long id, @RequestBody TransaccionDTO dto) throws EntityNotFoundException {
        // Usamos la constante del logger
        LOGGER.log(
            Level.INFO,
            "update({0}) - Considera usar el endpoint específico para actualizar estado.",
            id
        );

        if (dto.getEstado() == null || dto.getEstado().isBlank()) {
            throw new IllegalArgumentException("El DTO debe incluir el nuevo estado para actualizar.");
        }
        // Llamada al método específico para actualizar estado
        return updateEstado(id, dto.getEstado());
    }


    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) throws EntityNotFoundException {
        try {
            // Primero verifica si existe para lanzar EntityNotFoundException si aplica
            transaccionService.obtenerTransaccionPorId(id);
            // Luego intenta eliminar (que lanzará UnsupportedOperationException)
            transaccionService.eliminarTransaccion(id);
            // Esta línea no debería alcanzarse si eliminarTransaccion siempre lanza excepción
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IllegalArgumentException e) {
             // Usamos la constante
            throw new EntityNotFoundException(MSG_TRANSACCION_NO_ENCONTRADA + id);
        } catch (UnsupportedOperationException e) {
            // Si el servicio lanza la excepción esperada, retornamos METHOD_NOT_ALLOWED
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
        }
    }
}