package co.edu.udistrital.mdp.back.services;

import co.edu.udistrital.mdp.back.entities.UniversidadCercaEntity;
import co.edu.udistrital.mdp.back.repositories.UniversidadCercaRepository;
import co.edu.udistrital.mdp.back.repositories.ViviendaRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
class UniversidadCercaServiceTest {

    @Mock
    private UniversidadCercaRepository universidadRepo;

    @Mock
    private ViviendaRepository viviendaRepo;

    @InjectMocks
    private UniversidadCercaService universidadService;

    private UniversidadCercaEntity uniPersistida;

    @BeforeEach
    void setup() {
        uniPersistida = new UniversidadCercaEntity();
        uniPersistida.setId(3L);
        uniPersistida.setNombre("U Test");
        uniPersistida.setCiudad("CiudadX");

        // Simula guardado común
        when(universidadRepo.save(any(UniversidadCercaEntity.class))).thenAnswer(inv -> {
            UniversidadCercaEntity u = inv.getArgument(0);
            if (u.getId() == null) u.setId(7L); // Asigna ID si es nueva
            return u;
        });

        // Mocks por defecto para existencia
        when(universidadRepo.existsByNombreIgnoreCase(anyString())).thenReturn(false);
        when(viviendaRepo.countByUniversidadCercaId(anyLong())).thenReturn(0L);
        when(viviendaRepo.countEstanciasByUniversidadCercaId(anyLong())).thenReturn(0L);
        when(universidadRepo.findById(anyLong())).thenReturn(Optional.empty());
        when(universidadRepo.findById(3L)).thenReturn(Optional.of(uniPersistida));
    }

    @Test
    void crearUniversidad_success() {
        UniversidadCercaEntity in = new UniversidadCercaEntity();
        in.setNombre("NuevaUni");
        in.setCiudad("Bogota");

        UniversidadCercaEntity creado = universidadService.crearUniversidad(in);

        assertThat(creado).isNotNull();
        assertThat(creado.getId()).isEqualTo(7L); // Verifica ID simulado
        assertThat(creado.getNombre()).isEqualTo("NuevaUni");
        verify(universidadRepo).existsByNombreIgnoreCase("NuevaUni");
        verify(universidadRepo).save(in);
    }

