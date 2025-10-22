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

@RestController
@RequestMapping("/transacciones")
public class TransaccionController {

    @Autowired
    private TransaccionService transaccionService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<TransaccionDTO> findAll() {
        System.out.println("findAll() requiere implementación en TransaccionService.");
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
            throw new EntityNotFoundException("Transacción no encontrada con id: " + id);
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
                 throw new EntityNotFoundException("Transacción no encontrada con id: " + id);
             } else {
                 throw new IllegalArgumentException("Estado proporcionado inválido: " + nuevoEstado);
             }
        }
    }
    @PutMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public TransaccionDTO update(@PathVariable("id") Long id, @RequestBody TransaccionDTO dto) throws EntityNotFoundException {
         System.out.println("update(" + id + ") - Considera usar el endpoint específico para actualizar estado.");
         if (dto.getEstado() == null || dto.getEstado().isBlank()) {
             throw new IllegalArgumentException("El DTO debe incluir el nuevo estado para actualizar.");
         }
         return updateEstado(id, dto.getEstado());
    }


    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) throws EntityNotFoundException {
        try {
            transaccionService.obtenerTransaccionPorId(id); 
            transaccionService.eliminarTransaccion(id); 
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException("Transacción no encontrada con id: " + id);
        } catch (UnsupportedOperationException e) {
            return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
        }
    }
}