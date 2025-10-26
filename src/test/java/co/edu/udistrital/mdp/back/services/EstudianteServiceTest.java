package co.edu.udistrital.mdp.back.services;

import co.edu.udistrital.mdp.back.entities.EstudianteEntity;
import co.edu.udistrital.mdp.back.repositories.EstudianteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream; 

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EstudianteServiceTest {

    @Mock
    private EstudianteRepository estudianteRepo;

    @InjectMocks
    private EstudianteService estudianteService;

    private EstudianteEntity estudianteValido;

    @BeforeEach
    void setUp() {
        estudianteValido = new EstudianteEntity();
        estudianteValido.setId(1L);
        estudianteValido.setNombre("Juan Pérez");
        estudianteValido.setCorreo("juan.perez@example.com");
        estudianteValido.setTelefono("3001234567");
        estudianteValido.setUniversidad("Universidad Distrital");
    }

    @Test
    @DisplayName("Crear estudiante con datos válidos")
    void crear_ConDatosValidos_DeberiaCrearEstudiante() {
        // Arrange
        EstudianteEntity nuevoEstudiante = new EstudianteEntity();
        nuevoEstudiante.setNombre("María López");
        nuevoEstudiante.setCorreo("maria.lopez@example.com");

        when(estudianteRepo.existsByCorreoIgnoreCase(anyString())).thenReturn(false);
        when(estudianteRepo.save(any(EstudianteEntity.class))).thenReturn(nuevoEstudiante);

        // Act
        EstudianteEntity resultado = estudianteService.crear(nuevoEstudiante);

        // Assert
        assertNotNull(resultado);
        assertEquals("María López", resultado.getNombre());
        assertEquals("maria.lopez@example.com", resultado.getCorreo());
        verify(estudianteRepo).existsByCorreoIgnoreCase("maria.lopez@example.com");
        verify(estudianteRepo).save(nuevoEstudiante);
    }

    @Test
    @DisplayName("Crear estudiante con nombre con espacios - debería hacer trim")
    void crear_ConNombreConEspacios_DeberiaNormalizarNombre() {
        // Arrange
        EstudianteEntity estudiante = new EstudianteEntity();
        estudiante.setNombre("  Pedro Gómez  ");
        estudiante.setCorreo("pedro@example.com");

        when(estudianteRepo.existsByCorreoIgnoreCase(anyString())).thenReturn(false);
        when(estudianteRepo.save(any(EstudianteEntity.class))).thenReturn(estudiante);

        // Act
        EstudianteEntity resultado = estudianteService.crear(estudiante);

        // Assert
        assertEquals("Pedro Gómez", resultado.getNombre());
        verify(estudianteRepo).save(estudiante);
    }

    @Test
    @DisplayName("Crear estudiante con entidad nula - debería lanzar excepción")
    void crear_ConEntidadNula_DeberiaLanzarExcepcion() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> estudianteService.crear(null));
        assertEquals("Entidad Estudiante es obligatoria", exception.getMessage());
    }

    private static Stream<Arguments> provideInvalidStudentDataForCreation() {
        return Stream.of(
            Arguments.of(null, "test@example.com", "Nombre obligatorio"), // Nombre nulo
            Arguments.of("   ", "test@example.com", "Nombre obligatorio"), // Nombre vacío
            Arguments.of("A".repeat(151), "test@example.com", "Nombre excede 150 caracteres"), // Nombre largo
            Arguments.of("Test", null, "Correo obligatorio"), // Correo nulo
            Arguments.of("Test", "   ", "Correo obligatorio"), // Correo vacío
            Arguments.of("Test", "correosinvalido.com", "Correo inválido"), // Correo sin @
            Arguments.of("Test", "a".repeat(140) + "@example.com", "Correo inválido") // Correo largo (ejemplo > 150)
        );
    }


    @ParameterizedTest(name = "[{index}] Nombre=\"{0}\", Correo=\"{1}\" -> Esperado=\"{2}\"")
    @MethodSource("provideInvalidStudentDataForCreation")
    @DisplayName("Crear estudiante con datos inválidos - debería lanzar IllegalArgumentException")
    void crear_ConDatosInvalidos_DeberiaLanzarExcepcion(String nombre, String correo, String mensajeEsperado) {
        // Arrange
        EstudianteEntity estudiante = new EstudianteEntity();
        estudiante.setNombre(nombre);
        estudiante.setCorreo(correo);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> estudianteService.crear(estudiante));
        assertEquals(mensajeEsperado, exception.getMessage());


        verify(estudianteRepo, never()).save(any(EstudianteEntity.class));
    }


    @Test
    @DisplayName("Crear estudiante con correo duplicado - debería lanzar excepción")
    void crear_ConCorreoDuplicado_DeberiaLanzarExcepcion() {
        // Arrange
        EstudianteEntity estudiante = new EstudianteEntity();
        estudiante.setNombre("Test");
        estudiante.setCorreo("existente@example.com");

        when(estudianteRepo.existsByCorreoIgnoreCase("existente@example.com")).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> estudianteService.crear(estudiante));
        assertEquals("Ya existe un estudiante con ese correo", exception.getMessage());
        verify(estudianteRepo, never()).save(any());
    }


    @Test
    @DisplayName("Obtener estudiante por ID existente - debería retornar estudiante")
    void obtenerPorId_ConIdExistente_DeberiaRetornarEstudiante() {
        // Arrange
        when(estudianteRepo.findById(1L)).thenReturn(Optional.of(estudianteValido));

        // Act
        EstudianteEntity resultado = estudianteService.obtenerPorId(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Juan Pérez", resultado.getNombre());
        verify(estudianteRepo).findById(1L);
    }

    @Test
    @DisplayName("Obtener estudiante por ID inexistente - debería lanzar excepción")
    void obtenerPorId_ConIdInexistente_DeberiaLanzarExcepcion() {
        // Arrange
        when(estudianteRepo.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> estudianteService.obtenerPorId(999L));
        assertEquals("Estudiante no encontrado con ID: 999", exception.getMessage());
    }

    @Test
    @DisplayName("Listar todos los estudiantes - debería retornar lista")
    void listarTodos_DeberiaRetornarListaDeEstudiantes() {
        // Arrange
        EstudianteEntity estudiante2 = new EstudianteEntity();
        estudiante2.setId(2L);
        estudiante2.setNombre("Ana García");
        estudiante2.setCorreo("ana@example.com");

        List<EstudianteEntity> estudiantes = Arrays.asList(estudianteValido, estudiante2);
        when(estudianteRepo.findAll()).thenReturn(estudiantes);

        // Act
        List<EstudianteEntity> resultado = estudianteService.listarTodos();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(estudianteRepo).findAll();
    }

    @Test
    @DisplayName("Listar todos sin estudiantes - debería retornar lista vacía")
    void listarTodos_SinEstudiantes_DeberiaRetornarListaVacia() {
        // Arrange
        when(estudianteRepo.findAll()).thenReturn(Arrays.asList());

        // Act
        List<EstudianteEntity> resultado = estudianteService.listarTodos();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("Actualizar estudiante con datos válidos - debería actualizar")
    void actualizar_ConDatosValidos_DeberiaActualizarEstudiante() {
        // Arrange
        EstudianteEntity updates = new EstudianteEntity();
        updates.setNombre("Juan Pérez Actualizado");
        updates.setTelefono("3009876543");
        updates.setUniversidad("Universidad Nacional");

        when(estudianteRepo.findById(1L)).thenReturn(Optional.of(estudianteValido));
        when(estudianteRepo.save(any(EstudianteEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        EstudianteEntity resultado = estudianteService.actualizar(1L, updates);

        // Assert
        assertEquals("Juan Pérez Actualizado", resultado.getNombre());
        assertEquals("3009876543", resultado.getTelefono());
        assertEquals("Universidad Nacional", resultado.getUniversidad());
        // Verifica que el correo NO cambió si no se pasó en 'updates'
        assertEquals("juan.perez@example.com", resultado.getCorreo());
        verify(estudianteRepo).save(estudianteValido); // Verifica que se guardó la entidad encontrada y modificada
    }

    @Test
    @DisplayName("Actualizar estudiante con nuevo correo válido - debería actualizar")
    void actualizar_ConNuevoCorreoValido_DeberiaActualizarCorreo() {
        // Arrange
        EstudianteEntity updates = new EstudianteEntity();
        updates.setNombre("Juan Pérez"); // Necesario para la validación del servicio
        updates.setCorreo("nuevo.correo@example.com");

        when(estudianteRepo.findById(1L)).thenReturn(Optional.of(estudianteValido));
        when(estudianteRepo.existsByCorreoIgnoreCase("nuevo.correo@example.com")).thenReturn(false);
        when(estudianteRepo.save(any(EstudianteEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        EstudianteEntity resultado = estudianteService.actualizar(1L, updates);

        // Assert
        assertEquals("nuevo.correo@example.com", resultado.getCorreo());
        verify(estudianteRepo).existsByCorreoIgnoreCase("nuevo.correo@example.com");
        verify(estudianteRepo).save(estudianteValido);
    }

    @Test
    @DisplayName("Actualizar estudiante con mismo correo - no debería validar duplicado")
    void actualizar_ConMismoCorreo_NoDeberiaValidarDuplicado() {
        // Arrange
        EstudianteEntity updates = new EstudianteEntity();
        updates.setNombre("Juan Pérez Actualizado"); // Nombre válido
        updates.setCorreo("juan.perez@example.com"); // Mismo correo

        when(estudianteRepo.findById(1L)).thenReturn(Optional.of(estudianteValido));
        when(estudianteRepo.save(any(EstudianteEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        estudianteService.actualizar(1L, updates);

        verify(estudianteRepo, never()).existsByCorreoIgnoreCase(anyString());
        verify(estudianteRepo).save(estudianteValido); 
        assertEquals("Juan Pérez Actualizado", estudianteValido.getNombre()); 
    }

    @Test
    @DisplayName("Actualizar estudiante con correo duplicado - debería lanzar excepción")
    void actualizar_ConCorreoDuplicado_DeberiaLanzarExcepcion() {
        // Arrange
        EstudianteEntity updates = new EstudianteEntity();
        updates.setNombre("Juan Pérez"); 
        updates.setCorreo("otro@example.com"); 

        when(estudianteRepo.findById(1L)).thenReturn(Optional.of(estudianteValido));
        when(estudianteRepo.existsByCorreoIgnoreCase("otro@example.com")).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> estudianteService.actualizar(1L, updates));
        assertEquals("Ya existe un estudiante con ese correo", exception.getMessage());
        verify(estudianteRepo, never()).save(any());
    }


    // --- Tests parametrizados para actualizar con datos inválidos ---

    private static Stream<Arguments> provideInvalidStudentDataForUpdate() {
        return Stream.of(
            Arguments.of(null, "nuevo@example.com", "Nombre obligatorio"), // Nombre nulo
            Arguments.of("   ", "nuevo@example.com", "Nombre obligatorio"), // Nombre vacío
            Arguments.of("A".repeat(151), "nuevo@example.com", "Nombre excede 150 caracteres"), // Nombre largo
            Arguments.of("Nombre Valido", "correo sin arroba", "Correo inválido"), // Correo inválido (sin @)
            Arguments.of("Nombre Valido", "a".repeat(140) + "@toolong.com", "Correo inválido") // Correo largo
        );
    }

    @ParameterizedTest(name = "[{index}] Update Nombre=\"{0}\", Correo=\"{1}\" -> Esperado=\"{2}\"")
    @MethodSource("provideInvalidStudentDataForUpdate")
    @DisplayName("Actualizar estudiante con datos inválidos - debería lanzar IllegalArgumentException")
    void actualizar_ConDatosInvalidos_DeberiaLanzarExcepcion(String nombre, String correo, String mensajeEsperado) {
        // Arrange
        EstudianteEntity updates = new EstudianteEntity();
        updates.setNombre(nombre);
        updates.setCorreo(correo); // Pasamos el correo para probar su validación

        when(estudianteRepo.findById(1L)).thenReturn(Optional.of(estudianteValido));
        // Mock para el caso de correo inválido (no llega a verificar existencia)
        // Mock para el caso de correo duplicado (se prueba en otro test)

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> estudianteService.actualizar(1L, updates));
        assertEquals(mensajeEsperado, exception.getMessage());
        verify(estudianteRepo, never()).save(any());
    }

    // --- Fin tests parametrizados para actualizar ---

    @Test
    @DisplayName("Actualizar estudiante inexistente - debería lanzar excepción")
    void actualizar_ConIdInexistente_DeberiaLanzarExcepcion() {
        // Arrange
        EstudianteEntity updates = new EstudianteEntity();
        updates.setNombre("Test Valido"); // Datos válidos para el DTO
        updates.setCorreo("valido@example.com");

        when(estudianteRepo.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> estudianteService.actualizar(999L, updates));
        assertEquals("Estudiante no encontrado con ID: 999", exception.getMessage());
        verify(estudianteRepo, never()).save(any());
    }

    @Test
    @DisplayName("Eliminar estudiante sin reservas ni estancias - debería eliminar")
    void eliminar_SinReservasNiEstancias_DeberiaEliminar() {
        // Arrange
        when(estudianteRepo.findById(1L)).thenReturn(Optional.of(estudianteValido));
        when(estudianteRepo.countReservasActivasByEstudianteId(1L)).thenReturn(0L);
        when(estudianteRepo.countEstanciasActivasByEstudianteId(1L)).thenReturn(0L);

        // Act
        estudianteService.eliminar(1L);

        // Assert
        verify(estudianteRepo).delete(estudianteValido);
    }

    @Test
    @DisplayName("Eliminar estudiante con reservas activas - debería lanzar excepción")
    void eliminar_ConReservasActivas_DeberiaLanzarExcepcion() {
        // Arrange
        when(estudianteRepo.findById(1L)).thenReturn(Optional.of(estudianteValido));
        when(estudianteRepo.countReservasActivasByEstudianteId(1L)).thenReturn(2L);
        // No necesitamos mockear countEstancias si la primera validación falla

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> estudianteService.eliminar(1L));
        assertEquals("No se puede eliminar: tiene reservas activas", exception.getMessage());
        verify(estudianteRepo, never()).delete(any());
    }

    @Test
    @DisplayName("Eliminar estudiante con estancias activas - debería lanzar excepción")
    void eliminar_ConEstanciasActivas_DeberiaLanzarExcepcion() {
        // Arrange
        when(estudianteRepo.findById(1L)).thenReturn(Optional.of(estudianteValido));
        when(estudianteRepo.countReservasActivasByEstudianteId(1L)).thenReturn(0L); // Pasa la primera validación
        when(estudianteRepo.countEstanciasActivasByEstudianteId(1L)).thenReturn(1L); // Falla aquí

        // Act & Assert
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> estudianteService.eliminar(1L));
        assertEquals("No se puede eliminar: tiene estancias activas", exception.getMessage());
        verify(estudianteRepo, never()).delete(any());
    }

    @Test
    @DisplayName("Eliminar estudiante inexistente - debería lanzar excepción")
    void eliminar_ConIdInexistente_DeberiaLanzarExcepcion() {
        // Arrange
        when(estudianteRepo.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> estudianteService.eliminar(999L));
        assertEquals("Estudiante no encontrado con ID: 999", exception.getMessage());
        verify(estudianteRepo, never()).delete(any());
    }
}