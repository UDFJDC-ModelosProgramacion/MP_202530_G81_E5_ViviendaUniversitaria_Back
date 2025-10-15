package co.edu.udistrital.mdp.back.services;

import co.edu.udistrital.mdp.back.entities.UniversidadCercaEntity;
import co.edu.udistrital.mdp.back.repositories.UniversidadCercaRepository;
import co.edu.udistrital.mdp.back.repositories.ViviendaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UniversidadCercaService {

    private final UniversidadCercaRepository universidadRepo;
    private final ViviendaRepository viviendaRepo;

    /**
     * CREATE - crea una UniversidadCerca validando reglas:
     * - nombre obligatorio (no nulo/ni en blanco, <=150 chars)
     * - nombre único (case-insensitive)
     */
    public UniversidadCercaEntity crearUniversidad(UniversidadCercaEntity in) {
        if (in == null) throw new IllegalArgumentException("Entidad UniversidadCerca es obligatoria");
        String nombre = safeTrim(in.getNombre());
        if (nombre.isBlank()) throw new IllegalArgumentException("Nombre obligatorio para UniversidadCerca");
        if (nombre.length() > 150) throw new IllegalArgumentException("Nombre excede 150 caracteres");
        if (universidadRepo.existsByNombreIgnoreCase(nombre)) throw new IllegalArgumentException("Ya existe otra UniversidadCerca con ese nombre");
        in.setNombre(nombre);
        return universidadRepo.save(in);
    }

    /**
     * READ - buscar por id
     */
    public UniversidadCercaEntity obtenerPorId(Long id) {
        return universidadRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("UniversidadCerca no encontrada con ID: " + id));
    }

    /**
     * READ - listar todas
     */
    public List<UniversidadCercaEntity> listarTodas() {
        return universidadRepo.findAll();
    }

    /**
     * UPDATE - actualizar (no permitir cambiar nombre a uno que ya existe)
     */
    public UniversidadCercaEntity actualizar(Long id, UniversidadCercaEntity updates) {
        UniversidadCercaEntity found = obtenerPorId(id);

        String nuevoNombre = safeTrim(updates.getNombre());
        if (nuevoNombre.isBlank()) throw new IllegalArgumentException("Nombre obligatorio para UniversidadCerca");
        if (!found.getNombre().equalsIgnoreCase(nuevoNombre)) {
            if (universidadRepo.existsByNombreIgnoreCase(nuevoNombre)) {
                throw new IllegalArgumentException("No se puede cambiar el nombre porque ya existe otra UniversidadCerca con ese nombre");
            }
            if (nuevoNombre.length() > 150) throw new IllegalArgumentException("Nombre excede 150 caracteres");
            found.setNombre(nuevoNombre);
        }

        // ciudad es opcional en tu entidad — actualizar si viene
        if (updates.getCiudad() != null) {
            found.setCiudad(updates.getCiudad());
        }

        return universidadRepo.save(found);
    }

    /**
     * DELETE - prohibido eliminar si tiene viviendas asociadas o estancias indirectas
     */
    public void eliminar(Long id) {
        UniversidadCercaEntity found = obtenerPorId(id);

        long numViviendas = viviendaRepo.countByUniversidadCercaId(id);
        if (numViviendas > 0) {
            throw new IllegalStateException("No se puede eliminar: tiene viviendas asociadas (" + numViviendas + ")");
        }

        long numEstanciasIndirectas = viviendaRepo.countEstanciasByUniversidadCercaId(id);
        if (numEstanciasIndirectas > 0) {
            throw new IllegalStateException("No se puede eliminar: existen estancias indirectas a través de sus viviendas (" + numEstanciasIndirectas + ")");
        }

        universidadRepo.delete(found);
    }

    private String safeTrim(String s) {
        return s == null ? "" : s.trim();
    }
}