    @Test
    void crearUniversidad_nombreEnBlanco_throws() {
        UniversidadCercaEntity in = new UniversidadCercaEntity();
        in.setNombre("   "); // Nombre en blanco

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> universidadService.crearUniversidad(in));
        assertThat(ex.getMessage()).contains("Nombre obligatorio");
        verify(universidadRepo, never()).save(any());
    }

    @Test
    void crearUniversidad_nullInput_throws() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> universidadService.crearUniversidad(null));
        assertThat(ex.getMessage()).contains("Entidad UniversidadCerca es obligatoria");
    }

    @Test
    void crearUniversidad_nombreMuyLargo_debeLanzar() {
        UniversidadCercaEntity in = new UniversidadCercaEntity();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 151; i++) sb.append("a");
        in.setNombre(sb.toString());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> universidadService.crearUniversidad(in));
        assertThat(ex.getMessage()).contains("Nombre excede 150 caracteres");
        verify(universidadRepo, never()).save(any());
    }

    @Test
    void obtenerPorId_encontrado() {
        UniversidadCercaEntity res = universidadService.obtenerPorId(3L);
        assertThat(res).isSameAs(uniPersistida);
        verify(universidadRepo).findById(3L);
    }

    @Test
    void obtenerPorId_noEncontrado_debeLanzar() {
        when(universidadRepo.findById(999L)).thenReturn(Optional.empty());
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> universidadService.obtenerPorId(999L));
        assertThat(ex.getMessage()).contains("UniversidadCerca no encontrada con ID: 999");
    }

    @Test
    void listarTodas_delegaEnRepo() {
        when(universidadRepo.findAll()).thenReturn(new ArrayList<>());
        List<UniversidadCercaEntity> list = universidadService.listarTodas();
        assertThat(list).isNotNull();
        verify(universidadRepo).findAll();
    }

    @Test
    void actualizarNombre_duplicado_debeLanzar() {
        UniversidadCercaEntity updates = new UniversidadCercaEntity();
        updates.setNombre("UniB"); // Intenta cambiar a un nombre que ya existe

        when(universidadRepo.existsByNombreIgnoreCase("UniB")).thenReturn(true); // Simula que "UniB" ya existe

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> universidadService.actualizar(3L, updates)); // Actualiza la ID 3L
        assertThat(ex.getMessage()).contains("No se puede cambiar el nombre porque ya existe");

        verify(universidadRepo).findById(3L); // Verifica que buscó la original
        verify(universidadRepo).existsByNombreIgnoreCase("UniB"); // Verifica la existencia del nuevo nombre
        verify(universidadRepo, never()).save(any()); // No debe guardar
    }

    @Test
    void actualizar_happyPath_actualizaCiudadYNombre() {
        UniversidadCercaEntity updates = new UniversidadCercaEntity();
        updates.setNombre("NuevoNombre");
        updates.setCiudad("NuevaCiudad");

        when(universidadRepo.existsByNombreIgnoreCase("NuevoNombre")).thenReturn(false);
        when(universidadRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        UniversidadCercaEntity res = universidadService.actualizar(3L, updates);

        assertThat(res.getNombre()).isEqualTo("NuevoNombre");
        assertThat(res.getCiudad()).isEqualTo("NuevaCiudad");
        verify(universidadRepo).save(res);
    }

    @Test
    void actualizar_nombreMuyLargo_debeLanzar() {
        UniversidadCercaEntity updates = new UniversidadCercaEntity();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 151; i++) sb.append("x");
        updates.setNombre(sb.toString());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> universidadService.actualizar(3L, updates));
        assertThat(ex.getMessage()).contains("Nombre excede 150 caracteres");
        verify(universidadRepo, never()).save(any());
    }

    @Test
    void eliminar_conViviendasAsociadas_debeLanzar() {
        when(viviendaRepo.countByUniversidadCercaId(3L)).thenReturn(2L); // Simula que tiene 2 viviendas

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> universidadService.eliminar(3L));
        assertThat(ex.getMessage()).contains("No se puede eliminar: tiene viviendas asociadas (2)");

        verify(universidadRepo).findById(3L); // Verifica que buscó la universidad
        verify(viviendaRepo).countByUniversidadCercaId(3L); // Verifica conteo de viviendas
        verify(viviendaRepo, never()).countEstanciasByUniversidadCercaId(anyLong()); // No debe llegar a contar estancias
        verify(universidadRepo, never()).delete(any()); // No debe borrar
    }

    @Test
    void eliminar_conEstanciasIndirectas_debeLanzar() {
        when(viviendaRepo.countEstanciasByUniversidadCercaId(3L)).thenReturn(3L); // Simula 3 estancias indirectas

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> universidadService.eliminar(3L));
        assertThat(ex.getMessage()).contains("existen estancias indirectas a través de sus viviendas (3)");

        verify(universidadRepo).findById(3L);
        verify(viviendaRepo).countByUniversidadCercaId(3L);
        verify(viviendaRepo).countEstanciasByUniversidadCercaId(3L); // Verifica conteo de estancias
        verify(universidadRepo, never()).delete(any());
    }

    @Test
    void eliminar_sinViviendas_debeEliminar() {
        universidadService.eliminar(3L);

        verify(universidadRepo).findById(3L);
        verify(viviendaRepo).countByUniversidadCercaId(3L);
        verify(viviendaRepo).countEstanciasByUniversidadCercaId(3L);
        verify(universidadRepo).delete(uniPersistida); // Verifica que se llamó a delete con el objeto
    }
}
