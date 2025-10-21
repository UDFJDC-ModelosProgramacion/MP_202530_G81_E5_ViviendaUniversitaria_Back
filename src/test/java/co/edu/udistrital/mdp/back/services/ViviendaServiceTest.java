/*
 * package co.edu.udistrital.mdp.back.services;
 * 
 * import co.edu.udistrital.mdp.back.entities.PropietarioEntity;
 * import co.edu.udistrital.mdp.back.entities.ViviendaEntity;
 * import co.edu.udistrital.mdp.back.repositories.PropietarioRepository;
 * import co.edu.udistrital.mdp.back.repositories.ViviendaRepository;
 * import org.junit.jupiter.api.BeforeEach;
 * import org.junit.jupiter.api.Test;
 * import org.junit.jupiter.api.extension.ExtendWith;
 * import org.mockito.InjectMocks;
 * import org.mockito.Mock;
 * import org.mockito.junit.jupiter.MockitoExtension;
 * import java.math.BigDecimal;
 * import java.util.Arrays;
 * import java.util.List;
 * import java.util.Optional;
 * import static org.assertj.core.api.Assertions.assertThat;
 * import static org.junit.jupiter.api.Assertions.assertThrows;
 * import static org.mockito.ArgumentMatchers.any;
 * import static org.mockito.ArgumentMatchers.anyLong;
 * import static org.mockito.Mockito.*;
 * 
 * @ExtendWith(MockitoExtension.class)
 * class ViviendaServiceTest {
 * 
 * @Mock
 * private ViviendaRepository viviendaRepository;
 * 
 * @Mock
 * private PropietarioRepository propietarioRepository;
 * 
 * @InjectMocks
 * private ViviendaService viviendaService;
 * 
 * private PropietarioEntity propietarioMock;
 * 
 * @BeforeEach
 * void setup() {
 * propietarioMock = mock(PropietarioEntity.class);
 * when(propietarioMock.getId()).thenReturn(1L);
 * // By default save returns the entity passed
 * when(viviendaRepository.save(any(ViviendaEntity.class))).thenAnswer(
 * invocation -> invocation.getArgument(0));
 * }
 * 
 * private ViviendaEntity buildValidVivienda() {
 * ViviendaEntity v = new ViviendaEntity();
 * v.setPropietario(propietarioMock);
 * v.setDireccion("Calle 1 #2-3");
 * v.setCiudad("Bogota");
 * v.setBarrio("Chapinero");
 * v.setPrecioMensual(new BigDecimal("1200000"));
 * v.setAreaMetrosCuadrados(45.0);
 * v.setNumeroHabitaciones(2);
 * v.setNumeroBaños(1);
 * v.setTipo(ViviendaEntity.TipoVivienda.APARTAMENTO);
 * v.setDescripcion("Descripción");
 * return v;
 * }
 * 
 * @Test
 * void crearVivienda_success_setsDisponibleAndSaves() {
 * ViviendaEntity v = buildValidVivienda();
 * when(propietarioRepository.existsById(1L)).thenReturn(true);
 * 
 * ViviendaEntity saved = viviendaService.crearVivienda(v);
 * 
 * assertThat(saved.getDisponible()).isTrue();
 * verify(propietarioRepository).existsById(1L);
 * verify(viviendaRepository).save(v);
 * }
 * 
 * @Test
 * void crearVivienda_missingPropietario_throws() {
 * ViviendaEntity v = buildValidVivienda();
 * v.setPropietario(null);
 * 
 * assertThrows(IllegalArgumentException.class, () ->
 * viviendaService.crearVivienda(v));
 * verify(propietarioRepository, never()).existsById(anyLong());
 * verify(viviendaRepository, never()).save(any());
 * }
 * 
 * @Test
 * void crearVivienda_propietarioNotExists_throws() {
 * ViviendaEntity v = buildValidVivienda();
 * when(propietarioRepository.existsById(1L)).thenReturn(false);
 * 
 * assertThrows(IllegalArgumentException.class, () ->
 * viviendaService.crearVivienda(v));
 * verify(propietarioRepository).existsById(1L);
 * verify(viviendaRepository, never()).save(any());
 * }
 * 
 * @Test
 * void crearVivienda_precioMensualNull_throws() {
 * ViviendaEntity v = buildValidVivienda();
 * v.setPrecioMensual(null);
 * when(propietarioRepository.existsById(1L)).thenReturn(true);
 * 
 * assertThrows(IllegalArgumentException.class, () ->
 * viviendaService.crearVivienda(v));
 * verify(viviendaRepository, never()).save(any());
 * }
 * 
 * @Test
 * void crearVivienda_areaZero_throws() {
 * ViviendaEntity v = buildValidVivienda();
 * v.setAreaMetrosCuadrados(0.0);
 * when(propietarioRepository.existsById(1L)).thenReturn(true);
 * 
 * assertThrows(IllegalArgumentException.class, () ->
 * viviendaService.crearVivienda(v));
 * verify(viviendaRepository, never()).save(any());
 * }
 * 
 * @Test
 * void crearVivienda_numeroHabitacionesInvalid_throws() {
 * ViviendaEntity v = buildValidVivienda();
 * v.setNumeroHabitaciones(0);
 * when(propietarioRepository.existsById(1L)).thenReturn(true);
 * 
 * assertThrows(IllegalArgumentException.class, () ->
 * viviendaService.crearVivienda(v));
 * verify(viviendaRepository, never()).save(any());
 * }
 * 
 * @Test
 * void crearVivienda_numeroBanosInvalid_throws() {
 * ViviendaEntity v = buildValidVivienda();
 * v.setNumeroBaños(0);
 * when(propietarioRepository.existsById(1L)).thenReturn(true);
 * 
 * assertThrows(IllegalArgumentException.class, () ->
 * viviendaService.crearVivienda(v));
 * verify(viviendaRepository, never()).save(any());
 * }
 * 
 * @Test
 * void crearVivienda_tipoNull_throws() {
 * ViviendaEntity v = buildValidVivienda();
 * v.setTipo(null);
 * when(propietarioRepository.existsById(1L)).thenReturn(true);
 * 
 * assertThrows(IllegalArgumentException.class, () ->
 * viviendaService.crearVivienda(v));
 * verify(viviendaRepository, never()).save(any());
 * }
 * 
 * @Test
 * void obtenerViviendaPorId_notFound_throws() {
 * when(viviendaRepository.findById(5L)).thenReturn(Optional.empty());
 * assertThrows(IllegalArgumentException.class, () ->
 * viviendaService.obtenerViviendaPorId(5L));
 * }
 * 
 * @Test
 * void marcarComoNoDisponible_and_MarcarComoDisponible_toggleDisponible() {
 * ViviendaEntity v = buildValidVivienda();
 * v.setDisponible(true);
 * when(viviendaRepository.findById(2L)).thenReturn(Optional.of(v));
 * 
 * ViviendaEntity noDisp = viviendaService.marcarComoNoDisponible(2L);
 * assertThat(noDisp.getDisponible()).isFalse();
 * verify(viviendaRepository).save(noDisp);
 * 
 * when(viviendaRepository.findById(2L)).thenReturn(Optional.of(noDisp));
 * ViviendaEntity disp = viviendaService.marcarComoDisponible(2L);
 * assertThat(disp.getDisponible()).isTrue();
 * verify(viviendaRepository, times(2)).save(any(ViviendaEntity.class));
 * }
 * 
 * @Test
 * void eliminarVivienda_available_true_deletes() {
 * ViviendaEntity v = buildValidVivienda();
 * v.setDisponible(true);
 * when(viviendaRepository.findById(3L)).thenReturn(Optional.of(v));
 * 
 * viviendaService.eliminarVivienda(3L);
 * 
 * verify(viviendaRepository).deleteById(3L);
 * }
 * 
 * @Test
 * void eliminarVivienda_notAvailable_throws() {
 * ViviendaEntity v = buildValidVivienda();
 * v.setDisponible(false);
 * when(viviendaRepository.findById(4L)).thenReturn(Optional.of(v));
 * 
 * assertThrows(IllegalStateException.class, () ->
 * viviendaService.eliminarVivienda(4L));
 * verify(viviendaRepository, never()).deleteById(anyLong());
 * }
 * 
 * @Test
 * void obtenerViviendasDisponiblesPorCiudad_delegatesToRepository() {
 * ViviendaEntity v1 = buildValidVivienda();
 * ViviendaEntity v2 = buildValidVivienda();
 * List<ViviendaEntity> list = Arrays.asList(v1, v2);
 * when(viviendaRepository.findByCiudadAndDisponible("Bogota",
 * true)).thenReturn(list);
 * 
 * List<ViviendaEntity> result =
 * viviendaService.obtenerViviendasDisponiblesPorCiudad("Bogota");
 * assertThat(result).isSameAs(list);
 * verify(viviendaRepository).findByCiudadAndDisponible("Bogota", true);
 * }
 * 
 * @Test
 * void actualizarVivienda_updatesFields_and_keepsDisponibleManagedSeparately()
 * {
 * ViviendaEntity existente = buildValidVivienda();
 * existente.setDireccion("Old");
 * existente.setCiudad("OldCity");
 * existente.setBarrio("OldBarrio");
 * existente.setDisponible(true);
 * when(viviendaRepository.findById(10L)).thenReturn(Optional.of(existente));
 * 
 * ViviendaEntity actualizado = buildValidVivienda();
 * actualizado.setDireccion("New");
 * actualizado.setCiudad("NewCity");
 * actualizado.setBarrio("NewBarrio");
 * actualizado.setPrecioMensual(new BigDecimal("2000000"));
 * actualizado.setNumeroHabitaciones(3);
 * actualizado.setNumeroBaños(2);
 * actualizado.setAreaMetrosCuadrados(60.0);
 * // Provide propietario to trigger propietario existence check
 * PropietarioEntity newProp = mock(PropietarioEntity.class);
 * when(newProp.getId()).thenReturn(99L);
 * actualizado.setPropietario(newProp);
 * when(propietarioRepository.existsById(99L)).thenReturn(true);
 * 
 * ViviendaEntity saved = viviendaService.actualizarVivienda(10L, actualizado);
 * 
 * assertThat(saved.getDireccion()).isEqualTo("New");
 * assertThat(saved.getCiudad()).isEqualTo("NewCity");
 * assertThat(saved.getBarrio()).isEqualTo("NewBarrio");
 * assertThat(saved.getPrecioMensual()).isEqualByComparingTo(new
 * BigDecimal("2000000"));
 * assertThat(saved.getNumeroHabitaciones()).isEqualTo(3);
 * assertThat(saved.getNumeroBaños()).isEqualTo(2);
 * assertThat(saved.getAreaMetrosCuadrados()).isEqualTo(60);
 * // disponible should remain as it was on existente (true)
 * assertThat(saved.getDisponible()).isTrue();
 * verify(propietarioRepository).existsById(99L);
 * verify(viviendaRepository).save(existente);
 * }
 * }
 */