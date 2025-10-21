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
        // List<TransaccionEntity> trans = transaccionService.getTransacciones();
        // return modelMapper.map(trans, new TypeToken<List<TransaccionDTO>>() {}.getType());
        System.out.println("findAll() (sin servicio) - Placeholder");
        return List.of(); // Placeholder
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public TransaccionDetailDTO findOne(@PathVariable("id") Long id) throws EntityNotFoundException {
        // TransaccionEntity tran = transaccionService.getTransaccion(id);
        // return modelMapper.map(tran, TransaccionDetailDTO.class);
        System.out.println("findOne(" + id + ") (sin servicio) - Placeholder");
        if (id == 1) return new TransaccionDetailDTO(); // Placeholder
        throw new EntityNotFoundException("Transaccion no encontrada");
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public TransaccionDTO create(@RequestBody TransaccionDTO dto) {
        // TransaccionEntity tranEntity = modelMapper.map(dto, TransaccionEntity.class);
        // TransaccionEntity nuevaTran = transaccionService.createTransaccion(tranEntity);
        // return modelMapper.map(nuevaTran, TransaccionDTO.class);
        System.out.println("create() (sin servicio) - Placeholder");
        dto.setId(1L); // Placeholder
        return dto;
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public TransaccionDTO update(@PathVariable("id") Long id, @RequestBody TransaccionDTO dto) throws EntityNotFoundException {
        // TransaccionEntity tranEntity = modelMapper.map(dto, TransaccionEntity.class);
        // TransaccionEntity tranActualizada = transaccionService.updateTransaccion(id, tranEntity);
        // return modelMapper.map(tranActualizada, TransaccionDTO.class);
        System.out.println("update(" + id + ") (sin servicio) - Placeholder");
        return dto;
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) throws EntityNotFoundException {
        // transaccionService.deleteTransaccion(id);
        System.out.println("delete(" + id + ") (sin servicio) - Placeholder");
    }
}