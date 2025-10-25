
package co.edu.udistrital.mdp.back.services;

import co.edu.udistrital.mdp.back.entities.SitioInteresEntity;
import co.edu.udistrital.mdp.back.entities.ViviendaEntity;
import co.edu.udistrital.mdp.back.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.back.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.back.repositories.SitioInteresRepository;
import co.edu.udistrital.mdp.back.repositories.ViviendaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SitioInteresServiceTest {

    @Mock
    private SitioInteresRepository sitioRepo;

    @Mock
    private ViviendaRepository viviendaRepo;

    @InjectMocks
    private SitioInteresService sitioService;

    private SitioInteresEntity sitio;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sitio = new SitioInteresEntity();
        sitio.setId(1L);
        sitio.setNombre("Biblioteca Central");
        sitio.setUbicacion("Carrera 45");
        sitio.setTiempoCaminando(10);
    }

    @Test
    void testCreateSitioInteres_Success() throws IllegalOperationException {
        when(sitioRepo.findByNombreContaining(anyString())).thenReturn(Collections.emptyList());
        when(sitioRepo.save(any(SitioInteresEntity.class))).thenReturn(sitio);

        SitioInteresEntity result = sitioService.createSitioInteres(sitio);

        assertNotNull(result);
        verify(sitioRepo).save(any(SitioInteresEntity.class));
    }

    @Test
    void testCreateSitioInteres_NombreDuplicado() {
        when(sitioRepo.findByNombreContaining(anyString())).thenReturn(List.of(sitio));

        assertThrows(IllegalOperationException.class, () -> sitioService.createSitioInteres(sitio));
    }

    @Test
    void testGetAllSitios() {
        when(sitioRepo.findAll()).thenReturn(List.of(sitio));

        List<SitioInteresEntity> result = sitioService.getAllSitios();

        assertEquals(1, result.size());
        verify(sitioRepo).findAll();
    }

    @Test
    void testGetSitioInteres_Success() throws EntityNotFoundException {
        when(sitioRepo.findById(1L)).thenReturn(Optional.of(sitio));

        SitioInteresEntity result = sitioService.getSitioInteres(1L);

        assertEquals("Biblioteca Central", result.getNombre());
    }

    @Test
    void testGetSitioInteres_NotFound() {
        when(sitioRepo.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> sitioService.getSitioInteres(1L));
    }

    @Test
    void testUpdateSitioInteres_Success() throws Exception {
        SitioInteresEntity update = new SitioInteresEntity();
        update.setNombre("Nuevo nombre");

        when(sitioRepo.findById(1L)).thenReturn(Optional.of(sitio));
        when(sitioRepo.save(any(SitioInteresEntity.class))).thenReturn(sitio);

        SitioInteresEntity result = sitioService.updateSitioInteres(1L, update);

        assertEquals("Nuevo nombre", result.getNombre());
        verify(sitioRepo).save(any(SitioInteresEntity.class));
    }

    @Test
    void testDeleteSitioInteres_Success() throws Exception {
        sitio.setViviendas(Collections.emptyList());
        when(sitioRepo.findById(1L)).thenReturn(Optional.of(sitio));

        sitioService.deleteSitioInteres(1L);

        verify(sitioRepo).delete(sitio);
    }

    @Test
    void testDeleteSitioInteres_WithViviendas() {
        ViviendaEntity vivienda = new ViviendaEntity();
        sitio.setViviendas(List.of(vivienda));
        when(sitioRepo.findById(1L)).thenReturn(Optional.of(sitio));

        assertThrows(IllegalOperationException.class, () -> sitioService.deleteSitioInteres(1L));
    }
}
