package co.edu.udistrital.mdp.back.controllers;

import co.edu.udistrital.mdp.back.dto.NotificacionDTO;
import co.edu.udistrital.mdp.back.dto.NotificacionDetailDTO;
import co.edu.udistrital.mdp.back.entities.NotificacionEntity;
import co.edu.udistrital.mdp.back.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.back.services.NotificacionService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/notificaciones")
public class NotificacionController {

    
    @Autowired
    private NotificacionService notificacionService; 
    
    
    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<NotificacionDTO> findAll() {
        // List<NotificacionEntity> notifs = notificacionService.getNotificaciones();
        // return modelMapper.map(notifs, new TypeToken<List<NotificacionDTO>>() {}.getType());
        System.out.println("findAll() (sin servicio) - Placeholder");
        return List.of(); // Placeholder
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public NotificacionDetailDTO findOne(@PathVariable("id") Long id) throws EntityNotFoundException {
        // NotificacionEntity notif = notificacionService.getNotificacion(id);
        // return modelMapper.map(notif, NotificacionDetailDTO.class);
        System.out.println("findOne(" + id + ") (sin servicio) - Placeholder");
        if (id == 1) return new NotificacionDetailDTO(); // Placeholder
        throw new EntityNotFoundException("Notificacion no encontrada con id: " + id);
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public NotificacionDTO create(@RequestBody NotificacionDTO dto) {
        // NotificacionEntity notifEntity = modelMapper.map(dto, NotificacionEntity.class);
        // NotificacionEntity nuevaNotif = notificacionService.createNotificacion(notifEntity);
        // return modelMapper.map(nuevaNotif, NotificacionDTO.class);
        System.out.println("create() (sin servicio) - Placeholder");
        dto.setId(1L); // Placeholder
        return dto;
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public NotificacionDTO update(@PathVariable("id") Long id, @RequestBody NotificacionDTO dto) throws EntityNotFoundException {
        // NotificacionEntity notifEntity = modelMapper.map(dto, NotificacionEntity.class);
        // NotificacionEntity notifActualizada = notificacionService.updateNotificacion(id, notifEntity);
        // return modelMapper.map(notifActualizada, NotificacionDTO.class);
        System.out.println("update(" + id + ") (sin servicio) - Placeholder");
        return dto;
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) throws EntityNotFoundException {
        // notificacionService.deleteNotificacion(id);
        System.out.println("delete(" + id + ") (sin servicio) - Placeholder");
    }
}