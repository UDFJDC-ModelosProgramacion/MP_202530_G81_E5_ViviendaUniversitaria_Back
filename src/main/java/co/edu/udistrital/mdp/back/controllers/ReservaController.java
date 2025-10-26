package co.edu.udistrital.mdp.back.controllers;

import co.edu.udistrital.mdp.back.dto.ReservaDTO;
import co.edu.udistrital.mdp.back.dto.ReservaDetailDTO;
import co.edu.udistrital.mdp.back.entities.ReservaEntity;
import co.edu.udistrital.mdp.back.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.back.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.back.services.ReservaService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/reservas")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private ModelMapper modelMapper;

    /** GET /reservas */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ReservaDTO> findAll() {
        List<ReservaEntity> list = reservaService.getAllReservas();
        return modelMapper.map(list, new TypeToken<List<ReservaDTO>>() {
        }.getType());
    }

    /** GET /reservas/{id} */
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ReservaDetailDTO findOne(@PathVariable Long id) throws EntityNotFoundException {
        try {
            ReservaEntity entity = reservaService.getReserva(id);
            return modelMapper.map(entity, ReservaDetailDTO.class);
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException(e.getMessage());
        }
    }

    /** POST /reservas */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservaDTO create(@RequestBody ReservaDTO dto) throws IllegalOperationException {
        try {
            ReservaEntity entity = modelMapper.map(dto, ReservaEntity.class);
            ReservaEntity created = reservaService.createReserva(entity);
            return modelMapper.map(created, ReservaDTO.class);
        } catch (IllegalArgumentException e) {
            throw new IllegalOperationException(e.getMessage());
        }
    }

    /** PUT /reservas/{id} */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ReservaDTO update(@PathVariable Long id, @RequestBody ReservaDTO dto)
            throws EntityNotFoundException {
        try {
            ReservaEntity entity = modelMapper.map(dto, ReservaEntity.class);
            ReservaEntity updated = reservaService.updateReserva(id, entity);
            return modelMapper.map(updated, ReservaDTO.class);
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException(e.getMessage());
        }
    }

    /** DELETE /reservas/{id} */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id)
            throws EntityNotFoundException{
        try {
            reservaService.deleteReserva(id);
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException(e.getMessage());
        }
    }

    /** GET /reservas/activas */
    @GetMapping("/activas")
    @ResponseStatus(HttpStatus.OK)
    public List<ReservaDTO> findActivas() {
        List<ReservaEntity> list = reservaService.getReservasActivasHoy();
        return modelMapper.map(list, new TypeToken<List<ReservaDTO>>() {
        }.getType());
    }

    /** GET /reservas/pendientes */
    @GetMapping("/pendientes")
    @ResponseStatus(HttpStatus.OK)
    public List<ReservaDTO> findPendientes() {
        List<ReservaEntity> list = reservaService.getReservasPendientes();
        return modelMapper.map(list, new TypeToken<List<ReservaDTO>>() {
        }.getType());
    }
}
