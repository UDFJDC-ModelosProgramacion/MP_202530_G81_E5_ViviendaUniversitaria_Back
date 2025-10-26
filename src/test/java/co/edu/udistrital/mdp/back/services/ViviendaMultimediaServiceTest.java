package co.edu.udistrital.mdp.back.services;

import static org.junit.jupiter.api.Assertions.*; // Mantenemos la importación general

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import co.edu.udistrital.mdp.back.entities.MultimediaEntity;
import co.edu.udistrital.mdp.back.entities.ViviendaEntity;
import co.edu.udistrital.mdp.back.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.back.exceptions.IllegalOperationException;
import jakarta.transaction.Transactional;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@DataJpaTest
@Transactional
@Import(ViviendaMultimediaService.class)
// Quitamos 'public' si usas JUnit 5
class ViviendaMultimediaServiceTest {

    @Autowired
    private ViviendaMultimediaService viviendaMultimediaService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();

    private ViviendaEntity vivienda;
    private List<MultimediaEntity> multimediaList = new ArrayList<>();
    private Long viviendaId;
    // Quitamos multimediaId2 y multimediaId3 porque no se usan
    private Long multimediaId1;

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("DELETE FROM MultimediaEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("DELETE FROM ViviendaEntity").executeUpdate();
    }

    private void insertData() {
        vivienda = factory.manufacturePojo(ViviendaEntity.class);
        vivienda.setId(null);
        vivienda.setMultimedia(new ArrayList<>());
        vivienda.setServicios(new ArrayList<>());
        vivienda.setComentarios(new ArrayList<>());
        vivienda.setPropietario(null);
        vivienda.setUniversidadCerca(null);
        vivienda = entityManager.persistFlushFind(vivienda);
        viviendaId = vivienda.getId();

        multimediaList.clear();
        for (int i = 0; i < 3; i++) {
            MultimediaEntity entity = factory.manufacturePojo(MultimediaEntity.class);
            entity.setId(null);
            entity.setVivienda(vivienda);
            entity = entityManager.persistFlushFind(entity);
            multimediaList.add(entity);
        }
        multimediaId1 = multimediaList.get(0).getId();

    }

    @Test

    void testAddMultimedia() throws EntityNotFoundException {
        ViviendaEntity newVivienda = factory.manufacturePojo(ViviendaEntity.class);
        newVivienda.setId(null);
        newVivienda.setMultimedia(new ArrayList<>());
        newVivienda.setServicios(new ArrayList<>());
        newVivienda.setComentarios(new ArrayList<>());
        newVivienda.setPropietario(null);
        newVivienda.setUniversidadCerca(null);
        newVivienda = entityManager.persistFlushFind(newVivienda);
        Long newViviendaId = newVivienda.getId();

        MultimediaEntity newMultimedia = factory.manufacturePojo(MultimediaEntity.class);
        newMultimedia.setId(null);
        newMultimedia.setVivienda(newVivienda); // Associate before persisting
        newMultimedia = entityManager.persistFlushFind(newMultimedia);
        Long newMultimediaId = newMultimedia.getId();

        MultimediaEntity response = viviendaMultimediaService.addMultimedia(newViviendaId, newMultimediaId);

        assertNotNull(response);
        assertEquals(newMultimediaId, response.getId());
        assertNotNull(response.getVivienda());
        assertEquals(newViviendaId, response.getVivienda().getId());

        MultimediaEntity found = entityManager.find(MultimediaEntity.class, newMultimediaId);
        assertNotNull(found.getVivienda());
        assertEquals(newViviendaId, found.getVivienda().getId());
    }

    @Test
    void testAddMultimediaInvalidVivienda() {
        Long invalidViviendaId = 0L;
        Long existingMultimediaId = multimediaId1;

        // Quitamos la asignación 'EntityNotFoundException ex ='
        assertThrows(EntityNotFoundException.class, () -> {
            viviendaMultimediaService.addMultimedia(invalidViviendaId, existingMultimediaId);
        });
    }

    @Test
    void testAddInvalidMultimedia() {
        Long existingViviendaId = viviendaId;
        Long invalidMultimediaId = 0L;

        // Quitamos la asignación 'EntityNotFoundException ex ='
        assertThrows(EntityNotFoundException.class, () -> {
            viviendaMultimediaService.addMultimedia(existingViviendaId, invalidMultimediaId);
        });
    }

    @Test
    // Quitamos 'throws EntityNotFoundException'
    void testGetMultimedia() throws EntityNotFoundException{
        // La llamada puede lanzar EntityNotFoundException (unchecked)
        List<MultimediaEntity> multimedia = viviendaMultimediaService.getMultimedia(viviendaId);
        assertNotNull(multimedia);
        assertEquals(multimediaList.size(), multimedia.size());
        List<Long> expectedIds = multimediaList.stream().map(MultimediaEntity::getId).toList();
        List<Long> actualIds = multimedia.stream().map(MultimediaEntity::getId).toList();
        assertTrue(actualIds.containsAll(expectedIds));
    }

