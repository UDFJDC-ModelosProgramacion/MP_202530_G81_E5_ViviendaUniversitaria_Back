package co.edu.udistrital.mdp.back.services;

import co.edu.udistrital.mdp.back.entities.SitioInteresEntity;
import co.edu.udistrital.mdp.back.entities.ViviendaEntity;
// Ya no necesitas importar EntityNotFoundException ni IllegalOperationException aquí si no las usas directamente en assertThrows
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

// Quita 'public' de la clase si usas JUnit 5
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
    // Se quita 'throws IllegalOperationException'
    void testCreateSitioInteres_Success() {
        when(sitioRepo.existsByNombreIgnoreCase(anyString())).thenReturn(false); // Corregido para simular que no existe
        when(sitioRepo.save(any(SitioInteresEntity.class))).thenReturn(sitio);

        // Llamada al método que ahora podría lanzar IllegalArgumentException (unchecked)
        SitioInteresEntity result = sitioService.createSitioInteres(sitio);

        assertNotNull(result);
        verify(sitioRepo).save(any(SitioInteresEntity.class));
    }

    @Test
    void testCreateSitioInteres_NombreDuplicado() {
        // Simula que el nombre SÍ existe para causar la excepción
        when(sitioRepo.existsByNombreIgnoreCase(anyString())).thenReturn(true);

        // El método assertThrows ya maneja la excepción esperada (IllegalArgumentException)
        assertThrows(IllegalArgumentException.class, () -> sitioService.createSitioInteres(sitio));
    }

    @Test
    void testGetAllSitios() {
        when(sitioRepo.findAll()).thenReturn(List.of(sitio));
        List<SitioInteresEntity> result = sitioService.getAllSitios();
        assertEquals(1, result.size());
        verify(sitioRepo).findAll();
    }

    @Test
    // Se quita 'throws EntityNotFoundException'
    void testGetSitioInteres_Success() {
        when(sitioRepo.findById(1L)).thenReturn(Optional.of(sitio));
        // La llamada puede lanzar IllegalArgumentException (unchecked) si no se encuentra
        SitioInteresEntity result = sitioService.getSitioInteres(1L);
        assertEquals("Biblioteca Central", result.getNombre());
    }

    @Test
    void testGetSitioInteres_NotFound() {
        when(sitioRepo.findById(anyLong())).thenReturn(Optional.empty());
        // El método assertThrows maneja la excepción esperada (IllegalArgumentException)
        assertThrows(IllegalArgumentException.class, () -> sitioService.getSitioInteres(1L));
    }

    @Test
    // Se quita 'throws Exception'
    void testUpdateSitioInteres_Success() {
        SitioInteresEntity update = new SitioInteresEntity();
        update.setNombre("Nuevo nombre");
        update.setUbicacion("Nueva Ubicacion"); // Añadido para pasar validación
        update.setTiempoCaminando(5); // Añadido para pasar validación

        when(sitioRepo.findById(1L)).thenReturn(Optional.of(sitio));
        when(sitioRepo.existsByNombreIgnoreCase("Nuevo nombre")).thenReturn(false); // Simula que el nuevo nombre no existe
        when(sitioRepo.save(any(SitioInteresEntity.class))).thenReturn(sitio);

        // La llamada puede lanzar IllegalArgumentException (unchecked)
        SitioInteresEntity result = sitioService.updateSitioInteres(1L, update);

        assertEquals("Nuevo nombre", result.getNombre());
        verify(sitioRepo).save(any(SitioInteresEntity.class));
    }

    @Test
    // Se quita 'throws Exception'
    void testDeleteSitioInteres_Success() {
        // Aseguramos que la lista exista y esté vacía
        sitio.setViviendas(new ArrayList<>());
        when(sitioRepo.findById(1L)).thenReturn(Optional.of(sitio));
        when(sitioRepo.countViviendasAsociadas(1L)).thenReturn(0L); // Simula 0 viviendas

        // La llamada puede lanzar IllegalArgumentException o IllegalStateException (unchecked)
        sitioService.deleteSitioInteres(1L);

        verify(sitioRepo).delete(sitio);
    }

    @Test
    void testDeleteSitioInteres_WithViviendas() {
        ViviendaEntity vivienda = new ViviendaEntity();
        // Aseguramos que la lista exista
        sitio.setViviendas(new ArrayList<>(List.of(vivienda)));
        when(sitioRepo.findById(1L)).thenReturn(Optional.of(sitio));
        when(sitioRepo.countViviendasAsociadas(1L)).thenReturn(1L); // Simula que tiene viviendas

        // El método assertThrows maneja la excepción esperada (IllegalStateException)
        assertThrows(IllegalStateException.class, () -> sitioService.deleteSitioInteres(1L));
    }
}