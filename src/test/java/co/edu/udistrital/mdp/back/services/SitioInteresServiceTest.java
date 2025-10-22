/*   
package co.edu.udistrital.mdp.back.services;

import co.edu.udistrital.mdp.back.entities.SitioInteresEntity;
import co.edu.udistrital.mdp.back.entities.ViviendaEntity;
import co.edu.udistrital.mdp.back.repositories.SitioInteresRepository;
import co.edu.udistrital.mdp.back.services.SitioInteresService;
import co.edu.udistrital.mdp.back.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.back.exceptions.IllegalOperationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SitioInteresServiceTest {

    @Mock
    private SitioInteresRepository sitioRepo;

    @InjectMocks
    private SitioInteresService sitioService;

    private SitioInteresEntity sitio;

    @BeforeEach
    void setup() {
        sitio = new SitioInteresEntity();
        sitio.setId(1L);
        sitio.setNombre("Parque Central");
        sitio.setUbicacion("Calle 10");
    }

    @Test
    void createSitioInteres_success() throws IllegalOperationException {
        when(sitioRepo.findByNombreContaining("Parque Central")).thenReturn(Collections.emptyList());
        when(sitioRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        SitioInteresEntity creado = sitioService.createSitioInteres(sitio);
        assertThat(creado).isNotNull();
        verify(sitioRepo).save(any());
    }

    @Test
    void createSitioInteres_nombreDuplicado_debeLanzar() {
        when(sitioRepo.findByNombreContaining("Parque Central")).thenReturn(List.of(sitio));

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> sitioService.createSitioInteres(sitio));
        assertThat(ex.getMessage().toLowerCase()).contains("ya existe un sitio");
    }

    @Test
    void deleteSitioInteres_conViviendasAsociadas_debeLanzar() {
        sitio.setViviendas(List.of(new ViviendaEntity()));
        when(sitioRepo.findById(1L)).thenReturn(Optional.of(sitio));

        IllegalOperationException ex = assertThrows(IllegalOperationException.class,
                () -> sitioService.deleteSitioInteres(1L));
        assertThat(ex.getMessage().toLowerCase()).contains("tiene viviendas asociadas");
    }

    @Test
    void deleteSitioInteres_success() throws EntityNotFoundException, IllegalOperationException {
        sitio.setViviendas(Collections.emptyList());
        when(sitioRepo.findById(1L)).thenReturn(Optional.of(sitio));

        sitioService.deleteSitioInteres(1L);
        verify(sitioRepo).delete(sitio);
    }
}
*/