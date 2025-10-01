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
import com.recetas.backend.domain.entity.Comentario;
import com.recetas.backend.domain.repository.UsuarioRepository;
import com.recetas.backend.exception.ComentarioException;
import com.recetas.backend.exception.MeGustaException;
import com.recetas.backend.exception.RecetaNoEncontradaException;
import com.recetas.backend.exception.UsuarioNoEncontradoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.recetas.backend.domain.entity.Categoria;
import com.recetas.backend.domain.entity.Calificacion;
import com.recetas.backend.domain.repository.CategoriaRepository;
import com.recetas.backend.exception.CategoriaNoEncontradaException;
import com.recetas.backend.service.NotificacionService; // Importar NotificacionService
import java.time.LocalDateTime;
import java.util.HashSet; // Importar HashSet
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
import java.util.Arrays; // Importar Arrays

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

    @Mock
    private NotificacionService notificacionService; // Mock para NotificacionService

    @Mock
    private CategoriaRepository categoriaRepository; // Añadido para tests de categoría

    @InjectMocks
    private RecetaServiceImpl recetaService;

    private Usuario usuario;
    private Receta receta;
    private MeGustaRecetaId meGustaRecetaId;
    private MeGustaReceta meGustaReceta;
    private Comentario comentario;
    private Categoria categoria; // Añadido para tests de categoría

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1);
        usuario.setNombreUsuario("testUser");
        usuario.setEmail("test@example.com");
        usuario.setContrasena("password");

        categoria = new Categoria();
        categoria.setId(1);
        categoria.setNombre("Postres");

        receta = new Receta();
        receta.setId(10);
        receta.setTitulo("Test Receta");
        receta.setDescripcion("Una receta de prueba");
        receta.setUsuario(usuario);
        receta.setCategorias(new HashSet<>()); // Inicializar la colección de categorías

        meGustaRecetaId = new MeGustaRecetaId(usuario.getId(), receta.getId());
        meGustaReceta = new MeGustaReceta(meGustaRecetaId, usuario, receta);

        comentario = new Comentario();
        comentario.setId(1);
        comentario.setComentario("Test comentario");
        comentario.setUsuario(usuario);
        comentario.setReceta(receta);
    }

    @Test
    void darMeGusta_success() {
        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
        when(recetaRepository.findById(receta.getId())).thenReturn(Optional.of(receta));
        when(meGustaRecetaRepository.existsById_UsuarioIdAndId_RecetaId(usuario.getId(), receta.getId()))
                .thenReturn(false);
        // Mockear el comportamiento de crearNotificacion
        when(notificacionService.crearNotificacion(any(Integer.class), any(), any(Integer.class), any()))
                .thenReturn(null); // O devolver un objeto Notificacion mockeado si es necesario

        assertDoesNotThrow(() -> recetaService.darMeGusta(usuario.getId(), receta.getId()));

        verify(meGustaRecetaRepository).save(any(MeGustaReceta.class));
        verify(notificacionService, times(1)).crearNotificacion(any(Integer.class), any(), any(Integer.class), any());
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
        Pageable pageable = PageRequest.of(0, 10);
        Page<Receta> pageRecetas = new PageImpl<>(Arrays.asList(receta), pageable, 1);
        when(recetaRepository.findAll(pageable)).thenReturn(pageRecetas);

        Page<Receta> resultPage = recetaService.obtenerTodasLasRecetas(pageable);

        assertNotNull(resultPage);
        assertFalse(resultPage.isEmpty());
        assertEquals(1, resultPage.getTotalElements());
        assertEquals(receta.getTitulo(), resultPage.getContent().get(0).getTitulo());
        verify(recetaRepository, times(1)).findAll(pageable);
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

    @Test
    void agregarComentario_success() {
        when(comentarioRepository.save(any(Comentario.class))).thenReturn(comentario);

        Comentario result = recetaService.agregarComentario(comentario);

        assertNotNull(result);
        assertEquals("Test comentario", result.getComentario());
        verify(comentarioRepository).save(comentario);
    }

    @Test
    void agregarComentario_emptyComment() {
        comentario.setComentario(" ");

        ComentarioException exception = assertThrows(ComentarioException.class,
                () -> recetaService.agregarComentario(comentario));
        assertEquals("El comentario no puede estar vacío.", exception.getMessage());
    }

    @Test
    void agregarComentario_nullUser() {
        comentario.setUsuario(null);

        ComentarioException exception = assertThrows(ComentarioException.class,
                () -> recetaService.agregarComentario(comentario));
        assertEquals("El comentario debe estar asociado a un usuario.", exception.getMessage());
    }

    @Test
    void agregarComentario_nullReceta() {
        comentario.setReceta(null);

        ComentarioException exception = assertThrows(ComentarioException.class,
                () -> recetaService.agregarComentario(comentario));
        assertEquals("El comentario debe estar asociado a una receta.", exception.getMessage());
    }

    @Test
    void calificarReceta_success_newRating() {
        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
        when(recetaRepository.findById(receta.getId())).thenReturn(Optional.of(receta));
        when(calificacionRepository.findByUsuarioIdAndRecetaId(usuario.getId(), receta.getId()))
                .thenReturn(Optional.empty());
        when(calificacionRepository.save(any(Calificacion.class))).thenAnswer(invocation -> {
            Calificacion cal = invocation.getArgument(0);
            cal.setId(1); // Simular ID generado
            return cal;
        });

        assertDoesNotThrow(() -> recetaService.calificarReceta(usuario.getId(), receta.getId(), 4));

        verify(calificacionRepository, times(1)).save(any(Calificacion.class));
    }

    @Test
    void calificarReceta_success_updateRating() {
        Calificacion existingCalificacion = new Calificacion(1, 3, LocalDateTime.now(), usuario, receta);
        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
        when(recetaRepository.findById(receta.getId())).thenReturn(Optional.of(receta));
        when(calificacionRepository.findByUsuarioIdAndRecetaId(usuario.getId(), receta.getId()))
                .thenReturn(Optional.of(existingCalificacion));
        when(calificacionRepository.save(any(Calificacion.class))).thenReturn(existingCalificacion);

        assertDoesNotThrow(() -> recetaService.calificarReceta(usuario.getId(), receta.getId(), 5));

        assertEquals(5, existingCalificacion.getPuntuacion());
        verify(calificacionRepository, times(1)).save(any(Calificacion.class));
    }

    @Test
    void calificarReceta_usuarioNotFound() {
        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recetaService.calificarReceta(usuario.getId(), receta.getId(), 4));
        assertEquals("Usuario no encontrado", exception.getMessage());
    }

    @Test
    void calificarReceta_recetaNotFound() {
        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
        when(recetaRepository.findById(receta.getId())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recetaService.calificarReceta(usuario.getId(), receta.getId(), 4));
        assertEquals("Receta no encontrada", exception.getMessage());
    }

    @Test
    void calificarReceta_invalidPuntuacion() {
        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
        when(recetaRepository.findById(receta.getId())).thenReturn(Optional.of(receta));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> recetaService.calificarReceta(usuario.getId(), receta.getId(), 0));
        assertEquals("La puntuación debe estar entre 1 y 5.", exception.getMessage());

        exception = assertThrows(IllegalArgumentException.class,
                () -> recetaService.calificarReceta(usuario.getId(), receta.getId(), 6));
        assertEquals("La puntuación debe estar entre 1 y 5.", exception.getMessage());
    }

    @Test
    void agregarCategoria_success() {
        when(recetaRepository.findById(receta.getId())).thenReturn(Optional.of(receta));
        when(categoriaRepository.findById(categoria.getId())).thenReturn(Optional.of(categoria));
        when(recetaRepository.save(any(Receta.class))).thenReturn(receta);

        Receta result = recetaService.agregarCategoria(receta.getId(), categoria.getId());

        assertNotNull(result);
        assertTrue(result.getCategorias().contains(categoria));
        verify(recetaRepository, times(1)).save(receta);
    }

    @Test
    void agregarCategoria_recetaNotFound() {
        when(recetaRepository.findById(receta.getId())).thenReturn(Optional.empty());

        RecetaNoEncontradaException exception = assertThrows(RecetaNoEncontradaException.class,
                () -> recetaService.agregarCategoria(receta.getId(), categoria.getId()));
        assertEquals("Receta no encontrada con id: " + receta.getId(), exception.getMessage());
    }

    @Test
    void agregarCategoria_categoriaNotFound() {
        when(recetaRepository.findById(receta.getId())).thenReturn(Optional.of(receta));
        when(categoriaRepository.findById(categoria.getId())).thenReturn(Optional.empty());

        CategoriaNoEncontradaException exception = assertThrows(CategoriaNoEncontradaException.class,
                () -> recetaService.agregarCategoria(receta.getId(), categoria.getId()));
        assertEquals("Categoria no encontrada con id: " + categoria.getId(), exception.getMessage());
    }

    @Test
    void eliminarCategoria_success() {
        receta.getCategorias().add(categoria); // Asegurarse de que la categoría existe en la receta
        when(recetaRepository.findById(receta.getId())).thenReturn(Optional.of(receta));
        when(categoriaRepository.findById(categoria.getId())).thenReturn(Optional.of(categoria));
        when(recetaRepository.save(any(Receta.class))).thenReturn(receta);

        Receta result = recetaService.eliminarCategoria(receta.getId(), categoria.getId());

        assertNotNull(result);
        assertFalse(result.getCategorias().contains(categoria));
        verify(recetaRepository, times(1)).save(receta);
    }

    @Test
    void eliminarCategoria_recetaNotFound() {
        when(recetaRepository.findById(receta.getId())).thenReturn(Optional.empty());

        RecetaNoEncontradaException exception = assertThrows(RecetaNoEncontradaException.class,
                () -> recetaService.eliminarCategoria(receta.getId(), categoria.getId()));
        assertEquals("Receta no encontrada con id: " + receta.getId(), exception.getMessage());
    }

    @Test
    void eliminarCategoria_categoriaNotFound() {
        when(recetaRepository.findById(receta.getId())).thenReturn(Optional.of(receta));
        when(categoriaRepository.findById(categoria.getId())).thenReturn(Optional.empty());

        CategoriaNoEncontradaException exception = assertThrows(CategoriaNoEncontradaException.class,
                () -> recetaService.eliminarCategoria(receta.getId(), categoria.getId()));
        assertEquals("Categoria no encontrada con id: " + categoria.getId(), exception.getMessage());
    }
}
