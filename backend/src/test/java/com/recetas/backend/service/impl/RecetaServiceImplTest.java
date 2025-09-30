package com.recetas.backend.service.impl;

import com.recetas.backend.domain.entity.MeGustaReceta;
import com.recetas.backend.domain.entity.MeGustaRecetaId;
import com.recetas.backend.domain.entity.Receta;
import com.recetas.backend.domain.entity.Usuario;
import com.recetas.backend.domain.repository.CalificacionRepository;
import com.recetas.backend.domain.repository.ComentarioRepository;
import com.recetas.backend.domain.repository.MeGustaRecetaRepository;
import com.recetas.backend.domain.repository.NotificacionRepository;
import com.recetas.backend.domain.repository.RecetaRepository;
import com.recetas.backend.domain.repository.UsuarioRepository;
import com.recetas.backend.exception.MeGustaException;
import com.recetas.backend.exception.RecetaNoEncontradaException;
import com.recetas.backend.exception.UsuarioNoEncontradoException;
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
class RecetaServiceImplTest {

    @Mock
    private RecetaRepository recetaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private MeGustaRecetaRepository meGustaRecetaRepository;

    @Mock
    private ComentarioRepository comentarioRepository;

    @Mock
    private CalificacionRepository calificacionRepository;

    @Mock
    private NotificacionRepository notificacionRepository;

    @InjectMocks
    private RecetaServiceImpl recetaService;

    private Usuario usuario;
    private Receta receta;
    private MeGustaRecetaId meGustaRecetaId;
    private MeGustaReceta meGustaReceta;

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

        meGustaRecetaId = new MeGustaRecetaId(usuario.getId(), receta.getId());
        meGustaReceta = new MeGustaReceta(meGustaRecetaId, usuario, receta);
    }

    @Test
    void darMeGusta_success() {
        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
        when(recetaRepository.findById(receta.getId())).thenReturn(Optional.of(receta));
        when(meGustaRecetaRepository.existsById_UsuarioIdAndId_RecetaId(usuario.getId(), receta.getId()))
                .thenReturn(false);

        assertDoesNotThrow(() -> recetaService.darMeGusta(usuario.getId(), receta.getId()));

        verify(meGustaRecetaRepository).save(any(MeGustaReceta.class));
    }

    @Test
    void darMeGusta_usuarioNotFound() {
        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.empty());

        UsuarioNoEncontradoException exception = assertThrows(UsuarioNoEncontradoException.class,
                () -> recetaService.darMeGusta(usuario.getId(), receta.getId()));
        assertEquals("Usuario no encontrado con id: 1", exception.getMessage());
    }

    @Test
    void darMeGusta_recetaNotFound() {
        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
        when(recetaRepository.findById(receta.getId())).thenReturn(Optional.empty());

        RecetaNoEncontradaException exception = assertThrows(RecetaNoEncontradaException.class,
                () -> recetaService.darMeGusta(usuario.getId(), receta.getId()));
        assertEquals("Receta no encontrada con id: 10", exception.getMessage());
    }

    @Test
    void darMeGusta_alreadyLiked() {
        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
        when(recetaRepository.findById(receta.getId())).thenReturn(Optional.of(receta));
        when(meGustaRecetaRepository.existsById_UsuarioIdAndId_RecetaId(usuario.getId(), receta.getId()))
                .thenReturn(true);

        MeGustaException exception = assertThrows(MeGustaException.class,
                () -> recetaService.darMeGusta(usuario.getId(), receta.getId()));
        assertEquals("Ya has dado 'me gusta' a esta receta.", exception.getMessage());
    }

    @Test
    void quitarMeGusta_success() {
        when(usuarioRepository.existsById(usuario.getId())).thenReturn(true);
        when(recetaRepository.existsById(receta.getId())).thenReturn(true);
        when(meGustaRecetaRepository.existsById(meGustaRecetaId)).thenReturn(true);

        assertDoesNotThrow(() -> recetaService.quitarMeGusta(usuario.getId(), receta.getId()));

        verify(meGustaRecetaRepository).deleteById(meGustaRecetaId);
    }

    @Test
    void quitarMeGusta_notLiked() {
        when(usuarioRepository.existsById(usuario.getId())).thenReturn(true);
        when(recetaRepository.existsById(receta.getId())).thenReturn(true);
        when(meGustaRecetaRepository.existsById(meGustaRecetaId)).thenReturn(false);

        MeGustaException exception = assertThrows(MeGustaException.class,
                () -> recetaService.quitarMeGusta(usuario.getId(), receta.getId()));
        assertEquals("No has dado 'me gusta' a esta receta.", exception.getMessage());
    }

    @Test
    void findById_success() {
        when(recetaRepository.findById(receta.getId())).thenReturn(Optional.of(receta));
        Receta foundReceta = recetaService.findById(receta.getId());
        assertNotNull(foundReceta);
        assertEquals(receta.getId(), foundReceta.getId());
        verify(recetaRepository, times(1)).findById(receta.getId());
    }

    @Test
    void findById_notFound() {
        when(recetaRepository.findById(anyInt())).thenReturn(Optional.empty());
        Receta foundReceta = recetaService.findById(99);
        assertNull(foundReceta);
        verify(recetaRepository, times(1)).findById(anyInt());
    }

    @Test
    void guardarReceta_success() {
        when(recetaRepository.save(any(Receta.class))).thenReturn(receta);
        Receta savedReceta = recetaService.guardarReceta(receta);
        assertNotNull(savedReceta);
        assertEquals(receta.getTitulo(), savedReceta.getTitulo());
        verify(recetaRepository, times(1)).save(receta);
    }

    @Test
    void obtenerTodasLasRecetas_success() {
        when(recetaRepository.findAll()).thenReturn(java.util.Collections.singletonList(receta));
        java.util.List<Receta> recetas = recetaService.obtenerTodasLasRecetas();
        assertNotNull(recetas);
        assertFalse(recetas.isEmpty());
        assertEquals(1, recetas.size());
        verify(recetaRepository, times(1)).findAll();
    }

    @Test
    void obtenerRecetaPorId_success() {
        when(recetaRepository.findById(receta.getId())).thenReturn(Optional.of(receta));
        Optional<Receta> foundReceta = recetaService.obtenerRecetaPorId(receta.getId());
        assertTrue(foundReceta.isPresent());
        assertEquals(receta.getId(), foundReceta.get().getId());
        verify(recetaRepository, times(1)).findById(receta.getId());
    }

    @Test
    void obtenerRecetaPorId_notFound() {
        when(recetaRepository.findById(anyInt())).thenReturn(Optional.empty());
        Optional<Receta> foundReceta = recetaService.obtenerRecetaPorId(99);
        assertFalse(foundReceta.isPresent());
        verify(recetaRepository, times(1)).findById(anyInt());
    }

    @Test
    void eliminarReceta_success() {
        doNothing().when(recetaRepository).deleteById(receta.getId());
        assertDoesNotThrow(() -> recetaService.eliminarReceta(receta.getId()));
        verify(recetaRepository, times(1)).deleteById(receta.getId());
    }
}
