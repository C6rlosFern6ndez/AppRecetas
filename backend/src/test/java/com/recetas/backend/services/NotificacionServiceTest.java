package com.recetas.backend.services;

import com.recetas.backend.dtos.NotificacionDto;
import com.recetas.backend.exceptions.ResourceNotFoundException;
import com.recetas.backend.models.Notificacion;
import com.recetas.backend.models.Usuario;
import com.recetas.backend.repositories.NotificacionRepository;
import com.recetas.backend.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificacionServiceTest {

    @Mock
    private NotificacionRepository notificacionRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private NotificacionService notificacionService;

    private Usuario usuario;
    private Notificacion notificacion;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1);
        usuario.setNombreUsuario("testuser");

        notificacion = new Notificacion();
        notificacion.setId(1);
        notificacion.setUsuario(usuario);
        notificacion.setLeida(false);
        notificacion.setFechaCreacion(LocalDateTime.now());
    }

    @Test
    void testGetNotificacionesByUsuario() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Notificacion> notificacionPage = new PageImpl<>(Collections.singletonList(notificacion), pageable, 1);

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(notificacionRepository.findByUsuario(usuario, pageable)).thenReturn(notificacionPage);

        Page<NotificacionDto> result = notificacionService.getNotificacionesByUsuario(1, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().get(0).getId());
    }

    @Test
    void testMarcarComoLeidaSuccess() {
        when(notificacionRepository.findById(1)).thenReturn(Optional.of(notificacion));
        when(notificacionRepository.save(any(Notificacion.class))).thenReturn(notificacion);

        NotificacionDto result = notificacionService.marcarComoLeida(1, 1);

        assertNotNull(result);
        assertTrue(result.isLeida());
    }

    @Test
    void testMarcarComoLeidaForbidden() {
        when(notificacionRepository.findById(1)).thenReturn(Optional.of(notificacion));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            notificacionService.marcarComoLeida(1, 2); // Usuario incorrecto
        });

        assertEquals("No tienes permiso para modificar esta notificación.", exception.getMessage());
    }

    @Test
    void testMarcarTodasComoLeidas() {
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(notificacionRepository.findByUsuarioAndLeidaFalse(usuario))
                .thenReturn(Collections.singletonList(notificacion));

        notificacionService.marcarTodasComoLeidas(1);

        verify(notificacionRepository, times(1)).save(any(Notificacion.class));
    }

    @Test
    void testDeleteNotificacionSuccess() {
        when(notificacionRepository.findById(1)).thenReturn(Optional.of(notificacion));
        doNothing().when(notificacionRepository).delete(notificacion);

        notificacionService.deleteNotificacion(1, 1);

        verify(notificacionRepository, times(1)).delete(notificacion);
    }

    @Test
    void testDeleteNotificacionForbidden() {
        when(notificacionRepository.findById(1)).thenReturn(Optional.of(notificacion));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            notificacionService.deleteNotificacion(1, 2); // Usuario incorrecto
        });

        assertEquals("No tienes permiso para eliminar esta notificación.", exception.getMessage());
    }
}
