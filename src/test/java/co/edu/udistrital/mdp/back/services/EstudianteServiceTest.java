package co.edu.udistrital.mdp.back.services;

import co.edu.udistrital.mdp.back.entities.EstudianteEntity;
import co.edu.udistrital.mdp.back.entities.ReservaEntity;
import co.edu.udistrital.mdp.back.repositories.EstudianteRepository;
import co.edu.udistrital.mdp.back.repositories.ReservaRepository;
import co.edu.udistrital.mdp.back.exceptions.IllegalOperationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EstudianteServiceTest {

    @Mock
    private EstudianteRepository estudianteRepo;

    @Mock
    private ReservaRepository reservaRepo; // Mock para verificar reservas

    @InjectMocks
    private EstudianteService estudianteService;

    private EstudianteEntity estudiante;

    @BeforeEach
    void setup() {
        estudiante = new EstudianteEntity();
        estudiante.setId(1L);
        estudiante.setNombre("Juan");
        estudiante.setCorreo("juan@mail.com");
        // No se asocian reservas por defecto aquí, se hará en la prueba específica
    }

    @Test
    void createEstudiante_success() throws IllegalOperationException {
        when(estudianteRepo.findByCorreo("juan@mail.com")).thenReturn(Optional.empty());
        when(estudianteRepo.save(any(EstudianteEntity.class))).thenAnswer(inv -> inv.getArgument(0)); // Simula guardar

        EstudianteEntity creado = estudianteService.createEstudiante(estudiante);

        assertThat(creado).isNotNull();
        assertThat(creado.getNombre()).isEqualTo("Juan");
        verify(estudianteRepo).save(estudiante); // Verifica que se llamó a save con el objeto estudiante
    }

    @Test
    void createEstudiante_correoDuplicado_debeLanzar() {
        // Simula que ya existe un estudiante con ese correo
        when(estudianteRepo.findByCorreo("juan@mail.com")).thenReturn(Optional.of(estudiante));

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> estudianteService.createEstudiante(estudiante));

        // Ajusta el mensaje esperado para que coincida exactamente con el lanzado
        assertThat(ex.getMessage().toLowerCase()).contains("el correo ya está registrado");
        verify(estudianteRepo, never()).save(any()); // Verifica que no se intentó guardar
    }

    @Test
    void deleteEstudiante_conReservasAsociadas_debeLanzar() {
        // Simula que el estudiante existe
        when(estudianteRepo.findById(1L)).thenReturn(Optional.of(estudiante));
        // Simula que el repositorio de reservas encuentra reservas para este estudiante
        when(reservaRepo.findByEstudianteId(1L)).thenReturn(List.of(new ReservaEntity())); // Devuelve una lista NO vacía

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> estudianteService.deleteEstudiante(1L));

        assertThat(ex.getMessage()).contains("No se puede eliminar el estudiante con reservas");
        verify(estudianteRepo, never()).delete(any()); // Verifica que no se llamó a delete
        verify(reservaRepo).findByEstudianteId(1L); // Verifica que se consultaron las reservas
    }

    @Test
    void deleteEstudiante_success() throws IllegalOperationException {
        // Simula que el estudiante existe
        when(estudianteRepo.findById(1L)).thenReturn(Optional.of(estudiante));
        // Simula que el repositorio de reservas NO encuentra reservas
        when(reservaRepo.findByEstudianteId(1L)).thenReturn(Collections.emptyList()); // Devuelve lista VACÍA

        // Llama al método a probar
        estudianteService.deleteEstudiante(1L);

        // Verifica que se llamó a delete con el OBJETO estudiante encontrado
        verify(estudianteRepo).delete(estudiante);
        verify(estudianteRepo, never()).deleteById(anyLong()); // Asegura que no se llamó a deleteById
        verify(reservaRepo).findByEstudianteId(1L); // Verifica que se consultaron las reservas
    }

    @Test
    void deleteEstudiante_noEncontrado_debeLanzar() {
        // Simula que el estudiante NO existe
        when(estudianteRepo.findById(1L)).thenReturn(Optional.empty());

        // El servicio lanza IllegalArgumentException si no lo encuentra
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> estudianteService.deleteEstudiante(1L));

        assertThat(ex.getMessage()).contains("Estudiante no encontrado con ID: 1");
        verify(estudianteRepo, never()).delete(any()); // Verifica que no se intentó borrar
        verify(reservaRepo, never()).findByEstudianteId(anyLong()); // Verifica que no se buscaron reservas
    }
}