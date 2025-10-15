package co.edu.udistrital.mdp.back.services;

import co.edu.udistrital.mdp.back.entities.TransaccionEntity;
import co.edu.udistrital.mdp.back.repositories.TransaccionRepository;
import co.edu.udistrital.mdp.back.repositories.EstanciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TransaccionService {

    private final TransaccionRepository transaccionRepository;
    private final EstanciaRepository estanciaRepository;

    /**
     * CREATE - Registra una nueva transacción financiera.
     *
     * Reglas aplicadas:
     * - Debe estar asociada a una Estancia existente.
     * - El monto debe ser mayor que cero.
     * - metodoPago y estado no pueden ser nulos o vacíos.
     * - fechaTransaccion se establece automáticamente.
     */
    public TransaccionEntity crearTransaccion(TransaccionEntity transaccion) {
        validarEstanciaAsociada(transaccion);
        validarCamposObligatorios(transaccion);

        transaccion.setFechaTransaccion(LocalDateTime.now());
        
        return transaccionRepository.save(transaccion);
    }

    /**
     * READ - Obtiene una transacción por su ID.
     */
    public TransaccionEntity obtenerTransaccionPorId(Long id) {
        return transaccionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transacción no encontrada con ID: " + id));
    }

    /**
     * READ - Obtiene todas las transacciones de una Estancia.
     */
    public List<TransaccionEntity> obtenerTransaccionesPorEstancia(Long estanciaId) {
        return transaccionRepository.findByEstanciaId(estanciaId);
    }

    /**
     * UPDATE - Actualiza el estado de una transacción.
     *
     * Reglas aplicadas:
     * - Solo se permite modificar el estado.
     * - El monto y la estancia no son modificables.
     */
    public TransaccionEntity actualizarEstadoTransaccion(Long id, String nuevoEstado) {
        TransaccionEntity transaccion = obtenerTransaccionPorId(id);
        
        if (nuevoEstado == null || nuevoEstado.trim().isEmpty()) {
             throw new IllegalArgumentException("El nuevo estado no puede ser vacío.");
        }

        transaccion.setEstado(nuevoEstado);
        return transaccionRepository.save(transaccion);
    }

    /**
     * DELETE - No implementado por regla de negocio.
     * Las transacciones no se eliminan para mantener el historial financiero.
     */
    public void eliminarTransaccion(Long id) {
        throw new UnsupportedOperationException("Las transacciones no pueden ser eliminadas.");
    }


    private void validarEstanciaAsociada(TransaccionEntity transaccion) {
        if (transaccion.getEstancia() == null || transaccion.getEstancia().getId() == null) {
            throw new IllegalArgumentException("La transacción debe estar asociada a una estancia.");
        }
        if (!estanciaRepository.existsById(transaccion.getEstancia().getId())) {
            throw new IllegalArgumentException("La estancia con ID " + transaccion.getEstancia().getId() + " no existe.");
        }
    }

    private void validarCamposObligatorios(TransaccionEntity transaccion) {
        if (transaccion.getMonto() == null || transaccion.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El 'monto' de la transacción debe ser mayor que cero.");
        }
        if (transaccion.getMetodoPago() == null || transaccion.getMetodoPago().trim().isEmpty()) {
            throw new IllegalArgumentException("El 'metodoPago' no puede ser vacío.");
        }
        if (transaccion.getEstado() == null || transaccion.getEstado().trim().isEmpty()) {
            throw new IllegalArgumentException("El 'estado' de la transacción no puede ser vacío.");
        }
    }
}