/* 
package co.edu.udistrital.mdp.back.services;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
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

import co.edu.udistrital.mdp.back.entities.PropietarioEntity;
import co.edu.udistrital.mdp.back.entities.ViviendaEntity;
import co.edu.udistrital.mdp.back.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.back.exceptions.IllegalOperationException;
import jakarta.transaction.Transactional;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Transactional
@Import(PropietarioViviendaService.class)
class PropietarioViviendaServiceTest {

    @Autowired
    private PropietarioViviendaService propietarioViviendaService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();

    private PropietarioEntity propietario;
    private List<ViviendaEntity> viviendaList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from ViviendaEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from PropietarioEntity").executeUpdate();
    }
    
    private void insertData() {
    propietario = factory.manufacturePojo(PropietarioEntity.class);
    propietario.setViviendas(new ArrayList<>());
    entityManager.persist(propietario);

    for (int i = 0; i < 3; i++) {
        ViviendaEntity entity = factory.manufacturePojo(ViviendaEntity.class);
        entity.setMultimedia(new ArrayList<>());
        entity.setServicios(new ArrayList<>());
        entity.setComentarios(new ArrayList<>());
        entity.setPropietario(propietario);
        entity.setUniversidadCerca(null);
        entityManager.persist(entity);
        viviendaList.add(entity);
        propietario.getViviendas().add(entity);
    }
    entityManager.flush();
}

    @Test
    void testAddVivienda() throws EntityNotFoundException {
        PropietarioEntity newPropietario = factory.manufacturePojo(PropietarioEntity.class);
        entityManager.persist(newPropietario);

        ViviendaEntity newVivienda = factory.manufacturePojo(ViviendaEntity.class);
        entityManager.persist(newVivienda);

        ViviendaEntity response = propietarioViviendaService.addVivienda(newPropietario.getId(), newVivienda.getId());

        assertNotNull(response);
        assertEquals(newVivienda.getId(), response.getId());
        assertEquals(newPropietario.getId(), response.getPropietario().getId());
    }

    @Test
    void testAddViviendaInvalidPropietario() {
        assertThrows(EntityNotFoundException.class, () -> {
            ViviendaEntity newVivienda = factory.manufacturePojo(ViviendaEntity.class);
            entityManager.persist(newVivienda);
            propietarioViviendaService.addVivienda(0L, newVivienda.getId());
        });
    }

    @Test
    void testAddInvalidVivienda() {
        assertThrows(EntityNotFoundException.class, () -> {
            propietarioViviendaService.addVivienda(propietario.getId(), 0L);
        });
    }

    @Test
    void testGetViviendas() throws EntityNotFoundException {
        List<ViviendaEntity> viviendas = propietarioViviendaService.getViviendas(propietario.getId());
        assertEquals(viviendaList.size(), viviendas.size());
    }

    @Test
    void testGetViviendasInvalidPropietario() {
        assertThrows(EntityNotFoundException.class, () -> {
            propietarioViviendaService.getViviendas(0L);
        });
    }

    @Test
    void testGetVivienda() throws EntityNotFoundException, IllegalOperationException {
        ViviendaEntity vivienda = viviendaList.get(0);
        ViviendaEntity response = propietarioViviendaService.getVivienda(propietario.getId(), vivienda.getId());
        
        assertNotNull(response);
        assertEquals(vivienda.getId(), response.getId());
    }

    @Test
    void testGetViviendaInvalidPropietario() {
        assertThrows(EntityNotFoundException.class, () -> {
            ViviendaEntity vivienda = viviendaList.get(0);
            propietarioViviendaService.getVivienda(0L, vivienda.getId());
        });
    }

    @Test
    void testGetInvalidVivienda() {
        assertThrows(EntityNotFoundException.class, () -> {
            propietarioViviendaService.getVivienda(propietario.getId(), 0L);
        });
    }

    @Test
    void testGetViviendaNotAssociated() {
        assertThrows(IllegalOperationException.class, () -> {
            PropietarioEntity newPropietario = factory.manufacturePojo(PropietarioEntity.class);
            entityManager.persist(newPropietario);

            ViviendaEntity vivienda = viviendaList.get(0);
            propietarioViviendaService.getVivienda(newPropietario.getId(), vivienda.getId());
        });
    }

    @Test
    void testRemoveVivienda() throws EntityNotFoundException, IllegalOperationException {
        ViviendaEntity vivienda = viviendaList.get(0);
        propietarioViviendaService.removeVivienda(propietario.getId(), vivienda.getId());

        ViviendaEntity deleted = entityManager.find(ViviendaEntity.class, vivienda.getId());
        assertNull(deleted.getPropietario());
    }

    @Test
    void testRemoveInvalidVivienda() {
        assertThrows(EntityNotFoundException.class, () -> {
            propietarioViviendaService.removeVivienda(propietario.getId(), 0L);
        });
    }

    @Test
    void testRemoveViviendaInvalidPropietario() {
        assertThrows(EntityNotFoundException.class, () -> {
            ViviendaEntity vivienda = viviendaList.get(0);
            propietarioViviendaService.removeVivienda(0L, vivienda.getId());
        });
    }
}
*/