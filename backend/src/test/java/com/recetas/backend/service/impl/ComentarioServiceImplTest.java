package com.recetas.backend.service.impl;

import com.recetas.backend.domain.entity.Comentario;
import com.recetas.backend.domain.entity.Receta;
import com.recetas.backend.domain.entity.Usuario;
import com.recetas.backend.domain.repository.ComentarioRepository;
import com.recetas.backend.domain.repository.RecetaRepository;
import com.recetas.backend.domain.repository.UsuarioRepository;
import com.recetas.backend.service.NotificacionService; // Importar NotificacionService
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
class ComentarioServiceImplTest {

    @Mock
    private ComentarioRepository comentarioRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RecetaRepository recetaRepository;

    @Mock
    private NotificacionService notificacionService; // Mock para NotificacionService

    @InjectMocks
    private ComentarioServiceImpl comentarioService;

    private Usuario usuario;
    private Receta receta;
    private Comentario comentario;

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

        comentario = new Comentario();
        comentario.setId(5);
        comentario.setComentario("Este es un comentario de prueba.");
        comentario.setUsuario(usuario);
        comentario.setReceta(receta);
    }

    @Test
    void crearComentario_success() {
        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
        when(recetaRepository.findById(receta.getId())).thenReturn(Optional.of(receta));
        when(comentarioRepository.save(any(Comentario.class))).thenReturn(comentario);
        // Mockear el comportamiento de crearNotificacion
        when(notificacionService.crearNotificacion(any(Integer.class), any(), any(Integer.class), any()))
                .thenReturn(null); // O devolver un objeto Notificacion mockeado si es necesario

        Comentario createdComentario = comentarioService.crearComentario("Este es un comentario de prueba.",
                usuario.getId(), receta.getId());

        assertNotNull(createdComentario);
        assertEquals("Este es un comentario de prueba.", createdComentario.getComentario());
        assertEquals(usuario.getId(), createdComentario.getUsuario().getId());
        assertEquals(receta.getId(), createdComentario.getReceta().getId());

        verify(usuarioRepository, times(1)).findById(usuario.getId());
        verify(recetaRepository, times(1)).findById(receta.getId());
        verify(comentarioRepository, times(1)).save(any(Comentario.class));
    }

    @Test
    void crearComentario_usuarioNotFound() {
        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> comentarioService.crearComentario("Test comment", usuario.getId(), receta.getId()));
        assertEquals("Usuario no encontrado", exception.getMessage());

        verify(usuarioRepository, times(1)).findById(usuario.getId());
        verifyNoInteractions(recetaRepository, comentarioRepository);
    }

    @Test
    void crearComentario_recetaNotFound() {
        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
        when(recetaRepository.findById(receta.getId())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> comentarioService.crearComentario("Test comment", usuario.getId(), receta.getId()));
        assertEquals("Receta no encontrada", exception.getMessage());

        verify(usuarioRepository, times(1)).findById(usuario.getId());
        verify(recetaRepository, times(1)).findById(receta.getId());
        verifyNoInteractions(comentarioRepository);
    }

    @Test
    void eliminarComentario_success() {
        when(comentarioRepository.findById(comentario.getId())).thenReturn(Optional.of(comentario));
        doNothing().when(comentarioRepository).deleteById(comentario.getId());

        assertDoesNotThrow(() -> comentarioService.eliminarComentario(comentario.getId(), usuario.getId()));

        verify(comentarioRepository, times(1)).findById(comentario.getId());
        verify(comentarioRepository, times(1)).deleteById(comentario.getId());
    }

    @Test
    void eliminarComentario_comentarioNotFound() {
        when(comentarioRepository.findById(comentario.getId())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> comentarioService.eliminarComentario(comentario.getId(), usuario.getId()));
        assertEquals("Comentario no encontrado", exception.getMessage());

        verify(comentarioRepository, times(1)).findById(comentario.getId());
        verifyNoInteractions(usuarioRepository, recetaRepository);
    }

    @Test
    void eliminarComentario_unauthorizedUser() {
        Usuario otherUser = new Usuario();
        otherUser.setId(2);
        otherUser.setNombreUsuario("otherUser");

        when(comentarioRepository.findById(comentario.getId())).thenReturn(Optional.of(comentario));
        comentario.setUsuario(usuario); // Ensure the comment is linked to the correct user

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> comentarioService.eliminarComentario(comentario.getId(), otherUser.getId()));
        assertEquals("No tienes permisos para eliminar este comentario.", exception.getMessage());

        verify(comentarioRepository, times(1)).findById(comentario.getId());
        verifyNoInteractions(usuarioRepository, recetaRepository);
    }

    @Test
    void findById_success() {
        when(comentarioRepository.findById(comentario.getId())).thenReturn(Optional.of(comentario));
        Comentario foundComentario = comentarioService.findById(comentario.getId());
        assertNotNull(foundComentario);
        assertEquals(comentario.getId(), foundComentario.getId());
        verify(comentarioRepository, times(1)).findById(comentario.getId());
    }

    @Test
    void findById_notFound() {
        when(comentarioRepository.findById(anyInt())).thenReturn(Optional.empty());
        Comentario foundComentario = comentarioService.findById(99);
        assertNull(foundComentario);
        verify(comentarioRepository, times(1)).findById(anyInt());
    }
}
