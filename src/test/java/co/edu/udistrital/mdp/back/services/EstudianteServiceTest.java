package co.edu.udistrital.mdp.back.services;

import co.edu.udistrital.mdp.back.entities.SitioInteresEntity;
import co.edu.udistrital.mdp.back.repositories.SitioInteresRepository;
import co.edu.udistrital.mdp.back.repositories.ViviendaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SitioInteresServiceTest {

    @Mock
    private SitioInteresRepository sitioRepo;

    @Mock
    private ViviendaRepository viviendaRepo;

    @InjectMocks
    private SitioInteresService sitioService;

    private SitioInteresEntity sitioPersistido;

    @BeforeEach
    void setup() {
        sitioPersistido = new SitioInteresEntity();
        sitioPersistido.setId(1L);
        sitioPersistido.setNombre("Parque Central");
        sitioPersistido.setUbicacion("Calle 45 #20");
        sitioPersistido.setTiempoCaminando(5);

        when(sitioRepo.save(any(SitioInteresEntity.class))).thenAnswer(inv -> {
            SitioInteresEntity s = inv.getArgument(0);
            if (s.getId() == null)
                s.setId(10L);
            return s;
        });

        when(sitioRepo.findById(anyLong())).thenReturn(Optional.empty());
        when(sitioRepo.findById(1L)).thenReturn(Optional.of(sitioPersistido));

        when(sitioRepo.existsByNombreIgnoreCase(anyString())).thenReturn(false);
        when(sitioRepo.countViviendasAsociadas(anyLong())).thenReturn(0L);
    }

    @Test
    void crearSitio_success() {
        SitioInteresEntity in = new SitioInteresEntity();
        in.setNombre("Museo Arte");
        in.setUbicacion("Cra 10 #15");
        in.setTiempoCaminando(12);

        SitioInteresEntity creado = sitioService.createSitioInteres(in);

        assertThat(creado.getId()).isEqualTo(10L);
        assertThat(creado.getNombre()).isEqualTo("Museo Arte");
        verify(sitioRepo).existsByNombreIgnoreCase("Museo Arte");
        verify(sitioRepo).save(in);
    }

    @Test
    void crearSitio_nombreDuplicado_lanzaExcepcion() {
        when(sitioRepo.existsByNombreIgnoreCase("Parque Central")).thenReturn(true);

        SitioInteresEntity in = new SitioInteresEntity();
        in.setNombre("Parque Central");
        in.setUbicacion("Cra 5 #10");
        in.setTiempoCaminando(10);

        var ex = assertThrows(IllegalArgumentException.class, () -> sitioService.createSitioInteres(in));
        assertThat(ex.getMessage()).contains("Ya existe un SitioInteres");
        verify(sitioRepo, never()).save(any());
    }

    @Test
    void actualizar_nombreDuplicado_debeLanzar() {
        SitioInteresEntity updates = new SitioInteresEntity();
        updates.setNombre("Otro Nombre");
        updates.setUbicacion("Nueva Dir");
        updates.setTiempoCaminando(8);

        when(sitioRepo.existsByNombreIgnoreCase("Otro Nombre")).thenReturn(true);

        var ex = assertThrows(IllegalArgumentException.class, () -> sitioService.updateSitioInteres(1L, updates));
        assertThat(ex.getMessage()).contains("Ya existe otro SitioInteres");
        verify(sitioRepo, never()).save(any());
    }

    @Test
    void eliminar_conViviendasAsociadas_debeLanzar() {
        when(sitioRepo.countViviendasAsociadas(1L)).thenReturn(2L);

        var ex = assertThrows(IllegalStateException.class, () -> sitioService.deleteSitioInteres(1L));
        assertThat(ex.getMessage()).contains("tiene viviendas asociadas");
        verify(sitioRepo, never()).delete(any());
    }

    @Test
    void eliminar_sinViviendas_debeEliminar() {
        sitioService.deleteSitioInteres(1L);
        verify(sitioRepo).delete(sitioPersistido);
    }
}