    @Test
    void testGetMultimediaInvalidVivienda() {
        Long invalidViviendaId = 0L;
        assertThrows(EntityNotFoundException.class, () -> {
            viviendaMultimediaService.getMultimedia(invalidViviendaId);
        });
    }

    @Test
    // Quitamos 'throws EntityNotFoundException, IllegalOperationException'
    void testGetMultimediaItem() throws EntityNotFoundException, IllegalOperationException {
        Long existingMultimediaId = multimediaId1;

        // La llamada puede lanzar EntityNotFoundException o IllegalOperationException (unchecked)
        MultimediaEntity response = viviendaMultimediaService.getMultimediaItem(viviendaId, existingMultimediaId);

        assertNotNull(response);
        assertEquals(existingMultimediaId, response.getId());
        assertNotNull(response.getVivienda());
        assertEquals(viviendaId, response.getVivienda().getId());
    }

    @Test
    void testGetMultimediaItemInvalidVivienda() {
        Long invalidViviendaId = 0L;
        Long existingMultimediaId = multimediaId1;

        assertThrows(EntityNotFoundException.class, () -> {
            viviendaMultimediaService.getMultimediaItem(invalidViviendaId, existingMultimediaId);
        });
    }

    @Test
    void testGetInvalidMultimediaItem() {
        Long invalidMultimediaId = 0L;

        assertThrows(EntityNotFoundException.class, () -> {
            viviendaMultimediaService.getMultimediaItem(viviendaId, invalidMultimediaId);
        });
    }

    @Test
    void testGetMultimediaItemNotAssociated() {
        ViviendaEntity otherVivienda = factory.manufacturePojo(ViviendaEntity.class);
        otherVivienda.setId(null);
        otherVivienda = entityManager.persistFlushFind(otherVivienda);
        Long otherViviendaId = otherVivienda.getId();

        Long multimediaBelongingToVivienda = multimediaId1;

        // Quitamos la asignación 'IllegalOperationException ex ='
        IllegalOperationException thrown = assertThrows(IllegalOperationException.class, () -> {
            viviendaMultimediaService.getMultimediaItem(otherViviendaId, multimediaBelongingToVivienda);
        });
        // Si necesitas verificar el mensaje, puedes mantener la asignación
        assertEquals("El archivo multimedia no pertenece a la vivienda", thrown.getMessage());
    }

    @Test
    // Quitamos 'throws EntityNotFoundException, IllegalOperationException'
    void testRemoveMultimedia() throws EntityNotFoundException, IllegalOperationException{
        Long multimediaToRemoveId = multimediaId1;

        MultimediaEntity beforeDelete = entityManager.find(MultimediaEntity.class, multimediaToRemoveId);
        assertNotNull(beforeDelete);
        assertEquals(viviendaId, beforeDelete.getVivienda().getId());

        // La llamada puede lanzar EntityNotFoundException o IllegalOperationException (unchecked)
        viviendaMultimediaService.removeMultimedia(viviendaId, multimediaToRemoveId);
        entityManager.flush();

        ViviendaEntity updatedVivienda = entityManager.find(ViviendaEntity.class, viviendaId);
        entityManager.refresh(updatedVivienda);
        Optional<MultimediaEntity> removedItem = updatedVivienda.getMultimedia().stream()
                .filter(m -> m.getId().equals(multimediaToRemoveId))
                .findFirst();
        assertFalse(removedItem.isPresent(), "El multimedia removido no debería estar en la lista de la vivienda");
        assertEquals(multimediaList.size() - 1, updatedVivienda.getMultimedia().size());
    }

    @Test
    void testRemoveInvalidMultimedia() {
        Long invalidMultimediaId = 0L;
        assertThrows(EntityNotFoundException.class, () -> {
            viviendaMultimediaService.removeMultimedia(viviendaId, invalidMultimediaId);
        });
    }

    @Test
    void testRemoveMultimediaInvalidVivienda() {
        Long invalidViviendaId = 0L;
        Long existingMultimediaId = multimediaId1;

        assertThrows(EntityNotFoundException.class, () -> {
            viviendaMultimediaService.removeMultimedia(invalidViviendaId, existingMultimediaId);
        });
    }

    @Test
    void testRemoveMultimediaNotAssociated() {
        ViviendaEntity otherVivienda = factory.manufacturePojo(ViviendaEntity.class);
        otherVivienda.setId(null);
        otherVivienda = entityManager.persistFlushFind(otherVivienda);
        Long otherViviendaId = otherVivienda.getId();

        Long multimediaBelongingToVivienda = multimediaId1;

        // Quitamos la asignación 'IllegalOperationException ex ='
        IllegalOperationException thrown = assertThrows(IllegalOperationException.class, () -> {
            viviendaMultimediaService.removeMultimedia(otherViviendaId, multimediaBelongingToVivienda);
        });
        // Si necesitas verificar el mensaje, puedes mantener la asignación
        assertEquals("El archivo multimedia no pertenece a la vivienda", thrown.getMessage());
    }
}