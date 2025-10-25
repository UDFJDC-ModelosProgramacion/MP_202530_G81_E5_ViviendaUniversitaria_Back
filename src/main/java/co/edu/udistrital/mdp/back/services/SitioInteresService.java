package co.edu.udistrital.mdp.back.services;

import co.edu.udistrital.mdp.back.entities.SitioInteresEntity;
import co.edu.udistrital.mdp.back.repositories.SitioInteresRepository;
import co.edu.udistrital.mdp.back.repositories.ViviendaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SitioInteresService {

    private final SitioInteresRepository sitioRepo;
    private final ViviendaRepository viviendaRepo;

    /**
     * CREATE - crea un sitio de interés con validaciones.
     */
    public SitioInteresEntity createSitioInteres(SitioInteresEntity in) {
        if (in == null)
            throw new IllegalArgumentException("Entidad SitioInteres es obligatoria");

        String nombre = safeTrim(in.getNombre());
        String ubicacion = safeTrim(in.getUbicacion());

        if (nombre.isBlank())
            throw new IllegalArgumentException("Nombre obligatorio");
        if (nombre.length() > 150)
            throw new IllegalArgumentException("Nombre excede 150 caracteres");
        if (sitioRepo.existsByNombreIgnoreCase(nombre))
            throw new IllegalArgumentException("Ya existe un SitioInteres con ese nombre");

        if (ubicacion.isBlank())
            throw new IllegalArgumentException("Ubicación obligatoria");
        if (in.getTiempoCaminando() == null || in.getTiempoCaminando() < 0)
            throw new IllegalArgumentException("Tiempo caminando inválido");

        in.setNombre(nombre);
        in.setUbicacion(ubicacion);

        return sitioRepo.save(in);
    }

    /**
     * READ - obtiene todos los sitios
     */
    public List<SitioInteresEntity> getAllSitios() {
        return sitioRepo.findAll();
    }

    /**
     * READ - obtiene un sitio por su ID
     */
    public SitioInteresEntity getSitioInteres(long id) {
        return sitioRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("SitioInteres no encontrado con ID: " + id));
    }

    /**
     * UPDATE - actualiza los datos de un sitio existente.
     */
    public SitioInteresEntity updateSitioInteres(long id, SitioInteresEntity updates) {
        SitioInteresEntity found = getSitioInteres(id);

        String nuevoNombre = safeTrim(updates.getNombre());
        if (nuevoNombre.isBlank())
            throw new IllegalArgumentException("Nombre obligatorio");
        if (!found.getNombre().equalsIgnoreCase(nuevoNombre)
                && sitioRepo.existsByNombreIgnoreCase(nuevoNombre)) {
            throw new IllegalArgumentException("Ya existe otro SitioInteres con ese nombre");
        }

        found.setNombre(nuevoNombre);

        String nuevaUbicacion = safeTrim(updates.getUbicacion());
        if (nuevaUbicacion.isBlank())
            throw new IllegalArgumentException("Ubicación obligatoria");
        found.setUbicacion(nuevaUbicacion);

        if (updates.getDescripcion() != null)
            found.setDescripcion(updates.getDescripcion());
        if (updates.getFoto() != null)
            found.setFoto(updates.getFoto());
        if (updates.getTiempoCaminando() != null && updates.getTiempoCaminando() >= 0)
            found.setTiempoCaminando(updates.getTiempoCaminando());

        return sitioRepo.save(found);
    }

    /**
     * DELETE - elimina un sitio si no tiene viviendas asociadas.
     */
    public void deleteSitioInteres(long id) {
        SitioInteresEntity found = getSitioInteres(id);

        long viviendasAsociadas = sitioRepo.countViviendasAsociadas(id);
        if (viviendasAsociadas > 0)
            throw new IllegalStateException(
                    "No se puede eliminar: tiene viviendas asociadas (" + viviendasAsociadas + ")");

        sitioRepo.delete(found);
    }

    private String safeTrim(String s) {
        return s == null ? "" : s.trim();
    }
}
