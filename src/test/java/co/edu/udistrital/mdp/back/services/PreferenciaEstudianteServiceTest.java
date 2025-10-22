
package co.edu.udistrital.mdp.back.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import co.edu.udistrital.mdp.back.entities.EstudianteEntity;
import co.edu.udistrital.mdp.back.entities.PreferenciaEstudianteEntity;
import co.edu.udistrital.mdp.back.repositories.EstudianteRepository;
import co.edu.udistrital.mdp.back.repositories.PreferenciaEstudianteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class PreferenciaEstudianteServiceTest {

    @Mock
    private PreferenciaEstudianteRepository preferenciaRepository;

    @Mock
    private EstudianteRepository estudianteRepository;

    @InjectMocks
    private PreferenciaEstudianteService preferenciaService;

    private PodamFactory factory = new PodamFactoryImpl();
    private EstudianteEntity estudianteValido;
    private PreferenciaEstudianteEntity preferenciaExistente;

    @BeforeEach
    void setUp() {
        estudianteValido = factory.manufacturePojo(EstudianteEntity.class);
        estudianteValido.setId(1L);

        preferenciaExistente = factory.manufacturePojo(PreferenciaEstudianteEntity.class);
        preferenciaExistente.setId(1L);
        preferenciaExistente.setEstudiante(estudianteValido);
        preferenciaExistente.setPrecioMaximo(1000); // Valor positivo
        preferenciaExistente.setTipoVivienda("Apartamento"); // No vacío
    }

    // --- Tests para crearPreferencias (CREATE) ---

    @Test
    void testCrearPreferenciasExitosa() {
        PreferenciaEstudianteEntity nuevaPref = factory.manufacturePojo(PreferenciaEstudianteEntity.class);
        nuevaPref.setEstudiante(estudianteValido);
        nuevaPref.setPrecioMaximo(500);
        nuevaPref.setTipoVivienda("Habitación");
        nuevaPref.setId(null);

        // Mock validaciones: Estudiante existe, no hay pref previas
        when(estudianteRepository.existsById(estudianteValido.getId())).thenReturn(true);
        when(preferenciaRepository.findByEstudianteId(estudianteValido.getId())).thenReturn(Optional.empty());
        // Mock save
        when(preferenciaRepository.save(any(PreferenciaEstudianteEntity.class))).thenAnswer(invocation -> {
            PreferenciaEstudianteEntity guardada = invocation.getArgument(0);
            guardada.setId(10L); // Simula ID asignado
            return guardada;
        });

        PreferenciaEstudianteEntity result = preferenciaService.crearPreferencias(nuevaPref);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals(estudianteValido.getId(), result.getEstudiante().getId());

        verify(estudianteRepository, times(1)).existsById(estudianteValido.getId());
        verify(preferenciaRepository, times(1)).findByEstudianteId(estudianteValido.getId());
        verify(preferenciaRepository, times(1)).save(any(PreferenciaEstudianteEntity.class));
    }

    @Test
    void testCrearPreferenciasSinEstudiante() {
        PreferenciaEstudianteEntity pref = new PreferenciaEstudianteEntity();
        pref.setEstudiante(null);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            preferenciaService.crearPreferencias(pref);
        });
        assertEquals("Las preferencias deben estar asociadas a un estudiante.", thrown.getMessage());
        verify(preferenciaRepository, never()).save(any());
    }
    
    @Test
    void testCrearPreferenciasEstudianteNoExiste() {
        PreferenciaEstudianteEntity pref = factory.manufacturePojo(PreferenciaEstudianteEntity.class);
        pref.setEstudiante(estudianteValido);
        pref.setPrecioMaximo(500);
        pref.setTipoVivienda("Habitación");

        when(estudianteRepository.existsById(estudianteValido.getId())).thenReturn(false); // Estudiante NO existe

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            preferenciaService.crearPreferencias(pref);
        });
        assertTrue(thrown.getMessage().contains("El estudiante con ID"));
        assertTrue(thrown.getMessage().contains("no existe."));
        verify(preferenciaRepository, never()).save(any());
    }

    @Test
    void testCrearPreferenciasEstudianteYaTiene() {
        PreferenciaEstudianteEntity nuevaPref = factory.manufacturePojo(PreferenciaEstudianteEntity.class);
        nuevaPref.setEstudiante(estudianteValido);
        nuevaPref.setPrecioMaximo(500);
        nuevaPref.setTipoVivienda("Habitación");

        when(estudianteRepository.existsById(estudianteValido.getId())).thenReturn(true);
        // Simula que ya existen preferencias para este estudiante
        when(preferenciaRepository.findByEstudianteId(estudianteValido.getId())).thenReturn(Optional.of(preferenciaExistente));

        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            preferenciaService.crearPreferencias(nuevaPref);
        });
        assertTrue(thrown.getMessage().contains("ya tiene un perfil de preferencias."));
        verify(preferenciaRepository, never()).save(any());
    }
    
    @Test
    void testCrearPreferenciasPrecioInvalido() {
        PreferenciaEstudianteEntity pref = factory.manufacturePojo(PreferenciaEstudianteEntity.class);
        pref.setEstudiante(estudianteValido);
        pref.setPrecioMaximo(0); // Precio inválido
        pref.setTipoVivienda("Habitación");

        when(estudianteRepository.existsById(estudianteValido.getId())).thenReturn(true);
        when(preferenciaRepository.findByEstudianteId(estudianteValido.getId())).thenReturn(Optional.empty());

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            preferenciaService.crearPreferencias(pref);
        });
        assertEquals("El 'precioMaximo' debe ser un valor positivo.", thrown.getMessage());
         verify(preferenciaRepository, never()).save(any());
    }

    @Test
    void testCrearPreferenciasTipoViviendaVacio() {
        PreferenciaEstudianteEntity pref = factory.manufacturePojo(PreferenciaEstudianteEntity.class);
        pref.setEstudiante(estudianteValido);
        pref.setPrecioMaximo(500);
        pref.setTipoVivienda(" "); // Tipo vacío

        when(estudianteRepository.existsById(estudianteValido.getId())).thenReturn(true);
        when(preferenciaRepository.findByEstudianteId(estudianteValido.getId())).thenReturn(Optional.empty());
        
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            preferenciaService.crearPreferencias(pref);
        });
        assertEquals("El campo 'tipoVivienda' no puede ser nulo o vacío.", thrown.getMessage());
         verify(preferenciaRepository, never()).save(any());
    }

    // --- Tests para obtenerPreferenciasPorId (READ ONE by Pref ID) ---
    @Test
    void testObtenerPreferenciasPorIdExistente() {
        Long idBuscado = 1L;
        when(preferenciaRepository.findById(idBuscado)).thenReturn(Optional.of(preferenciaExistente));

        PreferenciaEstudianteEntity result = preferenciaService.obtenerPreferenciasPorId(idBuscado);

        assertNotNull(result);
        assertEquals(idBuscado, result.getId());
        verify(preferenciaRepository, times(1)).findById(idBuscado);
    }

    @Test
    void testObtenerPreferenciasPorIdNoExistente() {
        Long idBuscado = 99L;
        when(preferenciaRepository.findById(idBuscado)).thenReturn(Optional.empty());

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            preferenciaService.obtenerPreferenciasPorId(idBuscado);
        });
        assertEquals("Perfil de preferencias no encontrado con ID: " + idBuscado, thrown.getMessage());
        verify(preferenciaRepository, times(1)).findById(idBuscado);
    }

    // --- Tests para obtenerPreferenciasPorEstudianteId (READ ONE by Estudiante ID) ---
     @Test
    void testObtenerPreferenciasPorEstudianteIdExistente() {
        Long estudianteId = estudianteValido.getId();
        when(preferenciaRepository.findByEstudianteId(estudianteId)).thenReturn(Optional.of(preferenciaExistente));

        PreferenciaEstudianteEntity result = preferenciaService.obtenerPreferenciasPorEstudianteId(estudianteId);

        assertNotNull(result);
        assertEquals(estudianteId, result.getEstudiante().getId());
        verify(preferenciaRepository, times(1)).findByEstudianteId(estudianteId);
    }

    @Test
    void testObtenerPreferenciasPorEstudianteIdNoExistente() {
        Long estudianteId = 99L; // ID de estudiante que no tiene preferencias
        when(preferenciaRepository.findByEstudianteId(estudianteId)).thenReturn(Optional.empty());

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            preferenciaService.obtenerPreferenciasPorEstudianteId(estudianteId);
        });
        assertEquals("No se encontraron preferencias para el estudiante con ID: " + estudianteId, thrown.getMessage());
        verify(preferenciaRepository, times(1)).findByEstudianteId(estudianteId);
    }


    // --- Tests para actualizarPreferencias (UPDATE) ---
    @Test
    void testActualizarPreferenciasExitosa() {
        Long idPref = 1L;
        PreferenciaEstudianteEntity actualizaciones = factory.manufacturePojo(PreferenciaEstudianteEntity.class);
        // Asegura que el estudiante sea el mismo que el existente
        actualizaciones.setEstudiante(estudianteValido); 
        actualizaciones.setPrecioMaximo(1500);
        actualizaciones.setTipoVivienda("Casa");
        actualizaciones.setZonaPreferida("Norte");

        when(preferenciaRepository.findById(idPref)).thenReturn(Optional.of(preferenciaExistente));
        when(preferenciaRepository.save(any(PreferenciaEstudianteEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PreferenciaEstudianteEntity result = preferenciaService.actualizarPreferencias(idPref, actualizaciones);

        assertNotNull(result);
        assertEquals(idPref, result.getId());
        assertEquals(1500, result.getPrecioMaximo()); // Verifica campo actualizado
        assertEquals("Casa", result.getTipoVivienda());
        assertEquals("Norte", result.getZonaPreferida());

        verify(preferenciaRepository, times(1)).findById(idPref);
        verify(preferenciaRepository, times(1)).save(preferenciaExistente);
    }
    
    @Test
    void testActualizarPreferenciasNoEncontrada() {
         Long idPref = 99L;
         PreferenciaEstudianteEntity actualizaciones = new PreferenciaEstudianteEntity();
         
         when(preferenciaRepository.findById(idPref)).thenReturn(Optional.empty());

         IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
             preferenciaService.actualizarPreferencias(idPref, actualizaciones);
         });
         assertTrue(thrown.getMessage().contains("Perfil de preferencias no encontrado con ID:"));
         verify(preferenciaRepository, times(1)).findById(idPref);
         verify(preferenciaRepository, never()).save(any());
    }

    @Test
    void testActualizarPreferenciasCambiandoEstudiante() {
        Long idPref = 1L;
        PreferenciaEstudianteEntity actualizaciones = factory.manufacturePojo(PreferenciaEstudianteEntity.class);
        EstudianteEntity otroEstudiante = factory.manufacturePojo(EstudianteEntity.class);
        otroEstudiante.setId(2L); // ID diferente
        actualizaciones.setEstudiante(otroEstudiante); // Intenta cambiar estudiante
        actualizaciones.setPrecioMaximo(1500);
        actualizaciones.setTipoVivienda("Casa");

        when(preferenciaRepository.findById(idPref)).thenReturn(Optional.of(preferenciaExistente));

        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            preferenciaService.actualizarPreferencias(idPref, actualizaciones);
        });
        assertEquals("No se puede cambiar el estudiante de un perfil de preferencias.", thrown.getMessage());
        verify(preferenciaRepository, times(1)).findById(idPref);
        verify(preferenciaRepository, never()).save(any());
    }
    
    @Test
    void testActualizarPreferenciasPrecioInvalido() {
        Long idPref = 1L;
        PreferenciaEstudianteEntity actualizaciones = new PreferenciaEstudianteEntity();
        actualizaciones.setEstudiante(estudianteValido); // Mismo estudiante
        actualizaciones.setPrecioMaximo(-100); // Precio inválido
        actualizaciones.setTipoVivienda("Casa");

        when(preferenciaRepository.findById(idPref)).thenReturn(Optional.of(preferenciaExistente));

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            preferenciaService.actualizarPreferencias(idPref, actualizaciones);
        });
        assertEquals("El 'precioMaximo' debe ser un valor positivo.", thrown.getMessage());
        verify(preferenciaRepository, times(1)).findById(idPref);
        verify(preferenciaRepository, never()).save(any());
    }
     @Test
    void testActualizarPreferenciasTipoViviendaVacio() {
        Long idPref = 1L;
        PreferenciaEstudianteEntity actualizaciones = new PreferenciaEstudianteEntity();
        actualizaciones.setEstudiante(estudianteValido); // Mismo estudiante
        actualizaciones.setPrecioMaximo(1500);
        actualizaciones.setTipoVivienda(""); // Tipo vacío

        when(preferenciaRepository.findById(idPref)).thenReturn(Optional.of(preferenciaExistente));

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            preferenciaService.actualizarPreferencias(idPref, actualizaciones);
        });
        assertEquals("El campo 'tipoVivienda' no puede ser nulo o vacío.", thrown.getMessage());
        verify(preferenciaRepository, times(1)).findById(idPref);
        verify(preferenciaRepository, never()).save(any());
    }


    
}

/* Fin de los tests para PreferenciaEstudianteService */