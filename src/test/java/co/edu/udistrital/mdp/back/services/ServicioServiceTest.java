package co.edu.udistrital.mdp.back.services;

import co.edu.udistrital.mdp.back.entities.ServicioEntity;
import co.edu.udistrital.mdp.back.entities.ServicioEntity.CategoriaServicio;
import co.edu.udistrital.mdp.back.entities.ViviendaEntity;
import co.edu.udistrital.mdp.back.repositories.ServicioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas unitarias de ServicioService")
class ServicioServiceTest {

    @Mock
    private ServicioRepository servicioRepository;

    @InjectMocks
    private ServicioService servicioService;

    private ServicioEntity servicioValido;

    @BeforeEach
    void setUp() {
        servicioValido = new ServicioEntity();
        servicioValido.setId(1L);
        servicioValido.setNombre("Internet");
        servicioValido.setDescripcion("Conexión de alta velocidad");
        servicioValido.setIcono("wifi-icon.png");
        servicioValido.setCategoria(CategoriaServicio.CONECTIVIDAD);
        servicioValido.setViviendas(new ArrayList<>());
    }

    // ==================== PRUEBAS DE CREAR SERVICIO ====================

    @Test
    @DisplayName("Crear servicio con datos válidos - debería crear correctamente")
    void crearServicio_ConDatosValidos_DeberiaCrearServicio() {
        // Arrange
        when(servicioRepository.findByNombre("Internet")).thenReturn(Optional.empty());
        when(servicioRepository.save(any(ServicioEntity.class))).thenReturn(servicioValido);

        // Act
        ServicioEntity resultado = servicioService.crearServicio(servicioValido);

        // Assert
        assertNotNull(resultado);
        assertEquals("Internet", resultado.getNombre());
        assertEquals(CategoriaServicio.CONECTIVIDAD, resultado.getCategoria());
        verify(servicioRepository).save(servicioValido);
    }

    @Test
    @DisplayName("Crear servicio con nombre en minúsculas - debería normalizar a capitalizado")
    void crearServicio_ConNombreMinusculas_DeberiaNormalizarNombre() {
        // Arrange
        ServicioEntity servicio = new ServicioEntity();
        servicio.setNombre("internet");
        servicio.setCategoria(CategoriaServicio.CONECTIVIDAD);
        servicio.setIcono("icon.png");

        when(servicioRepository.findByNombre("internet")).thenReturn(Optional.empty());
        when(servicioRepository.save(any(ServicioEntity.class))).thenReturn(servicio);

        // Act
        servicioService.crearServicio(servicio);

        // Assert
        assertEquals("Internet", servicio.getNombre());
    }

    @Test
    @DisplayName("Crear servicio sin lista de viviendas - debería inicializar lista vacía")
    void crearServicio_SinListaViviendas_DeberiaInicializarLista() {
        // Arrange
        ServicioEntity servicio = new ServicioEntity();
        servicio.setNombre("Agua");
        servicio.setCategoria(CategoriaServicio.BASICO);
        servicio.setIcono("icon.png");
        servicio.setViviendas(null);

        when(servicioRepository.findByNombre("Agua")).thenReturn(Optional.empty());
        when(servicioRepository.save(any(ServicioEntity.class))).thenReturn(servicio);

        // Act
        servicioService.crearServicio(servicio);

        // Assert
        assertNotNull(servicio.getViviendas());
        assertTrue(servicio.getViviendas().isEmpty());
    }

