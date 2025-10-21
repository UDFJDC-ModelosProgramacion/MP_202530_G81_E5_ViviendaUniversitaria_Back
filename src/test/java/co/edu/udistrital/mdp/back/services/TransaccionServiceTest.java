/* 
package co.edu.udistrital.mdp.back.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import co.edu.udistrital.mdp.back.entities.EstanciaEntity;
import co.edu.udistrital.mdp.back.entities.TransaccionEntity;
import co.edu.udistrital.mdp.back.repositories.EstanciaRepository;
import co.edu.udistrital.mdp.back.repositories.TransaccionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class TransaccionServiceTest {

    @Mock
    private TransaccionRepository transaccionRepository;

    @Mock
    private EstanciaRepository estanciaRepository;

    @InjectMocks
    private TransaccionService transaccionService;

    private PodamFactory factory = new PodamFactoryImpl();
    private List<TransaccionEntity> dataList = new ArrayList<>();
    private EstanciaEntity estanciaValida;

    @BeforeEach
    void setUp() {
        dataList.clear();
        estanciaValida = factory.manufacturePojo(EstanciaEntity.class);
        estanciaValida.setId(1L);

        for (int i = 0; i < 3; i++) {
            TransaccionEntity transaccion = factory.manufacturePojo(TransaccionEntity.class);
            transaccion.setEstancia(estanciaValida);
            transaccion.setMonto(BigDecimal.valueOf(100.0 * (i + 1))); // Asegura monto positivo
            transaccion.setEstado("Pendiente"); // Estado inicial
            transaccion.setMetodoPago("PSE"); // Asegura no vacío
            dataList.add(transaccion);
        }
    }

    // --- Tests para crearTransaccion (CREATE) ---

    @Test
    void testCrearTransaccionExitosa() {
        TransaccionEntity nuevaTransaccion = factory.manufacturePojo(TransaccionEntity.class);
        nuevaTransaccion.setEstancia(estanciaValida);
        nuevaTransaccion.setMonto(BigDecimal.TEN);
        nuevaTransaccion.setMetodoPago("Tarjeta");
        nuevaTransaccion.setEstado("Pendiente");
        nuevaTransaccion.setId(null);

        when(estanciaRepository.existsById(estanciaValida.getId())).thenReturn(true);
        when(transaccionRepository.save(any(TransaccionEntity.class))).thenAnswer(invocation -> {
            TransaccionEntity guardada = invocation.getArgument(0);
            guardada.setId(10L);
            guardada.setFechaTransaccion(LocalDateTime.now()); // Simula auto-asignación
            return guardada;
        });

        TransaccionEntity result = transaccionService.crearTransaccion(nuevaTransaccion);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals(BigDecimal.TEN, result.getMonto());
        assertNotNull(result.getFechaTransaccion());
        assertEquals(estanciaValida.getId(), result.getEstancia().getId());

        verify(estanciaRepository, times(1)).existsById(estanciaValida.getId());
        verify(transaccionRepository, times(1)).save(any(TransaccionEntity.class));
    }

    @Test
    void testCrearTransaccionSinEstancia() {
        TransaccionEntity transaccion = new TransaccionEntity();
        transaccion.setMonto(BigDecimal.ONE);
        transaccion.setEstancia(null);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            transaccionService.crearTransaccion(transaccion);
        });
        assertEquals("La transacción debe estar asociada a una estancia.", thrown.getMessage());
        verify(transaccionRepository, never()).save(any());
    }
    
    @Test
    void testCrearTransaccionEstanciaNoExiste() {
        TransaccionEntity transaccion = factory.manufacturePojo(TransaccionEntity.class);
        transaccion.setEstancia(estanciaValida);
        transaccion.setMonto(BigDecimal.TEN);
        transaccion.setMetodoPago("PSE");
        transaccion.setEstado("OK");

        when(estanciaRepository.existsById(estanciaValida.getId())).thenReturn(false);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            transaccionService.crearTransaccion(transaccion);
        });
        assertTrue(thrown.getMessage().contains("La estancia con ID"));
        assertTrue(thrown.getMessage().contains("no existe."));
        verify(transaccionRepository, never()).save(any());
    }

    @Test
    void testCrearTransaccionMontoInvalido() {
        TransaccionEntity transaccion = factory.manufacturePojo(TransaccionEntity.class);
        transaccion.setEstancia(estanciaValida);
        transaccion.setMonto(BigDecimal.ZERO); // Monto cero o negativo
        transaccion.setMetodoPago("PSE");
        transaccion.setEstado("OK");


        when(estanciaRepository.existsById(estanciaValida.getId())).thenReturn(true);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            transaccionService.crearTransaccion(transaccion);
        });
        assertEquals("El 'monto' de la transacción debe ser mayor que cero.", thrown.getMessage());
        verify(transaccionRepository, never()).save(any());
    }
    
    @Test
    void testCrearTransaccionMetodoPagoVacio() {
        TransaccionEntity transaccion = factory.manufacturePojo(TransaccionEntity.class);
        transaccion.setEstancia(estanciaValida);
        transaccion.setMonto(BigDecimal.TEN);
        transaccion.setMetodoPago(" "); // Vacío
        transaccion.setEstado("OK");

        when(estanciaRepository.existsById(estanciaValida.getId())).thenReturn(true);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            transaccionService.crearTransaccion(transaccion);
        });
         assertEquals("El 'metodoPago' no puede ser vacío.", thrown.getMessage());
        verify(transaccionRepository, never()).save(any());
    }
    
     @Test
    void testCrearTransaccionEstadoVacio() {
        TransaccionEntity transaccion = factory.manufacturePojo(TransaccionEntity.class);
        transaccion.setEstancia(estanciaValida);
        transaccion.setMonto(BigDecimal.TEN);
        transaccion.setMetodoPago("PSE");
        transaccion.setEstado(null); // Nulo

        when(estanciaRepository.existsById(estanciaValida.getId())).thenReturn(true);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            transaccionService.crearTransaccion(transaccion);
        });
        assertEquals("El 'estado' de la transacción no puede ser vacío.", thrown.getMessage());
        verify(transaccionRepository, never()).save(any());
    }


    // --- Tests para obtenerTransaccionPorId (READ ONE) ---
    @Test
    void testObtenerTransaccionPorIdExistente() {
        Long idBuscado = 1L;
        TransaccionEntity transaccionPrueba = dataList.get(0);
        transaccionPrueba.setId(idBuscado);

        when(transaccionRepository.findById(idBuscado)).thenReturn(Optional.of(transaccionPrueba));

        TransaccionEntity result = transaccionService.obtenerTransaccionPorId(idBuscado);

        assertNotNull(result);
        assertEquals(idBuscado, result.getId());
        verify(transaccionRepository, times(1)).findById(idBuscado);
    }

    @Test
    void testObtenerTransaccionPorIdNoExistente() {
        Long idBuscado = 99L;
        when(transaccionRepository.findById(idBuscado)).thenReturn(Optional.empty());

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            transaccionService.obtenerTransaccionPorId(idBuscado);
        });
        assertEquals("Transacción no encontrada con ID: " + idBuscado, thrown.getMessage());
        verify(transaccionRepository, times(1)).findById(idBuscado);
    }

    // --- Tests para obtenerTransaccionesPorEstancia (READ MANY) ---
    @Test
    void testObtenerTransaccionesPorEstancia() {
        Long estanciaId = estanciaValida.getId();
        when(transaccionRepository.findByEstanciaId(estanciaId)).thenReturn(dataList);

        List<TransaccionEntity> result = transaccionService.obtenerTransaccionesPorEstancia(estanciaId);

        assertNotNull(result);
        assertEquals(dataList.size(), result.size());
        verify(transaccionRepository, times(1)).findByEstanciaId(estanciaId);
    }

    // --- Tests para actualizarEstadoTransaccion (UPDATE) ---
    @Test
    void testActualizarEstadoTransaccionExitosa() {
        Long idTransaccion = 1L;
        String nuevoEstado = "Completada";
        TransaccionEntity transaccionExistente = dataList.get(0);
        transaccionExistente.setId(idTransaccion);
        transaccionExistente.setEstado("Pendiente");

        when(transaccionRepository.findById(idTransaccion)).thenReturn(Optional.of(transaccionExistente));
        when(transaccionRepository.save(any(TransaccionEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransaccionEntity result = transaccionService.actualizarEstadoTransaccion(idTransaccion, nuevoEstado);

        assertNotNull(result);
        assertEquals(nuevoEstado, result.getEstado());
        verify(transaccionRepository, times(1)).findById(idTransaccion);
        verify(transaccionRepository, times(1)).save(transaccionExistente);
    }

    @Test
    void testActualizarEstadoTransaccionNoEncontrada() {
        Long idTransaccion = 99L;
        String nuevoEstado = "Completada";
        when(transaccionRepository.findById(idTransaccion)).thenReturn(Optional.empty());

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            transaccionService.actualizarEstadoTransaccion(idTransaccion, nuevoEstado);
        });
        assertEquals("Transacción no encontrada con ID: " + idTransaccion, thrown.getMessage());
        verify(transaccionRepository, times(1)).findById(idTransaccion);
        verify(transaccionRepository, never()).save(any());
    }
    
    @Test
    void testActualizarEstadoTransaccionEstadoVacio() {
        Long idTransaccion = 1L;
        String nuevoEstado = " "; // Estado vacío
        TransaccionEntity transaccionExistente = dataList.get(0);
        transaccionExistente.setId(idTransaccion);

        when(transaccionRepository.findById(idTransaccion)).thenReturn(Optional.of(transaccionExistente));

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            transaccionService.actualizarEstadoTransaccion(idTransaccion, nuevoEstado);
        });
        assertEquals("El nuevo estado no puede ser vacío.", thrown.getMessage());
        verify(transaccionRepository, times(1)).findById(idTransaccion);
        verify(transaccionRepository, never()).save(any());
    }


    // --- Test para eliminarTransaccion (DELETE - Debe fallar) ---
    @Test
    void testEliminarTransaccionLanzaExcepcion() {
        Long idAEliminar = 1L;

        // Llama al método y espera la excepción definida
        UnsupportedOperationException thrown = assertThrows(UnsupportedOperationException.class, () -> {
            transaccionService.eliminarTransaccion(idAEliminar);
        });
        assertEquals("Las transacciones no pueden ser eliminadas.", thrown.getMessage());

        // Verifica que NUNCA se llame al método delete del repositorio
        verify(transaccionRepository, never()).deleteById(anyLong());
        verify(transaccionRepository, never()).delete(any());
    }
}
    
*/