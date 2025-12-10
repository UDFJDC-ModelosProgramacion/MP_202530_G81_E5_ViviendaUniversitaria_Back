package co.edu.udistrital.mdp.back.services;

import co.edu.udistrital.mdp.back.dto.ViviendaEstadisticasDTO;
import co.edu.udistrital.mdp.back.entities.ReservaEntity;
import co.edu.udistrital.mdp.back.entities.ViviendaEntity;
import co.edu.udistrital.mdp.back.repositories.ReservaRepository;
import co.edu.udistrital.mdp.back.repositories.ViviendaRepository;
import co.edu.udistrital.mdp.back.repositories.PropietarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ViviendaService {

    private final ViviendaRepository viviendaRepository;
    private final PropietarioRepository propietarioRepository;
    private final ReservaRepository reservaRepository;

    public ViviendaEntity crearVivienda(ViviendaEntity vivienda) {
        validarCamposObligatorios(vivienda);
        validarPropietarioExiste(vivienda.getPropietario().getId());
        validarValoresNumericos(vivienda);
        validarHabitacionesYBanos(vivienda);
        validarTipoVivienda(vivienda);
        vivienda.setDisponible(true);
        return viviendaRepository.save(vivienda);
    }

    public ViviendaEntity obtenerViviendaPorId(Long id) {
        return viviendaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vivienda no encontrada con ID: " + id));
    }

    public List<ViviendaEntity> obtenerTodasLasViviendas() {
        return viviendaRepository.findAll();
    }

    public List<ViviendaEntity> obtenerViviendasDisponiblesPorCiudad(String ciudad) {
        return viviendaRepository.findByCiudadAndDisponible(ciudad, true);
    }

    public List<ViviendaEntity> obtenerViviendasPorPropietario(Long propietarioId) {
        return viviendaRepository.findByPropietarioId(propietarioId);
    }

    public ViviendaEntity actualizarVivienda(Long id, ViviendaEntity viviendaActualizada) {
        ViviendaEntity viviendaExistente = obtenerViviendaPorId(id);
        validarCamposObligatorios(viviendaActualizada);
        validarValoresNumericos(viviendaActualizada);
        validarHabitacionesYBanos(viviendaActualizada);
        validarTipoVivienda(viviendaActualizada);

        if (viviendaActualizada.getPropietario() != null) {
            validarPropietarioExiste(viviendaActualizada.getPropietario().getId());
            viviendaExistente.setPropietario(viviendaActualizada.getPropietario());
        } else {
            throw new IllegalArgumentException("La actualización debe incluir un propietario válido.");
        }

        viviendaExistente.setDireccion(viviendaActualizada.getDireccion());
        viviendaExistente.setCiudad(viviendaActualizada.getCiudad());
        viviendaExistente.setBarrio(viviendaActualizada.getBarrio());
        viviendaExistente.setPrecioMensual(viviendaActualizada.getPrecioMensual());
        viviendaExistente.setDescripcion(viviendaActualizada.getDescripcion());
        viviendaExistente.setNumeroHabitaciones(viviendaActualizada.getNumeroHabitaciones());
        viviendaExistente.setNumeroBanos(viviendaActualizada.getNumeroBanos());
        viviendaExistente.setAreaMetrosCuadrados(viviendaActualizada.getAreaMetrosCuadrados());
        viviendaExistente.setTipo(viviendaActualizada.getTipo());

        return viviendaRepository.save(viviendaExistente);
    }

    public ViviendaEntity marcarComoNoDisponible(Long viviendaId) {
        ViviendaEntity vivienda = obtenerViviendaPorId(viviendaId);
        vivienda.setDisponible(false);
        return viviendaRepository.save(vivienda);
    }

    public ViviendaEntity marcarComoDisponible(Long viviendaId) {
        ViviendaEntity vivienda = obtenerViviendaPorId(viviendaId);
        vivienda.setDisponible(true);
        return viviendaRepository.save(vivienda);
    }

    public void eliminarVivienda(Long id) {
        ViviendaEntity vivienda = obtenerViviendaPorId(id);
        if (!vivienda.isDisponible()) {
            throw new IllegalStateException(
                    "No se puede eliminar la vivienda con ID " + id +
                            " porque está actualmente arrendada (disponible = false)");
        }
        viviendaRepository.deleteById(id);
    }

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

    private void validarPropietarioExiste(Long propietarioId) {
        if (!propietarioRepository.existsById(propietarioId)) {
            throw new IllegalArgumentException(
                    "El propietario con ID " + propietarioId + " no existe en el sistema");
        }
    }

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

    private void validarHabitacionesYBanos(ViviendaEntity vivienda) {
        if (vivienda.getNumeroHabitaciones() < 1) {
            throw new IllegalArgumentException(
                    "El campo 'numeroHabitaciones' debe tener un valor mínimo de 1");
        }
        if (vivienda.getNumeroBanos() < 1) {
            throw new IllegalArgumentException(
                    "El campo 'numeroBaños' debe tener un valor mínimo de 1");
        }
    }

    private void validarTipoVivienda(ViviendaEntity vivienda) {
        if (vivienda.getTipo() == null) {
            throw new IllegalArgumentException(
                    "El campo 'tipo' debe especificar claramente el tipo de vivienda " +
                            "(APARTAMENTO, CASA, HABITACION, ESTUDIO, COMPARTIDO)");
        }
    }

    public ViviendaEstadisticasDTO obtenerEstadisticas(Long viviendaId) {
        ViviendaEntity vivienda = obtenerViviendaPorId(viviendaId);
        ViviendaEstadisticasDTO estadisticas = new ViviendaEstadisticasDTO();

        // Calcular ocupación anual
        Double ocupacionAnual = calcularOcupacionAnual(viviendaId);
        estadisticas.setOcupacionAnual(ocupacionAnual);

        // Calcular ingresos
        BigDecimal ingresosMesActual = calcularIngresosMesActual(viviendaId, vivienda.getPrecioMensual());
        BigDecimal ingresosAnual = calcularIngresosAnual(viviendaId, vivienda.getPrecioMensual());
        estadisticas.setIngresosMesActual(ingresosMesActual);
        estadisticas.setIngresosAnual(ingresosAnual);

        return estadisticas;
    }

    private Double calcularOcupacionAnual(Long viviendaId) {
        List<ReservaEntity> reservas = reservaRepository.findByViviendaId(viviendaId);

        if (reservas.isEmpty()) {
            return 0.0;
        }

        LocalDate inicioAnio = LocalDate.now().withDayOfYear(1);
        LocalDate finAnio = LocalDate.now().withDayOfYear(LocalDate.now().lengthOfYear());

        long diasOcupados = reservas.stream()
                .filter(r -> r.getFechaFin() != null && r.getFechaInicio() != null)
                .mapToLong(r -> {
                    LocalDate inicio = r.getFechaInicio().isAfter(inicioAnio) ? r.getFechaInicio() : inicioAnio;
                    LocalDate fin = r.getFechaFin().isBefore(finAnio) ? r.getFechaFin() : finAnio;
                    return inicio.isBefore(fin) ? ChronoUnit.DAYS.between(inicio, fin) : 0;
                })
                .sum();

        long diasEnAnio = ChronoUnit.DAYS.between(inicioAnio, finAnio);
        return diasEnAnio > 0 ? (diasOcupados * 100.0) / diasEnAnio : 0.0;
    }

    private BigDecimal calcularIngresosMesActual(Long viviendaId, BigDecimal precioMensual) {
        LocalDate inicioMes = LocalDate.now().withDayOfMonth(1);
        LocalDate finMes = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

        List<ReservaEntity> reservasMes = reservaRepository.findByViviendaId(viviendaId)
                .stream()
                .filter(r -> r.getFechaInicio() != null && r.getFechaFin() != null)
                .filter(r -> !r.getFechaFin().isBefore(inicioMes) && !r.getFechaInicio().isAfter(finMes))
                .toList();

        return reservasMes.isEmpty() ? BigDecimal.ZERO : precioMensual;
    }

    private BigDecimal calcularIngresosAnual(Long viviendaId, BigDecimal precioMensual) {
        List<ReservaEntity> reservas = reservaRepository.findByViviendaId(viviendaId);

        LocalDate inicioAnio = LocalDate.now().withDayOfYear(1);
        LocalDate finAnio = LocalDate.now().withDayOfYear(LocalDate.now().lengthOfYear());

        long mesesOcupados = reservas.stream()
                .filter(r -> r.getFechaFin() != null && r.getFechaInicio() != null)
                .filter(r -> !r.getFechaFin().isBefore(inicioAnio) && !r.getFechaInicio().isAfter(finAnio))
                .count();

        return precioMensual.multiply(BigDecimal.valueOf(mesesOcupados));
    }
}