package com.recetas.backend.service.impl;

import com.recetas.backend.domain.entity.Calificacion;
import com.recetas.backend.domain.entity.Receta;
import com.recetas.backend.domain.entity.Usuario;
import com.recetas.backend.domain.repository.CalificacionRepository;
import com.recetas.backend.domain.repository.RecetaRepository;
import com.recetas.backend.domain.repository.UsuarioRepository;
import com.recetas.backend.service.CalificacionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalificacionServiceImplTest {

    @Mock
    private CalificacionRepository calificacionRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RecetaRepository recetaRepository;

    @InjectMocks
    private CalificacionService calificacionService;

    private Usuario usuario;
    private Receta receta;
    private Calificacion calificacion;

    @BeforeEach
    void setUp() {
        calificacionService = new CalificacionServiceImpl(calificacionRepository, usuarioRepository, recetaRepository);

        usuario = new Usuario();
        usuario.setId(1);
        usuario.setNombreUsuario("testUser");
        usuario.setEmail("test@example.com");
        usuario.setContrasena("password");

        receta = new Receta();
        receta.setId(10);
        receta.setTitulo("Test Receta");
        receta.setDescripcion("Una receta de prueba");
        receta.setUsuario(usuario);

        calificacion = new Calificacion();
        calificacion.setId(20);
        calificacion.setPuntuacion(5);
        calificacion.setUsuario(usuario);
        calificacion.setReceta(receta);
    }

    @Test
    void calificarReceta_success_create() {
        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
        when(recetaRepository.findById(receta.getId())).thenReturn(Optional.of(receta));
        when(calificacionRepository.findByUsuarioIdAndRecetaId(usuario.getId(), receta.getId()))
                .thenReturn(Optional.empty());
        when(calificacionRepository.save(any(Calificacion.class))).thenReturn(calificacion);

        Calificacion createdCalificacion = calificacionService.calificarReceta(usuario.getId(), receta.getId(), 5);

        assertNotNull(createdCalificacion);
        assertEquals(5, createdCalificacion.getPuntuacion());
        assertEquals(usuario.getId(), createdCalificacion.getUsuario().getId());
        assertEquals(receta.getId(), createdCalificacion.getReceta().getId());

        verify(usuarioRepository, times(1)).findById(usuario.getId());
        verify(recetaRepository, times(1)).findById(receta.getId());
        verify(calificacionRepository, times(1)).findByUsuarioIdAndRecetaId(usuario.getId(), receta.getId());
        verify(calificacionRepository, times(1)).save(any(Calificacion.class));
    }

    @Test
    void calificarReceta_success_update() {
        Calificacion existingCalificacion = new Calificacion();
        existingCalificacion.setId(20);
        existingCalificacion.setPuntuacion(3);
        existingCalificacion.setUsuario(usuario);
        existingCalificacion.setReceta(receta);

        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
        when(recetaRepository.findById(receta.getId())).thenReturn(Optional.of(receta));
        when(calificacionRepository.findByUsuarioIdAndRecetaId(usuario.getId(), receta.getId()))
                .thenReturn(Optional.of(existingCalificacion));
        when(calificacionRepository.save(any(Calificacion.class))).thenReturn(existingCalificacion); // Mock save to
                                                                                                     // return the
                                                                                                     // updated object

        Calificacion updatedCalificacion = calificacionService.calificarReceta(usuario.getId(), receta.getId(), 5);

        assertNotNull(updatedCalificacion);
        assertEquals(5, updatedCalificacion.getPuntuacion()); // New score
        assertEquals(usuario.getId(), updatedCalificacion.getUsuario().getId());
        assertEquals(receta.getId(), updatedCalificacion.getReceta().getId());

        verify(usuarioRepository, times(1)).findById(usuario.getId());
        verify(recetaRepository, times(1)).findById(receta.getId());
        verify(calificacionRepository, times(1)).findByUsuarioIdAndRecetaId(usuario.getId(), receta.getId());
        verify(calificacionRepository, times(1)).save(any(Calificacion.class));
    }

    @Test
    void calificarReceta_usuarioNotFound() {
        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> calificacionService.calificarReceta(usuario.getId(), receta.getId(), 5));
        assertEquals("Usuario no encontrado", exception.getMessage());

        verify(usuarioRepository, times(1)).findById(usuario.getId());
        verifyNoInteractions(recetaRepository, calificacionRepository);
    }

    @Test
    void calificarReceta_recetaNotFound() {
        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
        when(recetaRepository.findById(receta.getId())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> calificacionService.calificarReceta(usuario.getId(), receta.getId(), 5));
        assertEquals("Receta no encontrada", exception.getMessage());

        verify(usuarioRepository, times(1)).findById(usuario.getId());
        verify(recetaRepository, times(1)).findById(receta.getId());
        verifyNoInteractions(calificacionRepository);
    }

    @Test
    void calificarReceta_invalidPuntuacion_tooLow() {
        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
        when(recetaRepository.findById(receta.getId())).thenReturn(Optional.of(receta));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> calificacionService.calificarReceta(usuario.getId(), receta.getId(), 0));
        assertEquals("La puntuación debe estar entre 1 y 5.", exception.getMessage());

        verify(usuarioRepository, times(1)).findById(usuario.getId());
        verify(recetaRepository, times(1)).findById(receta.getId());
        verifyNoInteractions(calificacionRepository);
    }

    @Test
    void calificarReceta_invalidPuntuacion_tooHigh() {
        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
        when(recetaRepository.findById(receta.getId())).thenReturn(Optional.of(receta));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> calificacionService.calificarReceta(usuario.getId(), receta.getId(), 6));
        assertEquals("La puntuación debe estar entre 1 y 5.", exception.getMessage());

        verify(usuarioRepository, times(1)).findById(usuario.getId());
        verify(recetaRepository, times(1)).findById(receta.getId());
        verifyNoInteractions(calificacionRepository);
    }

    @Test
    void eliminarCalificacion_success() {
        when(calificacionRepository.findByUsuarioIdAndRecetaId(usuario.getId(), receta.getId()))
                .thenReturn(Optional.of(calificacion));
        doNothing().when(calificacionRepository).delete(calificacion);

        assertDoesNotThrow(() -> calificacionService.eliminarCalificacion(usuario.getId(), receta.getId()));

        verify(calificacionRepository, times(1)).findByUsuarioIdAndRecetaId(usuario.getId(), receta.getId());
        verify(calificacionRepository, times(1)).delete(calificacion);
    }

    @Test
    void eliminarCalificacion_notFound() {
        when(calificacionRepository.findByUsuarioIdAndRecetaId(usuario.getId(), receta.getId()))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> calificacionService.eliminarCalificacion(usuario.getId(), receta.getId()));
        assertEquals("No se encontró una calificación para este usuario y receta.", exception.getMessage());

        verify(calificacionRepository, times(1)).findByUsuarioIdAndRecetaId(usuario.getId(), receta.getId());
        verifyNoInteractions(usuarioRepository, recetaRepository);
    }

    @Test
    void findById_success() {
        when(calificacionRepository.findById(calificacion.getId())).thenReturn(Optional.of(calificacion));
        Calificacion foundCalificacion = calificacionService.findById(calificacion.getId());
        assertNotNull(foundCalificacion);
        assertEquals(calificacion.getId(), foundCalificacion.getId());
        verify(calificacionRepository, times(1)).findById(calificacion.getId());
    }

    @Test
    void findById_notFound() {
        when(calificacionRepository.findById(anyInt())).thenReturn(Optional.empty());
        Calificacion foundCalificacion = calificacionService.findById(99);
        assertNull(foundCalificacion);
        verify(calificacionRepository, times(1)).findById(anyInt());
    }
}
