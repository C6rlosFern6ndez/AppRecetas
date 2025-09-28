package com.recetas.backend.service.impl;

import com.recetas.backend.domain.entity.MeGustaReceta;
import com.recetas.backend.domain.entity.MeGustaRecetaId;
import com.recetas.backend.domain.entity.Receta;
import com.recetas.backend.domain.entity.Usuario;
import com.recetas.backend.domain.repository.MeGustaRecetaRepository;
import com.recetas.backend.domain.repository.RecetaRepository;
import com.recetas.backend.domain.repository.UsuarioRepository;
import com.recetas.backend.service.RecetaService;
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
        when(meGustaRecetaRepository.existsById(meGustaRecetaId)).thenReturn(false);
        when(meGustaRecetaRepository.save(any(MeGustaReceta.class))).thenReturn(meGustaReceta);

        assertDoesNotThrow(() -> recetaService.darMeGusta(usuario.getId(), receta.getId()));

        verify(usuarioRepository, times(1)).findById(usuario.getId());
        verify(recetaRepository, times(1)).findById(receta.getId());
        verify(meGustaRecetaRepository, times(1)).existsById(meGustaRecetaId);
        verify(meGustaRecetaRepository, times(1)).save(any(MeGustaReceta.class));
    }

    @Test
    void darMeGusta_usuarioNotFound() {
        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recetaService.darMeGusta(usuario.getId(), receta.getId()));
        assertEquals("Usuario no encontrado", exception.getMessage());

        verify(usuarioRepository, times(1)).findById(usuario.getId());
        verifyNoInteractions(recetaRepository, meGustaRecetaRepository);
    }

    @Test
    void darMeGusta_recetaNotFound() {
        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
        when(recetaRepository.findById(receta.getId())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recetaService.darMeGusta(usuario.getId(), receta.getId()));
        assertEquals("Receta no encontrada", exception.getMessage());

        verify(usuarioRepository, times(1)).findById(usuario.getId());
        verify(recetaRepository, times(1)).findById(receta.getId());
        verifyNoInteractions(meGustaRecetaRepository);
    }

    @Test
    void darMeGusta_alreadyLiked() {
        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
        when(recetaRepository.findById(receta.getId())).thenReturn(Optional.of(receta));
        when(meGustaRecetaRepository.existsById(meGustaRecetaId)).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recetaService.darMeGusta(usuario.getId(), receta.getId()));
        assertEquals("Ya has dado 'me gusta' a esta receta.", exception.getMessage());

        verify(usuarioRepository, times(1)).findById(usuario.getId());
        verify(recetaRepository, times(1)).findById(receta.getId());
        verify(meGustaRecetaRepository, times(1)).existsById(meGustaRecetaId);
        verifyNoMoreInteractions(meGustaRecetaRepository);
    }

    @Test
    void quitarMeGusta_success() {
        when(meGustaRecetaRepository.existsById(meGustaRecetaId)).thenReturn(true);
        doNothing().when(meGustaRecetaRepository).deleteById(meGustaRecetaId);

        assertDoesNotThrow(() -> recetaService.quitarMeGusta(usuario.getId(), receta.getId()));

        verify(meGustaRecetaRepository, times(1)).existsById(meGustaRecetaId);
        verify(meGustaRecetaRepository, times(1)).deleteById(meGustaRecetaId);
    }

    @Test
    void quitarMeGusta_notLiked() {
        when(meGustaRecetaRepository.existsById(meGustaRecetaId)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recetaService.quitarMeGusta(usuario.getId(), receta.getId()));
        assertEquals("No has dado 'me gusta' a esta receta.", exception.getMessage());

        verify(meGustaRecetaRepository, times(1)).existsById(meGustaRecetaId);
        verifyNoMoreInteractions(meGustaRecetaRepository);
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
