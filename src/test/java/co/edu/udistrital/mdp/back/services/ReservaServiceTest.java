package co.edu.udistrital.mdp.back.services;

import co.edu.udistrital.mdp.back.entities.EstudianteEntity;
import co.edu.udistrital.mdp.back.entities.ReservaEntity;
import co.edu.udistrital.mdp.back.entities.ViviendaEntity;
import co.edu.udistrital.mdp.back.repositories.ReservaRepository;
import co.edu.udistrital.mdp.back.repositories.EstudianteRepository;
import co.edu.udistrital.mdp.back.repositories.ViviendaRepository;
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
        vivienda.setDisponible(true); // Vivienda disponible por defecto

        estudiante = new EstudianteEntity();
        estudiante.setId(2L);

        reserva = new ReservaEntity();
        reserva.setId(3L);
        reserva.setFechaInicio(LocalDate.now().plusDays(1));
        reserva.setFechaFin(LocalDate.now().plusDays(10));
        reserva.setEstudiante(estudiante);
        reserva.setVivienda(vivienda);
        reserva.setEstado("Pendiente"); 
    }

    @Test
    void createReserva_success() throws IllegalOperationException {
        ReservaEntity reservaNueva = new ReservaEntity();
        reservaNueva.setFechaInicio(LocalDate.now().plusDays(1));
        reservaNueva.setFechaFin(LocalDate.now().plusDays(10));
        reservaNueva.setEstudiante(estudiante);
        reservaNueva.setVivienda(vivienda);

        when(viviendaRepo.findById(1L)).thenReturn(Optional.of(vivienda));
        when(estudianteRepo.findById(2L)).thenReturn(Optional.of(estudiante));
        when(reservaRepo.save(any(ReservaEntity.class))).thenAnswer(inv -> {
            ReservaEntity r = inv.getArgument(0);
            r.setId(4L); 
            r.setEstado("Pendiente"); 
            return r;
        });
        when(viviendaRepo.save(any(ViviendaEntity.class))).thenReturn(vivienda);

        ReservaEntity creada = reservaService.createReserva(reservaNueva);

        assertThat(creada).isNotNull();
        assertThat(creada.getId()).isEqualTo(4L); 
        assertThat(creada.getEstado()).isEqualTo("Pendiente"); 
        assertThat(vivienda.getDisponible()).isFalse(); 

        verify(estudianteRepo).findById(2L); 
        verify(viviendaRepo).findById(1L);   
        verify(reservaRepo).save(reservaNueva); 
        verify(viviendaRepo).save(vivienda);    
    }

    @Test
    void createReserva_viviendaNoDisponible_debeLanzar() {
        
        vivienda.setDisponible(false);
        when(viviendaRepo.findById(1L)).thenReturn(Optional.of(vivienda));
        when(estudianteRepo.findById(2L)).thenReturn(Optional.of(estudiante)); 

        
        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> reservaService.createReserva(reserva)); 
        assertThat(ex.getMessage()).contains("La vivienda no está disponible para reservar");

        
        verify(reservaRepo, never()).save(any());
        verify(viviendaRepo, never()).save(any());
    }

    @Test
    void deleteReserva_noEncontrada_debeLanzar() {
        when(reservaRepo.findById(3L)).thenReturn(Optional.empty());
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> reservaService.deleteReserva(3L));
        assertThat(ex.getMessage()).contains("Reserva no encontrada con ID: 3");

        verify(reservaRepo, never()).delete(any());
        verify(viviendaRepo, never()).save(any());
    }

    @Test
    void deleteReserva_success() throws IllegalOperationException {
        
        reserva.setEstado("Cancelada");
        when(reservaRepo.findById(3L)).thenReturn(Optional.of(reserva));
        
        when(viviendaRepo.save(any(ViviendaEntity.class))).thenReturn(vivienda);

        
        reservaService.deleteReserva(3L);

        
        verify(reservaRepo).findById(3L); 
        verify(viviendaRepo).save(vivienda); 
        verify(reservaRepo).delete(reserva); 
    }

    @Test
    void deleteReserva_estadoNoCancelada_debeLanzar() {
        when(reservaRepo.findById(3L)).thenReturn(Optional.of(reserva));

        
        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> reservaService.deleteReserva(3L));
        assertThat(ex.getMessage()).contains("Solo se pueden eliminar reservas canceladas");

        
        verify(reservaRepo, never()).delete(any());
        verify(viviendaRepo, never()).save(any());
    }
}