package co.edu.udistrital.mdp.back.services;

import co.edu.udistrital.mdp.back.entities.SitioInteresEntity;
import co.edu.udistrital.mdp.back.entities.ViviendaEntity;
import co.edu.udistrital.mdp.back.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.back.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.back.repositories.SitioInteresRepository;
import co.edu.udistrital.mdp.back.repositories.ViviendaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
public class SitioInteresService {

    @Autowired
    private SitioInteresRepository sitioRepo;

    @Autowired
    private ViviendaRepository viviendaRepo;

    /**
     * CREATE - Crear un nuevo sitio de interés validando reglas de negocio.
     */
    public SitioInteresEntity createSitioInteres(SitioInteresEntity in) throws IllegalOperationException {
        if (in == null)
            throw new IllegalArgumentException("Entidad SitioInteres obligatoria");
        if (in.getNombre() == null || in.getNombre().isBlank())
            throw new IllegalArgumentException("El nombre del sitio no puede estar vacío");
        if (in.getUbicacion() == null || in.getUbicacion().isBlank())
            throw new IllegalArgumentException("Debe especificarse la ubicación del sitio");

        List<SitioInteresEntity> existentes = sitioRepo.findByNombreContaining(in.getNombre());
        if (!existentes.isEmpty())
            throw new IllegalOperationException("Ya existe un sitio con un nombre similar");

        return sitioRepo.save(in);
    }

    /**
     * READ - Obtener todos los sitios
     */
    public List<SitioInteresEntity> getAllSitios() {
        return sitioRepo.findAll();
    }

    /**
     * READ - Obtener sitio por id
     */
    public SitioInteresEntity getSitioInteres(Long id) throws EntityNotFoundException {
        return sitioRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sitio de interés no encontrado con id: " + id));
    }

    /**
     * UPDATE (PUT) - Actualización completa de los campos del sitio
     */
    public SitioInteresEntity updateSitioInteres(Long id, SitioInteresEntity in)
            throws EntityNotFoundException, IllegalOperationException {

        SitioInteresEntity found = getSitioInteres(id);

        if (in.getNombre() != null && !in.getNombre().isBlank())
            found.setNombre(in.getNombre());
        if (in.getUbicacion() != null && !in.getUbicacion().isBlank())
            found.setUbicacion(in.getUbicacion());
        if (in.getTiempoCaminando() != null)
            found.setTiempoCaminando(in.getTiempoCaminando());
        if (in.getFoto() != null)
            found.setFoto(in.getFoto());

        return sitioRepo.save(found);
    }

    /**
     * DELETE - Eliminar un sitio solo si no tiene viviendas asociadas
     */
    public void deleteSitioInteres(Long id)
            throws EntityNotFoundException, IllegalOperationException {
        SitioInteresEntity found = getSitioInteres(id);

        List<ViviendaEntity> viviendas = found.getViviendas();
        if (viviendas != null && !viviendas.isEmpty()) {
            throw new IllegalOperationException("No se puede eliminar el sitio porque tiene viviendas asociadas");
        }

        sitioRepo.delete(found);
    }
}
