package co.edu.udistrital.mdp.back.services;

import co.edu.udistrital.mdp.back.entities.ComentarioEntity;
import co.edu.udistrital.mdp.back.entities.EstanciaEntity.EstadoEstancia;
import co.edu.udistrital.mdp.back.entities.EstudianteEntity;
import co.edu.udistrital.mdp.back.entities.ViviendaEntity;
import co.edu.udistrital.mdp.back.repositories.ComentarioRepository;
import co.edu.udistrital.mdp.back.repositories.EstanciaRepository;
import co.edu.udistrital.mdp.back.repositories.EstudianteRepository;
import co.edu.udistrital.mdp.back.repositories.ViviendaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias de ComentarioService")
class ComentarioServiceTest {

    @Mock
    private ComentarioRepository comentarioRepository;

    @Mock
    private ViviendaRepository viviendaRepository;

    @Mock
    private EstudianteRepository estudianteRepository;

    @Mock
    private EstanciaRepository estanciaRepository;

    @InjectMocks
    private ComentarioService comentarioService;

    private ComentarioEntity comentarioValido;
    private ViviendaEntity viviendaValida;
    private EstudianteEntity estudianteValido;

    @BeforeEach
    void setUp() {
        // Configurar vivienda válida
        viviendaValida = new ViviendaEntity();
        viviendaValida.setId(1L);

        // Configurar estudiante válido
        estudianteValido = new EstudianteEntity();
        estudianteValido.setId(1L);
        estudianteValido.setNombre("Juan Pérez");
        estudianteValido.setCorreo("juan@example.com");

        // Configurar comentario válido
        comentarioValido = new ComentarioEntity();
        comentarioValido.setId(1L);
        comentarioValido.setContenido("Excelente vivienda, muy cómoda y limpia.");
        comentarioValido.setCalificacion(5);
        comentarioValido.setFechaCreacion(LocalDateTime.now());
        comentarioValido.setVivienda(viviendaValida);
        comentarioValido.setAutor(estudianteValido);
    }

    // ==================== PRUEBAS DE CREAR COMENTARIO ====================

    @Test
    @DisplayName("Crear comentario con datos válidos - debería crear correctamente")
    void crearComentario_ConDatosValidos_DeberiaCrearComentario() {
        // Arrange
        when(viviendaRepository.findById(1L)).thenReturn(Optional.of(viviendaValida));
        when(estudianteRepository.findById(1L)).thenReturn(Optional.of(estudianteValido));
        when(estanciaRepository.existsByEstudianteArrendador_IdAndViviendaArrendada_IdAndEstado(
                1L, 1L, EstadoEstancia.COMPLETADA)).thenReturn(true);
        when(comentarioRepository.save(any(ComentarioEntity.class))).thenReturn(comentarioValido);

        // Act
        ComentarioEntity resultado = comentarioService.crearComentario(comentarioValido);

        // Assert
        assertNotNull(resultado);
        assertEquals("Excelente vivienda, muy cómoda y limpia.", resultado.getContenido());
        assertEquals(5, resultado.getCalificacion());
        verify(comentarioRepository).save(comentarioValido);
    }

    @ParameterizedTest
    @DisplayName("Crear comentario con contenido inválido - debería lanzar excepción")
    @CsvSource({
            "null, contenido", // Caso 1: contenido nulo
            "'   ', contenido", // Caso 2: contenido vacío
            "'Muy bien', al menos 10 caracteres" // Caso 3: contenido corto
    })
    void crearComentario_ContenidoInvalido_DeberiaLanzarExcepcion(String contenido, String mensajeEsperado) {
        // Arrange
        if ("null".equals(contenido)) {
            comentarioValido.setContenido(null);
        } else {
            comentarioValido.setContenido(contenido);
        }

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> comentarioService.crearComentario(comentarioValido));

