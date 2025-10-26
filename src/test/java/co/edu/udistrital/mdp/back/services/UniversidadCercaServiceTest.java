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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) 
class UniversidadCercaServiceTest {

    @Mock
    private UniversidadCercaRepository universidadRepo;

    @Mock
    private ViviendaRepository viviendaRepo;

    @InjectMocks
    private UniversidadCercaService universidadService;

    private UniversidadCercaEntity uniPersistida;
    //@SuppressWarnings("unused") // Puedes quitarla
    //private List<ViviendaEntity> viviendas = new ArrayList<>(); // No parece usarse

    @BeforeEach
    void setup() {
        uniPersistida = new UniversidadCercaEntity();
        uniPersistida.setId(3L);
        uniPersistida.setNombre("U Test");
        uniPersistida.setCiudad("CiudadX");

        // Simula guardado
        when(universidadRepo.save(any(UniversidadCercaEntity.class))).thenAnswer(inv -> {
            UniversidadCercaEntity u = inv.getArgument(0);
            if (u.getId() == null) u.setId(7L); // Asigna ID si es nueva
            return u;
        });

        // Mocks por defecto para existencia
        when(universidadRepo.existsByNombreIgnoreCase(anyString())).thenReturn(false);
        when(viviendaRepo.countByUniversidadCercaId(anyLong())).thenReturn(0L);
        when(viviendaRepo.countEstanciasByUniversidadCercaId(anyLong())).thenReturn(0L);
        when(universidadRepo.findById(anyLong())).thenReturn(Optional.empty()); // Por defecto no encuentra
        when(universidadRepo.findById(3L)).thenReturn(Optional.of(uniPersistida)); // Sí encuentra la del setup

    }

    @Test
    void crearUniversidad_success() {
        UniversidadCercaEntity in = new UniversidadCercaEntity();
        in.setNombre("NuevaUni");
        in.setCiudad("Bogota");

        // El mock existsByNombreIgnoreCase ya está en false por defecto

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
    void actualizarNombre_duplicado_debeLanzar() {
        // Usa uniPersistida (ID 3L, Nombre "U Test") del setup
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
    void eliminar_conViviendasAsociadas_debeLanzar() {
        // Usa uniPersistida (ID 3L) del setup
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
        // Usa uniPersistida (ID 3L) del setup
        // countByUniversidadCercaId ya devuelve 0L por defecto
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
        // Usa uniPersistida (ID 3L) del setup
        // countByUniversidadCercaId y countEstanciasByUniversidadCercaId ya devuelven 0L por defecto

        universidadService.eliminar(3L);

        verify(universidadRepo).findById(3L);
        verify(viviendaRepo).countByUniversidadCercaId(3L);
        verify(viviendaRepo).countEstanciasByUniversidadCercaId(3L);
        verify(universidadRepo).delete(uniPersistida); // Verifica que se llamó a delete con el objeto
    }
}