/*   
package co.edu.udistrital.mdp.back.services;

import co.edu.udistrital.mdp.back.entities.EstudianteEntity;
import co.edu.udistrital.mdp.back.entities.ReservaEntity;
import co.edu.udistrital.mdp.back.repositories.EstudianteRepository;
import co.edu.udistrital.mdp.back.repositories.ReservaRepository;
import co.edu.udistrital.mdp.back.services.EstudianteService;
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
    private ReservaRepository reservaRepo;

    @InjectMocks
    private EstudianteService estudianteService;

    private EstudianteEntity estudiante;

    @BeforeEach
    void setup() {
        estudiante = new EstudianteEntity();
        estudiante.setId(1L);
        estudiante.setNombre("Juan");
        estudiante.setCorreo("juan@mail.com");
    }

    @Test
    void createEstudiante_success() throws IllegalOperationException {
        when(estudianteRepo.findByCorreo("juan@mail.com")).thenReturn(Optional.empty());
        when(estudianteRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        EstudianteEntity creado = estudianteService.createEstudiante(estudiante);

        assertThat(creado).isNotNull();
        verify(estudianteRepo).save(any());
    }

    @Test
    void createEstudiante_correoDuplicado_debeLanzar() {
        when(estudianteRepo.findByCorreo("juan@mail.com")).thenReturn(Optional.of(estudiante));

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> estudianteService.createEstudiante(estudiante));
        assertThat(ex.getMessage().toLowerCase()).contains("ya se encuentra registrado");
        verify(estudianteRepo, never()).save(any());
    }

    @Test
    void deleteEstudiante_conReservasAsociadas_debeLanzar() {
        estudiante.setReservas(List.of(new ReservaEntity()));
        when(estudianteRepo.findById(1L)).thenReturn(Optional.of(estudiante));

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> estudianteService.deleteEstudiante(1L));
        assertThat(ex.getMessage().toLowerCase()).contains("reservas asociadas");
        verify(estudianteRepo, never()).deleteById(any());
    }

    @Test
    void deleteEstudiante_success() throws IllegalOperationException {
        estudiante.setReservas(Collections.emptyList());
        when(estudianteRepo.findById(1L)).thenReturn(Optional.of(estudiante));

        estudianteService.deleteEstudiante(1L);
        verify(estudianteRepo).deleteById(1L);
    }

    @Test
    void deleteEstudiante_noEncontrado_debeLanzar() {
        when(estudianteRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> estudianteService.deleteEstudiante(1L));
    }
}
*/