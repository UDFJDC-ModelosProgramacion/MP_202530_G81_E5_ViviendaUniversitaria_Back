package co.edu.udistrital.mdp.back.services;

import co.edu.udistrital.mdp.back.entities.ContratoEntity;
import co.edu.udistrital.mdp.back.entities.EstanciaEntity;
import co.edu.udistrital.mdp.back.entities.EstudianteEntity;
import co.edu.udistrital.mdp.back.entities.ViviendaEntity;
import co.edu.udistrital.mdp.back.repositories.EstanciaRepository;
import co.edu.udistrital.mdp.back.repositories.EstudianteRepository;
import co.edu.udistrital.mdp.back.repositories.ViviendaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
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

        // Mock para simular guardado de estancia
        when(estanciaRepo.save(any(EstanciaEntity.class))).thenAnswer(inv -> {
            EstanciaEntity e = inv.getArgument(0);
            if (e.getId() == null)
                e.setId(50L); // Asigna ID si es nueva
            return e;
        });

        // Mock para simular guardado de vivienda (actualización de disponibilidad)
        when(viviendaRepo.save(any(ViviendaEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        // Mocks por defecto para existencia de estudiante y viviendas
        when(estudianteRepo.findById(1L)).thenReturn(Optional.of(estudiantePersistido));
        when(viviendaRepo.findById(100L)).thenReturn(Optional.of(viviendaDisponible));
        when(viviendaRepo.findById(101L)).thenReturn(Optional.of(viviendaNoDisponible));
    }

    @Test
    void crearEstancia_success() {
        EstanciaEntity in = new EstanciaEntity();
        in.setEstudianteArrendador(estudiantePersistido);
        in.setViviendaArrendada(viviendaDisponible);
        in.setTiempoEstancia(6); // Tiempo válido

        EstanciaEntity creado = estanciaService.crearEstancia(in);

        assertThat(creado).isNotNull();
        assertThat(creado.getId()).isEqualTo(50L); // Verifica ID asignado
        assertThat(creado.getEstado()).isEqualTo(EstanciaEntity.EstadoEstancia.ACTIVA); // Verifica estado inicial
        assertThat(creado.getFechaInicio()).isNotNull(); // Verifica fecha inicio
        assertThat(viviendaDisponible.isDisponible()).isFalse(); // Verifica que la vivienda se marcó no disponible

        verify(estanciaRepo).save(any(EstanciaEntity.class)); // Verifica guardado de estancia
        verify(viviendaRepo).save(viviendaDisponible); // Verifica actualización de vivienda
    }

    @Test
    void crearEstancia_viviendaNoDisponible_debeLanzar() {
        EstanciaEntity in = new EstanciaEntity();
        in.setEstudianteArrendador(estudiantePersistido);
        in.setViviendaArrendada(viviendaNoDisponible); // Usa la vivienda no disponible
        in.setTiempoEstancia(3);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> estanciaService.crearEstancia(in));
        assertThat(ex.getMessage()).contains("La vivienda no está disponible para arrendar");
        verify(estanciaRepo, never()).save(any());
        verify(viviendaRepo, never()).save(any(ViviendaEntity.class)); // Verifica que no se guardó la vivienda tampoco
    }

    @Test
    void crearEstancia_estudianteNoExiste_debeLanzar() {
        EstanciaEntity in = new EstanciaEntity();
        EstudianteEntity estRef = new EstudianteEntity();
        estRef.setId(999L); // ID que no existe
        in.setEstudianteArrendador(estRef);
        ViviendaEntity vivRef = new ViviendaEntity();
        vivRef.setId(100L); // Vivienda sí existe
        in.setViviendaArrendada(vivRef);
        in.setTiempoEstancia(5);

        when(estudianteRepo.findById(999L)).thenReturn(Optional.empty()); // Simula que estudiante no existe

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> estanciaService.crearEstancia(in));
        assertThat(ex.getMessage()).contains("Estudiante no encontrado con ID: 999");
        verify(estanciaRepo, never()).save(any());
    }

    @Test
    void crearEstancia_viviendaNoExiste_debeLanzar() {
        EstanciaEntity in = new EstanciaEntity();
        EstudianteEntity estRef = new EstudianteEntity();
        estRef.setId(1L); // Estudiante sí existe
        in.setEstudianteArrendador(estRef);
        ViviendaEntity vivRef = new ViviendaEntity();
        vivRef.setId(999L); // ID que no existe
        in.setViviendaArrendada(vivRef);
        in.setTiempoEstancia(5);

        when(viviendaRepo.findById(999L)).thenReturn(Optional.empty()); // Simula que vivienda no existe

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> estanciaService.crearEstancia(in));
        assertThat(ex.getMessage()).contains("Vivienda no encontrada con ID: 999");
        verify(estanciaRepo, never()).save(any());
    }

    @Test
    void crearEstancia_tiempoExcede36_debeLanzar() {
        EstanciaEntity in = new EstanciaEntity();
        in.setEstudianteArrendador(estudiantePersistido);
        in.setViviendaArrendada(viviendaDisponible);
        in.setTiempoEstancia(48); // > 36

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> estanciaService.crearEstancia(in));
        assertThat(ex.getMessage()).contains("tiempoEstancia no debe exceder 36 meses");
        verify(estanciaRepo, never()).save(any());
    }

    @Test
    void crearEstancia_tiempoNull_debeLanzar() {
        EstanciaEntity in = new EstanciaEntity();
        in.setEstudianteArrendador(estudiantePersistido);
        in.setViviendaArrendada(viviendaDisponible);
        in.setTiempoEstancia(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> estanciaService.crearEstancia(in));
        assertThat(ex.getMessage()).contains("tiempoEstancia debe ser >= 1");
        verify(estanciaRepo, never()).save(any());
    }

    @Test
    void eliminar_estanciaConContrato_debeLanzar() {
        EstanciaEntity found = new EstanciaEntity(estudiantePersistido, viviendaDisponible, 4);
        found.setId(5L);
        ContratoEntity contrato = new ContratoEntity();
        contrato.setId(20L);
        found.setContrato(contrato); // Asocia un contrato

        when(estanciaRepo.findById(5L)).thenReturn(Optional.of(found)); // Simula encontrar esta estancia

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> estanciaService.eliminar(5L));
        assertThat(ex.getMessage()).contains("existe un contrato asociado");
        verify(estanciaRepo, never()).delete(any());
    }

    @Test
    void actualizar_noPermiteCambiarViviendaSiTieneContrato_debeLanzar() {
        EstanciaEntity original = new EstanciaEntity(estudiantePersistido, viviendaDisponible, 6);
        original.setId(11L);
        ContratoEntity contrato = new ContratoEntity();
        contrato.setId(30L);
        original.setContrato(contrato); // Tiene contrato

        when(estanciaRepo.findById(11L)).thenReturn(Optional.of(original));

        // Intenta actualizar cambiando la vivienda
        EstanciaEntity updates = new EstanciaEntity();
        ViviendaEntity nuevaVivienda = new ViviendaEntity();
        nuevaVivienda.setId(200L); // ID diferente
        updates.setViviendaArrendada(nuevaVivienda);
        updates.setTiempoEstancia(7); // Actualiza también el tiempo para que sea válido

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> estanciaService.actualizar(11L, updates));
        assertThat(ex.getMessage())
                .contains("No se permite cambiar viviendaArrendada cuando la estancia tiene contrato asociado");
        verify(estanciaRepo, never()).save(any()); // No debe intentar guardar
    }

    @Test
    void actualizar_cambiarViviendaValida_happyPath() {
        // preparar original sin contrato
        EstanciaEntity original = new EstanciaEntity(estudiantePersistido, viviendaDisponible, 6);
        original.setId(21L);
        ViviendaEntity vieja = new ViviendaEntity();
        vieja.setId(100L);
        vieja.setDisponible(false); // ocupada actualmente
        original.setViviendaArrendada(vieja);

        when(estanciaRepo.findById(21L)).thenReturn(Optional.of(original));

        // nueva vivienda disponible
        ViviendaEntity nueva = new ViviendaEntity();
        nueva.setId(300L);
        nueva.setDisponible(true);
        when(viviendaRepo.findById(300L)).thenReturn(Optional.of(nueva));

        EstanciaEntity updates = new EstanciaEntity();
        ViviendaEntity nvRef = new ViviendaEntity();
        nvRef.setId(300L);
        updates.setViviendaArrendada(nvRef);
        updates.setTiempoEstancia(8);

        when(estanciaRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        EstanciaEntity res = estanciaService.actualizar(21L, updates);

        // vieja vivienda liberada, nueva ocupada
        assertThat(res.getViviendaArrendada().getId()).isEqualTo(300L);
        verify(viviendaRepo).save(vieja); // liberada -> disponible=true guardada
        verify(viviendaRepo).save(nueva); // ocupada -> disponible=false guardada
        verify(estanciaRepo).save(original);
    }

    @Test
    void completarEstancia_noActiva_debeLanzar() {
        EstanciaEntity e = new EstanciaEntity(estudiantePersistido, viviendaDisponible, 3);
        e.setId(40L);
        e.setEstado(EstanciaEntity.EstadoEstancia.COMPLETADA);
        when(estanciaRepo.findById(40L)).thenReturn(Optional.of(e));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> estanciaService.completarEstancia(40L));
        assertThat(ex.getMessage()).contains("No se puede completar una estancia con estado");
        verify(viviendaRepo, never()).save(any());
    }

    @Test
    void completarEstancia_happyPath() {
        EstanciaEntity e = new EstanciaEntity(estudiantePersistido, viviendaDisponible, 3);
        e.setId(41L);
        e.setEstado(EstanciaEntity.EstadoEstancia.ACTIVA);
        when(estanciaRepo.findById(41L)).thenReturn(Optional.of(e));
        when(estanciaRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(viviendaRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        EstanciaEntity res = estanciaService.completarEstancia(41L);

        assertThat(res.getEstado()).isEqualTo(EstanciaEntity.EstadoEstancia.COMPLETADA);
        assertThat(res.getFechaFin()).isNotNull();
        verify(viviendaRepo).save(viviendaDisponible);
        verify(estanciaRepo).save(res);
    }

    @Test
    void cancelarEstancia_completada_debeLanzar() {
        EstanciaEntity e = new EstanciaEntity(estudiantePersistido, viviendaDisponible, 3);
        e.setId(42L);
        e.setEstado(EstanciaEntity.EstadoEstancia.COMPLETADA);
        when(estanciaRepo.findById(42L)).thenReturn(Optional.of(e));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> estanciaService.cancelarEstancia(42L));
        assertThat(ex.getMessage()).contains("No se puede cancelar una estancia ya completada");
        verify(viviendaRepo, never()).save(any());
    }

    @Test
    void cancelarEstancia_happyPath() {
        EstanciaEntity e = new EstanciaEntity(estudiantePersistido, viviendaDisponible, 3);
        e.setId(43L);
        e.setEstado(EstanciaEntity.EstadoEstancia.ACTIVA);
        when(estanciaRepo.findById(43L)).thenReturn(Optional.of(e));
        when(estanciaRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(viviendaRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        EstanciaEntity res = estanciaService.cancelarEstancia(43L);

        assertThat(res.getEstado()).isEqualTo(EstanciaEntity.EstadoEstancia.CANCELADA);
        assertThat(res.getFechaFin()).isNotNull();
        verify(viviendaRepo).save(viviendaDisponible);
        verify(estanciaRepo).save(res);
    }

    @Test
    void obtenerEstanciasActivas_delegaARepo() {
        when(estanciaRepo.findByEstudianteArrendador_IdAndEstado(1L, EstanciaEntity.EstadoEstancia.ACTIVA))
                .thenReturn(Collections.emptyList());

        List<EstanciaEntity> list = estanciaService.obtenerEstanciasActivas(1L);
        assertThat(list).isNotNull();
        verify(estanciaRepo).findByEstudianteArrendador_IdAndEstado(1L, EstanciaEntity.EstadoEstancia.ACTIVA);
    }

    @Test
    void obtenerTodas_delegaARepo() {
        when(estanciaRepo.findAll()).thenReturn(Collections.emptyList());
        estanciaService.obtenerTodas();
        verify(estanciaRepo).findAll();
    }
}
