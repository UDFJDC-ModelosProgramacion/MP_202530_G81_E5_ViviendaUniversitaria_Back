package co.edu.udistrital.mdp.back.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

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

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from MultimediaEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from ViviendaEntity").executeUpdate();
    }

    private void insertData() {
    vivienda = factory.manufacturePojo(ViviendaEntity.class);
    vivienda.setMultimedia(new ArrayList<>());
    vivienda.setServicios(new ArrayList<>());
    vivienda.setComentarios(new ArrayList<>());
    vivienda.setPropietario(null);
    vivienda.setUniversidadCerca(null);
    entityManager.persist(vivienda);

    for (int i = 0; i < 3; i++) {
        MultimediaEntity entity = factory.manufacturePojo(MultimediaEntity.class);
        entity.setVivienda(vivienda);
        entityManager.persist(entity);
        multimediaList.add(entity);
        vivienda.getMultimedia().add(entity);
    }
    entityManager.flush();
}

    @Test
void testAddMultimedia() throws EntityNotFoundException {
    ViviendaEntity newVivienda = factory.manufacturePojo(ViviendaEntity.class);
    newVivienda.setMultimedia(new ArrayList<>());
    newVivienda.setServicios(new ArrayList<>());
    newVivienda.setComentarios(new ArrayList<>());
    newVivienda.setPropietario(null);
    newVivienda.setUniversidadCerca(null);
    entityManager.persist(newVivienda);
    entityManager.flush();

    MultimediaEntity newMultimedia = factory.manufacturePojo(MultimediaEntity.class);
    newMultimedia.setVivienda(null);
    entityManager.persist(newMultimedia);
    entityManager.flush();

    MultimediaEntity response = viviendaMultimediaService.addMultimedia(newVivienda.getId(), newMultimedia.getId());

    assertNotNull(response);
    assertEquals(newMultimedia.getId(), response.getId());
    assertEquals(newVivienda.getId(), response.getVivienda().getId());
}

    @Test
    void testAddMultimediaInvalidVivienda() {
        assertThrows(EntityNotFoundException.class, () -> {
            MultimediaEntity newMultimedia = factory.manufacturePojo(MultimediaEntity.class);
            entityManager.persist(newMultimedia);
            viviendaMultimediaService.addMultimedia(0L, newMultimedia.getId());
        });
    }

    @Test
    void testAddInvalidMultimedia() {
        assertThrows(EntityNotFoundException.class, () -> {
            viviendaMultimediaService.addMultimedia(vivienda.getId(), 0L);
        });
    }

    @Test
    void testGetMultimedia() throws EntityNotFoundException {
        List<MultimediaEntity> multimedia = viviendaMultimediaService.getMultimedia(vivienda.getId());
        assertEquals(multimediaList.size(), multimedia.size());
    }

    @Test
    void testGetMultimediaInvalidVivienda() {
        assertThrows(EntityNotFoundException.class, () -> {
            viviendaMultimediaService.getMultimedia(0L);
        });
    }

    @Test
    void testGetMultimediaItem() throws EntityNotFoundException, IllegalOperationException {
        MultimediaEntity multimedia = multimediaList.get(0);
        MultimediaEntity response = viviendaMultimediaService.getMultimediaItem(vivienda.getId(), multimedia.getId());

        assertNotNull(response);
        assertEquals(multimedia.getId(), response.getId());
    }

    @Test
    void testGetMultimediaItemInvalidVivienda() {
        assertThrows(EntityNotFoundException.class, () -> {
            MultimediaEntity multimedia = multimediaList.get(0);
            viviendaMultimediaService.getMultimediaItem(0L, multimedia.getId());
        });
    }

    @Test
    void testGetInvalidMultimediaItem() {
        assertThrows(EntityNotFoundException.class, () -> {
            viviendaMultimediaService.getMultimediaItem(vivienda.getId(), 0L);
        });
    }

    @Test
    void testGetMultimediaItemNotAssociated() {
        assertThrows(IllegalOperationException.class, () -> {
            ViviendaEntity newVivienda = factory.manufacturePojo(ViviendaEntity.class);
            entityManager.persist(newVivienda);

            MultimediaEntity multimedia = multimediaList.get(0);
            viviendaMultimediaService.getMultimediaItem(newVivienda.getId(), multimedia.getId());
        });
    }

    @Test
    void testRemoveMultimedia() throws EntityNotFoundException, IllegalOperationException {
        MultimediaEntity multimedia = multimediaList.get(0);
        viviendaMultimediaService.removeMultimedia(vivienda.getId(), multimedia.getId());

        ViviendaEntity updated = entityManager.find(ViviendaEntity.class, vivienda.getId());
        assertFalse(updated.getMultimedia().contains(multimedia));
    }

    @Test
    void testRemoveInvalidMultimedia() {
        assertThrows(EntityNotFoundException.class, () -> {
            viviendaMultimediaService.removeMultimedia(vivienda.getId(), 0L);
        });
    }

    @Test
    void testRemoveMultimediaInvalidVivienda() {
        assertThrows(EntityNotFoundException.class, () -> {
            MultimediaEntity multimedia = multimediaList.get(0);
            viviendaMultimediaService.removeMultimedia(0L, multimedia.getId());
        });
    }
}