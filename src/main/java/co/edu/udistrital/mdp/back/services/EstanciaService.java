package co.edu.udistrital.mdp.back.services;

import co.edu.udistrital.mdp.back.entities.EstanciaEntity;
import co.edu.udistrital.mdp.back.entities.EstudianteEntity;
import co.edu.udistrital.mdp.back.entities.ViviendaEntity;
import co.edu.udistrital.mdp.back.repositories.EstanciaRepository;
import co.edu.udistrital.mdp.back.entities.EstanciaEntity.EstadoEstancia;
import java.time.LocalDateTime;
import java.util.List;
import co.edu.udistrital.mdp.back.repositories.EstudianteRepository;
import co.edu.udistrital.mdp.back.repositories.ViviendaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class EstanciaService {

    private final EstanciaRepository estanciaRepo;
    private final EstudianteRepository estudianteRepo;
    private final ViviendaRepository viviendaRepo;

    /**
     * CREATE - crear estancia validando:
     * - estudiante y vivienda existen
     * - tiempoEstancia >=1 (y opcional <=36)
     * - vivienda disponible == true
     * - estado inicializado en ACTIVA
     * - fechaInicio asignada automáticamente
     */
    public EstanciaEntity crearEstancia(EstanciaEntity in) {
        if (in == null)
            throw new IllegalArgumentException("Entidad Estancia es obligatoria");

        if (in.getEstudianteArrendador() == null || in.getEstudianteArrendador().getId() == null) {
            throw new IllegalArgumentException("estudianteArrendador obligatorio");
        }
        if (in.getViviendaArrendada() == null || in.getViviendaArrendada().getId() == null) {
            throw new IllegalArgumentException("viviendaArrendada obligatorio");
        }
        if (in.getTiempoEstancia() == null || in.getTiempoEstancia() < 1) {
            throw new IllegalArgumentException("tiempoEstancia debe ser >= 1");
        }
        if (in.getTiempoEstancia() > 36) {
            throw new IllegalArgumentException("tiempoEstancia no debe exceder 36 meses");
        }

        EstudianteEntity estudiante = estudianteRepo.findById(in.getEstudianteArrendador().getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Estudiante no encontrado con ID: " + in.getEstudianteArrendador().getId()));

        ViviendaEntity vivienda = viviendaRepo.findById(in.getViviendaArrendada().getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Vivienda no encontrada con ID: " + in.getViviendaArrendada().getId()));

        // Regla: la Vivienda debe estar disponible
        if (!Boolean.TRUE.equals(vivienda.getDisponible())) {
            throw new IllegalStateException("La vivienda no está disponible para arrendar");
        }

        // Inicializar y persistir
        EstanciaEntity nueva = new EstanciaEntity(estudiante, vivienda, in.getTiempoEstancia());
        EstanciaEntity saved = estanciaRepo.save(nueva);

        // Opcional: marcar vivienda como no disponible (si esa es la política)
        vivienda.setDisponible(false);
        viviendaRepo.save(vivienda);

        return saved;
    }

    /**
     * READ - obtener por id
     */
    public EstanciaEntity obtenerPorId(Long id) {
        return estanciaRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Estancia no encontrada con ID: " + id));
    }

    /**
     * READ - obtener estancias activas de un estudiante
     */
    public List<EstanciaEntity> obtenerEstanciasActivas(Long estudianteId) {
        return estanciaRepo.findByEstudianteArrendador_IdAndEstado(estudianteId, EstadoEstancia.ACTIVA);
    }

    /**
     * UPDATE - actualizar tiempoEstancia y (condicionalmente) vivienda
     * - No permitir cambiar vivienda si la estancia ya tiene contrato
     * - Mantener tiempoEstancia >=1
     */
    public EstanciaEntity actualizar(Long id, EstanciaEntity updates) {
        EstanciaEntity found = obtenerPorId(id);

        if (updates.getViviendaArrendada() != null && updates.getViviendaArrendada().getId() != null) {
            Long nuevaViviendaId = updates.getViviendaArrendada().getId();
            if (!nuevaViviendaId.equals(found.getViviendaArrendada().getId())) {
                // si ya tiene contrato => prohibido cambiar
                if (found.getContrato() != null) {
                    throw new IllegalStateException(
                            "No se permite cambiar viviendaArrendada cuando la estancia tiene contrato asociado");
                }
                ViviendaEntity nueva = viviendaRepo.findById(nuevaViviendaId)
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Vivienda nueva no encontrada con ID: " + nuevaViviendaId));
                if (!Boolean.TRUE.equals(nueva.getDisponible())) {
                    throw new IllegalStateException("La nueva vivienda no está disponible");
                }
                // actualizar disponibilidad: liberar antigua, ocupar nueva
                found.getViviendaArrendada().setDisponible(true);
                viviendaRepo.save(found.getViviendaArrendada());

                nueva.setDisponible(false);
                viviendaRepo.save(nueva);

                found.setViviendaArrendada(nueva);
            }
        }

        if (updates.getTiempoEstancia() != null) {
            if (updates.getTiempoEstancia() < 1)
                throw new IllegalArgumentException("tiempoEstancia debe ser >= 1");
            if (updates.getTiempoEstancia() > 36)
                throw new IllegalArgumentException("tiempoEstancia no debe exceder 36 meses");
            found.setTiempoEstancia(updates.getTiempoEstancia());
        }

        return estanciaRepo.save(found);
    }

    /**
     * UPDATE - completar estancia (marca como COMPLETADA y asigna fechaFin)
     * Se llama cuando termina el arrendamiento
     */
    public EstanciaEntity completarEstancia(Long id) {
        EstanciaEntity estancia = obtenerPorId(id);

        if (!estancia.getEstado().equals(EstadoEstancia.ACTIVA)) {
            throw new IllegalStateException(
                    "No se puede completar una estancia con estado: " + estancia.getEstado());
        }

        estancia.setEstado(EstadoEstancia.COMPLETADA);
        estancia.setFechaFin(LocalDateTime.now());

        // Liberar vivienda
        ViviendaEntity vivienda = estancia.getViviendaArrendada();
        vivienda.setDisponible(true);
        viviendaRepo.save(vivienda);

        return estanciaRepo.save(estancia);
    }

    /**
     * UPDATE - cancelar estancia (marca como CANCELADA y asigna fechaFin)
     */
    public EstanciaEntity cancelarEstancia(Long id) {
        EstanciaEntity estancia = obtenerPorId(id);

        if (estancia.getEstado().equals(EstadoEstancia.COMPLETADA)) {
            throw new IllegalStateException("No se puede cancelar una estancia ya completada");
        }

        estancia.setEstado(EstadoEstancia.CANCELADA);
        estancia.setFechaFin(LocalDateTime.now());

        // Liberar vivienda
        ViviendaEntity vivienda = estancia.getViviendaArrendada();
        vivienda.setDisponible(true);
        viviendaRepo.save(vivienda);

        return estanciaRepo.save(estancia);
    }

    /**
     * DELETE - eliminar estancia (prohibido si tiene contrato asociado)
     */
    public void eliminar(Long id) {
        EstanciaEntity found = obtenerPorId(id);
        if (found.getContrato() != null) {
            throw new IllegalStateException("No se puede eliminar la estancia: existe un contrato asociado");
        }

        // Liberar vivienda (marcar disponible)
        ViviendaEntity viv = found.getViviendaArrendada();
        if (viv != null) {
            viv.setDisponible(true);
            viviendaRepo.save(viv);
        }

        estanciaRepo.delete(found);
    }

    /**
     * READ - obtener todas las estancias
     */
    
    public java.util.List<EstanciaEntity> obtenerTodas() {
        return estanciaRepo.findAll();
    }
}