        assertThat(exception.getMessage()).contains(mensajeEsperado);
        verify(comentarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Crear comentario con contenido mayor a 2000 caracteres - debería lanzar excepción")
    void crearComentario_ConContenidoMuyLargo_DeberiaLanzarExcepcion() {
        // Arrange
        comentarioValido.setContenido("A".repeat(2001));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> comentarioService.crearComentario(comentarioValido));
        assertTrue(exception.getMessage().contains("2000 caracteres"));
        verify(comentarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Crear comentario con calificación nula - debería lanzar excepción")
    void crearComentario_ConCalificacionNula_DeberiaLanzarExcepcion() {
        // Arrange
        comentarioValido.setCalificacion(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> comentarioService.crearComentario(comentarioValido));
        assertTrue(exception.getMessage().contains("calificacion"));
        verify(comentarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Crear comentario con calificación menor a 1 - debería lanzar excepción")
    void crearComentario_ConCalificacionMenorA1_DeberiaLanzarExcepcion() {
        // Arrange
        comentarioValido.setCalificacion(0);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> comentarioService.crearComentario(comentarioValido));
        assertTrue(exception.getMessage().contains("rango válido"));
        verify(comentarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Crear comentario con calificación mayor a 5 - debería lanzar excepción")
    void crearComentario_ConCalificacionMayorA5_DeberiaLanzarExcepcion() {
        // Arrange
        comentarioValido.setCalificacion(6);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> comentarioService.crearComentario(comentarioValido));
        assertTrue(exception.getMessage().contains("rango válido"));
        verify(comentarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Crear comentario con vivienda inexistente - debería lanzar excepción")
    void crearComentario_ConViviendaInexistente_DeberiaLanzarExcepcion() {
        // Arrange
        when(viviendaRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> comentarioService.crearComentario(comentarioValido));
        assertTrue(exception.getMessage().contains("vivienda"));
        verify(comentarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Crear comentario con estudiante inexistente - debería lanzar excepción")
    void crearComentario_ConEstudianteInexistente_DeberiaLanzarExcepcion() {
        // Arrange
        when(viviendaRepository.findById(1L)).thenReturn(Optional.of(viviendaValida));
        when(estudianteRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> comentarioService.crearComentario(comentarioValido));
        assertTrue(exception.getMessage().contains("estudiante"));
        verify(comentarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Crear comentario sin estancia completada - debería lanzar excepción")
    void crearComentario_SinEstanciaCompletada_DeberiaLanzarExcepcion() {
        // Arrange
        when(viviendaRepository.findById(1L)).thenReturn(Optional.of(viviendaValida));
        when(estudianteRepository.findById(1L)).thenReturn(Optional.of(estudianteValido));
        when(estanciaRepository.existsByEstudianteArrendador_IdAndViviendaArrendada_IdAndEstado(
                1L, 1L, EstadoEstancia.COMPLETADA)).thenReturn(false);

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> comentarioService.crearComentario(comentarioValido));
        assertTrue(exception.getMessage().contains("Estancia completada"));
        verify(comentarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Crear comentario sin fecha de creación - debería asignarla automáticamente")
    void crearComentario_SinFechaCreacion_DeberiaAsignarFecha() {
        // Arrange
        comentarioValido.setFechaCreacion(null);
        when(viviendaRepository.findById(1L)).thenReturn(Optional.of(viviendaValida));
        when(estudianteRepository.findById(1L)).thenReturn(Optional.of(estudianteValido));
        when(estanciaRepository.existsByEstudianteArrendador_IdAndViviendaArrendada_IdAndEstado(
                1L, 1L, EstadoEstancia.COMPLETADA)).thenReturn(true);
        when(comentarioRepository.save(any(ComentarioEntity.class))).thenReturn(comentarioValido);

        // Act
        comentarioService.crearComentario(comentarioValido);

        // Assert
        assertNotNull(comentarioValido.getFechaCreacion());
    }

    // ==================== PRUEBAS DE OBTENER COMENTARIO ====================

    @Test
    @DisplayName("Obtener comentario por ID existente - debería retornar comentario")
    void obtenerComentarioPorId_ConIdExistente_DeberiaRetornarComentario() {
        // Arrange
        when(comentarioRepository.findById(1L)).thenReturn(Optional.of(comentarioValido));

        // Act
        ComentarioEntity resultado = comentarioService.obtenerComentarioPorId(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(comentarioRepository).findById(1L);
    }

    @Test
    @DisplayName("Obtener comentario por ID inexistente - debería lanzar excepción")
    void obtenerComentarioPorId_ConIdInexistente_DeberiaLanzarExcepcion() {
        // Arrange
        when(comentarioRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> comentarioService.obtenerComentarioPorId(999L));
        assertTrue(exception.getMessage().contains("no encontrado"));
    }

    // ==================== PRUEBAS DE LISTAR COMENTARIOS ====================

    @Test
    @DisplayName("Obtener todos los comentarios - debería retornar lista")
    void obtenerTodosLosComentarios_DeberiaRetornarLista() {
        // Arrange
        ComentarioEntity comentario2 = new ComentarioEntity();
        comentario2.setId(2L);
        comentario2.setContenido("Buen lugar para estudiar");
        comentario2.setCalificacion(4);

        List<ComentarioEntity> comentarios = Arrays.asList(comentarioValido, comentario2);
        when(comentarioRepository.findAll()).thenReturn(comentarios);

        // Act
        List<ComentarioEntity> resultado = comentarioService.obtenerTodosLosComentarios();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(comentarioRepository).findAll();
    }

    @Test
    @DisplayName("Obtener comentarios por vivienda - debería retornar lista ordenada")
    void obtenerComentariosPorVivienda_DeberiaRetornarListaOrdenada() {
        // Arrange
        List<ComentarioEntity> comentarios = Arrays.asList(comentarioValido);
        when(comentarioRepository.findByViviendaIdOrderByFechaCreacionDesc(1L))
                .thenReturn(comentarios);

        // Act
        List<ComentarioEntity> resultado = comentarioService.obtenerComentariosPorVivienda(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(comentarioRepository).findByViviendaIdOrderByFechaCreacionDesc(1L);
    }

    @Test
    @DisplayName("Obtener comentarios por estudiante - debería retornar lista")
    void obtenerComentariosPorEstudiante_DeberiaRetornarLista() {
        // Arrange
        List<ComentarioEntity> comentarios = Arrays.asList(comentarioValido);
        when(comentarioRepository.findByAutorId(1L)).thenReturn(comentarios);

        // Act
        List<ComentarioEntity> resultado = comentarioService.obtenerComentariosPorEstudiante(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(comentarioRepository).findByAutorId(1L);
    }

    @Test
    @DisplayName("Obtener promedio de calificación - debería calcular correctamente")
    void obtenerPromedioCalificacion_DeberiaCalcularPromedio() {
        // Arrange
        when(comentarioRepository.calcularPromedioCalificacion(1L)).thenReturn(4.5);

        // Act
        Double resultado = comentarioService.obtenerPromedioCalificacion(1L);

        // Assert
        assertEquals(4.5, resultado);
        verify(comentarioRepository).calcularPromedioCalificacion(1L);
    }

    @Test
    @DisplayName("Obtener promedio sin comentarios - debería retornar 0.0")
    void obtenerPromedioCalificacion_SinComentarios_DeberiaRetornarCero() {
        // Arrange
        when(comentarioRepository.calcularPromedioCalificacion(1L)).thenReturn(null);

        // Act
        Double resultado = comentarioService.obtenerPromedioCalificacion(1L);

        // Assert
        assertEquals(0.0, resultado);
    }

    // ==================== PRUEBAS DE ACTUALIZAR COMENTARIO ====================

    @Test
    @DisplayName("Actualizar comentario como autor - debería actualizar correctamente")
    void actualizarComentario_ComoAutor_DeberiaActualizar() {
        // Arrange
        ComentarioEntity actualizacion = new ComentarioEntity();
        actualizacion.setContenido("Contenido actualizado correctamente");
        actualizacion.setCalificacion(4);

        when(comentarioRepository.findById(1L)).thenReturn(Optional.of(comentarioValido));
        when(comentarioRepository.save(any(ComentarioEntity.class))).thenReturn(comentarioValido);

        // Act
        ComentarioEntity resultado = comentarioService.actualizarComentario(1L, 1L, actualizacion);

        // Assert
        assertEquals("Contenido actualizado correctamente", resultado.getContenido());
        assertEquals(4, resultado.getCalificacion());
        assertNotNull(resultado.getFechaModificacion());
        verify(comentarioRepository).save(comentarioValido);
    }

    @Test
    @DisplayName("Actualizar comentario como no autor - debería lanzar excepción")
    void actualizarComentario_ComoNoAutor_DeberiaLanzarExcepcion() {
        // Arrange
        ComentarioEntity actualizacion = new ComentarioEntity();
        actualizacion.setContenido("Intento de modificación");

        when(comentarioRepository.findById(1L)).thenReturn(Optional.of(comentarioValido));

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> comentarioService.actualizarComentario(1L, 999L, actualizacion));
        assertTrue(exception.getMessage().contains("autor original"));
        verify(comentarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Actualizar solo contenido como autor - debería actualizar")
    void actualizarContenido_ComoAutor_DeberiaActualizar() {
        // Arrange
        when(comentarioRepository.findById(1L)).thenReturn(Optional.of(comentarioValido));
        when(comentarioRepository.save(any(ComentarioEntity.class))).thenReturn(comentarioValido);

        // Act
        ComentarioEntity resultado = comentarioService.actualizarContenido(
                1L, 1L, "Nuevo contenido del comentario");

        // Assert
        assertEquals("Nuevo contenido del comentario", resultado.getContenido());
        verify(comentarioRepository).save(comentarioValido);
    }

    @Test
    @DisplayName("Actualizar contenido con texto inválido - debería lanzar excepción")
    void actualizarContenido_ConTextoInvalido_DeberiaLanzarExcepcion() {
        // Arrange
        when(comentarioRepository.findById(1L)).thenReturn(Optional.of(comentarioValido));

        // Act & Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> comentarioService.actualizarContenido(1L, 1L, "Corto"));
        verify(comentarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Actualizar solo calificación como autor - debería actualizar")
    void actualizarCalificacion_ComoAutor_DeberiaActualizar() {
        // Arrange
        when(comentarioRepository.findById(1L)).thenReturn(Optional.of(comentarioValido));
        when(comentarioRepository.save(any(ComentarioEntity.class))).thenReturn(comentarioValido);

        // Act
        ComentarioEntity resultado = comentarioService.actualizarCalificacion(1L, 1L, 3);

        // Assert
        assertEquals(3, resultado.getCalificacion());
        verify(comentarioRepository).save(comentarioValido);
    }

    @Test
    @DisplayName("Actualizar calificación con valor inválido - debería lanzar excepción")
    void actualizarCalificacion_ConValorInvalido_DeberiaLanzarExcepcion() {
        // Arrange
        when(comentarioRepository.findById(1L)).thenReturn(Optional.of(comentarioValido));

        // Act & Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> comentarioService.actualizarCalificacion(1L, 1L, 10));
        verify(comentarioRepository, never()).save(any());
    }

    // ==================== PRUEBAS DE ELIMINAR COMENTARIO ====================

    @Test
    @DisplayName("Eliminar comentario como autor - debería eliminar")
    void eliminarComentario_ComoAutor_DeberiaEliminar() {
        // Arrange
        when(comentarioRepository.findById(1L)).thenReturn(Optional.of(comentarioValido));
        when(viviendaRepository.existsById(1L)).thenReturn(true);

        // Act
        comentarioService.eliminarComentario(1L, 1L, false);

        // Assert
        verify(comentarioRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Eliminar comentario como administrador - debería eliminar")
    void eliminarComentario_ComoAdministrador_DeberiaEliminar() {
        // Arrange
        when(comentarioRepository.findById(1L)).thenReturn(Optional.of(comentarioValido));
        when(viviendaRepository.existsById(1L)).thenReturn(true);

        // Act
        comentarioService.eliminarComentario(1L, 999L, true);

        // Assert
        verify(comentarioRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Eliminar comentario como no autor ni admin - debería lanzar excepción")
    void eliminarComentario_SinPermisos_DeberiaLanzarExcepcion() {
        // Arrange
        when(comentarioRepository.findById(1L)).thenReturn(Optional.of(comentarioValido));

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> comentarioService.eliminarComentario(1L, 999L, false));
        assertTrue(exception.getMessage().contains("autor original o un administrador"));
        verify(comentarioRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Eliminar comentario con vivienda inexistente - debería lanzar excepción")
    void eliminarComentario_ConViviendaInexistente_DeberiaLanzarExcepcion() {
        // Arrange
        when(comentarioRepository.findById(1L)).thenReturn(Optional.of(comentarioValido));
        when(viviendaRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> comentarioService.eliminarComentario(1L, 1L, false));
        assertTrue(exception.getMessage().contains("vivienda asociada"));
        verify(comentarioRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Eliminar comentario como autor (método simplificado) - debería eliminar")
    void eliminarComentarioComoAutor_DeberiaEliminar() {
        // Arrange
        when(comentarioRepository.findById(1L)).thenReturn(Optional.of(comentarioValido));
        when(viviendaRepository.existsById(1L)).thenReturn(true);

        // Act
        comentarioService.eliminarComentarioComoAutor(1L, 1L);

        // Assert
        verify(comentarioRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Eliminar comentario como administrador (método simplificado) - debería eliminar")
    void eliminarComentarioComoAdministrador_DeberiaEliminar() {
        // Arrange
        when(comentarioRepository.findById(1L)).thenReturn(Optional.of(comentarioValido));
        when(viviendaRepository.existsById(1L)).thenReturn(true);

        // Act
        comentarioService.eliminarComentarioComoAdministrador(1L);

        // Assert
        verify(comentarioRepository).deleteById(1L);
    }

    // ==================== PRUEBAS DE PUEDE COMENTAR VIVIENDA ====================

    @Test
    @DisplayName("Puede comentar vivienda con estancia completada - debería retornar true")
    void puedeComentarVivienda_ConEstanciaCompletada_DeberiaRetornarTrue() {
        // Arrange
        when(estanciaRepository.existsByEstudianteArrendador_IdAndViviendaArrendada_IdAndEstado(
                1L, 1L, EstadoEstancia.COMPLETADA)).thenReturn(true);

        // Act
        boolean resultado = comentarioService.puedeComentarVivienda(1L, 1L);

        // Assert
        assertTrue(resultado);
    }

    @Test
    @DisplayName("Puede comentar vivienda sin estancia completada - debería retornar false")
    void puedeComentarVivienda_SinEstanciaCompletada_DeberiaRetornarFalse() {
        // Arrange
        when(estanciaRepository.existsByEstudianteArrendador_IdAndViviendaArrendada_IdAndEstado(
                1L, 1L, EstadoEstancia.COMPLETADA)).thenReturn(false);

        // Act
        boolean resultado = comentarioService.puedeComentarVivienda(1L, 1L);

        // Assert
        assertFalse(resultado);
    }
}