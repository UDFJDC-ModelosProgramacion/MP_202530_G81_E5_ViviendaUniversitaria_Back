package co.edu.udistrital.mdp.back.services;

import co.edu.udistrital.mdp.back.entities.ServicioEntity;
import co.edu.udistrital.mdp.back.repositories.ServicioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Servicio que maneja la lógica de negocio para la entidad Servicio
 * Implementa las reglas de negocio definidas para CREATE, UPDATE y DELETE
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ServicioService {

    private final ServicioRepository servicioRepository;

    /**
     * Crea un nuevo servicio validando todas las reglas de negocio
     */
    public ServicioEntity crearServicio(ServicioEntity servicio) {
        // Validar nombre no vacío
        validarNombreNoVacio(servicio.getNombre());

        // Validar que el nombre sea único
        validarNombreUnico(servicio.getNombre(), null);

        // Validar que la categoría esté especificada
        validarCategoriaEspecificada(servicio);

        // Validar descripción
        validarDescripcion(servicio.getDescripcion());

        // Normalizar nombre (capitalizar primera letra)
        servicio.setNombre(normalizarNombre(servicio.getNombre()));

        // Inicializar lista de viviendas vacía
        if (servicio.getViviendas() == null) {
            servicio.setViviendas(new ArrayList<>());
        }

        // No se puede crear un servicio directamente asociado a viviendas
        if (!servicio.getViviendas().isEmpty()) {
            throw new IllegalStateException(
                    "No se puede crear un Servicio directamente asociado a Viviendas. " +
                            "La asociación debe hacerse posteriormente desde la Vivienda");
        }

        // Guardar y retornar
        return servicioRepository.save(servicio);
    }

    /**
     * Obtiene un servicio por ID
     */
    public ServicioEntity obtenerServicioPorId(Long id) {
        return servicioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Servicio no encontrado con ID: " + id));
    }

    /**
     * Obtiene todos los servicios
     */
    public List<ServicioEntity> obtenerTodosLosServicios() {
        return servicioRepository.findAll();
    }

    /**
     * Obtiene servicios por categoría
     */
    public List<ServicioEntity> obtenerServiciosPorCategoria(ServicioEntity.CategoriaServicio categoria) {
        return servicioRepository.findByCategoria(categoria);
    }

    /**
     * Busca un servicio por nombre
     */
    public ServicioEntity obtenerServicioPorNombre(String nombre) {
        return servicioRepository.findByNombre(nombre)
                .orElseThrow(() -> new IllegalArgumentException("Servicio no encontrado con nombre: " + nombre));
    }

    /**
     * Actualiza un servicio existente validando reglas de negocio
     */
    public ServicioEntity actualizarServicio(Long id, ServicioEntity servicioActualizado) {
        // Verificar que el servicio existe
        ServicioEntity servicioExistente = obtenerServicioPorId(id);

        // Validar nombre no vacío si se está cambiando
        if (servicioActualizado.getNombre() != null) {
            validarNombreNoVacio(servicioActualizado.getNombre());

            // Si el nombre cambió, validar que el nuevo sea único
            if (!servicioExistente.getNombre().equalsIgnoreCase(servicioActualizado.getNombre())) {
                validarNombreUnico(servicioActualizado.getNombre(), id);
                servicioExistente.setNombre(normalizarNombre(servicioActualizado.getNombre()));
            }
        }

        // Actualizar descripción libremente
        if (servicioActualizado.getDescripcion() != null) {
            validarDescripcion(servicioActualizado.getDescripcion());
            servicioExistente.setDescripcion(servicioActualizado.getDescripcion());
        }

        // Actualizar icono
        if (servicioActualizado.getIcono() != null) {
            validarIcono(servicioActualizado.getIcono());
            servicioExistente.setIcono(servicioActualizado.getIcono());
        }

        // Actualizar categoría si se especificó
        if (servicioActualizado.getCategoria() != null) {
            servicioExistente.setCategoria(servicioActualizado.getCategoria());
        }

        return servicioRepository.save(servicioExistente);
    }

    /**
     * Actualiza solo la descripción de un servicio
     */
    public ServicioEntity actualizarDescripcion(Long id, String nuevaDescripcion) {
        ServicioEntity servicio = obtenerServicioPorId(id);
        validarDescripcion(nuevaDescripcion);
        servicio.setDescripcion(nuevaDescripcion);
        return servicioRepository.save(servicio);
    }

    /**
     * Actualiza solo el icono de un servicio
     */
    public ServicioEntity actualizarIcono(Long id, String nuevoIcono) {
        ServicioEntity servicio = obtenerServicioPorId(id);
        validarIcono(nuevoIcono);
        servicio.setIcono(nuevoIcono);
        return servicioRepository.save(servicio);
    }

    /**
     * Elimina un servicio
     */
    public void eliminarServicio(Long id) {
        ServicioEntity servicio = obtenerServicioPorId(id);

        // Validar que no esté asociado a ninguna vivienda
        if (!servicio.getViviendas().isEmpty()) {
            throw new IllegalStateException(
                    "No se puede eliminar el Servicio con ID " + id +
                            " porque está asociado a " + servicio.getViviendas().size() + " vivienda(s). " +
                            "Primero debe desasociarlo de todas las viviendas");
        }

        servicioRepository.deleteById(id);
    }

    /**
     * Verifica si un servicio puede ser eliminado
     */
    public boolean puedeEliminarServicio(Long id) {
        ServicioEntity servicio = obtenerServicioPorId(id);
        return servicio.getViviendas().isEmpty();
    }

    /**
     * Cuenta cuántas viviendas están usando un servicio
     */
    public int contarViviendasAsociadas(Long servicioId) {
        ServicioEntity servicio = obtenerServicioPorId(servicioId);
        return servicio.getViviendas().size();
    }

    /**
     * Valida que el nombre no esté vacío
     */
    private void validarNombreNoVacio(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El campo 'nombre' no puede estar vacío");
        }
    }

    /**
     * Valida que el nombre sea único en el sistema
     */
    private void validarNombreUnico(String nombre, Long idActual) {
        // Buscar si existe otro servicio con el mismo nombre
        servicioRepository.findByNombre(nombre.trim()).ifPresent(servicioExistente -> {
            // Si estamos actualizando, permitir que sea el mismo servicio
            if (idActual == null || !servicioExistente.getId().equals(idActual)) {
                throw new IllegalArgumentException(
                        "Ya existe un servicio con el nombre '" + nombre + "' en el sistema. " +
                                "El nombre debe ser único");
            }
        });
    }

    /**
     * Valida que la categoría esté especificada
     */
    private void validarCategoriaEspecificada(ServicioEntity servicio) {
        if (servicio.getCategoria() == null) {
            throw new IllegalArgumentException(
                    "El campo 'categoria' debe especificar claramente la categoría a la que pertenece el servicio " +
                            "(BASICO, CONECTIVIDAD, MOBILIARIO, SEGURIDAD, RECREACION, ADICIONAL)");
        }
    }

    /**
     * Valida la descripción que no supere los 500 caracteres
     */
    private void validarDescripcion(String descripcion) {
        if (descripcion != null && descripcion.length() > 500) {
            throw new IllegalArgumentException(
                    "El campo 'descripcion' no puede superar los 500 caracteres. " +
                            "Longitud actual: " + descripcion.length());
        }
    }

    /**
     * Valida que el icono sea una ruta o referencia válida
     */
    private void validarIcono(String icono) {
        if (icono == null || icono.trim().isEmpty()) {
            throw new IllegalArgumentException("El campo 'icono' no puede estar vacío");
        }

        // Validación básica de formato (tal vez luego le agreguemos que solo acepte jpg
        // o png)
        String iconoTrim = icono.trim();

        // Validar que no tenga caracteres no permitidos
        if (iconoTrim.contains("..") || iconoTrim.contains("//")) {
            throw new IllegalArgumentException(
                    "El campo 'icono' contiene caracteres no permitidos");
        }
    }

    /**
     * Normaliza el nombre del servicio poniendo la primera letra en mayúscula
     */
    private String normalizarNombre(String nombre) {
        if (nombre == null || nombre.isEmpty()) {
            return nombre;
        }

        String nombreTrim = nombre.trim();
        return nombreTrim.substring(0, 1).toUpperCase() + nombreTrim.substring(1).toLowerCase();
    }
}