package co.edu.udistrital.mdp.back.services;

import co.edu.udistrital.mdp.back.entities.EstudianteEntity;
import co.edu.udistrital.mdp.back.entities.ReservaEntity;
import co.edu.udistrital.mdp.back.entities.ViviendaEntity;
import co.edu.udistrital.mdp.back.repositories.ReservaRepository;
import co.edu.udistrital.mdp.back.repositories.EstudianteRepository;
import co.edu.udistrital.mdp.back.repositories.ViviendaRepository;
import co.edu.udistrital.mdp.back.services.ReservaService;
import co.edu.udistrital.mdp.back.exceptions.IllegalOperationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepo;

    @Mock
    private ViviendaRepository viviendaRepo;

    @Mock
    private EstudianteRepository estudianteRepo;

    @InjectMocks
    private ReservaService reservaService;

    private ReservaEntity reserva;
    private ViviendaEntity vivienda;
    private EstudianteEntity estudiante;

    @BeforeEach
    void setup() {
        vivienda = new ViviendaEntity();
        vivienda.setId(1L);
        vivienda.setDisponible(true);

        estudiante = new EstudianteEntity();
        estudiante.setId(2L);

        reserva = new ReservaEntity();
        reserva.setId(3L);
        reserva.setFechaInicio(LocalDate.now().plusDays(1));
        reserva.setFechaFin(LocalDate.now().plusDays(10));
        reserva.setEstudiante(estudiante);
        reserva.setVivienda(vivienda);
    }

    @Test
    void createReserva_success() throws IllegalOperationException {
        when(viviendaRepo.findById(1L)).thenReturn(Optional.of(vivienda));
        when(estudianteRepo.findById(2L)).thenReturn(Optional.of(estudiante));
        when(reservaRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ReservaEntity creada = reservaService.createReserva(reserva);
        assertThat(creada).isNotNull();
        verify(reservaRepo).save(any());
    }

    @Test
    void createReserva_viviendaNoDisponible_debeLanzar() {
        vivienda.setDisponible(false);
        when(viviendaRepo.findById(1L)).thenReturn(Optional.of(vivienda));
        when(estudianteRepo.findById(2L)).thenReturn(Optional.of(estudiante));

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> reservaService.createReserva(reserva));
        assertThat(ex.getMessage().toLowerCase()).contains("no está disponible");
    }

    @Test
    void deleteReserva_noEncontrada_debeLanzar() {
        when(reservaRepo.findById(3L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> reservaService.deleteReserva(3L));
    }

    @Test
    void deleteReserva_success() throws IllegalOperationException {
        when(reservaRepo.findById(3L)).thenReturn(Optional.of(reserva));
        reservaService.deleteReserva(3L);
        verify(reservaRepo).delete(reserva);
    }
}