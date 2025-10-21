/*   
package co.edu.udistrital.mdp.back.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import co.edu.udistrital.mdp.back.entities.EstudianteEntity;
import co.edu.udistrital.mdp.back.entities.NotificacionEntity;
import co.edu.udistrital.mdp.back.repositories.EstudianteRepository;
import co.edu.udistrital.mdp.back.repositories.NotificacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class) // Habilita Mockito con JUnit 5
class NotificacionServiceTest {

    @Mock // Mock del Repositorio de Notificaciones
    private NotificacionRepository notificacionRepository;

    @Mock // Mock del Repositorio de Estudiantes (necesario para validaciones)
    private EstudianteRepository estudianteRepository;

    @InjectMocks // Instancia real del Servicio con mocks inyectados
    private NotificacionService notificacionService;

    private PodamFactory factory = new PodamFactoryImpl();
    private List<NotificacionEntity> dataList = new ArrayList<>();
    private EstudianteEntity estudianteValido;

    @BeforeEach
    void setUp() {
        dataList.clear();
        estudianteValido = factory.manufacturePojo(EstudianteEntity.class);
        estudianteValido.setId(1L); // Asigna un ID válido

        for (int i = 0; i < 3; i++) {
            NotificacionEntity notificacion = factory.manufacturePojo(NotificacionEntity.class);
            notificacion.setEstudiante(estudianteValido); // Asocia al estudiante válido
            notificacion.setLeida(false); // Asegura estado inicial
            dataList.add(notificacion);
        }
    }

    // --- Tests para enviarNotificacion (CREATE) ---

    @Test
    void testEnviarNotificacionExitosa() {
        NotificacionEntity nuevaNotificacion = factory.manufacturePojo(NotificacionEntity.class);
        nuevaNotificacion.setEstudiante(estudianteValido); // Estudiante válido
        nuevaNotificacion.setMensaje("Mensaje válido");
        nuevaNotificacion.setId(null); // Asegura que no tenga ID previo

        // Simula que el estudiante existe
        when(estudianteRepository.existsById(estudianteValido.getId())).thenReturn(true);
        // Simula la respuesta del save
        when(notificacionRepository.save(any(NotificacionEntity.class))).thenAnswer(invocation -> {
            NotificacionEntity guardada = invocation.getArgument(0);
            guardada.setId(10L); // Asigna un ID simulado
            guardada.setFechaEnvio(LocalDateTime.now()); // Simula @PrePersist
            guardada.setLeida(false); // Verifica estado inicial
            return guardada;
        });

        NotificacionEntity result = notificacionService.enviarNotificacion(nuevaNotificacion);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("Mensaje válido", result.getMensaje());
        assertFalse(result.getLeida());
        assertNotNull(result.getFechaEnvio());
        assertEquals(estudianteValido.getId(), result.getEstudiante().getId());

        verify(estudianteRepository, times(1)).existsById(estudianteValido.getId());
        verify(notificacionRepository, times(1)).save(any(NotificacionEntity.class));
    }

    @Test
    void testEnviarNotificacionSinEstudiante() {
        NotificacionEntity notificacion = new NotificacionEntity();
        notificacion.setMensaje("Test");
        notificacion.setEstudiante(null); // Sin estudiante

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            notificacionService.enviarNotificacion(notificacion);
        });
        assertEquals("La notificación debe tener un destinatario.", thrown.getMessage());
        verify(notificacionRepository, never()).save(any()); // No debe intentar guardar
    }
    
    @Test
    void testEnviarNotificacionEstudianteNoExiste() {
        NotificacionEntity notificacion = factory.manufacturePojo(NotificacionEntity.class);
        notificacion.setEstudiante(estudianteValido);
        notificacion.setMensaje("Test");

        // Simula que el estudiante NO existe
        when(estudianteRepository.existsById(estudianteValido.getId())).thenReturn(false);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            notificacionService.enviarNotificacion(notificacion);
        });
        assertTrue(thrown.getMessage().contains("El estudiante destinatario con ID"));
        assertTrue(thrown.getMessage().contains("no existe."));
        verify(notificacionRepository, never()).save(any());
    }

    @Test
    void testEnviarNotificacionMensajeVacio() {
        NotificacionEntity notificacion = factory.manufacturePojo(NotificacionEntity.class);
        notificacion.setEstudiante(estudianteValido);
        notificacion.setMensaje("   "); // Mensaje vacío o solo espacios

        // Simula que el estudiante existe
        when(estudianteRepository.existsById(estudianteValido.getId())).thenReturn(true);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            notificacionService.enviarNotificacion(notificacion);
        });
        assertEquals("El mensaje de la notificación no puede ser vacío.", thrown.getMessage());
        verify(notificacionRepository, never()).save(any());
    }

    // --- Tests para obtenerNotificacionPorId (READ ONE) ---
    @Test
    void testObtenerNotificacionPorIdExistente() {
        Long idBuscado = 1L;
        NotificacionEntity notificacionPrueba = dataList.get(0);
        notificacionPrueba.setId(idBuscado);

        when(notificacionRepository.findById(idBuscado)).thenReturn(Optional.of(notificacionPrueba));

        NotificacionEntity result = notificacionService.obtenerNotificacionPorId(idBuscado);

        assertNotNull(result);
        assertEquals(idBuscado, result.getId());
        verify(notificacionRepository, times(1)).findById(idBuscado);
    }

    @Test
    void testObtenerNotificacionPorIdNoExistente() {
        Long idBuscado = 99L;
        when(notificacionRepository.findById(idBuscado)).thenReturn(Optional.empty());

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            notificacionService.obtenerNotificacionPorId(idBuscado);
        });
        assertEquals("Notificación no encontrada con ID: " + idBuscado, thrown.getMessage());
        verify(notificacionRepository, times(1)).findById(idBuscado);
    }

    // --- Tests para obtenerNotificacionesPorEstudiante (READ MANY) ---
    @Test
    void testObtenerNotificacionesPorEstudiante() {
        Long estudianteId = estudianteValido.getId();
        when(notificacionRepository.findByEstudiante_Id(estudianteId)).thenReturn(dataList);

        List<NotificacionEntity> result = notificacionService.obtenerNotificacionesPorEstudiante(estudianteId);

        assertNotNull(result);
        assertEquals(dataList.size(), result.size());
        verify(notificacionRepository, times(1)).findByEstudiante_Id(estudianteId);
    }

    // --- Tests para marcarComoLeida (UPDATE) ---
    @Test
    void testMarcarComoLeidaExitosa() {
        Long idNotificacion = 1L;
        NotificacionEntity notificacionNoLeida = dataList.get(0);
        notificacionNoLeida.setId(idNotificacion);
        notificacionNoLeida.setLeida(false); // Estado inicial

        when(notificacionRepository.findById(idNotificacion)).thenReturn(Optional.of(notificacionNoLeida));
        when(notificacionRepository.save(any(NotificacionEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        NotificacionEntity result = notificacionService.marcarComoLeida(idNotificacion);

        assertNotNull(result);
        assertTrue(result.getLeida()); // Debe cambiar a true
        verify(notificacionRepository, times(1)).findById(idNotificacion);
        verify(notificacionRepository, times(1)).save(notificacionNoLeida);
    }

    @Test
    void testMarcarComoLeidaYaLeida() {
        Long idNotificacion = 1L;
        NotificacionEntity notificacionLeida = dataList.get(0);
        notificacionLeida.setId(idNotificacion);
        notificacionLeida.setLeida(true); // Ya estaba leída

        when(notificacionRepository.findById(idNotificacion)).thenReturn(Optional.of(notificacionLeida));

        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            notificacionService.marcarComoLeida(idNotificacion);
        });
        assertEquals("La notificación ya está marcada como leída.", thrown.getMessage());
        verify(notificacionRepository, times(1)).findById(idNotificacion);
        verify(notificacionRepository, never()).save(any()); // No debe intentar guardar
    }
    
    @Test
    void testMarcarComoLeidaNoEncontrada() {
        Long idNotificacion = 99L;
        when(notificacionRepository.findById(idNotificacion)).thenReturn(Optional.empty());

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            notificacionService.marcarComoLeida(idNotificacion);
        });
        assertEquals("Notificación no encontrada con ID: " + idNotificacion, thrown.getMessage());
        verify(notificacionRepository, times(1)).findById(idNotificacion);
        verify(notificacionRepository, never()).save(any());
    }

    // --- Tests para eliminarNotificacion (DELETE) ---
    @Test
    void testEliminarNotificacionExitosa() {
        Long idAEliminar = 1L;
        when(notificacionRepository.existsById(idAEliminar)).thenReturn(true);
        // Configura el mock para deleteById (no devuelve nada)
        doNothing().when(notificacionRepository).deleteById(idAEliminar);

        // Llama al método (no devuelve nada)
        assertDoesNotThrow(() -> {
            notificacionService.eliminarNotificacion(idAEliminar);
        });

        // Verifica que se llamó a existsById y deleteById
        verify(notificacionRepository, times(1)).existsById(idAEliminar);
        verify(notificacionRepository, times(1)).deleteById(idAEliminar);
    }

    @Test
    void testEliminarNotificacionNoExistente() {
        Long idAEliminar = 99L;
        when(notificacionRepository.existsById(idAEliminar)).thenReturn(false);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            notificacionService.eliminarNotificacion(idAEliminar);
        });
        assertEquals("No se puede eliminar la notificación con ID " + idAEliminar + " porque no existe.", thrown.getMessage());

        verify(notificacionRepository, times(1)).existsById(idAEliminar);
        verify(notificacionRepository, never()).deleteById(anyLong()); // No debe intentar borrar
    }
}
    */