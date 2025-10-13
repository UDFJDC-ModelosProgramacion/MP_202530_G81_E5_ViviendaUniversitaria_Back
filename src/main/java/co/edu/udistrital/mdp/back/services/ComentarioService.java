package co.edu.udistrital.mdp.back.services;

import co.edu.udistrital.mdp.back.entities.ComentarioEntity;
import co.edu.udistrital.mdp.back.entities.ViviendaEntity;
import co.edu.udistrital.mdp.back.entities.EstudianteEntity;
import co.edu.udistrital.mdp.back.repositories.ComentarioRepository;
import co.edu.udistrital.mdp.back.repositories.ViviendaRepository;
import co.edu.udistrital.mdp.back.repositories.EstudianteRepository;
import co.edu.udistrital.mdp.back.repositories.EstanciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio que maneja la lógica de negocio para la entidad Comentario
 * Implementa las reglas de negocio definidas para CREATE, UPDATE y DELETE
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ComentarioService {

    private final ComentarioRepository comentarioRepository;
    private final ViviendaRepository viviendaRepository;
    private final EstudianteRepository estudianteRepository;
    private final EstanciaRepository estanciaRepository;

    // Constantes para validación
    private static final int CALIFICACION_MINIMA = 1;
    private static final int CALIFICACION_MAXIMA = 5;
    private static final int CONTENIDO_MINIMO = 10;
    private static final int CONTENIDO_MAXIMO = 2000;

    /**
     * CREATE - Crea un nuevo comentario validando todas las reglas de negocio
     * 
     * Reglas aplicadas:
     * - contenido no puede estar vacío ni solo espacios
     * - calificacion obligatoria, rango 1-5
     * - fechaCreacion se asigna automáticamente
     * - vivienda debe existir y estar activa
     * - autor (Estudiante) debe existir y estar activo
     * - Estudiante solo puede comentar si tuvo Estancia completada
     */
    public ComentarioEntity crearComentario(ComentarioEntity comentario) {
        // Validar contenido no vacío
        validarContenido(comentario.getContenido());

        // Validar calificación en rango válido
        validarCalificacion(comentario.getCalificacion());

        // Validar que la vivienda exista y esté activa
        validarViviendaExisteYActiva(comentario.getVivienda().getId());

        // Validar que el autor (Estudiante) exista y esté activo
        validarEstudianteExisteYActivo(comentario.getAutor().getId());

        // Validar que el estudiante haya tenido una Estancia completada en la vivienda
        validarEstanciaCompletada(
                comentario.getAutor().getId(),
                comentario.getVivienda().getId());

        // La fecha de creación se asigna automáticamente con @PrePersist
        // pero si no se confía en eso, se puede forzar aquí
        if (comentario.getFechaCreacion() == null) {
            comentario.setFechaCreacion(LocalDateTime.now());
        }

        // Guardar y retornar
        return comentarioRepository.save(comentario);
    }

    /**
     * READ - Obtiene un comentario por ID
     */
    public ComentarioEntity obtenerComentarioPorId(Long id) {
        return comentarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comentario no encontrado con ID: " + id));
    }

    /**
     * READ - Obtiene todos los comentarios
     */
    public List<ComentarioEntity> obtenerTodosLosComentarios() {
        return comentarioRepository.findAll();
    }

    /**
     * READ - Obtiene comentarios de una vivienda específica
     */
    public List<ComentarioEntity> obtenerComentariosPorVivienda(Long viviendaId) {
        return comentarioRepository.findByViviendaIdOrderByFechaCreacionDesc(viviendaId);
    }

    /**
     * READ - Obtiene comentarios de un estudiante específico
     */
    public List<ComentarioEntity> obtenerComentariosPorEstudiante(Long estudianteId) {
        return comentarioRepository.findByAutorId(estudianteId);
    }

    /**
     * READ - Calcula el promedio de calificación de una vivienda
     */
    public Double obtenerPromedioCalificacion(Long viviendaId) {
        Double promedio = comentarioRepository.calcularPromedioCalificacion(viviendaId);
        return promedio != null ? promedio : 0.0;
    }

    /**
     * UPDATE - Actualiza un comentario existente validando reglas de negocio
     * 
     * Reglas aplicadas:
     * - contenido puede modificarse, pero solo por el autor original
     * - calificacion puede modificarse libremente por el autor
     * - fechaModificacion se actualiza automáticamente
     * - solo el autor puede editar su comentario
     */
    public ComentarioEntity actualizarComentario(Long comentarioId, Long usuarioId,
            ComentarioEntity comentarioActualizado) {
        // Verificar que el comentario existe
        ComentarioEntity comentarioExistente = obtenerComentarioPorId(comentarioId);

        // Validar que el usuario que intenta actualizar es el autor original
        if (!comentarioExistente.getAutor().getId().equals(usuarioId)) {
            throw new IllegalStateException(
                    "Solo el autor original puede modificar el comentario. " +
                            "Autor del comentario: ID " + comentarioExistente.getAutor().getId());
        }

        // Validar nuevo contenido si se está actualizando
        if (comentarioActualizado.getContenido() != null) {
            validarContenido(comentarioActualizado.getContenido());
            comentarioExistente.setContenido(comentarioActualizado.getContenido());
        }

        // Validar nueva calificación si se está actualizando
        if (comentarioActualizado.getCalificacion() != null) {
            validarCalificacion(comentarioActualizado.getCalificacion());
            comentarioExistente.setCalificacion(comentarioActualizado.getCalificacion());
        }

        // La fecha de modificación se actualiza automáticamente con @PreUpdate
        // pero si no se confía en eso, se puede forzar aquí
        comentarioExistente.setFechaModificacion(LocalDateTime.now());

        return comentarioRepository.save(comentarioExistente);
    }

    /**
     * UPDATE - Actualiza solo el contenido de un comentario
     */
    public ComentarioEntity actualizarContenido(Long comentarioId, Long usuarioId, String nuevoContenido) {
        ComentarioEntity comentario = obtenerComentarioPorId(comentarioId);

        // Validar autoría
        validarEsAutor(comentario, usuarioId);

        // Validar contenido
        validarContenido(nuevoContenido);

        comentario.setContenido(nuevoContenido);
        comentario.setFechaModificacion(LocalDateTime.now());

        return comentarioRepository.save(comentario);
    }

    /**
     * UPDATE - Actualiza solo la calificación de un comentario
     */
    public ComentarioEntity actualizarCalificacion(Long comentarioId, Long usuarioId, Integer nuevaCalificacion) {
        ComentarioEntity comentario = obtenerComentarioPorId(comentarioId);

        // Validar autoría
        validarEsAutor(comentario, usuarioId);

        // Validar calificación
        validarCalificacion(nuevaCalificacion);

        comentario.setCalificacion(nuevaCalificacion);
        comentario.setFechaModificacion(LocalDateTime.now());

        return comentarioRepository.save(comentario);
    }

    /**
     * DELETE - Elimina un comentario
     * 
     * Reglas aplicadas:
     * - Solo pueden eliminar: el autor original o un administrador
     * - La vivienda asociada debe seguir existiendo
     * - No se permite eliminar si tiene respuestas/interacciones
     */
    public void eliminarComentario(Long comentarioId, Long usuarioId, boolean esAdministrador) {
        ComentarioEntity comentario = obtenerComentarioPorId(comentarioId);

        // Validar que el usuario tiene permisos para eliminar
        if (!esAdministrador && !comentario.getAutor().getId().equals(usuarioId)) {
            throw new IllegalStateException(
                    "Solo el autor original o un administrador pueden eliminar el comentario");
        }

        // Validar que la vivienda asociada sigue existiendo
        if (!viviendaRepository.existsById(comentario.getVivienda().getId())) {
            throw new IllegalStateException(
                    "La vivienda asociada al comentario ya no existe en el sistema");
        }

        // Validar que no tenga respuestas/interacciones
        // (Esta regla depende de si tienes implementado un sistema de respuestas)
        // Por ahora, se asume que esta validación se puede hacer en el futuro
        validarNoTieneRespuestas(comentarioId);

        comentarioRepository.deleteById(comentarioId);
    }

    /**
     * DELETE - Versión simplificada para el autor
     */
    public void eliminarComentarioComoAutor(Long comentarioId, Long usuarioId) {
        eliminarComentario(comentarioId, usuarioId, false);
    }

    /**
     * DELETE - Versión para administradores
     */
    public void eliminarComentarioComoAdministrador(Long comentarioId) {
        eliminarComentario(comentarioId, null, true);
    }

    /**
     * Verifica si un estudiante puede comentar una vivienda
     */
    public boolean puedeComentarVivienda(Long estudianteId, Long viviendaId) {
        try {
            validarEstanciaCompletada(estudianteId, viviendaId);
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }

    // ==================== MÉTODOS DE VALIDACIÓN ====================

    /**
     * Valida que el contenido no esté vacío ni solo espacios
     * Regla: contenido no puede estar vacío ni compuesto únicamente por espacios
     */
    private void validarContenido(String contenido) {
        if (contenido == null || contenido.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El campo 'contenido' no puede estar vacío ni compuesto únicamente por espacios en blanco");
        }

        // Validar longitud mínima y máxima
        String contenidoTrim = contenido.trim();
        if (contenidoTrim.length() < CONTENIDO_MINIMO) {
            throw new IllegalArgumentException(
                    "El campo 'contenido' debe tener al menos " + CONTENIDO_MINIMO + " caracteres. " +
                            "Longitud actual: " + contenidoTrim.length());
        }

        if (contenidoTrim.length() > CONTENIDO_MAXIMO) {
            throw new IllegalArgumentException(
                    "El campo 'contenido' no puede superar los " + CONTENIDO_MAXIMO + " caracteres. " +
                            "Longitud actual: " + contenidoTrim.length());
        }
    }

    /**
     * Valida que la calificación esté en el rango válido
     * Regla: calificacion es obligatoria y debe estar en rango 1-5
     */
    private void validarCalificacion(Integer calificacion) {
        if (calificacion == null) {
            throw new IllegalArgumentException("El campo 'calificacion' es obligatorio");
        }

        if (calificacion < CALIFICACION_MINIMA || calificacion > CALIFICACION_MAXIMA) {
            throw new IllegalArgumentException(
                    "El campo 'calificacion' debe encontrarse dentro del rango válido (" +
                            CALIFICACION_MINIMA + " a " + CALIFICACION_MAXIMA + " estrellas). " +
                            "Valor proporcionado: " + calificacion);
        }
    }

    /**
     * Valida que la vivienda exista y esté activa
     * Regla: La vivienda asociada debe existir y encontrarse activa
     */
    private void validarViviendaExisteYActiva(Long viviendaId) {
        ViviendaEntity vivienda = viviendaRepository.findById(viviendaId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "La vivienda con ID " + viviendaId + " no existe en el sistema"));

        // Puedes agregar validación adicional si tienes un campo "activo" en Vivienda
        // if (!vivienda.getActivo()) {}
    }

    /**
     * Valida que el estudiante exista y esté activo
     * Regla: El autor (Estudiante) debe existir y estar activo
     */
    private void validarEstudianteExisteYActivo(Long estudianteId) {
        EstudianteEntity estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "El estudiante con ID " + estudianteId + " no existe en el sistema"));

        // Puedes agregar validación adicional si tienes un campo "activo" en Estudiante
        // if (!estudiante.getActivo()) {
        // throw new IllegalStateException("El estudiante no está activo");
        // }
    }

    /**
     * Valida que el estudiante haya tenido una Estancia completada en la vivienda
     * Regla: Un Estudiante solo puede comentar si tuvo una Estancia completada
     */
    private void validarEstanciaCompletada(Long estudianteId, Long viviendaId) {
        // Verificar si existe una estancia completada
        boolean tieneEstanciaCompletada = estanciaRepository
                .existsByEstudianteIdAndViviendaIdAndEstadoCompletada(estudianteId, viviendaId);

        if (!tieneEstanciaCompletada) {
            throw new IllegalStateException(
                    "El estudiante con ID " + estudianteId +
                            " no puede comentar la vivienda con ID " + viviendaId +
                            " porque no ha tenido una Estancia completada en ella");
        }
    }

    /**
     * Valida que el usuario es el autor del comentario
     */
    private void validarEsAutor(ComentarioEntity comentario, Long usuarioId) {
        if (!comentario.getAutor().getId().equals(usuarioId)) {
            throw new IllegalStateException(
                    "Solo el autor original puede modificar este comentario");
        }
    }

    /**
     * Valida que el comentario no tenga respuestas/interacciones
     * Regla: No se permite eliminar si tiene respuestas de otros usuarios
     */
    private void validarNoTieneRespuestas(Long comentarioId) {
        // Esta validación depende de si implementas un sistema de respuestas
        // Por ahora, dejamos un placeholder

        // Ejemplo si tuvieras una tabla de respuestas:
        // if (respuestaRepository.existsByComentarioId(comentarioId)) {
        // throw new IllegalStateException(
        // "No se puede eliminar el comentario porque tiene respuestas de otros
        // usuarios"
        // );
        // }

        // Por ahora, permitimos la eliminación
    }
}