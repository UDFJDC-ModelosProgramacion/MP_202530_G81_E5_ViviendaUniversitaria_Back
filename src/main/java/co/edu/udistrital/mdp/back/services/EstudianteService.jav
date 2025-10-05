package co.edu.udistrital.mdp.back.services;

import co.edu.udistrital.mdp.back.entities.EstudianteEntity;
import co.edu.udistrital.mdp.back.repositories.EstudianteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 * Servicio que contiene la lógica de negocio para la entidad Estudiante.
 * Valida las reglas de creación, actualización y eliminación.
 */
@Slf4j
@Service
public class EstudianteService {

    @Autowired
    private EstudianteRepository estudianteRepository;

    /** Crear un nuevo estudiante aplicando las reglas de negocio */
    public EstudianteEntity createEstudiante(EstudianteEntity estudiante) throws Exception {
        log.info("Iniciando creación de estudiante...");

        if (estudiante.getNombre() == null || estudiante.getNombre().isBlank()) {
            throw new Exception("El nombre no puede ser nulo ni vacío.");
        }

        if (estudiante.getCorreo() == null || !estudiante.getCorreo().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new Exception("El correo electrónico no tiene un formato válido.");
        }

        Optional<EstudianteEntity> existente = estudianteRepository.findByCorreo(estudiante.getCorreo());
        if (existente.isPresent()) {
            throw new Exception("Ya existe un estudiante con el mismo correo electrónico.");
        }

        if (estudiante.getUniversidad() == null || estudiante.getUniversidad().isBlank()) {
            throw new Exception("La universidad no puede quedar vacía.");
        }

        log.info("Estudiante creado correctamente: {}", estudiante.getNombre());
        return estudianteRepository.save(estudiante);
    }

    /** Consultar todos los estudiantes */
    public List<EstudianteEntity> getEstudiantes() {
        log.info("Consultando todos los estudiantes...");
        return estudianteRepository.findAll();
    }

    /** Consultar un estudiante por su ID */
    public EstudianteEntity getEstudiante(Long id) throws Exception {
        log.info("Consultando estudiante con ID {}", id);
        return estudianteRepository.findById(id)
                .orElseThrow(() -> new Exception("El estudiante con ID " + id + " no existe."));
    }

    /** Actualizar los datos de un estudiante */
    public EstudianteEntity updateEstudiante(Long id, EstudianteEntity nuevo) throws Exception {
        EstudianteEntity existente = getEstudiante(id);

        if (nuevo.getCorreo() != null && !nuevo.getCorreo().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new Exception("Formato de correo inválido.");
        }

        existente.setNombre(nuevo.getNombre());
        existente.setCorreo(nuevo.getCorreo());
        existente.setUniversidad(nuevo.getUniversidad());

        log.info("Estudiante actualizado correctamente: {}", existente.getNombre());
        return estudianteRepository.save(existente);
    }

    /** Eliminar un estudiante junto con sus reservas asociadas */
    public void deleteEstudiante(Long id) throws Exception {
        EstudianteEntity estudiante = getEstudiante(id);
        log.info("Eliminando estudiante con ID {}", id);
        estudianteRepository.delete(estudiante);
    }
}
