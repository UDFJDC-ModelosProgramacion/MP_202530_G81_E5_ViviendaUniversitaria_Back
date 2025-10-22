package co.edu.udistrital.mdp.back.services;

import co.edu.udistrital.mdp.back.entities.ContratoEntity;
import co.edu.udistrital.mdp.back.entities.EstanciaEntity;
import co.edu.udistrital.mdp.back.entities.EstudianteEntity;
import co.edu.udistrital.mdp.back.entities.ViviendaEntity;
import co.edu.udistrital.mdp.back.repositories.ContratoRepository;
import co.edu.udistrital.mdp.back.repositories.EstanciaRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;


import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
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
        EstudianteEntity estudiante = new EstudianteEntity();
        estudiante.setId(1L);

        ViviendaEntity vivienda = new ViviendaEntity();
        vivienda.setId(2L);
        vivienda.setDisponible(true);

        estanciaPersistida = new EstanciaEntity(estudiante, vivienda, 6);
        estanciaPersistida.setId(10L);

        when(contratoRepo.save(any(ContratoEntity.class))).thenAnswer(inv -> {
            ContratoEntity c = inv.getArgument(0);
            if (c.getId() == null) c.setId(99L);
            return c;
        });

        when(contratoRepo.existsByEstancia_Id(anyLong())).thenReturn(false);
        when(estanciaRepo.findById(10L)).thenReturn(Optional.of(estanciaPersistida));
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

        when(contratoRepo.existsByCodigoIgnoreCase("CT-100")).thenReturn(false);

        ContratoEntity creado = contratoService.crearContrato(in);

        assertThat(creado).isNotNull();
        assertThat(creado.getId()).isEqualTo(99L);
        assertThat(creado.getEstancia().getId()).isEqualTo(10L);
        verify(contratoRepo).save(in);
        verify(estanciaRepo).save(estanciaPersistida);
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

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> contratoService.crearContrato(in));
        assertThat(ex.getMessage().toLowerCase()).contains("codigo obligatorio");
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

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> contratoService.crearContrato(in));
        assertThat(ex.getMessage().toLowerCase()).contains("ya existe un contrato con ese codigo");
        verify(contratoRepo, never()).save(any());
    }

    @Test
    void crearContrato_estanciaNull_debeLanzar() {
        ContratoEntity in = new ContratoEntity();
        in.setCodigo("CT-NO-EST");
        in.setEstancia(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> contratoService.crearContrato(in));
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

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> contratoService.crearContrato(in));
        assertThat(ex.getMessage().toLowerCase()).contains("estancia no encontrada con id: 999");
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

        ContratoEntity existingContract = new ContratoEntity();
        existingContract.setId(77L);
        estanciaPersistida.setContrato(existingContract);
        when(contratoRepo.existsByEstancia_Id(10L)).thenReturn(true);
        when(estanciaRepo.findById(10L)).thenReturn(Optional.of(estanciaPersistida));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> contratoService.crearContrato(in));
        assertThat(ex.getMessage().toLowerCase()).contains("la estancia ya tiene un contrato asociado");
        verify(contratoRepo, never()).save(any());

        estanciaPersistida.setContrato(null);
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

        when(estanciaRepo.findById(10L)).thenReturn(Optional.of(estanciaPersistida));
        when(contratoRepo.existsByCodigoIgnoreCase("CT-FE")).thenReturn(false);
        when(contratoRepo.existsByEstancia_Id(10L)).thenReturn(false);

        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class,
                () -> contratoService.crearContrato(in));
        assertThat(ex1.getMessage().toLowerCase()).contains("fechainicio y fechafin son obligatorias");

        in.setFechaInicio(LocalDate.of(2025,1,10));
        in.setFechaFin(LocalDate.of(2025,1,5));
        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class,
                () -> contratoService.crearContrato(in));
        assertThat(ex2.getMessage().toLowerCase()).contains("fechafin debe ser posterior a fechainicio");

        verify(contratoRepo, never()).save(any());
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

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> contratoService.crearContrato(in));
        assertThat(ex.getMessage().toLowerCase()).contains("montototal debe ser >= 0");
        verify(contratoRepo, never()).save(any());
    }

    @Test
    void actualizar_intentarCambiarEstancia_debeLanzar() {
        ContratoEntity existing = new ContratoEntity();
        existing.setId(12L);
        existing.setCodigo("OLD-CODE");
        existing.setFechaInicio(LocalDate.now().minusDays(5));
        existing.setFechaFin(LocalDate.now().plusDays(5));
        existing.setMontoTotal(100.0);
        EstanciaEntity estOld = new EstanciaEntity();
        estOld.setId(5L);
        existing.setEstancia(estOld);
        when(contratoRepo.findById(12L)).thenReturn(Optional.of(existing));

        ContratoEntity updates = new ContratoEntity();
        updates.setCodigo("NEW-CODE");
        updates.setFechaInicio(LocalDate.now().minusDays(4));
        updates.setFechaFin(LocalDate.now().plusDays(6));
        updates.setMontoTotal(120.0);
        EstanciaEntity estNew = new EstanciaEntity();
        estNew.setId(99L);
        updates.setEstancia(estNew);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> contratoService.actualizar(12L, updates));
        assertThat(ex.getMessage().toLowerCase()).contains("no está permitido cambiar la estancia del contrato");
        verify(contratoRepo, never()).save(any());
    }

    @Test
    void eliminar_contratoVigente_debeLanzar() {
        ContratoEntity found = new ContratoEntity();
        found.setId(1L);
        found.setFechaInicio(LocalDate.now().minusDays(10));
        found.setFechaFin(LocalDate.now().plusDays(1));
        found.setEstancia(estanciaPersistida);

        when(contratoRepo.findById(1L)).thenReturn(Optional.of(found));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> contratoService.eliminar(1L));
        assertThat(ex.getMessage().toLowerCase()).contains("no se puede eliminar un contrato vigente");
        verify(contratoRepo, never()).delete(any());
    }

    @Test
    void eliminar_contratoVencido_debeEliminarYDesasociarEstancia() {
        ContratoEntity found = new ContratoEntity();
        found.setId(2L);
        found.setFechaInicio(LocalDate.now().minusMonths(2));
        found.setFechaFin(LocalDate.now().minusDays(1));
        found.setEstancia(estanciaPersistida);
        estanciaPersistida.setContrato(found);

        when(contratoRepo.findById(2L)).thenReturn(Optional.of(found));

        contratoService.eliminar(2L);

        verify(contratoRepo).delete(found);
        verify(estanciaRepo).save(estanciaPersistida);
        assertThat(estanciaPersistida.getContrato()).isNull();
    }
}