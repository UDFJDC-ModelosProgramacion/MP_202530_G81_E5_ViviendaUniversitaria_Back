package co.edu.udistrital.mdp.back.services;

import co.edu.udistrital.mdp.back.entities.ContratoEntity;
import co.edu.udistrital.mdp.back.entities.EstanciaEntity;
import co.edu.udistrital.mdp.back.repositories.ContratoRepository;
import co.edu.udistrital.mdp.back.repositories.EstanciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class ContratoService {

    private final ContratoRepository contratoRepo;
    private final EstanciaRepository estanciaRepo;

    /**
     * CREATE - crear contrato validando:
     * - codigo obligatorio (<=50), unico
     * - estancia obligatoria y sin otro contrato (1:1)
     * - fechaInicio/fechaFin obligatorias y fechaFin > fechaInicio
     * - montoTotal >= 0
     */
    public ContratoEntity crearContrato(ContratoEntity in) {
        if (in == null) throw new IllegalArgumentException("Entidad Contrato es obligatoria");

        String codigo = in.getCodigo() == null ? "" : in.getCodigo().trim();
        if (codigo.isBlank()) throw new IllegalArgumentException("codigo obligatorio para Contrato");
        if (codigo.length() > 50) throw new IllegalArgumentException("codigo excede 50 caracteres");
        if (contratoRepo.existsByCodigoIgnoreCase(codigo)) throw new IllegalArgumentException("Ya existe un contrato con ese codigo");

        if (in.getEstancia() == null || in.getEstancia().getId() == null) {
            throw new IllegalArgumentException("Estancia obligatoria para crear Contrato");
        }
        Long estanciaId = in.getEstancia().getId();
        EstanciaEntity estancia = estanciaRepo.findById(estanciaId)
                .orElseThrow(() -> new IllegalArgumentException("Estancia no encontrada con ID: " + estanciaId));

        // la estancia no debe tener ya contrato
        if (estancia.getContrato() != null || contratoRepo.existsByEstancia_Id(estanciaId)) {
            throw new IllegalStateException("La estancia ya tiene un contrato asociado");
        }

        if (in.getFechaInicio() == null || in.getFechaFin() == null) {
            throw new IllegalArgumentException("fechaInicio y fechaFin son obligatorias");
        }
        if (!in.getFechaFin().isAfter(in.getFechaInicio())) {
            throw new IllegalArgumentException("fechaFin debe ser posterior a fechaInicio");
        }

        if (in.getMontoTotal() == null || in.getMontoTotal() < 0) {
            throw new IllegalArgumentException("montoTotal debe ser >= 0");
        }

        // Asociar estancia persistente y guardar
        in.setEstancia(estancia);
        ContratoEntity saved = contratoRepo.save(in);

        // Asegurar relación inversa (estancia -> contrato)
        estancia.setContrato(saved);
        estanciaRepo.save(estancia);

        return saved;
    }

    /**
     * READ - obtener por id
     */
    public ContratoEntity obtenerPorId(Long id) {
        return contratoRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Contrato no encontrado con ID: " + id));
    }

    /**
     * UPDATE - no permitir cambiar estancia. Mantener codigo unico y reglas temporales
     */
    public ContratoEntity actualizar(Long id, ContratoEntity updates) {
        ContratoEntity found = obtenerPorId(id);

        // No permitir cambiar estancia
        if (updates.getEstancia() != null && updates.getEstancia().getId() != null &&
                !updates.getEstancia().getId().equals(found.getEstancia().getId())) {
            throw new IllegalStateException("No está permitido cambiar la estancia del contrato");
        }

        String nuevoCodigo = updates.getCodigo() == null ? "" : updates.getCodigo().trim();
        if (nuevoCodigo.isBlank()) throw new IllegalArgumentException("codigo obligatorio");
        if (nuevoCodigo.length() > 50) throw new IllegalArgumentException("codigo excede 50 caracteres");
        if (!nuevoCodigo.equalsIgnoreCase(found.getCodigo()) && contratoRepo.existsByCodigoIgnoreCase(nuevoCodigo)) {
            throw new IllegalArgumentException("codigo ya en uso por otro contrato");
        }
        found.setCodigo(nuevoCodigo);

        if (updates.getFechaInicio() == null || updates.getFechaFin() == null) {
            throw new IllegalArgumentException("fechaInicio y fechaFin son obligatorias");
        }
        if (!updates.getFechaFin().isAfter(updates.getFechaInicio())) {
            throw new IllegalArgumentException("fechaFin debe ser posterior a fechaInicio");
        }
        found.setFechaInicio(updates.getFechaInicio());
        found.setFechaFin(updates.getFechaFin());

        if (updates.getMontoTotal() == null || updates.getMontoTotal() < 0) {
            throw new IllegalArgumentException("montoTotal debe ser >= 0");
        }
        found.setMontoTotal(updates.getMontoTotal());

        return contratoRepo.save(found);
    }

    /**
     * DELETE - no eliminar contratos vigentes (fechaFin >= hoy)
     */
    public void eliminar(Long id) {
        ContratoEntity found = obtenerPorId(id);
        LocalDate hoy = LocalDate.now();
        // si fechaFin >= hoy => vigente
        if (!found.getFechaFin().isBefore(hoy)) {
            throw new IllegalStateException("No se puede eliminar un contrato vigente o con fechaFin >= hoy");
        }

        // desasociar estancia si corresponde
        EstanciaEntity est = found.getEstancia();
        if (est != null) {
            est.setContrato(null);
            estanciaRepo.save(est);
        }

        contratoRepo.delete(found);
    }

    /**
     * READ - obtener todos los contratos
     */ 
    
    public java.util.List<ContratoEntity> obtenerTodos() {
        return contratoRepo.findAll();
    }
}
