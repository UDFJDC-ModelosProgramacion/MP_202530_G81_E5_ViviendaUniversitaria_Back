package co.edu.udistrital.mdp.back.services;

import co.edu.udistrital.mdp.back.entities.EstudianteEntity;
import co.edu.udistrital.mdp.back.entities.ReservaEntity;
import co.edu.udistrital.mdp.back.repositories.EstudianteRepository;
import co.edu.udistrital.mdp.back.repositories.ReservaRepository;
import co.edu.udistrital.mdp.back.exceptions.IllegalOperationException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class EstudianteService {

    private final EstudianteRepository estudianteRepo;
    private final ReservaRepository reservaRepo;

    /**
     * CREATE - Crear un nuevo estudiante validando:
     * - Correo único
     * - Campos obligatorios (nombre y correo)
     * 
     * @throws IllegalOperationException
     */
    public EstudianteEntity createEstudiante(EstudianteEntity in) throws IllegalOperationException {
        if (in == null)
            throw new IllegalArgumentException("Entidad Estudiante obligatoria");
        if (in.getCorreo() == null || in.getCorreo().isBlank())
            throw new IllegalArgumentException("El correo no puede estar vacío");
        if (in.getNombre() == null || in.getNombre().isBlank())
            throw new IllegalArgumentException("El nombre no puede estar vacío");

        if (estudianteRepo.findByCorreo(in.getCorreo()).isPresent()) {
            throw new IllegalOperationException("El correo ya está registrado");
        }

        return estudianteRepo.save(in);
    }

    /**
     * READ - Obtener todos los estudiantes
     */
    public List<EstudianteEntity> getEstudiantes() {
        return estudianteRepo.findAll();
    }

    /**
     * READ - Obtener estudiante por ID
     */
    public EstudianteEntity getEstudiante(Long id) {
        return estudianteRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Estudiante no encontrado con ID: " + id));
    }

    /**
     * UPDATE - Actualizar datos básicos del estudiante
     */
    public EstudianteEntity updateEstudiante(Long id, EstudianteEntity updates) {
        EstudianteEntity found = getEstudiante(id);

        if (updates.getNombre() != null && !updates.getNombre().isBlank())
            found.setNombre(updates.getNombre());
        if (updates.getTelefono() != null)
            found.setTelefono(updates.getTelefono());
        if (updates.getUniversidad() != null)
            found.setUniversidad(updates.getUniversidad());

        return estudianteRepo.save(found);
    }

    /**
     * DELETE - Eliminar estudiante si no tiene reservas activas
     * 
     * @throws IllegalOperationException
     */
    public void deleteEstudiante(Long id) throws IllegalOperationException {
        EstudianteEntity found = getEstudiante(id);
        List<ReservaEntity> reservas = reservaRepo.findByEstudianteId(id);

        if (!reservas.isEmpty()) {
            throw new IllegalOperationException("No se puede eliminar el estudiante con reservas activas o históricas");
        }

        estudianteRepo.delete(found);
    }
}
