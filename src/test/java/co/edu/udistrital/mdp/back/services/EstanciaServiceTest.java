package co.edu.udistrital.mdp.back;

import co.edu.udistrital.mdp.back.entities.ContratoEntity;
import co.edu.udistrital.mdp.back.entities.EstanciaEntity;
import co.edu.udistrital.mdp.back.entities.EstudianteEntity;
import co.edu.udistrital.mdp.back.entities.ViviendaEntity;
import co.edu.udistrital.mdp.back.repositories.EstanciaRepository;
import co.edu.udistrital.mdp.back.repositories.EstudianteRepository;
import co.edu.udistrital.mdp.back.repositories.ViviendaRepository;
import co.edu.udistrital.mdp.back.services.EstanciaService;

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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("unused")
@ExtendWith(MockitoExtension.class)
class EstanciaServiceTest {

    @Mock
    private EstanciaRepository estanciaRepo;

    @Mock
    private EstudianteRepository estudianteRepo;

    @Mock
    private ViviendaRepository viviendaRepo;

    @InjectMocks
    private EstanciaService estanciaService;

    private EstudianteEntity estudiantePersistido;
    private ViviendaEntity viviendaDisponible;
    private ViviendaEntity viviendaNoDisponible;

    @BeforeEach
    void setup() {
        estudiantePersistido = new EstudianteEntity();
        estudiantePersistido.setId(1L);

        viviendaDisponible = new ViviendaEntity();
        viviendaDisponible.setId(100L);
        viviendaDisponible.setDisponible(true);

        viviendaNoDisponible = new ViviendaEntity();
        viviendaNoDisponible.setId(101L);
        viviendaNoDisponible.setDisponible(false);

        when(estanciaRepo.save(any(EstanciaEntity.class))).thenAnswer(inv -> {
            EstanciaEntity e = inv.getArgument(0);
            if (e.getId() == null) e.setId(50L);
            return e;
        });

        when(viviendaRepo.save(any(ViviendaEntity.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void crearEstancia_success() {
        EstanciaEntity in = new EstanciaEntity();
        in.setEstudianteArrendador(estudiantePersistido);
        in.setViviendaArrendada(viviendaDisponible);
        in.setTiempoEstancia(6);

        when(estudianteRepo.findById(1L)).thenReturn(Optional.of(estudiantePersistido));
        when(viviendaRepo.findById(viviendaDisponible.getId())).thenReturn(Optional.of(viviendaDisponible));

        EstanciaEntity creado = estanciaService.crearEstancia(in);

        assertThat(creado).isNotNull();
        assertThat(creado.getId()).isEqualTo(50L);
        ViviendaEntity vivGuardada = viviendaRepo.findById(viviendaDisponible.getId()).orElse(null);
        // como usamos mocks, verificar que save fue llamado (estado actualizado)
        verify(viviendaRepo).save(viviendaDisponible);
    }

    @Test
    void crearEstancia_viviendaNoDisponible_debeLanzar() {
        EstanciaEntity in = new EstanciaEntity();
        in.setEstudianteArrendador(estudiantePersistido);
        in.setViviendaArrendada(viviendaNoDisponible);
        in.setTiempoEstancia(3);

        when(estudianteRepo.findById(1L)).thenReturn(Optional.of(estudiantePersistido));
        when(viviendaRepo.findById(viviendaNoDisponible.getId())).thenReturn(Optional.of(viviendaNoDisponible));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> estanciaService.crearEstancia(in));
        assertThat(ex.getMessage().toLowerCase()).contains("no está disponible");
        verify(estanciaRepo, never()).save(any());
    }

    @Test
    void crearEstancia_estudianteNoExiste_debeLanzar() {
        EstanciaEntity in = new EstanciaEntity();
        EstudianteEntity estRef = new EstudianteEntity();
        estRef.setId(999L);
        in.setEstudianteArrendador(estRef);
        ViviendaEntity vivRef = new ViviendaEntity();
        vivRef.setId(100L);
        in.setViviendaArrendada(vivRef);
        in.setTiempoEstancia(5);

        when(estudianteRepo.findById(999L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> estanciaService.crearEstancia(in));
        assertThat(ex.getMessage().toLowerCase()).contains("estudiante no encontrado");
        verify(estanciaRepo, never()).save(any());
    }

    @Test
    void crearEstancia_viviendaNoExiste_debeLanzar() {
        EstanciaEntity in = new EstanciaEntity();
        EstudianteEntity estRef = new EstudianteEntity();
        estRef.setId(1L);
        in.setEstudianteArrendador(estRef);
        ViviendaEntity vivRef = new ViviendaEntity();
        vivRef.setId(999L);
        in.setViviendaArrendada(vivRef);
        in.setTiempoEstancia(5);

        when(estudianteRepo.findById(1L)).thenReturn(Optional.of(estudiantePersistido));
        when(viviendaRepo.findById(999L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> estanciaService.crearEstancia(in));
        assertThat(ex.getMessage().toLowerCase()).contains("vivienda no encontrada");
        verify(estanciaRepo, never()).save(any());
    }

    @Test
    void crearEstancia_tiempoExcede36_debeLanzar() {
        EstanciaEntity in = new EstanciaEntity();
        in.setEstudianteArrendador(estudiantePersistido);
        in.setViviendaArrendada(viviendaDisponible);
        in.setTiempoEstancia(48); // > 36

        when(estudianteRepo.findById(1L)).thenReturn(Optional.of(estudiantePersistido));
        when(viviendaRepo.findById(viviendaDisponible.getId())).thenReturn(Optional.of(viviendaDisponible));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> estanciaService.crearEstancia(in));
        assertThat(ex.getMessage().toLowerCase()).contains("no debe exceder 36");
        verify(estanciaRepo, never()).save(any());
    }

    @Test
    void eliminar_estanciaConContrato_debeLanzar() {
        EstanciaEntity found = new EstanciaEntity(estudiantePersistido, viviendaDisponible, 4);
        found.setId(5L);
        ContratoEntity contrato = new ContratoEntity();
        contrato.setId(20L);
        found.setContrato(contrato);

        when(estanciaRepo.findById(5L)).thenReturn(Optional.of(found));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> estanciaService.eliminar(5L));
        assertThat(ex.getMessage().toLowerCase()).contains("contrato");
        verify(estanciaRepo, never()).delete(any());
    }

    @Test
    void actualizar_noPermiteCambiarViviendaSiTieneContrato_debeLanzar() {
        EstanciaEntity original = new EstanciaEntity(estudiantePersistido, viviendaDisponible, 6);
        original.setId(11L);
        ContratoEntity contrato = new ContratoEntity();
        contrato.setId(30L);
        original.setContrato(contrato);

        when(estanciaRepo.findById(11L)).thenReturn(Optional.of(original));

        EstanciaEntity updates = new EstanciaEntity();
        ViviendaEntity nueva = new ViviendaEntity();
        nueva.setId(200L);
        updates.setViviendaArrendada(nueva);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> estanciaService.actualizar(11L, updates));
        assertThat(ex.getMessage().toLowerCase()).contains("no se permite cambiar viviendaarrendada");
    }
}
