package co.edu.udistrital.mdp.back.services;

import co.edu.udistrital.mdp.back.entities.ViviendaEntity;
import co.edu.udistrital.mdp.back.repositories.ViviendaRepository;
import co.edu.udistrital.mdp.back.repositories.PropietarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

/**
 * Servicio que maneja la lógica de negocio para la entidad Vivienda
 * Implementa las reglas de negocio definidas para CREATE, UPDATE y DELETE
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ViviendaService {

    private final ViviendaRepository viviendaRepository;
    private final PropietarioRepository propietarioRepository;

    /**
     * CREATE - Crea una nueva vivienda validando todas las reglas de negocio
     * 
     * Reglas aplicadas:
     * - propietario, dirección, ciudad y barrio no pueden estar vacíos
     * - precioMensual y areaMetrosCuadrados deben ser mayores a cero
     * - numeroHabitaciones y numeroBaños mínimo 1
     * - tipo debe estar especificado
     * - disponible se inicializa en true
     */
    public ViviendaEntity crearVivienda(ViviendaEntity vivienda) {
        // Validar campos obligatorios no vacíos
        validarCamposObligatorios(vivienda);

        // Validar que el propietario exista
        validarPropietarioExiste(vivienda.getPropietario().getId());

        // Validar valores numéricos positivos
        validarValoresNumericos(vivienda);

        // Validar habitaciones y baños mínimo 1
        validarHabitacionesYBanos(vivienda);

        // Validar que el tipo esté especificado
        validarTipoVivienda(vivienda);

        // Inicializar disponible en true (regla de negocio)
        vivienda.setDisponible(true);

        // Guardar y retornar
        return viviendaRepository.save(vivienda);
    }

    /**
     * READ - Obtiene una vivienda por ID
     */
    public ViviendaEntity obtenerViviendaPorId(Long id) {
        return viviendaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vivienda no encontrada con ID: " + id));
    }

    /**
     * READ - Obtiene todas las viviendas
     */
    public List<ViviendaEntity> obtenerTodasLasViviendas() {
        return viviendaRepository.findAll();
    }

    /**
     * READ - Obtiene viviendas disponibles por ciudad
     */
    public List<ViviendaEntity> obtenerViviendasDisponiblesPorCiudad(String ciudad) {
        return viviendaRepository.findByCiudadAndDisponible(ciudad, true);
    }

    /**
     * UPDATE - Actualiza una vivienda existente validando reglas de negocio
     * 
     * Reglas aplicadas:
     * - precioMensual puede modificarse pero debe mantenerse positivo
     * - disponible cambia a false cuando tenga estudiante arrendatario activo
     */
    public ViviendaEntity actualizarVivienda(Long id, ViviendaEntity viviendaActualizada) {
        // Verificar que la vivienda existe
        ViviendaEntity viviendaExistente = obtenerViviendaPorId(id);

        // Validar campos obligatorios
        validarCamposObligatorios(viviendaActualizada);

        // Validar valores numéricos si fueron modificados
        validarValoresNumericos(viviendaActualizada);

        // Validar habitaciones y baños
        validarHabitacionesYBanos(viviendaActualizada);

        // Validar tipo de vivienda
        validarTipoVivienda(viviendaActualizada);

        // Validar que el propietario exista si fue cambiado
        if (viviendaActualizada.getPropietario() != null) {
            validarPropietarioExiste(viviendaActualizada.getPropietario().getId());
        }

        // Actualizar campos
        viviendaExistente.setDireccion(viviendaActualizada.getDireccion());
        viviendaExistente.setCiudad(viviendaActualizada.getCiudad());
        viviendaExistente.setBarrio(viviendaActualizada.getBarrio());
        viviendaExistente.setPrecioMensual(viviendaActualizada.getPrecioMensual());
        viviendaExistente.setDescripcion(viviendaActualizada.getDescripcion());
        viviendaExistente.setNumeroHabitaciones(viviendaActualizada.getNumeroHabitaciones());
        viviendaExistente.setNumeroBaños(viviendaActualizada.getNumeroBaños());
        viviendaExistente.setAreaMetrosCuadrados(viviendaActualizada.getAreaMetrosCuadrados());
        viviendaExistente.setTipo(viviendaActualizada.getTipo());

        // El campo disponible se gestiona con métodos específicos
        // No se actualiza directamente aquí para evitar inconsistencias

        return viviendaRepository.save(viviendaExistente);
    }

    /**
     * UPDATE - Marca una vivienda como no disponible
     * Se llama cuando hay un estudiante arrendatario activo
     */
    public ViviendaEntity marcarComoNoDisponible(Long viviendaId) {
        ViviendaEntity vivienda = obtenerViviendaPorId(viviendaId);
        vivienda.setDisponible(false);
        return viviendaRepository.save(vivienda);
    }

    /**
     * UPDATE - Marca una vivienda como disponible
     * Se llama cuando finaliza un arrendamiento
     */
    public ViviendaEntity marcarComoDisponible(Long viviendaId) {
        ViviendaEntity vivienda = obtenerViviendaPorId(viviendaId);
        vivienda.setDisponible(true);
        return viviendaRepository.save(vivienda);
    }

    /**
     * DELETE - Elimina una vivienda
     * 
     * Regla aplicada:
     * - Solo se puede eliminar si disponible = true (no está arrendada)
     */
    public void eliminarVivienda(Long id) {
        ViviendaEntity vivienda = obtenerViviendaPorId(id);

        // Validar que la vivienda esté disponible
        if (!vivienda.getDisponible()) {
            throw new IllegalStateException(
                    "No se puede eliminar la vivienda con ID " + id +
                            " porque está actualmente arrendada (disponible = false)");
        }

        viviendaRepository.deleteById(id);
    }

    // ==================== MÉTODOS DE VALIDACIÓN ====================

    /**
     * Valida que los campos obligatorios no estén vacíos
     * Regla: propietario, dirección, ciudad y barrio no pueden estar vacíos
     */
    private void validarCamposObligatorios(ViviendaEntity vivienda) {
        if (vivienda.getPropietario() == null) {
            throw new IllegalArgumentException("El campo 'propietario' no puede estar vacío");
        }

        if (vivienda.getDireccion() == null || vivienda.getDireccion().trim().isEmpty()) {
            throw new IllegalArgumentException("El campo 'dirección' no puede estar vacío");
        }

        if (vivienda.getCiudad() == null || vivienda.getCiudad().trim().isEmpty()) {
            throw new IllegalArgumentException("El campo 'ciudad' no puede estar vacío");
        }

        if (vivienda.getBarrio() == null || vivienda.getBarrio().trim().isEmpty()) {
            throw new IllegalArgumentException("El campo 'barrio' no puede estar vacío");
        }
    }

    /**
     * Valida que el propietario exista en la base de datos
     */
    private void validarPropietarioExiste(Long propietarioId) {
        if (!propietarioRepository.existsById(propietarioId)) {
            throw new IllegalArgumentException(
                    "El propietario con ID " + propietarioId + " no existe en el sistema");
        }
    }

    /**
     * Valida que los valores numéricos sean positivos
     * Regla: precioMensual y areaMetrosCuadrados deben ser mayores a cero
     */
    private void validarValoresNumericos(ViviendaEntity vivienda) {
        if (vivienda.getPrecioMensual() == null ||
                vivienda.getPrecioMensual().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "El campo 'precioMensual' debe ser un valor numérico mayor a cero");
        }

        if (vivienda.getAreaMetrosCuadrados() != null &&
                vivienda.getAreaMetrosCuadrados() <= 0) {
            throw new IllegalArgumentException(
                    "El campo 'areaMetrosCuadrados' debe ser un valor numérico mayor a cero");
        }
    }

    /**
     * Valida habitaciones y baños mínimo 1
     * Regla: numeroHabitaciones y numeroBaños deben tener un valor mínimo de 1
     */
    private void validarHabitacionesYBanos(ViviendaEntity vivienda) {
        if (vivienda.getNumeroHabitaciones() < 1) {
            throw new IllegalArgumentException(
                    "El campo 'numeroHabitaciones' debe tener un valor mínimo de 1");
        }

        if (vivienda.getNumeroBaños() < 1) {
            throw new IllegalArgumentException(
                    "El campo 'numeroBaños' debe tener un valor mínimo de 1");
        }
    }

    /**
     * Valida que el tipo de vivienda esté especificado
     * Regla: El campo tipo debe especificar claramente el tipo de vivienda
     */
    private void validarTipoVivienda(ViviendaEntity vivienda) {
        if (vivienda.getTipo() == null) {
            throw new IllegalArgumentException(
                    "El campo 'tipo' debe especificar claramente el tipo de vivienda " +
                            "(APARTAMENTO, CASA, HABITACION, ESTUDIO, COMPARTIDO)");
        }
    }
}