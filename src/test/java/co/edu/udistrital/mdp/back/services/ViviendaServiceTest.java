package co.edu.udistrital.mdp.back.services;

import co.edu.udistrital.mdp.back.entities.PropietarioEntity;
import co.edu.udistrital.mdp.back.entities.ViviendaEntity;
import co.edu.udistrital.mdp.back.repositories.PropietarioRepository;
import co.edu.udistrital.mdp.back.repositories.ViviendaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ViviendaServiceTest {

    @Mock
    private ViviendaRepository viviendaRepository;

    @Mock
    private PropietarioRepository propietarioRepository;

    @InjectMocks
    private ViviendaService viviendaService;

    private PropietarioEntity propietarioMock;
    private ViviendaEntity viviendaValida;
    private Long viviendaValidaId = 1L;

    @BeforeEach
    void setup() {
        propietarioMock = mock(PropietarioEntity.class);
        when(propietarioMock.getId()).thenReturn(1L);

        when(viviendaRepository.save(any(ViviendaEntity.class))).thenAnswer(
                invocation -> invocation.getArgument(0));

        when(propietarioRepository.existsById(1L)).thenReturn(true);

        viviendaValida = buildValidVivienda();
        viviendaValida.setId(viviendaValidaId); // Asigna ID
        when(viviendaRepository.findById(viviendaValidaId)).thenReturn(Optional.of(viviendaValida));
    }

    private ViviendaEntity buildValidVivienda() {
        ViviendaEntity v = new ViviendaEntity();
        v.setPropietario(propietarioMock);
        v.setDireccion("Calle 1 #2-3");
        v.setCiudad("Bogota");
        v.setBarrio("Chapinero");
        v.setPrecioMensual(new BigDecimal("1200000"));
        v.setAreaMetrosCuadrados(45.0);
        v.setNumeroHabitaciones(2);
        v.setNumeroBanos(1);
        v.setTipo(ViviendaEntity.TipoVivienda.APARTAMENTO);
        v.setDescripcion("Descripción");
        v.setDisponible(true);
        return v;
    }

    @Test
    void crearVivienda_success_setsDisponibleAndSaves() {
        ViviendaEntity v = buildValidVivienda();
        v.setId(null);

        ViviendaEntity saved = viviendaService.crearVivienda(v);

        assertThat(saved.isDisponible()).isTrue();
        verify(propietarioRepository).existsById(1L);
        verify(viviendaRepository).save(v);
    }

    @Test
    void crearVivienda_missingPropietario_throws() {
        ViviendaEntity v = buildValidVivienda();
        v.setPropietario(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> viviendaService.crearVivienda(v));
        assertThat(ex.getMessage()).contains("El campo 'propietario' no puede estar vacío");
        verify(viviendaRepository, never()).save(any());
    }

    @Test
    void crearVivienda_propietarioNotExists_throws() {
        ViviendaEntity v = buildValidVivienda();
        when(propietarioRepository.existsById(1L)).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> viviendaService.crearVivienda(v));
        assertThat(ex.getMessage()).contains("El propietario con ID 1 no existe");
        verify(viviendaRepository, never()).save(any());
    }

    @Test
    void crearVivienda_direccionVacia_throws() {
        ViviendaEntity v = buildValidVivienda();
        v.setDireccion("   ");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> viviendaService.crearVivienda(v));
        assertThat(ex.getMessage()).contains("El campo 'dirección' no puede estar vacío");
        verify(viviendaRepository, never()).save(any());
    }

    @Test
    void crearVivienda_ciudadVacia_throws() {
        ViviendaEntity v = buildValidVivienda();
        v.setCiudad("");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> viviendaService.crearVivienda(v));
        assertThat(ex.getMessage()).contains("El campo 'ciudad' no puede estar vacío");
        verify(viviendaRepository, never()).save(any());
    }

    @Test
    void crearVivienda_barrioNull_throws() {
        ViviendaEntity v = buildValidVivienda();
        v.setBarrio(null);
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> viviendaService.crearVivienda(v));
        assertThat(ex.getMessage()).contains("El campo 'barrio' no puede estar vacío");
        verify(viviendaRepository, never()).save(any());
    }
    // --- Fin nuevas pruebas ---

    @Test
    void crearVivienda_precioMensualNull_throws() {
        ViviendaEntity v = buildValidVivienda();
        v.setPrecioMensual(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> viviendaService.crearVivienda(v));
        assertThat(ex.getMessage()).contains("El campo 'precioMensual' debe ser un valor numérico mayor a cero");
        verify(viviendaRepository, never()).save(any());
    }

    @Test
    void crearVivienda_precioMensualZero_throws() {
        ViviendaEntity v = buildValidVivienda();
        v.setPrecioMensual(BigDecimal.ZERO);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> viviendaService.crearVivienda(v));
        assertThat(ex.getMessage()).contains("El campo 'precioMensual' debe ser un valor numérico mayor a cero");
        verify(viviendaRepository, never()).save(any());
    }

    @Test
    void crearVivienda_areaZero_throws() {
        ViviendaEntity v = buildValidVivienda();
        v.setAreaMetrosCuadrados(0.0);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> viviendaService.crearVivienda(v));
        assertThat(ex.getMessage()).contains("El campo 'areaMetrosCuadrados' debe ser un valor numérico mayor a cero");
        verify(viviendaRepository, never()).save(any());
    }

    @Test
    void crearVivienda_areaNull_noThrows() {
        ViviendaEntity v = buildValidVivienda();
        v.setAreaMetrosCuadrados(null);
        assertDoesNotThrow(() -> viviendaService.crearVivienda(v));
        verify(viviendaRepository).save(v);
    }

    @Test
    void crearVivienda_numeroHabitacionesInvalid_throws() {
        ViviendaEntity v = buildValidVivienda();
        v.setNumeroHabitaciones(0);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> viviendaService.crearVivienda(v));
        assertThat(ex.getMessage()).contains("El campo 'numeroHabitaciones' debe tener un valor mínimo de 1");
        verify(viviendaRepository, never()).save(any());
    }

    @Test
    void crearVivienda_numeroBanosInvalid_throws() {
        ViviendaEntity v = buildValidVivienda();
        v.setNumeroBanos(0);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> viviendaService.crearVivienda(v));
        assertThat(ex.getMessage()).contains("El campo 'numeroBaños' debe tener un valor mínimo de 1");
        verify(viviendaRepository, never()).save(any());
    }

    @Test
    void crearVivienda_tipoNull_throws() {
        ViviendaEntity v = buildValidVivienda();
        v.setTipo(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> viviendaService.crearVivienda(v));
        assertThat(ex.getMessage()).contains("El campo 'tipo' debe especificar claramente el tipo de vivienda");
        verify(viviendaRepository, never()).save(any());
    }

    @Test
    void obtenerViviendaPorId_success() {
        ViviendaEntity found = viviendaService.obtenerViviendaPorId(viviendaValidaId);
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(viviendaValidaId);
        verify(viviendaRepository).findById(viviendaValidaId);
    }

    @Test
    void obtenerViviendaPorId_notFound_throws() {
        when(viviendaRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> viviendaService.obtenerViviendaPorId(99L));
        assertThat(ex.getMessage()).contains("Vivienda no encontrada con ID: 99");
    }

    @Test
    void obtenerTodasLasViviendas_success() {
        ViviendaEntity v2 = buildValidVivienda();
        v2.setId(2L);
        List<ViviendaEntity> expectedList = Arrays.asList(viviendaValida, v2);
        when(viviendaRepository.findAll()).thenReturn(expectedList);

        List<ViviendaEntity> result = viviendaService.obtenerTodasLasViviendas();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(viviendaValida, v2);
        verify(viviendaRepository).findAll();
    }

    @Test
    void obtenerViviendasDisponiblesPorCiudad_notFound() {
        when(viviendaRepository.findByCiudadAndDisponible("CiudadInexistente", true))
                .thenReturn(Collections.emptyList());

        List<ViviendaEntity> result = viviendaService.obtenerViviendasDisponiblesPorCiudad("CiudadInexistente");

        assertThat(result)
                .isNotNull()
                .isEmpty();
        verify(viviendaRepository).findByCiudadAndDisponible("CiudadInexistente", true);
    }

    @Test
    void marcarComoNoDisponible_notFound_throws() {
        when(viviendaRepository.findById(99L)).thenReturn(Optional.empty());
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> viviendaService.marcarComoNoDisponible(99L));
        assertThat(ex.getMessage()).contains("Vivienda no encontrada con ID: 99");
        verify(viviendaRepository, never()).save(any());
    }

    @Test
    void marcarComoDisponible_notFound_throws() {
        when(viviendaRepository.findById(99L)).thenReturn(Optional.empty());
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> viviendaService.marcarComoDisponible(99L));
        assertThat(ex.getMessage()).contains("Vivienda no encontrada con ID: 99");
        verify(viviendaRepository, never()).save(any());
    }

    @Test
    void eliminarVivienda_available_true_deletes() {
        ViviendaEntity v = buildValidVivienda();
        v.setDisponible(true);

        viviendaService.eliminarVivienda(viviendaValidaId);

        verify(viviendaRepository).findById(viviendaValidaId);
        verify(viviendaRepository).deleteById(viviendaValidaId);
        verify(viviendaRepository, never()).delete(any(ViviendaEntity.class));
    }

    @Test
    void eliminarVivienda_notAvailable_throws() {

        ViviendaEntity v = buildValidVivienda();
        v.setId(4L);
        v.setDisponible(false);
        when(viviendaRepository.findById(4L)).thenReturn(Optional.of(v));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> viviendaService.eliminarVivienda(4L));
        assertThat(ex.getMessage())
                .contains("No se puede eliminar la vivienda con ID 4 porque está actualmente arrendada");

        verify(viviendaRepository).findById(4L);
        verify(viviendaRepository, never()).deleteById(anyLong());
    }

    @Test
    void eliminarVivienda_notFound_throws() {
        when(viviendaRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> viviendaService.eliminarVivienda(99L));
        assertThat(ex.getMessage()).contains("Vivienda no encontrada con ID: 99");
        verify(viviendaRepository, never()).deleteById(anyLong());
    }

    @Test
    void obtenerViviendasDisponiblesPorCiudad_delegatesToRepository() {
        ViviendaEntity v1 = buildValidVivienda();
        ViviendaEntity v2 = buildValidVivienda();
        v2.setCiudad("OtraCiudad");
        List<ViviendaEntity> list = Arrays.asList(v1);
        when(viviendaRepository.findByCiudadAndDisponible("Bogota", true)).thenReturn(list);

        List<ViviendaEntity> result = viviendaService.obtenerViviendasDisponiblesPorCiudad("Bogota");

        assertThat(result).isSameAs(list);
        verify(viviendaRepository).findByCiudadAndDisponible("Bogota", true);
    }

    @Test
    void actualizarVivienda_updatesFields_and_keepsDisponibleManagedSeparately() {
        ViviendaEntity existente = buildValidVivienda(); // Usa la del setup (ID 1L)
        existente.setDireccion("Old");
        existente.setCiudad("OldCity");
        existente.setBarrio("OldBarrio");
        existente.setDisponible(true);

        ViviendaEntity actualizado = new ViviendaEntity();
        actualizado.setDireccion("New");
        actualizado.setCiudad("NewCity");
        actualizado.setBarrio("NewBarrio");
        actualizado.setPrecioMensual(new BigDecimal("2000000"));
        actualizado.setNumeroHabitaciones(3);
        actualizado.setNumeroBanos(2);
        actualizado.setAreaMetrosCuadrados(60.0);
        actualizado.setTipo(ViviendaEntity.TipoVivienda.CASA);
        actualizado.setDescripcion("Nueva Descripcion");

        PropietarioEntity newProp = mock(PropietarioEntity.class);
        when(newProp.getId()).thenReturn(99L);
        actualizado.setPropietario(newProp);
        when(propietarioRepository.existsById(99L)).thenReturn(true);

        ViviendaEntity saved = viviendaService.actualizarVivienda(viviendaValidaId, actualizado);

        assertThat(saved.getPropietario().getId()).isEqualTo(newProp.getId());
        assertThat(saved.getDireccion()).isEqualTo("New");
        assertThat(saved.getCiudad()).isEqualTo("NewCity");
        assertThat(saved.getBarrio()).isEqualTo("NewBarrio");
        assertThat(saved.getPrecioMensual()).isEqualByComparingTo(new BigDecimal("2000000"));
        assertThat(saved.getNumeroHabitaciones()).isEqualTo(3);
        assertThat(saved.getNumeroBanos()).isEqualTo(2);
        assertThat(saved.getAreaMetrosCuadrados()).isEqualTo(60.0);
        assertThat(saved.getTipo()).isEqualTo(ViviendaEntity.TipoVivienda.CASA);
        assertThat(saved.getDescripcion()).isEqualTo("Nueva Descripcion");
        assertThat(saved.getPropietario().getId()).isEqualTo(newProp.getId());

        assertThat(saved.isDisponible()).isTrue();

        verify(viviendaRepository).findById(viviendaValidaId);
        verify(propietarioRepository).existsById(99L);
        verify(viviendaRepository).save(any(ViviendaEntity.class));
    }

    @Test
    void marcarComoNoDisponible_y_luego_MarcarComoDisponible() {

        Long viviendaId = 2L;
        ViviendaEntity viviendaOriginal = buildValidVivienda();
        viviendaOriginal.setId(viviendaId);
        viviendaOriginal.setDisponible(true);

        when(viviendaRepository.findById(viviendaId)).thenReturn(Optional.of(viviendaOriginal));

        ViviendaEntity viviendaNoDisponible = viviendaService.marcarComoNoDisponible(viviendaId);

        assertThat(viviendaNoDisponible.isDisponible()).isFalse();

        ArgumentCaptor<ViviendaEntity> viviendaCaptor1 = ArgumentCaptor.forClass(ViviendaEntity.class);
        verify(viviendaRepository, times(1)).save(viviendaCaptor1.capture());
        assertThat(viviendaCaptor1.getValue().getId()).isEqualTo(viviendaId);
        assertThat(viviendaCaptor1.getValue().isDisponible()).isFalse();

        ViviendaEntity viviendaGuardadaNoDisp = viviendaCaptor1.getValue();

        when(viviendaRepository.findById(viviendaId)).thenReturn(Optional.of(viviendaGuardadaNoDisp));

        ViviendaEntity viviendaDisponibleDeNuevo = viviendaService.marcarComoDisponible(viviendaId);

        assertThat(viviendaDisponibleDeNuevo.isDisponible()).isTrue();

        ArgumentCaptor<ViviendaEntity> viviendaCaptor2 = ArgumentCaptor.forClass(ViviendaEntity.class);
        verify(viviendaRepository, times(2)).save(viviendaCaptor2.capture());

        ViviendaEntity viviendaGuardadaDisp = viviendaCaptor2.getAllValues().get(1);
        assertThat(viviendaGuardadaDisp.getId()).isEqualTo(viviendaId);
        assertThat(viviendaGuardadaDisp.isDisponible()).isTrue();
    }

    @Test
    void actualizarVivienda_notFound_throws() {
        ViviendaEntity actualizado = buildValidVivienda();
        when(viviendaRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> viviendaService.actualizarVivienda(99L, actualizado));
        assertThat(ex.getMessage()).contains("Vivienda no encontrada con ID: 99");
        verify(viviendaRepository, never()).save(any());
    }

    @Test
    void actualizarVivienda_conDireccionVacia_throws() {
        ViviendaEntity actualizado = buildValidVivienda();
        actualizado.setDireccion(" "); // Dirección inválida

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> viviendaService.actualizarVivienda(viviendaValidaId, actualizado));
        assertThat(ex.getMessage()).contains("El campo 'dirección' no puede estar vacío");
        verify(viviendaRepository, never()).save(any());
    }

    @Test
    void actualizarVivienda_conPrecioCero_throws() {
        ViviendaEntity actualizado = buildValidVivienda();
        actualizado.setPrecioMensual(BigDecimal.ZERO); // Precio inválido

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> viviendaService.actualizarVivienda(viviendaValidaId, actualizado));
        assertThat(ex.getMessage()).contains("El campo 'precioMensual' debe ser un valor numérico mayor a cero");
        verify(viviendaRepository, never()).save(any());
    }

    @Test
    void actualizarVivienda_conPropietarioInexistente_throws() {
        ViviendaEntity actualizado = buildValidVivienda();
        PropietarioEntity newProp = mock(PropietarioEntity.class);
        when(newProp.getId()).thenReturn(99L);
        actualizado.setPropietario(newProp);
        when(propietarioRepository.existsById(99L)).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> viviendaService.actualizarVivienda(viviendaValidaId, actualizado));
        assertThat(ex.getMessage()).contains("El propietario con ID 99 no existe");
        verify(viviendaRepository, never()).save(any());
    }
}