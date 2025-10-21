/* 
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

import co.edu.udistrital.mdp.back.entities.UniversidadCercaEntity;
import co.edu.udistrital.mdp.back.entities.ViviendaEntity;
import co.edu.udistrital.mdp.back.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.back.exceptions.IllegalOperationException;
import jakarta.transaction.Transactional;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Transactional
@Import(UniversidadCercaViviendaService.class)
class UniversidadCercaViviendaServiceTest {

    @Autowired
    private UniversidadCercaViviendaService universidadCercaViviendaService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();

    private UniversidadCercaEntity universidadCerca;
    private List<ViviendaEntity> viviendaList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from ViviendaEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from UniversidadCercaEntity").executeUpdate();
    }

    private void insertData() {
    universidadCerca = factory.manufacturePojo(UniversidadCercaEntity.class);
    universidadCerca.setViviendas(new ArrayList<>());
    entityManager.persist(universidadCerca);

    for (int i = 0; i < 3; i++) {
        ViviendaEntity entity = factory.manufacturePojo(ViviendaEntity.class);
        entity.setMultimedia(new ArrayList<>());
        entity.setServicios(new ArrayList<>());
        entity.setComentarios(new ArrayList<>());
        entity.setUniversidadCerca(universidadCerca);
        entity.setPropietario(null);
        entityManager.persist(entity);
        viviendaList.add(entity);
        universidadCerca.getViviendas().add(entity);
    }
    entityManager.flush();
}

    @Test
    void testAddVivienda() throws EntityNotFoundException {
        UniversidadCercaEntity newUniversidad = factory.manufacturePojo(UniversidadCercaEntity.class);
        entityManager.persist(newUniversidad);

        ViviendaEntity newVivienda = factory.manufacturePojo(ViviendaEntity.class);
        entityManager.persist(newVivienda);

        ViviendaEntity response = universidadCercaViviendaService.addVivienda(newUniversidad.getId(), newVivienda.getId());

        assertNotNull(response);
        assertEquals(newVivienda.getId(), response.getId());
        assertEquals(newUniversidad.getId(), response.getUniversidadCerca().getId());
    }

    @Test
    void testAddViviendaInvalidUniversidad() {
        assertThrows(EntityNotFoundException.class, () -> {
            ViviendaEntity newVivienda = factory.manufacturePojo(ViviendaEntity.class);
            entityManager.persist(newVivienda);
            universidadCercaViviendaService.addVivienda(0L, newVivienda.getId());
        });
    }

    @Test
    void testAddInvalidVivienda() {
        assertThrows(EntityNotFoundException.class, () -> {
            universidadCercaViviendaService.addVivienda(universidadCerca.getId(), 0L);
        });
    }

    @Test
    void testGetViviendas() throws EntityNotFoundException {
        List<ViviendaEntity> viviendas = universidadCercaViviendaService.getViviendas(universidadCerca.getId());
        assertEquals(viviendaList.size(), viviendas.size());
    }

    @Test
    void testGetViviendasInvalidUniversidad() {
        assertThrows(EntityNotFoundException.class, () -> {
            universidadCercaViviendaService.getViviendas(0L);
        });
    }

    @Test
    void testGetVivienda() throws EntityNotFoundException, IllegalOperationException {
        ViviendaEntity vivienda = viviendaList.get(0);
        ViviendaEntity response = universidadCercaViviendaService.getVivienda(universidadCerca.getId(), vivienda.getId());
        
        assertNotNull(response);
        assertEquals(vivienda.getId(), response.getId());
    }

    @Test
    void testGetViviendaInvalidUniversidad() {
        assertThrows(EntityNotFoundException.class, () -> {
            ViviendaEntity vivienda = viviendaList.get(0);
            universidadCercaViviendaService.getVivienda(0L, vivienda.getId());
        });
    }

    @Test
    void testGetInvalidVivienda() {
        assertThrows(EntityNotFoundException.class, () -> {
            universidadCercaViviendaService.getVivienda(universidadCerca.getId(), 0L);
        });
    }

    @Test
    void testGetViviendaNotAssociated() {
        assertThrows(IllegalOperationException.class, () -> {
            UniversidadCercaEntity newUniversidad = factory.manufacturePojo(UniversidadCercaEntity.class);
            entityManager.persist(newUniversidad);

            ViviendaEntity vivienda = viviendaList.get(0);
            universidadCercaViviendaService.getVivienda(newUniversidad.getId(), vivienda.getId());
        });
    }

    @Test
    void testRemoveVivienda() throws EntityNotFoundException, IllegalOperationException {
        ViviendaEntity vivienda = viviendaList.get(0);
        universidadCercaViviendaService.removeVivienda(universidadCerca.getId(), vivienda.getId());

        ViviendaEntity deleted = entityManager.find(ViviendaEntity.class, vivienda.getId());
        assertNull(deleted.getUniversidadCerca());
    }

    @Test
    void testRemoveInvalidVivienda() {
        assertThrows(EntityNotFoundException.class, () -> {
            universidadCercaViviendaService.removeVivienda(universidadCerca.getId(), 0L);
        });
    }

    @Test
    void testRemoveViviendaInvalidUniversidad() {
        assertThrows(EntityNotFoundException.class, () -> {
            ViviendaEntity vivienda = viviendaList.get(0);
            universidadCercaViviendaService.removeVivienda(0L, vivienda.getId());
        });
    }
}
    
*/
