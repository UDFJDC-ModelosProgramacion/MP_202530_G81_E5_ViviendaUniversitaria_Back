/* 
package co.edu.udistrital.mdp.back.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import co.edu.udistrital.mdp.back.entities.MultimediaEntity;
import co.edu.udistrital.mdp.back.entities.ViviendaEntity;
import co.edu.udistrital.mdp.back.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.back.exceptions.IllegalOperationException;
import jakarta.transaction.Transactional;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Transactional
@Import(ViviendaMultimediaService.class)
class ViviendaMultimediaServiceTest {

    @Autowired
    private ViviendaMultimediaService viviendaMultimediaService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();

    private ViviendaEntity vivienda;
    private List<MultimediaEntity> multimediaList = new ArrayList<>();
    private Long viviendaId;
    private Long multimediaId1, multimediaId2, multimediaId3;

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
        multimediaId2 = multimediaList.get(1).getId();
        multimediaId3 = multimediaList.get(2).getId();
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

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> {
            viviendaMultimediaService.addMultimedia(invalidViviendaId, existingMultimediaId);
        });
    }

    @Test
    void testAddInvalidMultimedia() {
        Long existingViviendaId = viviendaId;
        Long invalidMultimediaId = 0L;

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> {
            viviendaMultimediaService.addMultimedia(existingViviendaId, invalidMultimediaId);
        });
    }

    @Test
    void testGetMultimedia() throws EntityNotFoundException {
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
    void testGetMultimediaItem() throws EntityNotFoundException, IllegalOperationException {
        Long existingMultimediaId = multimediaId1;

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

        IllegalOperationException ex = assertThrows(IllegalOperationException.class, () -> {
            viviendaMultimediaService.getMultimediaItem(otherViviendaId, multimediaBelongingToVivienda);
        });
        assertEquals("El archivo multimedia no pertenece a la vivienda", ex.getMessage());
    }

    @Test
    void testRemoveMultimedia() throws EntityNotFoundException, IllegalOperationException {
        Long multimediaToRemoveId = multimediaId1;

        MultimediaEntity beforeDelete = entityManager.find(MultimediaEntity.class, multimediaToRemoveId);
        assertNotNull(beforeDelete);
        assertEquals(viviendaId, beforeDelete.getVivienda().getId());

        viviendaMultimediaService.removeMultimedia(viviendaId, multimediaToRemoveId);
        entityManager.flush();

        // Check relationship removed, entity might still exist if not orphaned
        ViviendaEntity updatedVivienda = entityManager.find(ViviendaEntity.class, viviendaId);
        entityManager.refresh(updatedVivienda);
        Optional<MultimediaEntity> removedItem = updatedVivienda.getMultimedia().stream()
                .filter(m -> m.getId().equals(multimediaToRemoveId))
                .findFirst();
        assertFalse(removedItem.isPresent(), "El multimedia removido no debería estar en la lista de la vivienda");
        assertEquals(multimediaList.size() - 1, updatedVivienda.getMultimedia().size());

        // Optionally check if the Multimedia entity still exists but without the vivienda association
        MultimediaEntity afterDelete = entityManager.find(MultimediaEntity.class, multimediaToRemoveId);
        assertNotNull(afterDelete, "La entidad Multimedia no debería ser eliminada, solo desasociada");
        assertNull(afterDelete.getVivienda(), "La vivienda asociada debería ser null después de remover");
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

        IllegalOperationException ex = assertThrows(IllegalOperationException.class, () -> {
            viviendaMultimediaService.removeMultimedia(otherViviendaId, multimediaBelongingToVivienda);
        });
        assertEquals("El archivo multimedia no pertenece a la vivienda", ex.getMessage());
    }
}
*/