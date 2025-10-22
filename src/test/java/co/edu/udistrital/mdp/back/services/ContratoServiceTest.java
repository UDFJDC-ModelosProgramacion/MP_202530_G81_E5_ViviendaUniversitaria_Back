/*   
package co.edu.udistrital.mdp.back.services;

import co.edu.udistrital.mdp.back.entities.ContratoEntity;
import co.edu.udistrital.mdp.back.entities.EstanciaEntity;
import co.edu.udistrital.mdp.back.entities.EstudianteEntity;
import co.edu.udistrital.mdp.back.entities.ViviendaEntity;
import co.edu.udistrital.mdp.back.repositories.ContratoRepository;
import co.edu.udistrital.mdp.back.repositories.EstanciaRepository;
import co.edu.udistrital.mdp.back.services.ContratoService;

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

@ExtendWith(MockitoExtension.class)
class ContratoServiceTest {

    @Mock
    private ContratoRepository contratoRepo;

    @Mock
    private EstanciaRepository estanciaRepo;

    @InjectMocks
    private ContratoService contratoService;

    private EstanciaEntity estanciaPersistida;

    @BeforeEach
    void setup() {
        // preparar una estancia "persistida" para usarla en tests
        EstudianteEntity estudiante = new EstudianteEntity();
        estudiante.setId(1L);

        ViviendaEntity vivienda = new ViviendaEntity();
        vivienda.setId(2L);
        vivienda.setDisponible(true);

        estanciaPersistida = new EstanciaEntity(estudiante, vivienda, 6);
        estanciaPersistida.setId(10L);

        // comportamiento por defecto: save de contrato devuelve el argumento con id simulado
        when(contratoRepo.save(any(ContratoEntity.class))).thenAnswer(inv -> {
            ContratoEntity c = inv.getArgument(0);
            if (c.getId() == null) c.setId(99L);
            return c;
        });

        // por defecto existencia de contrato por estancia -> false
        when(contratoRepo.existsByEstancia_Id(anyLong())).thenReturn(false);
    }

    @Test
    void crearContrato_success() {
        ContratoEntity in = new ContratoEntity();
        in.setCodigo("CT-100");
        EstanciaEntity ref = new EstanciaEntity();
        ref.setId(10L);
        in.setEstancia(ref);
        in.setFechaInicio(LocalDate.of(2025, 1, 1));
        in.setFechaFin(LocalDate.of(2025, 6, 1));
        in.setMontoTotal(1500.0);

        when(estanciaRepo.findById(10L)).thenReturn(Optional.of(estanciaPersistida));
        when(contratoRepo.existsByCodigoIgnoreCase("CT-100")).thenReturn(false);
        when(contratoRepo.existsByEstancia_Id(10L)).thenReturn(false);

        ContratoEntity creado = contratoService.crearContrato(in);

        assertThat(creado).isNotNull();
        assertThat(creado.getId()).isEqualTo(99L);
        verify(contratoRepo).save(any(ContratoEntity.class));
        // verificar que el servicio intentó guardar la estancia (relación inversa)
        verify(estanciaRepo).save(any(EstanciaEntity.class));
    }

    @Test
    void crearContrato_codigoVacio_debeLanzar() {
        ContratoEntity in = new ContratoEntity();
        in.setCodigo("   ");
        EstanciaEntity ref = new EstanciaEntity();
        ref.setId(10L);
        in.setEstancia(ref);
        in.setFechaInicio(LocalDate.of(2025,1,1));
        in.setFechaFin(LocalDate.of(2025,2,1));
        in.setMontoTotal(100.0);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> contratoService.crearContrato(in));
        assertThat(ex.getMessage().toLowerCase()).contains("codigo");
        verify(contratoRepo, never()).save(any());
    }

    @Test
    void crearContrato_codigoDuplicado_debeLanzar() {
        ContratoEntity in = new ContratoEntity();
        in.setCodigo("DUP");
        EstanciaEntity ref = new EstanciaEntity();
        ref.setId(10L);
        in.setEstancia(ref);
        in.setFechaInicio(LocalDate.of(2025,1,1));
        in.setFechaFin(LocalDate.of(2025,2,1));
        in.setMontoTotal(100.0);

        when(contratoRepo.existsByCodigoIgnoreCase("DUP")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> contratoService.crearContrato(in));
        assertThat(ex.getMessage().toLowerCase()).contains("ya existe");
        verify(contratoRepo, never()).save(any());
    }

    @Test
    void crearContrato_estanciaNull_debeLanzar() {
        ContratoEntity in = new ContratoEntity();
        in.setCodigo("CT-NO-EST");
        in.setEstancia(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> contratoService.crearContrato(in));
        assertThat(ex.getMessage().toLowerCase()).contains("estancia obligatoria");
        verify(contratoRepo, never()).save(any());
    }

    @Test
    void crearContrato_estanciaNoEncontrada_debeLanzar() {
        ContratoEntity in = new ContratoEntity();
        in.setCodigo("CT-NO-EST2");
        EstanciaEntity ref = new EstanciaEntity();
        ref.setId(999L);
        in.setEstancia(ref);
        in.setFechaInicio(LocalDate.of(2025,1,1));
        in.setFechaFin(LocalDate.of(2025,2,1));
        in.setMontoTotal(200.0);

        when(estanciaRepo.findById(999L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> contratoService.crearContrato(in));
        assertThat(ex.getMessage().toLowerCase()).contains("estancia no encontrada");
        verify(contratoRepo, never()).save(any());
    }

    @Test
    void crearContrato_estanciaTieneContrato_debeLanzar() {
        ContratoEntity in = new ContratoEntity();
        in.setCodigo("CT-EX");
        EstanciaEntity ref = new EstanciaEntity();
        ref.setId(10L);
        in.setEstancia(ref);
        in.setFechaInicio(LocalDate.of(2025,1,1));
        in.setFechaFin(LocalDate.of(2025,2,1));
        in.setMontoTotal(100.0);

        EstanciaEntity estWithContract = new EstanciaEntity();
        estWithContract.setId(10L);
        ContratoEntity existing = new ContratoEntity();
        existing.setId(77L);
        estWithContract.setContrato(existing);

        when(estanciaRepo.findById(10L)).thenReturn(Optional.of(estWithContract));
        when(contratoRepo.existsByEstancia_Id(10L)).thenReturn(true);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> contratoService.crearContrato(in));
        assertThat(ex.getMessage().toLowerCase()).contains("ya tiene un contrato");
        verify(contratoRepo, never()).save(any());
    }

    @Test
    void crearContrato_fechasInvalidas_debeLanzar() {
        ContratoEntity in = new ContratoEntity();
        in.setCodigo("CT-FE");
        EstanciaEntity ref = new EstanciaEntity();
        ref.setId(10L);
        in.setEstancia(ref);
        in.setFechaInicio(null);
        in.setFechaFin(LocalDate.of(2025,1,1));
        in.setMontoTotal(100.0);

        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class, () -> contratoService.crearContrato(in));
        assertThat(ex1.getMessage().toLowerCase()).contains("fechainicio y fechafin");

        // fechaFin no puede ser posterior a fechaInicio
        in.setFechaInicio(LocalDate.of(2025,1,10));
        in.setFechaFin(LocalDate.of(2025,1,5));
        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class, () -> contratoService.crearContrato(in));
        assertThat(ex2.getMessage().toLowerCase()).contains("fechafin debe ser posterior");
    }

    @Test
    void crearContrato_montoNegativo_debeLanzar() {
        ContratoEntity in = new ContratoEntity();
        in.setCodigo("CT-MT");
        EstanciaEntity ref = new EstanciaEntity();
        ref.setId(10L);
        in.setEstancia(ref);
        in.setFechaInicio(LocalDate.of(2025,1,1));
        in.setFechaFin(LocalDate.of(2025,2,1));
        in.setMontoTotal(-50.0);

        when(estanciaRepo.findById(10L)).thenReturn(Optional.of(estanciaPersistida));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> contratoService.crearContrato(in));
        assertThat(ex.getMessage().toLowerCase()).contains("montototal");
        verify(contratoRepo, never()).save(any());
    }

    @Test
    void actualizar_intentarCambiarEstancia_debeLanzar() {
        ContratoEntity existing = new ContratoEntity();
        existing.setId(12L);
        EstanciaEntity estOld = new EstanciaEntity();
        estOld.setId(5L);
        existing.setEstancia(estOld);
        when(contratoRepo.findById(12L)).thenReturn(Optional.of(existing));

        ContratoEntity updates = new ContratoEntity();
        EstanciaEntity estNew = new EstanciaEntity();
        estNew.setId(99L);
        updates.setEstancia(estNew);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> contratoService.actualizar(12L, updates));
        assertThat(ex.getMessage().toLowerCase()).contains("no está permitido cambiar la estancia");
    }

    @Test
    void eliminar_contratoVigente_debeLanzar() {
        ContratoEntity found = new ContratoEntity();
        found.setId(1L);
        found.setFechaInicio(LocalDate.now().minusDays(10));
        found.setFechaFin(LocalDate.now().plusDays(1)); // vigente
        found.setEstancia(estanciaPersistida);

        when(contratoRepo.findById(1L)).thenReturn(Optional.of(found));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> contratoService.eliminar(1L));
        assertThat(ex.getMessage().toLowerCase()).contains("vigente");
        verify(contratoRepo, never()).delete(any());
    }
}
*/