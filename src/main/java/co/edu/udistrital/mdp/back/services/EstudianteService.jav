package co.edu.udistrital.mdp.back.services;

import co.edu.udistrital.mdp.back.entities.EstudianteEntity;
import co.edu.udistrital.mdp.back.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.back.exceptions.IllegalOperationException;
import co.edu.udistrital.mdp.back.repositories.EstudianteRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Clase que implementa la lógica de negocio para la entidad Estudiante.
 * Cumple con las reglas definidas en la capa de negocio del proyecto Vive Tu U.
 */
@Slf4j
@Service
public class EstudianteService {

    @Autowired
    private EstudianteRepository estudianteRepository;

    /**
     * [1] Crea un nuevo estudiante verificando las reglas de negocio.
     */
    @Transactional
    public EstudianteEntity createEstudiante(EstudianteEntity estudiante) throws IllegalOperationException {
        log.info("Inicia proceso de creación del estudiante con correo: {}", estudiante.getCorreo());

        // [2] Validación de unicidad del correo electrónico
        Optional<EstudianteEntity> existente = estudianteRepository.findByCorreo(estudiante.getCorreo());
        if (existente.isPresent()) {
            throw new IllegalOperationException("El correo electrónico ya está registrado en el sistema.");
        }

        // [3] Validar campos obligatorios
        if (estudiante.getNombre() == null || estudiante.getNombre().isBlank()) {
            throw new IllegalOperationException("El nombre no puede ser nulo ni vacío.");
        }

        if (estudiante.getCorreo() == null || !estudiante.getCorreo().contains("@")) {
            throw new IllegalOperationException("El correo electrónico no tiene un formato válido.");
        }

        // [4] Persistencia del estudiante
        EstudianteEntity nuevo = estudianteRepository.save(estudiante);
        log.info("Estudiante creado correctamente con id = {}", nuevo.getId());
        return nuevo;
    }

    /**
     * [5] Consulta todos los estudiantes registrados.
     */
    @Transactional
    public List<EstudianteEntity> getEstudiantes() {
        log.info("Inicia proceso de consulta de todos los estudiantes");
        return estudianteRepository.findAll();
    }

    /**
     * [6] Elimina un estudiante validando que no tenga reservas activas.
     */
    @Transactional
    public void deleteEstudiante(Long estudianteId) throws EntityNotFoundException, IllegalOperationException {
        log.info("Inicia proceso de eliminación del estudiante con id = {}", estudianteId);

        EstudianteEntity estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new EntityNotFoundException("El estudiante con el id proporcionado no existe."));

        if (!estudiante.getReservas().isEmpty()) {
            throw new IllegalOperationException("No se puede eliminar un estudiante con reservas asociadas.");
        }

        estudianteRepository.delete(estudiante);
        log.info("Termina proceso de eliminación del estudiante con id = {}", estudianteId);
    }
}

