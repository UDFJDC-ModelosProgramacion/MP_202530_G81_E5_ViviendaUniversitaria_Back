package co.edu.udistrital.mdp.back.services;

import co.edu.udistrital.mdp.back.entities.EstudianteEntity;
import co.edu.udistrital.mdp.back.repositories.EstudianteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EstudianteService {

    private final EstudianteRepository estudianteRepo;

    /**
     * CREATE - crea un estudiante con validaciones:
     * - nombre obligatorio, <=150 caracteres
     * - correo obligatorio, formato válido y único (case-insensitive)
     */
    public EstudianteEntity crear(EstudianteEntity in) {
        if (in == null)
            throw new IllegalArgumentException("Entidad Estudiante es obligatoria");

        String nombre = safeTrim(in.getNombre());
        String correo = safeTrim(in.getCorreo());

        if (nombre.isBlank())
            throw new IllegalArgumentException("Nombre obligatorio");
        if (nombre.length() > 150)
            throw new IllegalArgumentException("Nombre excede 150 caracteres");

        if (correo.isBlank())
            throw new IllegalArgumentException("Correo obligatorio");
        if (!correo.contains("@") || correo.length() > 150)
            throw new IllegalArgumentException("Correo inválido");

        if (estudianteRepo.existsByCorreoIgnoreCase(correo))
            throw new IllegalArgumentException("Ya existe un estudiante con ese correo");

        in.setNombre(nombre);
        in.setCorreo(correo);

        return estudianteRepo.save(in);
    }

    /**
     * READ - buscar por ID
     */
    public EstudianteEntity obtenerPorId(Long id) {
        return estudianteRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Estudiante no encontrado con ID: " + id));
    }

    /**
     * READ - listar todos los estudiantes
     */
    public List<EstudianteEntity> listarTodos() {
        return estudianteRepo.findAll();
    }

    /**
     * UPDATE - permite cambiar datos excepto el correo si ya existe en otro
     */
    public EstudianteEntity actualizar(Long id, EstudianteEntity updates) {
        EstudianteEntity found = obtenerPorId(id);

        String nuevoNombre = safeTrim(updates.getNombre());
        if (nuevoNombre.isBlank())
            throw new IllegalArgumentException("Nombre obligatorio");
        if (nuevoNombre.length() > 150)
            throw new IllegalArgumentException("Nombre excede 150 caracteres");
        found.setNombre(nuevoNombre);

        String nuevoCorreo = safeTrim(updates.getCorreo());
        if (!nuevoCorreo.isBlank() && !found.getCorreo().equalsIgnoreCase(nuevoCorreo)) {
            if (estudianteRepo.existsByCorreoIgnoreCase(nuevoCorreo))
                throw new IllegalArgumentException("Ya existe un estudiante con ese correo");
            found.setCorreo(nuevoCorreo);
        }

        if (updates.getTelefono() != null)
            found.setTelefono(updates.getTelefono());
        if (updates.getUniversidad() != null)
            found.setUniversidad(updates.getUniversidad());

        return estudianteRepo.save(found);
    }

    /**
     * DELETE - eliminar estudiante solo si no tiene reservas o estancias activas
     */
    public void eliminar(Long id) {
        EstudianteEntity found = obtenerPorId(id);

        if (estudianteRepo.countReservasActivasByEstudianteId(id) > 0)
            throw new IllegalStateException("No se puede eliminar: tiene reservas activas");

        if (estudianteRepo.countEstanciasActivasByEstudianteId(id) > 0)
            throw new IllegalStateException("No se puede eliminar: tiene estancias activas");

        estudianteRepo.delete(found);
    }

    private String safeTrim(String s) {
        return s == null ? "" : s.trim();
    }
}
