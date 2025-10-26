package co.edu.udistrital.mdp.back.services;

import co.edu.udistrital.mdp.back.entities.EstudianteEntity;
import co.edu.udistrital.mdp.back.repositories.EstudianteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EstudianteServiceTest {

    @Mock
    private EstudianteRepository estudianteRepo;

    @InjectMocks
    private EstudianteService estudianteService;

    private EstudianteEntity estPersistido;

    @BeforeEach
    void setup() {
        estPersistido = new EstudianteEntity();
        estPersistido.setId(5L);
        estPersistido.setNombre("Juan Perez");
        estPersistido.setCorreo("juan@uni.edu");

        when(estudianteRepo.save(any(EstudianteEntity.class))).thenAnswer(inv -> {
            EstudianteEntity e = inv.getArgument(0);
            if (e.getId() == null)
                e.setId(10L);
            return e;
        });

        when(estudianteRepo.findById(anyLong())).thenReturn(Optional.empty());
        when(estudianteRepo.findById(5L)).thenReturn(Optional.of(estPersistido));

        when(estudianteRepo.existsByCorreoIgnoreCase(anyString())).thenReturn(false);
        when(estudianteRepo.countReservasActivasByEstudianteId(anyLong())).thenReturn(0L);
        when(estudianteRepo.countEstanciasActivasByEstudianteId(anyLong())).thenReturn(0L);
    }

    @Test
    void crear_success() {
        EstudianteEntity in = new EstudianteEntity();
        in.setNombre("Laura Gómez");
        in.setCorreo("laura@uni.edu");

        EstudianteEntity creado = estudianteService.crear(in);

        assertThat(creado.getId()).isEqualTo(10L);
        assertThat(creado.getNombre()).isEqualTo("Laura Gómez");
        verify(estudianteRepo).existsByCorreoIgnoreCase("laura@uni.edu");
        verify(estudianteRepo).save(in);
    }

    @Test
    void crear_nombreVacio_debeLanzar() {
        EstudianteEntity in = new EstudianteEntity();
        in.setNombre(" ");
        in.setCorreo("test@uni.edu");

        var ex = assertThrows(IllegalArgumentException.class, () -> estudianteService.crear(in));
        assertThat(ex.getMessage()).contains("Nombre obligatorio");
        verify(estudianteRepo, never()).save(any());
    }

    @Test
    void crear_correoInvalido_debeLanzar() {
        EstudianteEntity in = new EstudianteEntity();
        in.setNombre("Maria");
        in.setCorreo("sin-arroba");

        var ex = assertThrows(IllegalArgumentException.class, () -> estudianteService.crear(in));
        assertThat(ex.getMessage()).contains("Correo inválido");
    }

    @Test
    void actualizar_cambiaNombreYCiudad() {
        EstudianteEntity updates = new EstudianteEntity();
        updates.setNombre("Nuevo Nombre");
        updates.setCorreo("juan@uni.edu"); // mismo correo, no cambia
        updates.setTelefono("3001234567");

        EstudianteEntity actualizado = estudianteService.actualizar(5L, updates);

        assertThat(actualizado.getNombre()).isEqualTo("Nuevo Nombre");
        assertThat(actualizado.getTelefono()).isEqualTo("3001234567");
        verify(estudianteRepo).save(estPersistido);
    }

    @Test
    void actualizar_correoDuplicado_debeLanzar() {
        EstudianteEntity updates = new EstudianteEntity();
        updates.setNombre("Otro");
        updates.setCorreo("nuevo@uni.edu");

        when(estudianteRepo.existsByCorreoIgnoreCase("nuevo@uni.edu")).thenReturn(true);

        var ex = assertThrows(IllegalArgumentException.class, () -> estudianteService.actualizar(5L, updates));
        assertThat(ex.getMessage()).contains("Ya existe un estudiante con ese correo");
        verify(estudianteRepo, never()).save(any());
    }

    @Test
    void eliminar_conReservasActivas_debeLanzar() {
        when(estudianteRepo.countReservasActivasByEstudianteId(5L)).thenReturn(2L);

        var ex = assertThrows(IllegalStateException.class, () -> estudianteService.eliminar(5L));
        assertThat(ex.getMessage()).contains("reservas activas");
        verify(estudianteRepo, never()).delete(any());
    }

    @Test
    void eliminar_conEstanciasActivas_debeLanzar() {
        when(estudianteRepo.countEstanciasActivasByEstudianteId(5L)).thenReturn(1L);

        var ex = assertThrows(IllegalStateException.class, () -> estudianteService.eliminar(5L));
        assertThat(ex.getMessage()).contains("estancias activas");
        verify(estudianteRepo, never()).delete(any());
    }

    @Test
    void eliminar_sinDependencias_debeEliminar() {
        estudianteService.eliminar(5L);

        verify(estudianteRepo).findById(5L);
        verify(estudianteRepo).delete(estPersistido);
    }
}
