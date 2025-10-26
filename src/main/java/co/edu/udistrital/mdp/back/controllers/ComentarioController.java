package co.edu.udistrital.mdp.back.controllers;

import co.edu.udistrital.mdp.back.dto.ComentarioDTO;
import co.edu.udistrital.mdp.back.entities.ComentarioEntity;
import co.edu.udistrital.mdp.back.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.back.services.ComentarioService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller REST para gestionar las operaciones CRUD de Comentario
 * Endpoints: /comentarios
 */
@RestController
@RequestMapping("/comentarios")
public class ComentarioController {

    private static final String MSG_COMENTARIO_NO_ENCONTRADO = "Comentario no encontrado con id: ";

    @Autowired
    private ComentarioService comentarioService;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * GET /comentarios
     * Obtiene todos los comentarios
     */
    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<ComentarioDTO> findAll() {
        List<ComentarioEntity> comentarios = comentarioService.obtenerTodosLosComentarios();
        return modelMapper.map(comentarios, new TypeToken<List<ComentarioDTO>>() {
        }.getType());
    }

    /**
     * GET /comentarios/{id}
     * Obtiene un comentario por ID
     */
    @GetMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public ComentarioDTO findOne(@PathVariable("id") Long id) throws EntityNotFoundException {
        try {
            ComentarioEntity comentario = comentarioService.obtenerComentarioPorId(id);
            return modelMapper.map(comentario, ComentarioDTO.class);
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException(MSG_COMENTARIO_NO_ENCONTRADO + id);
        }
    }

    /**
     * GET /comentarios/vivienda/{viviendaId}
     * Obtiene todos los comentarios de una vivienda
     */
    @GetMapping(value = "/vivienda/{viviendaId}")
    @ResponseStatus(code = HttpStatus.OK)
    public List<ComentarioDTO> findByVivienda(@PathVariable("viviendaId") Long viviendaId) {
        List<ComentarioEntity> comentarios = comentarioService.obtenerComentariosPorVivienda(viviendaId);
        return modelMapper.map(comentarios, new TypeToken<List<ComentarioDTO>>() {
        }.getType());
    }

    /**
     * GET /comentarios/estudiante/{estudianteId}
     * Obtiene todos los comentarios de un estudiante
     */
    @GetMapping(value = "/estudiante/{estudianteId}")
    @ResponseStatus(code = HttpStatus.OK)
    public List<ComentarioDTO> findByEstudiante(@PathVariable("estudianteId") Long estudianteId) {
        // Considera manejar EntityNotFoundException si el estudiante no existe
        List<ComentarioEntity> comentarios = comentarioService.obtenerComentariosPorEstudiante(estudianteId);
        return modelMapper.map(comentarios, new TypeToken<List<ComentarioDTO>>() {
        }.getType());
    }

    /**
     * GET /comentarios/vivienda/{viviendaId}/promedio
     * Obtiene el promedio de calificación de una vivienda
     */
    @GetMapping(value = "/vivienda/{viviendaId}/promedio")
    @ResponseStatus(code = HttpStatus.OK)
    public Double getPromedioCalificacion(@PathVariable("viviendaId") Long viviendaId) {
        return comentarioService.obtenerPromedioCalificacion(viviendaId);
    }

    /**
     * POST /comentarios
     * Crea un nuevo comentario
     */
    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public ComentarioDTO create(@RequestBody ComentarioDTO dto) {
        ComentarioEntity comentarioEntity = modelMapper.map(dto, ComentarioEntity.class);
        ComentarioEntity nuevoComentario = comentarioService.crearComentario(comentarioEntity);
        return modelMapper.map(nuevoComentario, ComentarioDTO.class);
    }

    /**
     * PUT /comentarios/{id}
     * Actualiza un comentario completo (requiere usuarioId)
     */
    @PutMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public ComentarioDTO update(
            @PathVariable("id") Long id,
            @RequestParam("usuarioId") Long usuarioId,
            @RequestBody ComentarioDTO dto) throws EntityNotFoundException {
        try {
            ComentarioEntity comentarioEntity = modelMapper.map(dto, ComentarioEntity.class);
            ComentarioEntity comentarioActualizado = comentarioService.actualizarComentario(id, usuarioId,
                    comentarioEntity);
            return modelMapper.map(comentarioActualizado, ComentarioDTO.class);
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException(MSG_COMENTARIO_NO_ENCONTRADO + id);
        }
    }

    /**
     * PATCH /comentarios/{id}/contenido
     * Actualiza solo el contenido de un comentario
     */
    @PatchMapping(value = "/{id}/contenido")
    @ResponseStatus(code = HttpStatus.OK)
    public ComentarioDTO updateContenido(
            @PathVariable("id") Long id,
            @RequestParam("usuarioId") Long usuarioId,
            @RequestParam("contenido") String nuevoContenido) throws EntityNotFoundException {
        try {
            ComentarioEntity comentario = comentarioService.actualizarContenido(id, usuarioId, nuevoContenido);
            return modelMapper.map(comentario, ComentarioDTO.class);
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException(MSG_COMENTARIO_NO_ENCONTRADO + id);
        }
    }

    /**
     * PATCH /comentarios/{id}/calificacion
     * Actualiza solo la calificación de un comentario
     */
    @PatchMapping(value = "/{id}/calificacion")
    @ResponseStatus(code = HttpStatus.OK)
    public ComentarioDTO updateCalificacion(
            @PathVariable("id") Long id,
            @RequestParam("usuarioId") Long usuarioId,
            @RequestParam("calificacion") Integer nuevaCalificacion) throws EntityNotFoundException {
        try {
            ComentarioEntity comentario = comentarioService.actualizarCalificacion(id, usuarioId, nuevaCalificacion);
            return modelMapper.map(comentario, ComentarioDTO.class);
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException(MSG_COMENTARIO_NO_ENCONTRADO + id);
        }
    }

    /**
     * DELETE /comentarios/{id}
     * Elimina un comentario (como autor o administrador)
     */
    @DeleteMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable("id") Long id,
            @RequestParam("usuarioId") Long usuarioId,
            @RequestParam(value = "esAdministrador", defaultValue = "false") boolean esAdministrador)
            throws EntityNotFoundException {
        try {
            comentarioService.eliminarComentario(id, usuarioId, esAdministrador);
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException(MSG_COMENTARIO_NO_ENCONTRADO + id);
        } catch (IllegalStateException e) {
            // Re-lanzar IllegalStateException (es unchecked)
            throw new IllegalStateException("No se puede eliminar el comentario: " + e.getMessage());
        }
    }

    /**
     * DELETE /comentarios/{id}/autor
     * Elimina un comentario como autor
     */
    @DeleteMapping(value = "/{id}/autor")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteComoAutor(
            @PathVariable("id") Long id,
            @RequestParam("usuarioId") Long usuarioId) throws EntityNotFoundException {
        try {
            comentarioService.eliminarComentarioComoAutor(id, usuarioId);
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException(MSG_COMENTARIO_NO_ENCONTRADO + id);
        }
    }

    /**
     * DELETE /comentarios/{id}/admin
     * Elimina un comentario como administrador
     */
    @DeleteMapping(value = "/{id}/admin")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteComoAdministrador(@PathVariable("id") Long id) throws EntityNotFoundException {
        try {
            comentarioService.eliminarComentarioComoAdministrador(id);
        } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException(MSG_COMENTARIO_NO_ENCONTRADO + id);
        }
    }
}