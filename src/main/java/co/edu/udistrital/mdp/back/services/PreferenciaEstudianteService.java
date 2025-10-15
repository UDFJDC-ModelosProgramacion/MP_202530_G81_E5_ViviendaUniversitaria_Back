package co.edu.udistrital.mdp.back.services;

import co.edu.udistrital.mdp.back.entities.PreferenciaEstudianteEntity;
import co.edu.udistrital.mdp.back.repositories.PreferenciaEstudianteRepository;
import co.edu.udistrital.mdp.back.repositories.EstudianteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PreferenciaEstudianteService {

    private final PreferenciaEstudianteRepository preferenciaRepository;
    private final EstudianteRepository estudianteRepository;

    /**
     * CREATE - Crea un nuevo perfil de preferencias para un estudiante.
     *
     * Reglas aplicadas:
     * - Debe asociarse a un Estudiante que exista.
     * - Un Estudiante no puede tener más de un perfil de preferencias.
     * - El precioMaximo debe ser un valor positivo.
     * - El tipoVivienda no puede ser nulo o vacío.
     */
    public PreferenciaEstudianteEntity crearPreferencias(PreferenciaEstudianteEntity preferencias) {
        validarEstudianteAsociado(preferencias);
        validarUnicidadPorEstudiante(preferencias.getEstudiante().getId());
        validarCamposObligatorios(preferencias);

        return preferenciaRepository.save(preferencias);
    }

    /**
     * READ - Obtiene las preferencias por el ID del perfil.
     */
    public PreferenciaEstudianteEntity obtenerPreferenciasPorId(Long id) {
        return preferenciaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Perfil de preferencias no encontrado con ID: " + id));
    }

    /**
     * READ - Obtiene las preferencias por el ID del estudiante.
     */
    public PreferenciaEstudianteEntity obtenerPreferenciasPorEstudianteId(Long estudianteId) {
        return preferenciaRepository.findByEstudianteId(estudianteId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontraron preferencias para el estudiante con ID: " + estudianteId));
    }

    /**
     * UPDATE - Actualiza un perfil de preferencias existente.
     *
     * Reglas aplicadas:
     * - El Estudiante asociado no se puede cambiar.
     * - Se validan los campos obligatorios y valores numéricos.
     */
    public PreferenciaEstudianteEntity actualizarPreferencias(Long id, PreferenciaEstudianteEntity preferenciasActualizadas) {
        PreferenciaEstudianteEntity existentes = obtenerPreferenciasPorId(id);
        
        // Regla: No se puede cambiar el estudiante asociado
        if (!existentes.getEstudiante().getId().equals(preferenciasActualizadas.getEstudiante().getId())) {
            throw new IllegalStateException("No se puede cambiar el estudiante de un perfil de preferencias.");
        }

        validarCamposObligatorios(preferenciasActualizadas);

        // Actualizar campos
        existentes.setPrecioMaximo(preferenciasActualizadas.getPrecioMaximo());
        existentes.setZonaPreferida(preferenciasActualizadas.getZonaPreferida());
        existentes.setAceptaMascotas(preferenciasActualizadas.getAceptaMascotas());
        existentes.setTipoVivienda(preferenciasActualizadas.getTipoVivienda());

        return preferenciaRepository.save(existentes);
    }
    
    // ==================== MÉTODOS DE VALIDACIÓN ====================

    private void validarEstudianteAsociado(PreferenciaEstudianteEntity preferencias) {
        if (preferencias.getEstudiante() == null || preferencias.getEstudiante().getId() == null) {
            throw new IllegalArgumentException("Las preferencias deben estar asociadas a un estudiante.");
        }
        if (!estudianteRepository.existsById(preferencias.getEstudiante().getId())) {
            throw new IllegalArgumentException("El estudiante con ID " + preferencias.getEstudiante().getId() + " no existe.");
        }
    }
    
    private void validarUnicidadPorEstudiante(Long estudianteId) {
        if (preferenciaRepository.findByEstudianteId(estudianteId).isPresent()) {
            throw new IllegalStateException("El estudiante con ID " + estudianteId + " ya tiene un perfil de preferencias.");
        }
    }

    private void validarCamposObligatorios(PreferenciaEstudianteEntity preferencias) {
        if (preferencias.getPrecioMaximo() != null && preferencias.getPrecioMaximo() <= 0) {
            throw new IllegalArgumentException("El 'precioMaximo' debe ser un valor positivo.");
        }
        if (preferencias.getTipoVivienda() == null || preferencias.getTipoVivienda().trim().isEmpty()) {
            throw new IllegalArgumentException("El campo 'tipoVivienda' no puede ser nulo o vacío.");
        }
    }
}