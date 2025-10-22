package co.edu.udistrital.mdp.back.controllers;

import co.edu.udistrital.mdp.back.dto.ReservaDTO;
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
    @ResponseStatus(code = HttpStatus.OK)
    public List<ReservaDTO> findAll() {
        List<ReservaEntity> list = reservaService.getReservas();
        return modelMapper.map(list, new TypeToken<List<ReservaDTO>>() {
        }.getType());
    }

    /** GET /reservas/{id} */
    @GetMapping("/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public ReservaDTO findOne(@PathVariable("id") Long id) throws EntityNotFoundException {
        ReservaEntity entity = reservaService.getReserva(id);
        return modelMapper.map(entity, ReservaDTO.class);
    }

    /** POST /reservas */
    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public ReservaDTO create(@RequestBody ReservaDTO dto) throws IllegalOperationException {
        ReservaEntity entity = modelMapper.map(dto, ReservaEntity.class);
        ReservaEntity created = reservaService.createReserva(entity);
        return modelMapper.map(created, ReservaDTO.class);
    }

    /** PUT /reservas/{id} */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ReservaDTO update(@PathVariable("id") Long id, @RequestBody ReservaDTO dto)
            throws EntityNotFoundException, IllegalOperationException {
        ReservaEntity entity = modelMapper.map(dto, ReservaEntity.class);
        ReservaEntity updated = reservaService.updateReserva(id, entity);
        return modelMapper.map(updated, ReservaDTO.class);
    }

    /** DELETE /reservas/{id} */
    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) throws EntityNotFoundException, IllegalOperationException {
        reservaService.deleteReserva(id);
    }
}
