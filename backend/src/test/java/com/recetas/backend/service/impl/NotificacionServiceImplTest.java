package com.recetas.backend.service.impl;

import com.recetas.backend.domain.entity.Notificacion;
import com.recetas.backend.domain.entity.Receta;
import com.recetas.backend.domain.entity.Usuario;
import com.recetas.backend.domain.model.enums.TipoNotificacion;
import com.recetas.backend.domain.repository.NotificacionRepository;
import com.recetas.backend.domain.repository.RecetaRepository;
import com.recetas.backend.domain.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificacionServiceImplTest {

    @Mock
    private NotificacionRepository notificacionRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RecetaRepository recetaRepository;

    @InjectMocks
    private NotificacionServiceImpl notificacionService;

    private Usuario usuario;
    private Receta receta;
    private Notificacion notificacion;

    @BeforeEach
    void setUp() {
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

        notificacion = new Notificacion();
        notificacion.setId(30);
        notificacion.setUsuario(usuario);
        notificacion.setTipo(TipoNotificacion.NUEVO_COMENTARIO);
        notificacion.setMensaje("Nuevo comentario en tu receta.");
        notificacion.setLeida(false);
        notificacion.setFechaCreacion(LocalDateTime.now());
        notificacion.setReceta(receta);
        notificacion.setEmisor(usuario); // Assuming the user is also the sender for simplicity in some cases
    }

    @Test
    void crearNotificacion_success() {
        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(notificacion);

        Notificacion createdNotificacion = notificacionService.crearNotificacion(usuario.getId(),
                TipoNotificacion.NUEVO_COMENTARIO, "Nuevo comentario en tu receta.");

        assertNotNull(createdNotificacion);
        assertEquals(usuario.getId(), createdNotificacion.getUsuario().getId());
        assertEquals(TipoNotificacion.NUEVO_COMENTARIO, createdNotificacion.getTipo());
        assertEquals("Nuevo comentario en tu receta.", createdNotificacion.getMensaje());
        assertFalse(createdNotificacion.isLeida());

        verify(usuarioRepository, times(1)).findById(usuario.getId());
        verify(notificacionRepository, times(1)).save(any(Notificacion.class));
    }

    @Test
    void crearNotificacion_usuarioNotFound() {
        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> notificacionService
                .crearNotificacion(usuario.getId(), TipoNotificacion.NUEVO_COMENTARIO, "Test message"));
        assertEquals("Usuario no encontrado", exception.getMessage());

        verify(usuarioRepository, times(1)).findById(usuario.getId());
        verifyNoInteractions(notificacionRepository);
    }

    @Test
    void obtenerNotificacionesUsuario_success() {
        List<Notificacion> notificacionesList = new ArrayList<>();
        notificacionesList.add(notificacion);
        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
        when(notificacionRepository.findByUsuarioId(usuario.getId())).thenReturn(notificacionesList);

        List<Notificacion> result = notificacionService.obtenerNotificacionesUsuario(usuario.getId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(notificacion.getId(), result.get(0).getId());

        verify(usuarioRepository, times(1)).findById(usuario.getId());
        verify(notificacionRepository, times(1)).findByUsuarioId(usuario.getId());
    }

    @Test
    void obtenerNotificacionesUsuario_userNotFound() {
        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> notificacionService.obtenerNotificacionesUsuario(usuario.getId()));
        assertEquals("Usuario no encontrado", exception.getMessage());

        verify(usuarioRepository, times(1)).findById(usuario.getId());
        verifyNoInteractions(notificacionRepository);
    }

    @Test
    void obtenerNotificacionesUsuario_noNotifications() {
        List<Notificacion> emptyList = new ArrayList<>();
        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
        when(notificacionRepository.findByUsuarioId(usuario.getId())).thenReturn(emptyList);

        List<Notificacion> result = notificacionService.obtenerNotificacionesUsuario(usuario.getId());

        assertTrue(result.isEmpty());
        verify(usuarioRepository, times(1)).findById(usuario.getId());
        verify(notificacionRepository, times(1)).findByUsuarioId(usuario.getId());
    }

    @Test
    void marcarComoLeida_success() {
        when(notificacionRepository.findById(notificacion.getId())).thenReturn(Optional.of(notificacion));
        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(notificacion);

        notificacionService.marcarComoLeida(notificacion.getId());

        verify(notificacionRepository, times(1)).findById(notificacion.getId());
        verify(notificacionRepository, times(1)).save(any(Notificacion.class));
        assertTrue(notificacion.isLeida()); // Check if the state was updated
    }

    @Test
    void marcarComoLeida_notFound() {
        when(notificacionRepository.findById(notificacion.getId())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> notificacionService.marcarComoLeida(notificacion.getId()));
        assertEquals("Notificación no encontrada", exception.getMessage());

        verify(notificacionRepository, times(1)).findById(notificacion.getId());
        verifyNoInteractions(usuarioRepository, recetaRepository);
    }

    @Test
    void eliminarNotificacion_success() {
        when(notificacionRepository.existsById(notificacion.getId())).thenReturn(true);
        doNothing().when(notificacionRepository).deleteById(notificacion.getId());

        assertDoesNotThrow(() -> notificacionService.eliminarNotificacion(notificacion.getId()));

        verify(notificacionRepository, times(1)).existsById(notificacion.getId());
        verify(notificacionRepository, times(1)).deleteById(notificacion.getId());
    }

    @Test
    void eliminarNotificacion_notFound() {
        when(notificacionRepository.existsById(notificacion.getId())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> notificacionService.eliminarNotificacion(notificacion.getId()));
        assertEquals("Notificación no encontrada", exception.getMessage());

        verify(notificacionRepository, times(1)).existsById(notificacion.getId());
        verifyNoInteractions(usuarioRepository, recetaRepository);
    }
}
