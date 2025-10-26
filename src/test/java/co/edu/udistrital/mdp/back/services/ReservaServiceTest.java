package co.edu.udistrital.mdp.back.services;

import co.edu.udistrital.mdp.back.entities.EstudianteEntity;
import co.edu.udistrital.mdp.back.entities.ReservaEntity;
import co.edu.udistrital.mdp.back.entities.ViviendaEntity;
import co.edu.udistrital.mdp.back.repositories.EstudianteRepository;
import co.edu.udistrital.mdp.back.repositories.ReservaRepository;
import co.edu.udistrital.mdp.back.repositories.ViviendaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepo;

    @Mock
    private ViviendaRepository viviendaRepo;

    @Mock
    private EstudianteRepository estudianteRepo;

    @InjectMocks
    private ReservaService reservaService;

    private EstudianteEntity estudiante;
    private ViviendaEntity vivienda;
    private ReservaEntity reservaPersistida;

    @BeforeEach
    void setup() {
        estudiante = new EstudianteEntity();
        estudiante.setId(1L);
        estudiante.setNombre("Carlos López");

        vivienda = new ViviendaEntity();
        vivienda.setId(2L);

        reservaPersistida = new ReservaEntity();
        reservaPersistida.setId(3L);
        reservaPersistida.setEstudiante(estudiante);
        reservaPersistida.setVivienda(vivienda);
        reservaPersistida.setFechaInicio(LocalDate.now());
        reservaPersistida.setFechaFin(LocalDate.now().plusDays(5));
        reservaPersistida.setEstado("Pendiente");

        // Mocks comunes
        when(estudianteRepo.findById(1L)).thenReturn(Optional.of(estudiante));
        when(viviendaRepo.findById(2L)).thenReturn(Optional.of(vivienda));
        when(reservaRepo.findById(3L)).thenReturn(Optional.of(reservaPersistida));
        when(reservaRepo.save(any(ReservaEntity.class))).thenAnswer(inv -> {
            ReservaEntity r = inv.getArgument(0);
            if (r.getId() == null)
                r.setId(10L);
            return r;
        });
        when(reservaRepo.existeReservaActiva(anyLong(), anyLong(), any(), any())).thenReturn(false);
    }

    @Test
    void createReserva_success() {
        ReservaEntity in = new ReservaEntity();
        in.setEstudiante(estudiante);
        in.setVivienda(vivienda);
        in.setFechaInicio(LocalDate.now());
        in.setFechaFin(LocalDate.now().plusDays(2));
        in.setEstado("Pendiente");

        ReservaEntity creada = reservaService.createReserva(in);

        assertThat(creada.getId()).isEqualTo(10L);
        assertThat(creada.getEstado()).isEqualTo("Pendiente");
        verify(reservaRepo).save(in);
    }

    @Test
    void createReserva_estudianteNoExiste_throws() {
        when(estudianteRepo.findById(1L)).thenReturn(Optional.empty());

        ReservaEntity in = new ReservaEntity();
        in.setEstudiante(estudiante);
        in.setVivienda(vivienda);
        in.setFechaInicio(LocalDate.now());
        in.setFechaFin(LocalDate.now().plusDays(1));

        var ex = assertThrows(IllegalArgumentException.class, () -> reservaService.createReserva(in));
        assertThat(ex.getMessage()).contains("Estudiante no encontrado");
        verify(reservaRepo, never()).save(any());
    }

    @Test
    void createReserva_viviendaNoExiste_throws() {
        when(viviendaRepo.findById(2L)).thenReturn(Optional.empty());

        ReservaEntity in = new ReservaEntity();
        in.setEstudiante(estudiante);
        in.setVivienda(vivienda);
        in.setFechaInicio(LocalDate.now());
        in.setFechaFin(LocalDate.now().plusDays(1));

        var ex = assertThrows(IllegalArgumentException.class, () -> reservaService.createReserva(in));
        assertThat(ex.getMessage()).contains("Vivienda no encontrada");
        verify(reservaRepo, never()).save(any());
    }

    @Test
    void createReserva_fechasInvalidas_throws() {
        ReservaEntity in = new ReservaEntity();
        in.setEstudiante(estudiante);
        in.setVivienda(vivienda);
        in.setFechaInicio(LocalDate.now().plusDays(5));
        in.setFechaFin(LocalDate.now());

        var ex = assertThrows(IllegalArgumentException.class, () -> reservaService.createReserva(in));
        assertThat(ex.getMessage()).contains("fecha de inicio no puede ser posterior");
        verify(reservaRepo, never()).save(any());
    }

    @Test
    void createReserva_duplicada_throws() {
        when(reservaRepo.existeReservaActiva(eq(1L), eq(2L), any(LocalDate.class), any(LocalDate.class))).thenReturn(true);

        ReservaEntity in = new ReservaEntity();
        in.setEstudiante(estudiante);
        in.setVivienda(vivienda);
        in.setFechaInicio(LocalDate.now());
        in.setFechaFin(LocalDate.now().plusDays(3));

        var ex = assertThrows(IllegalArgumentException.class, () -> reservaService.createReserva(in));
        assertThat(ex.getMessage()).contains("Ya existe una reserva activa");
        verify(reservaRepo, never()).save(any());
    }

    @Test
    void updateReserva_success() {
        ReservaEntity updates = new ReservaEntity();
        updates.setFechaInicio(LocalDate.now());
        updates.setFechaFin(LocalDate.now().plusDays(7));
        updates.setEstado("Confirmada");

        ReservaEntity updated = reservaService.updateReserva(3L, updates);

        assertThat(updated.getEstado()).isEqualTo("Confirmada");
        verify(reservaRepo).save(reservaPersistida);
    }

    @Test
    void updateReserva_fechasInvalidas_throws() {
        ReservaEntity updates = new ReservaEntity();
        updates.setFechaInicio(LocalDate.now().plusDays(5));
        updates.setFechaFin(LocalDate.now());

        var ex = assertThrows(IllegalArgumentException.class, () -> reservaService.updateReserva(3L, updates));
        assertThat(ex.getMessage()).contains("inicio no puede ser posterior");
        verify(reservaRepo, never()).save(any());
    }

    @Test
    void deleteReserva_activa_throws() {
        reservaPersistida.setEstado("Confirmada");

        var ex = assertThrows(IllegalStateException.class, () -> reservaService.deleteReserva(3L));
        assertThat(ex.getMessage()).contains("No se puede eliminar una reserva activa");
        verify(reservaRepo, never()).delete(any());
    }

    @Test
    void deleteReserva_cancelada_success() {
        reservaPersistida.setEstado("Cancelada");

        reservaService.deleteReserva(3L);

        verify(reservaRepo).delete(reservaPersistida);
    }

    @Test
    void getAllReservas_returnsList() {
        when(reservaRepo.findAll()).thenReturn(List.of(reservaPersistida));

        List<ReservaEntity> result = reservaService.getAllReservas();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(3L);
        verify(reservaRepo).findAll();
    }
}
