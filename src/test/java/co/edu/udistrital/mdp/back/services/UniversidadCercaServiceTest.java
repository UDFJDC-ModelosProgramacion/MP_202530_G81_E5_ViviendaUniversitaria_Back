package co.edu.udistrital.mdp.back;

import co.edu.udistrital.mdp.back.entities.UniversidadCercaEntity;
import co.edu.udistrital.mdp.back.entities.ViviendaEntity;
import co.edu.udistrital.mdp.back.repositories.UniversidadCercaRepository;
import co.edu.udistrital.mdp.back.repositories.ViviendaRepository;
import co.edu.udistrital.mdp.back.services.UniversidadCercaService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UniversidadCercaServiceTest {

    @Mock
    private UniversidadCercaRepository universidadRepo;

    @Mock
    private ViviendaRepository viviendaRepo;

    @InjectMocks
    private UniversidadCercaService universidadService;

    private UniversidadCercaEntity uniPersistida;
    @SuppressWarnings("unused")
    private List<ViviendaEntity> viviendas = new ArrayList<>();

    @BeforeEach
    void setup() {
        uniPersistida = new UniversidadCercaEntity();
        uniPersistida.setId(3L);
        uniPersistida.setNombre("U Test");
        uniPersistida.setCiudad("CiudadX");

        when(universidadRepo.save(any(UniversidadCercaEntity.class))).thenAnswer(inv -> {
            UniversidadCercaEntity u = inv.getArgument(0);
            if (u.getId() == null) u.setId(7L);
            return u;
        });
    }

    @Test
    void crearUniversidad_success() {
        UniversidadCercaEntity in = new UniversidadCercaEntity();
        in.setNombre("NuevaUni");
        in.setCiudad("Bogota");

        when(universidadRepo.existsByNombreIgnoreCase("NuevaUni")).thenReturn(false);
        when(universidadRepo.save(any())).thenReturn(in);

        UniversidadCercaEntity creado = universidadService.crearUniversidad(in);

        assertThat(creado).isNotNull();
        assertThat(creado.getNombre()).isEqualTo("NuevaUni");
        verify(universidadRepo).save(any());
    }

    @Test
    void crearUniversidad_nombreEnBlanco_throws() {
        UniversidadCercaEntity in = new UniversidadCercaEntity();
        in.setNombre("   ");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> universidadService.crearUniversidad(in));
        assertThat(ex.getMessage().toLowerCase()).contains("nombre");
        verify(universidadRepo, never()).save(any());
    }

    @Test
    void actualizarNombre_duplicado_debeLanzar() {
        UniversidadCercaEntity existing = new UniversidadCercaEntity();
        existing.setId(5L);
        existing.setNombre("UniA");
        when(universidadRepo.findById(5L)).thenReturn(Optional.of(existing));

        UniversidadCercaEntity updates = new UniversidadCercaEntity();
        updates.setNombre("UniB");
        when(universidadRepo.existsByNombreIgnoreCase("UniB")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> universidadService.actualizar(5L, updates));
        assertThat(ex.getMessage().toLowerCase()).contains("no se puede cambiar el nombre");
        verify(universidadRepo).findById(5L);
        verify(universidadRepo, never()).save(any());
    }

    @Test
    void eliminar_conViviendasAsociadas_debeLanzar() {
        when(universidadRepo.findById(3L)).thenReturn(Optional.of(uniPersistida));
        when(viviendaRepo.countByUniversidadCercaId(3L)).thenReturn(2L);
        when(viviendaRepo.countEstanciasByUniversidadCercaId(3L)).thenReturn(0L);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> universidadService.eliminar(3L));
        assertThat(ex.getMessage().toLowerCase()).contains("viviendas asociadas");
        verify(universidadRepo, never()).delete(any());
    }

    @Test
    void eliminar_conEstanciasIndirectas_debeLanzar() {
        when(universidadRepo.findById(6L)).thenReturn(Optional.of(new UniversidadCercaEntity()));
        when(viviendaRepo.countByUniversidadCercaId(6L)).thenReturn(0L);
        when(viviendaRepo.countEstanciasByUniversidadCercaId(6L)).thenReturn(3L);

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> universidadService.eliminar(6L));
        assertThat(ex.getMessage().toLowerCase()).contains("estancias indirectas");
        verify(universidadRepo, never()).delete(any());
    }

    @Test
    void eliminar_sinViviendas_debeEliminar() {
        when(universidadRepo.findById(4L)).thenReturn(Optional.of(new UniversidadCercaEntity()));
        when(viviendaRepo.countByUniversidadCercaId(4L)).thenReturn(0L);
        when(viviendaRepo.countEstanciasByUniversidadCercaId(4L)).thenReturn(0L);

        universidadService.eliminar(4L);
        verify(universidadRepo).delete(any());
    }
}
