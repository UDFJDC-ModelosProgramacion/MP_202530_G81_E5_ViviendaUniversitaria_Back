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

        System.out.println("findAll() requiere implementación en NotificacionService.");
        return List.of(); 
    }

    @GetMapping("/estudiante/{estudianteId}")
    @ResponseStatus(code = HttpStatus.OK)
    public List<NotificacionDTO> findByEstudiante(@PathVariable("estudianteId") Long estudianteId) {
        List<NotificacionEntity> notifs = notificacionService.obtenerNotificacionesPorEstudiante(estudianteId);
        return modelMapper.map(notifs, new TypeToken<List<NotificacionDTO>>() {}.getType());
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public NotificacionDetailDTO findOne(@PathVariable("id") Long id) throws EntityNotFoundException {
        try {
            NotificacionEntity notif = notificacionService.obtenerNotificacionPorId(id);
            return modelMapper.map(notif, NotificacionDetailDTO.class);
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException("Notificación no encontrada con id: " + id);
        }
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public NotificacionDTO create(@RequestBody NotificacionDTO dto) {
        NotificacionEntity notifEntity = modelMapper.map(dto, NotificacionEntity.class);

        NotificacionEntity nuevaNotif = notificacionService.enviarNotificacion(notifEntity);
        return modelMapper.map(nuevaNotif, NotificacionDTO.class);
    }

    @PatchMapping(value = "/{id}/marcarLeida")
    @ResponseStatus(code = HttpStatus.OK)
    public NotificacionDTO marcarComoLeida(@PathVariable("id") Long id) throws EntityNotFoundException {
         try {
            NotificacionEntity notifActualizada = notificacionService.marcarComoLeida(id);
            return modelMapper.map(notifActualizada, NotificacionDTO.class);
        } catch (IllegalArgumentException e) {
             throw new EntityNotFoundException("Notificación no encontrada con id: " + id);
        } catch (IllegalStateException e) {
             NotificacionEntity notifActual = notificacionService.obtenerNotificacionPorId(id);
             return modelMapper.map(notifActual, NotificacionDTO.class);
        }
    }


    @PutMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public NotificacionDTO update(@PathVariable("id") Long id, @RequestBody NotificacionDTO dto) throws EntityNotFoundException {
        System.out.println("update(" + id + ") requiere implementación en NotificacionService si es necesario.");
        dto.setId(id);
        return dto;
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) throws EntityNotFoundException {
         try {
            notificacionService.eliminarNotificacion(id);
        } catch (IllegalArgumentException e) {
             throw new EntityNotFoundException("No se pudo eliminar, notificación no encontrada con id: " + id);
        }
    }
}