    @Test
    @DisplayName("Crear servicio con nombre nulo - debería lanzar excepción")
    void crearServicio_ConNombreNulo_DeberiaLanzarExcepcion() {
        // Arrange
        servicioValido.setNombre(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> servicioService.crearServicio(servicioValido));
        assertTrue(exception.getMessage().contains("nombre"));
        verify(servicioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Crear servicio con nombre vacío - debería lanzar excepción")
    void crearServicio_ConNombreVacio_DeberiaLanzarExcepcion() {
        // Arrange
        servicioValido.setNombre("   ");

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> servicioService.crearServicio(servicioValido));
        assertTrue(exception.getMessage().contains("nombre"));
        verify(servicioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Crear servicio con nombre duplicado - debería lanzar excepción")
    void crearServicio_ConNombreDuplicado_DeberiaLanzarExcepcion() {
        // Arrange
        ServicioEntity existente = new ServicioEntity();
        existente.setId(2L);
        existente.setNombre("Internet");

        when(servicioRepository.findByNombre("Internet")).thenReturn(Optional.of(existente));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> servicioService.crearServicio(servicioValido));
        assertTrue(exception.getMessage().contains("único"));
        verify(servicioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Crear servicio sin categoría - debería lanzar excepción")
    void crearServicio_SinCategoria_DeberiaLanzarExcepcion() {
        // Arrange
        servicioValido.setCategoria(null);
        when(servicioRepository.findByNombre(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> servicioService.crearServicio(servicioValido));
        assertTrue(exception.getMessage().contains("categoria"));
        verify(servicioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Crear servicio con descripción mayor a 500 caracteres - debería lanzar excepción")
    void crearServicio_ConDescripcionMuyLarga_DeberiaLanzarExcepcion() {
        // Arrange
        servicioValido.setDescripcion("A".repeat(501));
        when(servicioRepository.findByNombre(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> servicioService.crearServicio(servicioValido));
        assertTrue(exception.getMessage().contains("500 caracteres"));
        verify(servicioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Crear servicio con viviendas asociadas - debería lanzar excepción")
    void crearServicio_ConViviendasAsociadas_DeberiaLanzarExcepcion() {
        // Arrange
        ViviendaEntity vivienda = new ViviendaEntity();
        vivienda.setId(1L);
        servicioValido.getViviendas().add(vivienda);

        when(servicioRepository.findByNombre(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> servicioService.crearServicio(servicioValido));
        assertTrue(exception.getMessage().contains("asociado a Viviendas"));
        verify(servicioRepository, never()).save(any());
    }

    // ==================== PRUEBAS DE OBTENER SERVICIO ====================

    @Test
    @DisplayName("Obtener servicio por ID existente - debería retornar servicio")
    void obtenerServicioPorId_ConIdExistente_DeberiaRetornarServicio() {
        // Arrange
        when(servicioRepository.findById(1L)).thenReturn(Optional.of(servicioValido));

        // Act
        ServicioEntity resultado = servicioService.obtenerServicioPorId(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Internet", resultado.getNombre());
        verify(servicioRepository).findById(1L);
    }

    @Test
    @DisplayName("Obtener servicio por ID inexistente - debería lanzar excepción")
    void obtenerServicioPorId_ConIdInexistente_DeberiaLanzarExcepcion() {
        // Arrange
        when(servicioRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> servicioService.obtenerServicioPorId(999L));
        assertTrue(exception.getMessage().contains("no encontrado"));
    }

    @Test
    @DisplayName("Obtener servicio por nombre existente - debería retornar servicio")
    void obtenerServicioPorNombre_ConNombreExistente_DeberiaRetornarServicio() {
        // Arrange
        when(servicioRepository.findByNombre("Internet")).thenReturn(Optional.of(servicioValido));

        // Act
        ServicioEntity resultado = servicioService.obtenerServicioPorNombre("Internet");

        // Assert
        assertNotNull(resultado);
        assertEquals("Internet", resultado.getNombre());
    }

    @Test
    @DisplayName("Obtener servicio por nombre inexistente - debería lanzar excepción")
    void obtenerServicioPorNombre_ConNombreInexistente_DeberiaLanzarExcepcion() {
        // Arrange
        when(servicioRepository.findByNombre("NoExiste")).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> servicioService.obtenerServicioPorNombre("NoExiste"));
        assertTrue(exception.getMessage().contains("no encontrado"));
    }

    // ==================== PRUEBAS DE LISTAR SERVICIOS ====================

    @Test
    @DisplayName("Obtener todos los servicios - debería retornar lista")
    void obtenerTodosLosServicios_DeberiaRetornarLista() {
        // Arrange
        ServicioEntity servicio2 = new ServicioEntity();
        servicio2.setId(2L);
        servicio2.setNombre("Agua");
        servicio2.setCategoria(CategoriaServicio.BASICO);

        List<ServicioEntity> servicios = Arrays.asList(servicioValido, servicio2);
        when(servicioRepository.findAll()).thenReturn(servicios);

        // Act
        List<ServicioEntity> resultado = servicioService.obtenerTodosLosServicios();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(servicioRepository).findAll();
    }

    @Test
    @DisplayName("Obtener servicios por categoría - debería retornar lista filtrada")
    void obtenerServiciosPorCategoria_DeberiaRetornarListaFiltrada() {
        // Arrange
        List<ServicioEntity> servicios = Arrays.asList(servicioValido);
        when(servicioRepository.findByCategoria(CategoriaServicio.CONECTIVIDAD))
                .thenReturn(servicios);

        // Act
        List<ServicioEntity> resultado = servicioService.obtenerServiciosPorCategoria(
                CategoriaServicio.CONECTIVIDAD);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(CategoriaServicio.CONECTIVIDAD, resultado.get(0).getCategoria());
    }

    // ==================== PRUEBAS DE ACTUALIZAR SERVICIO ====================

    @Test
    @DisplayName("Actualizar servicio con datos válidos - debería actualizar correctamente")
    void actualizarServicio_ConDatosValidos_DeberiaActualizar() {
        // Arrange
        ServicioEntity actualizacion = new ServicioEntity();
        actualizacion.setNombre("Internet Fibra");
        actualizacion.setDescripcion("Fibra óptica de alta velocidad");
        actualizacion.setIcono("fiber-icon.png");

        when(servicioRepository.findById(1L)).thenReturn(Optional.of(servicioValido));
        when(servicioRepository.findByNombre("Internet Fibra")).thenReturn(Optional.empty());
        when(servicioRepository.save(any(ServicioEntity.class))).thenReturn(servicioValido);

        // Act
        ServicioEntity resultado = servicioService.actualizarServicio(1L, actualizacion);

        // Assert
        assertEquals("Internet fibra", resultado.getNombre());
        assertEquals("Fibra óptica de alta velocidad", resultado.getDescripcion());
        assertEquals("fiber-icon.png", resultado.getIcono());
        verify(servicioRepository).save(servicioValido);
    }

    @Test
    @DisplayName("Actualizar servicio con mismo nombre - no debería validar duplicado")
    void actualizarServicio_ConMismoNombre_NoDeberiaValidarDuplicado() {
        // Arrange
        ServicioEntity actualizacion = new ServicioEntity();
        actualizacion.setNombre("Internet");
        actualizacion.setDescripcion("Nueva descripción");

        when(servicioRepository.findById(1L)).thenReturn(Optional.of(servicioValido));
        when(servicioRepository.save(any(ServicioEntity.class))).thenReturn(servicioValido);

        // Act
        ServicioEntity resultado = servicioService.actualizarServicio(1L, actualizacion);

        // Assert
        assertEquals("Nueva descripción", resultado.getDescripcion());
        verify(servicioRepository, never()).findByNombre(anyString());
    }

    @Test
    @DisplayName("Actualizar servicio con nombre duplicado - debería lanzar excepción")
    void actualizarServicio_ConNombreDuplicado_DeberiaLanzarExcepcion() {
        // Arrange
        ServicioEntity actualizacion = new ServicioEntity();
        actualizacion.setNombre("Agua");

        ServicioEntity otroServicio = new ServicioEntity();
        otroServicio.setId(2L);
        otroServicio.setNombre("Agua");

        when(servicioRepository.findById(1L)).thenReturn(Optional.of(servicioValido));
        when(servicioRepository.findByNombre("Agua")).thenReturn(Optional.of(otroServicio));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> servicioService.actualizarServicio(1L, actualizacion));
        assertTrue(exception.getMessage().contains("único"));
        verify(servicioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Actualizar servicio con nombre vacío - debería lanzar excepción")
    void actualizarServicio_ConNombreVacio_DeberiaLanzarExcepcion() {
        // Arrange
        ServicioEntity actualizacion = new ServicioEntity();
        actualizacion.setNombre("   ");

        when(servicioRepository.findById(1L)).thenReturn(Optional.of(servicioValido));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> servicioService.actualizarServicio(1L, actualizacion));
        assertTrue(exception.getMessage().contains("nombre"));
    }

    @Test
    @DisplayName("Actualizar servicio con descripción muy larga - debería lanzar excepción")
    void actualizarServicio_ConDescripcionMuyLarga_DeberiaLanzarExcepcion() {
        // Arrange
        ServicioEntity actualizacion = new ServicioEntity();
        actualizacion.setDescripcion("A".repeat(501));

        when(servicioRepository.findById(1L)).thenReturn(Optional.of(servicioValido));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> servicioService.actualizarServicio(1L, actualizacion));
        assertTrue(exception.getMessage().contains("500 caracteres"));
    }

    @Test
    @DisplayName("Actualizar servicio con icono inválido - debería lanzar excepción")
    void actualizarServicio_ConIconoInvalido_DeberiaLanzarExcepcion() {
        // Arrange
        ServicioEntity actualizacion = new ServicioEntity();
        actualizacion.setIcono("../../../malicious.png");

        when(servicioRepository.findById(1L)).thenReturn(Optional.of(servicioValido));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> servicioService.actualizarServicio(1L, actualizacion));
        assertTrue(exception.getMessage().contains("no permitidos"));
    }

    @Test
    @DisplayName("Actualizar servicio con icono vacío - debería lanzar excepción")
    void actualizarServicio_ConIconoVacio_DeberiaLanzarExcepcion() {
        // Arrange
        ServicioEntity actualizacion = new ServicioEntity();
        actualizacion.setIcono("   ");

        when(servicioRepository.findById(1L)).thenReturn(Optional.of(servicioValido));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> servicioService.actualizarServicio(1L, actualizacion));
        assertTrue(exception.getMessage().contains("icono"));
    }

    @Test
    @DisplayName("Actualizar solo descripción - debería actualizar correctamente")
    void actualizarDescripcion_DeberiaActualizar() {
        // Arrange
        when(servicioRepository.findById(1L)).thenReturn(Optional.of(servicioValido));
        when(servicioRepository.save(any(ServicioEntity.class))).thenReturn(servicioValido);

        // Act
        ServicioEntity resultado = servicioService.actualizarDescripcion(1L,
                "Nueva descripción actualizada");

        // Assert
        assertEquals("Nueva descripción actualizada", resultado.getDescripcion());
        verify(servicioRepository).save(servicioValido);
    }

    @Test
    @DisplayName("Actualizar solo icono - debería actualizar correctamente")
    void actualizarIcono_DeberiaActualizar() {
        // Arrange
        when(servicioRepository.findById(1L)).thenReturn(Optional.of(servicioValido));
        when(servicioRepository.save(any(ServicioEntity.class))).thenReturn(servicioValido);

        // Act
        ServicioEntity resultado = servicioService.actualizarIcono(1L, "nuevo-icono.png");

        // Assert
        assertEquals("nuevo-icono.png", resultado.getIcono());
        verify(servicioRepository).save(servicioValido);
    }

    @Test
    @DisplayName("Actualizar servicio inexistente - debería lanzar excepción")
    void actualizarServicio_ConIdInexistente_DeberiaLanzarExcepcion() {
        // Arrange
        ServicioEntity actualizacion = new ServicioEntity();
        when(servicioRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> servicioService.actualizarServicio(999L, actualizacion));
        assertTrue(exception.getMessage().contains("no encontrado"));
    }

    // ==================== PRUEBAS DE ELIMINAR SERVICIO ====================

    @Test
    @DisplayName("Eliminar servicio sin viviendas asociadas - debería eliminar")
    void eliminarServicio_SinViviendasAsociadas_DeberiaEliminar() {
        // Arrange
        when(servicioRepository.findById(1L)).thenReturn(Optional.of(servicioValido));

        // Act
        servicioService.eliminarServicio(1L);

        // Assert
        verify(servicioRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Eliminar servicio con viviendas asociadas - debería lanzar excepción")
    void eliminarServicio_ConViviendasAsociadas_DeberiaLanzarExcepcion() {
        // Arrange
        ViviendaEntity vivienda = new ViviendaEntity();
        vivienda.setId(1L);
        servicioValido.getViviendas().add(vivienda);

        when(servicioRepository.findById(1L)).thenReturn(Optional.of(servicioValido));

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> servicioService.eliminarServicio(1L));
        assertTrue(exception.getMessage().contains("asociado a"));
        verify(servicioRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Eliminar servicio inexistente - debería lanzar excepción")
    void eliminarServicio_ConIdInexistente_DeberiaLanzarExcepcion() {
        // Arrange
        when(servicioRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> servicioService.eliminarServicio(999L));
        assertTrue(exception.getMessage().contains("no encontrado"));
        verify(servicioRepository, never()).deleteById(any());
    }

    // ==================== PRUEBAS DE MÉTODOS AUXILIARES ====================

    @Test
    @DisplayName("Puede eliminar servicio sin viviendas - debería retornar true")
    void puedeEliminarServicio_SinViviendas_DeberiaRetornarTrue() {
        // Arrange
        when(servicioRepository.findById(1L)).thenReturn(Optional.of(servicioValido));

        // Act
        boolean resultado = servicioService.puedeEliminarServicio(1L);

        // Assert
        assertTrue(resultado);
    }

    @Test
    @DisplayName("Puede eliminar servicio con viviendas - debería retornar false")
    void puedeEliminarServicio_ConViviendas_DeberiaRetornarFalse() {
        // Arrange
        ViviendaEntity vivienda = new ViviendaEntity();
        vivienda.setId(1L);
        servicioValido.getViviendas().add(vivienda);

        when(servicioRepository.findById(1L)).thenReturn(Optional.of(servicioValido));

        // Act
        boolean resultado = servicioService.puedeEliminarServicio(1L);

        // Assert
        assertFalse(resultado);
    }

    @Test
    @DisplayName("Contar viviendas asociadas - debería retornar cantidad correcta")
    void contarViviendasAsociadas_DeberiaRetornarCantidad() {
        // Arrange
        ViviendaEntity vivienda1 = new ViviendaEntity();
        vivienda1.setId(1L);
        ViviendaEntity vivienda2 = new ViviendaEntity();
        vivienda2.setId(2L);

        servicioValido.getViviendas().add(vivienda1);
        servicioValido.getViviendas().add(vivienda2);

        when(servicioRepository.findById(1L)).thenReturn(Optional.of(servicioValido));

        // Act
        int resultado = servicioService.contarViviendasAsociadas(1L);

        // Assert
        assertEquals(2, resultado);
    }

    @Test
    @DisplayName("Contar viviendas sin asociaciones - debería retornar cero")
    void contarViviendasAsociadas_SinViviendas_DeberiaRetornarCero() {
        // Arrange
        when(servicioRepository.findById(1L)).thenReturn(Optional.of(servicioValido));

        // Act
        int resultado = servicioService.contarViviendasAsociadas(1L);

        // Assert
        assertEquals(0, resultado);
    }
}