package co.edu.udistrital.mdp.back.services;

import co.edu.udistrital.mdp.back.dto.PropietarioDTO;
import co.edu.udistrital.mdp.back.entities.PropietarioEntity;
import co.edu.udistrital.mdp.back.exceptions.EntityNotFoundException;
import co.edu.udistrital.mdp.back.exceptions.ErrorMessage;
import co.edu.udistrital.mdp.back.repositories.PropietarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
// Importa ArgumentCaptor
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PropietarioServiceTest {

    @Mock
    private PropietarioRepository propietarioRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private PropietarioService propietarioService;

    private PropietarioEntity propietarioEntityValido;
    private PropietarioDTO propietarioDTOValido;
    private Long propietarioIdValido = 1L;

    @BeforeEach
    void setUp() {
        // --- Configuración de Entidad ---
        propietarioEntityValido = new PropietarioEntity();
        propietarioEntityValido.setId(propietarioIdValido);
        propietarioEntityValido.setNombre("Carlos");
        propietarioEntityValido.setApellido("Gómez");
        propietarioEntityValido.setDocumento("1022345678");
        propietarioEntityValido.setTipoDocumento("CC");
        propietarioEntityValido.setTelefono("3101234567");
        propietarioEntityValido.setEmail("carlos.gomez@example.com");
        propietarioEntityValido.setFechaRegistro(LocalDateTime.now().minusDays(1));

        // --- Configuración de DTO ---
        propietarioDTOValido = new PropietarioDTO();
        propietarioDTOValido.setId(propietarioIdValido);
        propietarioDTOValido.setNombre("Carlos");
        propietarioDTOValido.setApellido("Gómez");
        propietarioDTOValido.setDocumento("1022345678");
        propietarioDTOValido.setTipoDocumento("CC");
        propietarioDTOValido.setTelefono("3101234567");
        propietarioDTOValido.setEmail("carlos.gomez@example.com");
        propietarioDTOValido.setFechaRegistro(propietarioEntityValido.getFechaRegistro());

        // --- Mocks Comunes ---
        // Mock de mapeo DTO -> Entidad
        when(modelMapper.map(any(PropietarioDTO.class), eq(PropietarioEntity.class)))
            .thenAnswer(invocation -> {
                PropietarioDTO dto = invocation.getArgument(0);
                PropietarioEntity entity = new PropietarioEntity();
                entity.setNombre(dto.getNombre());
                entity.setApellido(dto.getApellido());
                entity.setDocumento(dto.getDocumento());
                entity.setTipoDocumento(dto.getTipoDocumento());
                entity.setTelefono(dto.getTelefono());
                entity.setEmail(dto.getEmail());
                return entity;
            });

        // Mock de mapeo Entidad -> DTO
        when(modelMapper.map(any(PropietarioEntity.class), eq(PropietarioDTO.class)))
            .thenAnswer(invocation -> {
                PropietarioEntity entity = invocation.getArgument(0);
                PropietarioDTO dto = new PropietarioDTO();
                dto.setId(entity.getId());
                dto.setNombre(entity.getNombre());
                dto.setApellido(entity.getApellido());
                dto.setDocumento(entity.getDocumento());
                dto.setTipoDocumento(entity.getTipoDocumento());
                dto.setTelefono(entity.getTelefono());
                dto.setEmail(entity.getEmail());
                dto.setFechaRegistro(entity.getFechaRegistro());
                return dto;
            });

        // Mock de findById (encontrado)
        when(propietarioRepository.findById(propietarioIdValido)).thenReturn(Optional.of(propietarioEntityValido));
        // Mock de findById (no encontrado)
        when(propietarioRepository.findById((999L))).thenReturn(Optional.empty());

        // Mock de existsById (encontrado)
        when(propietarioRepository.existsById(propietarioIdValido)).thenReturn(true);
        // Mock de existsById (no encontrado)
        when(propietarioRepository.existsById((999L))).thenReturn(false);

        // Mock de save
        when(propietarioRepository.save(any(PropietarioEntity.class))).thenAnswer(invocation -> {
            PropietarioEntity entity = invocation.getArgument(0);
            if (entity.getId() == null) {
                entity.setId(2L);
                entity.setFechaRegistro(LocalDateTime.now());
            }
            // Importante: Devolver el mismo objeto que se recibió para simular la actualización
            return entity;
        });
    }

    // --- Tests para crearPropietario ---

    @Test
    @DisplayName("Crear propietario con datos válidos")
    void crearPropietario_conDatosValidos_deberiaCrear() {
        // Arrange
        PropietarioDTO dtoNuevo = new PropietarioDTO();
        dtoNuevo.setNombre("Nuevo");
        dtoNuevo.setApellido("Propietario");
        dtoNuevo.setDocumento("9876543210");
        dtoNuevo.setEmail("nuevo@example.com");
        dtoNuevo.setTelefono("3219876543");
        dtoNuevo.setTipoDocumento("CE");

        when(propietarioRepository.existsByDocumento("9876543210")).thenReturn(false);

        // Act
        PropietarioDTO resultadoDTO = propietarioService.crearPropietario(dtoNuevo);

        // Assert
        assertNotNull(resultadoDTO);
        assertEquals(2L, resultadoDTO.getId());
        assertEquals("Nuevo", resultadoDTO.getNombre());
        assertEquals("9876543210", resultadoDTO.getDocumento());
        assertNotNull(resultadoDTO.getFechaRegistro());

        verify(propietarioRepository).existsByDocumento("9876543210");
        verify(propietarioRepository).save(any(PropietarioEntity.class));
        verify(modelMapper).map(any(PropietarioDTO.class), eq(PropietarioEntity.class));
        verify(modelMapper).map(any(PropietarioEntity.class), eq(PropietarioDTO.class));
    }

    @Test
    @DisplayName("Crear propietario con documento duplicado")
    void crearPropietario_conDocumentoDuplicado_deberiaLanzarExcepcion() {
        // Arrange
        PropietarioDTO dtoDuplicado = new PropietarioDTO();
        dtoDuplicado.setDocumento("1022345678");
        dtoDuplicado.setNombre("Otro");

        when(propietarioRepository.existsByDocumento("1022345678")).thenReturn(true);

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
            () -> propietarioService.crearPropietario(dtoDuplicado));

        assertThat(ex.getMessage()).contains("Ya existe un propietario con el documento: 1022345678");
        verify(propietarioRepository).existsByDocumento("1022345678");
        verify(propietarioRepository, never()).save(any());
        verify(modelMapper, never()).map(any(PropietarioDTO.class), eq(PropietarioEntity.class));
    }

    // --- Tests para getPropietario ---

    @Test
    @DisplayName("Obtener propietario por ID existente")
    void getPropietario_conIdExistente_deberiaRetornarDTO() throws EntityNotFoundException {
        // Arrange (findById mockeado en setup)

        // Act
        PropietarioDTO resultadoDTO = propietarioService.getPropietario(propietarioIdValido);

        // Assert
        assertNotNull(resultadoDTO);
        assertEquals(propietarioIdValido, resultadoDTO.getId());
        assertEquals(propietarioEntityValido.getNombre(), resultadoDTO.getNombre());

        verify(propietarioRepository).findById(propietarioIdValido);
        verify(modelMapper).map(propietarioEntityValido, PropietarioDTO.class);
    }

    @Test
    @DisplayName("Obtener propietario por ID inexistente")
    void getPropietario_conIdInexistente_deberiaLanzarEntityNotFound() {
        // Arrange
        Long idInexistente = 999L;
        // findById(999L) mockeado en setup para devolver empty

        // Act & Assert
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
            () -> propietarioService.getPropietario(idInexistente));

        assertEquals(ErrorMessage.PROPIETARIO_NOT_FOUND.getMessage(), ex.getMessage());
        verify(propietarioRepository).findById(idInexistente);
        verify(modelMapper, never()).map(any(), any());
    }

    // --- Tests para getPropietarios ---

    @Test
    @DisplayName("Obtener todos los propietarios")
    void getPropietarios_deberiaRetornarListaDTO() {
        // Arrange
        PropietarioEntity otroEntity = new PropietarioEntity();
        otroEntity.setId(3L);
        List<PropietarioEntity> listaEntities = Arrays.asList(propietarioEntityValido, otroEntity);
        when(propietarioRepository.findAll()).thenReturn(listaEntities);

        PropietarioDTO otroDTO = new PropietarioDTO();
        otroDTO.setId(3L);
        when(modelMapper.map(otroEntity, PropietarioDTO.class)).thenReturn(otroDTO);
        // El mapeo para propietarioEntityValido ya está implícito en el mock general del setup

        // Act
        List<PropietarioDTO> resultadoListaDTO = propietarioService.getPropietarios();

        // Assert
        assertNotNull(resultadoListaDTO);
        assertEquals(2, resultadoListaDTO.size());
        assertThat(resultadoListaDTO).extracting(PropietarioDTO::getId).containsExactlyInAnyOrder(propietarioIdValido, 3L);

        verify(propietarioRepository).findAll();
        verify(modelMapper, times(listaEntities.size())).map(any(PropietarioEntity.class), eq(PropietarioDTO.class));
    }

    // --- Tests para updatePropietario ---

    @Test
    @DisplayName("Actualizar propietario existente")
    void updatePropietario_conIdExistente_deberiaActualizarYRetornarDTO() throws EntityNotFoundException {
        // Arrange
        PropietarioDTO dtoActualizado = new PropietarioDTO();
        dtoActualizado.setNombre("Carlos Actualizado");
        dtoActualizado.setApellido("Gómez Update");
        dtoActualizado.setTelefono("3000000000");
        dtoActualizado.setEmail("carlos.update@example.com");
        dtoActualizado.setTipoDocumento("PA");

        // findById y save mockeados en setup.

        // Act
        PropietarioDTO resultadoDTO = propietarioService.updatePropietario(propietarioIdValido, dtoActualizado);

        // Assert
        assertNotNull(resultadoDTO);
        assertEquals(propietarioIdValido, resultadoDTO.getId());
        assertEquals("Carlos Actualizado", resultadoDTO.getNombre());
        assertEquals("3000000000", resultadoDTO.getTelefono());

        // --- Corrección: Usa ArgumentCaptor ---
        ArgumentCaptor<PropietarioEntity> propietarioCaptor = ArgumentCaptor.forClass(PropietarioEntity.class);
        verify(propietarioRepository).save(propietarioCaptor.capture());
        PropietarioEntity entidadGuardada = propietarioCaptor.getValue();

        assertEquals("Carlos Actualizado", entidadGuardada.getNombre());
        assertEquals("Gómez Update", entidadGuardada.getApellido());
        assertEquals("3000000000", entidadGuardada.getTelefono());
        assertEquals("carlos.update@example.com", entidadGuardada.getEmail());
        assertEquals("PA", entidadGuardada.getTipoDocumento());
        assertEquals(propietarioIdValido, entidadGuardada.getId());
        // --- Fin Corrección ---

        verify(propietarioRepository).findById(propietarioIdValido);
        verify(modelMapper).map(entidadGuardada, PropietarioDTO.class); // Verifica mapeo final con la entidad capturada
    }

    @Test
    @DisplayName("Actualizar propietario inexistente")
    void updatePropietario_conIdInexistente_deberiaLanzarEntityNotFound() {
        // Arrange
        PropietarioDTO dtoActualizado = new PropietarioDTO();
        Long idInexistente = 999L;
        // findById(999L) mockeado en setup

        // Act & Assert
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
            () -> propietarioService.updatePropietario(idInexistente, dtoActualizado));

        assertEquals(ErrorMessage.PROPIETARIO_NOT_FOUND.getMessage(), ex.getMessage());
        verify(propietarioRepository).findById(idInexistente);
        verify(propietarioRepository, never()).save(any());
    }

    // --- Tests para deletePropietario ---

    @Test
    @DisplayName("Eliminar propietario existente")
    void deletePropietario_conIdExistente_deberiaLlamarDeleteById() throws EntityNotFoundException {
        // Arrange
        // existsById mockeado en setup
        doNothing().when(propietarioRepository).deleteById(propietarioIdValido);

        // Act
        propietarioService.deletePropietario(propietarioIdValido);

        // Assert
        verify(propietarioRepository).existsById(propietarioIdValido);
        verify(propietarioRepository).deleteById(propietarioIdValido);
    }

    @Test
    @DisplayName("Eliminar propietario inexistente")
    void deletePropietario_conIdInexistente_deberiaLanzarEntityNotFound() {
        // Arrange
        Long idInexistente = 999L;
        // existsById(999L) mockeado en setup

        // Act & Assert
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
            () -> propietarioService.deletePropietario(idInexistente));

        assertEquals(ErrorMessage.PROPIETARIO_NOT_FOUND.getMessage(), ex.getMessage());
        verify(propietarioRepository).existsById(idInexistente);
        verify(propietarioRepository, never()).deleteById(anyLong());
    }
}