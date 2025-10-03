package com.recetas.backend.service.impl;

import com.recetas.backend.domain.dto.RecetaRequestDto;
import com.recetas.backend.domain.entity.MeGustaReceta;
import com.recetas.backend.domain.entity.MeGustaRecetaId;
import com.recetas.backend.domain.entity.Receta;
import com.recetas.backend.domain.entity.Usuario;
import com.recetas.backend.domain.repository.CalificacionRepository;
import com.recetas.backend.domain.repository.ComentarioRepository;
import com.recetas.backend.domain.repository.MeGustaRecetaRepository;
import com.recetas.backend.domain.repository.RecetaRepository;
import com.recetas.backend.domain.entity.Comentario;
import com.recetas.backend.domain.repository.UsuarioRepository;
import com.recetas.backend.exception.AccesoDenegadoException;
import com.recetas.backend.exception.ComentarioException;
import com.recetas.backend.exception.MeGustaException;
import com.recetas.backend.exception.RecetaNoEncontradaException;
import com.recetas.backend.exception.UsuarioNoEncontradoException;
import com.recetas.backend.service.ImageUploadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.recetas.backend.domain.entity.Categoria;
import com.recetas.backend.domain.entity.Calificacion;
import com.recetas.backend.domain.model.enums.Dificultad;
import com.recetas.backend.domain.repository.CategoriaRepository;
import com.recetas.backend.exception.CategoriaNoEncontradaException;
import com.recetas.backend.exception.ImageUploadException;
import com.recetas.backend.service.NotificacionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Arrays;
import java.util.Set;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
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
    private NotificacionService notificacionService;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private ImageUploadService imageUploadService; // Mock para ImageUploadService

    @InjectMocks
    private RecetaServiceImpl recetaService;

    private Usuario usuario;
    private Receta receta;
    private MeGustaRecetaId meGustaRecetaId;
    private Comentario comentario;
    private Categoria categoria;
    private RecetaRequestDto recetaRequestDto;

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
        receta.setCategorias(new HashSet<>(Collections.singletonList(categoria))); // Inicializar con una categoría
        receta.setFechaCreacion(LocalDateTime.now());
        receta.setDificultad(Dificultad.FACIL);
        receta.setPorciones(4);
        receta.setTiempoPreparacion(30);

        meGustaRecetaId = new MeGustaRecetaId(usuario.getId(), receta.getId());

        comentario = new Comentario();
        comentario.setId(1);
        comentario.setComentario("Test comentario");
        comentario.setUsuario(usuario);
        comentario.setReceta(receta);

        recetaRequestDto = new RecetaRequestDto();
        recetaRequestDto.setTitulo("Nueva Receta");
        recetaRequestDto.setDescripcion("Descripción de la nueva receta");
        recetaRequestDto.setTiempoPreparacion(60);
        recetaRequestDto.setDificultad(Dificultad.MEDIA);
        recetaRequestDto.setPorciones(2);
        recetaRequestDto.setCategoriaIds(new HashSet<>(Arrays.asList(1)));
    }

    @Test
    void crearReceta_success() {
        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
        when(categoriaRepository.findById(categoria.getId())).thenReturn(Optional.of(categoria));
        when(recetaRepository.save(any(Receta.class))).thenReturn(receta);

        Receta result = recetaService.crearReceta(recetaRequestDto, usuario.getId());

        assertNotNull(result);
        assertEquals(recetaRequestDto.getTitulo(), result.getTitulo());
        assertTrue(result.getCategorias().contains(categoria));
        verify(recetaRepository, times(1)).save(any(Receta.class));
    }

    @Test
    void crearReceta_usuarioNotFound() {
        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.empty());

        assertThrows(UsuarioNoEncontradoException.class,
                () -> recetaService.crearReceta(recetaRequestDto, usuario.getId()));
    }

    @Test
    void crearReceta_categoriaNotFound() {
        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
        when(categoriaRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(CategoriaNoEncontradaException.class,
                () -> recetaService.crearReceta(recetaRequestDto, usuario.getId()));
    }

    @Test
    void actualizarReceta_success() {
        RecetaRequestDto updateDto = new RecetaRequestDto();
        updateDto.setTitulo("Receta Actualizada");
        updateDto.setDescripcion("Descripción actualizada");
        updateDto.setTiempoPreparacion(40);
        updateDto.setDificultad(Dificultad.DIFICIL);
        updateDto.setPorciones(5);
        updateDto.setUrlImagen("http://newimage.com/img.jpg");
        updateDto.setCategoriaIds(new HashSet<>(Arrays.asList(1)));

        when(recetaRepository.findById(receta.getId())).thenReturn(Optional.of(receta));
        when(categoriaRepository.findById(categoria.getId())).thenReturn(Optional.of(categoria));
        when(recetaRepository.save(any(Receta.class))).thenReturn(receta);

        Receta result = recetaService.actualizarReceta(receta.getId(), updateDto, usuario.getId());

        assertNotNull(result);
        assertEquals("Receta Actualizada", result.getTitulo());
        assertEquals("Descripción actualizada", result.getDescripcion());
        assertEquals("http://newimage.com/img.jpg", result.getUrlImagen());
        assertTrue(result.getCategorias().contains(categoria));
        verify(recetaRepository, times(1)).save(receta);
    }

    @Test
    void actualizarReceta_recetaNotFound() {
        when(recetaRepository.findById(receta.getId())).thenReturn(Optional.empty());

        assertThrows(RecetaNoEncontradaException.class,
                () -> recetaService.actualizarReceta(receta.getId(), recetaRequestDto, usuario.getId()));
    }

    @Test
    void actualizarReceta_accesoDenegado() {
        Usuario otroUsuario = new Usuario();
        otroUsuario.setId(2);
        when(recetaRepository.findById(receta.getId())).thenReturn(Optional.of(receta));

        assertThrows(AccesoDenegadoException.class,
                () -> recetaService.actualizarReceta(receta.getId(), recetaRequestDto, otroUsuario.getId()));
    }

    @Test
    void actualizarReceta_categoriaNotFound() {
        when(recetaRepository.findById(receta.getId())).thenReturn(Optional.of(receta));
        when(categoriaRepository.findById(anyInt())).thenReturn(Optional.empty());
        recetaRequestDto.setCategoriaIds(new HashSet<>(Arrays.asList(99))); // Categoría inexistente

        assertThrows(CategoriaNoEncontradaException.class,
                () -> recetaService.actualizarReceta(receta.getId(), recetaRequestDto, usuario.getId()));
    }

    @Test
    void subirImagenReceta_success() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", "some-image".getBytes());
        String expectedUrl = "http://imgbb.com/test.jpg";

        when(recetaRepository.findById(receta.getId())).thenReturn(Optional.of(receta));
        when(imageUploadService.uploadImage(any(MultipartFile.class), anyString(), anyString()))
                .thenReturn(expectedUrl);
        when(recetaRepository.save(any(Receta.class))).thenReturn(receta);

        String resultUrl = recetaService.subirImagenReceta(receta.getId(), mockFile);

        assertEquals(expectedUrl, resultUrl);
        assertEquals(expectedUrl, receta.getUrlImagen());
        verify(imageUploadService, times(1)).uploadImage(eq(mockFile), eq("Postres"), eq("TestReceta"));
        verify(recetaRepository, times(1)).save(receta);
    }

    @Test
    void subirImagenReceta_recetaNotFound() {
        MockMultipartFile mockFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", "some-image".getBytes());
        when(recetaRepository.findById(receta.getId())).thenReturn(Optional.empty());

        assertThrows(RecetaNoEncontradaException.class,
                () -> recetaService.subirImagenReceta(receta.getId(), mockFile));
    }

    @Test
    void subirImagenReceta_uploadFails() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", "some-image".getBytes());
        when(recetaRepository.findById(receta.getId())).thenReturn(Optional.of(receta));
        when(imageUploadService.uploadImage(any(MultipartFile.class), anyString(), anyString()))
                .thenThrow(new ImageUploadException("Error de subida"));

        assertThrows(ImageUploadException.class, () -> recetaService.subirImagenReceta(receta.getId(), mockFile));
    }

    @Test
    void eliminarImagenReceta_success() {
        receta.setUrlImagen("http://imgbb.com/old_image.jpg");
        when(recetaRepository.findById(receta.getId())).thenReturn(Optional.of(receta));
        when(recetaRepository.save(any(Receta.class))).thenReturn(receta);

        assertDoesNotThrow(() -> recetaService.eliminarImagenReceta(receta.getId()));

        assertNull(receta.getUrlImagen());
        verify(recetaRepository, times(1)).save(receta);
    }

    @Test
    void eliminarImagenReceta_recetaNotFound() {
        when(recetaRepository.findById(receta.getId())).thenReturn(Optional.empty());

        assertThrows(RecetaNoEncontradaException.class, () -> recetaService.eliminarImagenReceta(receta.getId()));
    }

    @Test
    void darMeGusta_success() {
        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
        when(recetaRepository.findById(receta.getId())).thenReturn(Optional.of(receta));
        when(meGustaRecetaRepository.existsById_UsuarioIdAndId_RecetaId(usuario.getId(), receta.getId()))
                .thenReturn(false);
        when(notificacionService.crearNotificacion(any(Integer.class), any(), any(Integer.class), any()))
                .thenReturn(null);

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
    void agregarComentario_success() {
        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
        when(recetaRepository.findById(receta.getId())).thenReturn(Optional.of(receta));
        when(comentarioRepository.save(any(Comentario.class))).thenReturn(comentario);

        Comentario result = recetaService.agregarComentario(receta.getId(), usuario.getId(), "Test comentario");

        assertNotNull(result);
        assertEquals("Test comentario", result.getComentario());
        verify(comentarioRepository).save(any(Comentario.class));
    }

    @Test
    void agregarComentario_emptyComment() {
        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
        when(recetaRepository.findById(receta.getId())).thenReturn(Optional.of(receta));

        ComentarioException exception = assertThrows(ComentarioException.class,
                () -> recetaService.agregarComentario(receta.getId(), usuario.getId(), " "));
        assertEquals("El comentario no puede estar vacío.", exception.getMessage());
    }

    @Test
    void agregarComentario_usuarioNotFound() {
        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.empty());

        UsuarioNoEncontradoException exception = assertThrows(UsuarioNoEncontradoException.class,
                () -> recetaService.agregarComentario(receta.getId(), usuario.getId(), "Test comentario"));
        assertEquals("Usuario no encontrado con id: 1", exception.getMessage());
    }

    @Test
    void agregarComentario_recetaNotFound() {
        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
        when(recetaRepository.findById(receta.getId())).thenReturn(Optional.empty());

        RecetaNoEncontradaException exception = assertThrows(RecetaNoEncontradaException.class,
                () -> recetaService.agregarComentario(receta.getId(), usuario.getId(), "Test comentario"));
        assertEquals("Receta no encontrada con ID: 10", exception.getMessage());
    }

    @Test
    void obtenerComentariosDeReceta_success() {
        Set<Comentario> comentarios = new HashSet<>(Collections.singletonList(comentario));
        receta.setComentarios(comentarios);
        when(recetaRepository.findById(receta.getId())).thenReturn(Optional.of(receta));

        Set<Comentario> result = recetaService.obtenerComentariosDeReceta(receta.getId());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertTrue(result.contains(comentario));
    }

    @Test
    void obtenerComentariosDeReceta_recetaNotFound() {
        when(recetaRepository.findById(receta.getId())).thenReturn(Optional.empty());

        assertThrows(RecetaNoEncontradaException.class, () -> recetaService.obtenerComentariosDeReceta(receta.getId()));
    }

    @Test
    void obtenerRecetaOExcepcion_success() {
        when(recetaRepository.findById(receta.getId())).thenReturn(Optional.of(receta));

        Receta result = recetaService.obtenerRecetaOExcepcion(receta.getId());

        assertNotNull(result);
        assertEquals(receta.getId(), result.getId());
    }

    @Test
    void obtenerRecetaOExcepcion_notFound() {
        when(recetaRepository.findById(receta.getId())).thenReturn(Optional.empty());

        assertThrows(RecetaNoEncontradaException.class, () -> recetaService.obtenerRecetaOExcepcion(receta.getId()));
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
        when(recetaRepository.findById(receta.getId())).thenReturn(Optional.of(receta));
        doNothing().when(recetaRepository).delete(receta);

        assertDoesNotThrow(() -> recetaService.eliminarReceta(receta.getId(), usuario.getId()));

        verify(recetaRepository, times(1)).delete(receta);
    }

    @Test
    void eliminarReceta_recetaNotFound() {
        when(recetaRepository.findById(receta.getId())).thenReturn(Optional.empty());

        assertThrows(RecetaNoEncontradaException.class,
                () -> recetaService.eliminarReceta(receta.getId(), usuario.getId()));
    }

    @Test
    void eliminarReceta_accesoDenegado() {
        Usuario otroUsuario = new Usuario();
        otroUsuario.setId(2);
        when(recetaRepository.findById(receta.getId())).thenReturn(Optional.of(receta));

        assertThrows(AccesoDenegadoException.class,
                () -> recetaService.eliminarReceta(receta.getId(), otroUsuario.getId()));
    }

    @Test
    void calificarReceta_success_newRating() {
        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
        when(recetaRepository.findById(receta.getId())).thenReturn(Optional.of(receta));
        when(calificacionRepository.findByUsuarioIdAndRecetaId(usuario.getId(), receta.getId()))
                .thenReturn(Optional.empty());
        when(calificacionRepository.save(any(Calificacion.class))).thenAnswer(invocation -> {
            Calificacion cal = invocation.getArgument(0);
            cal.setId(1);
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

        UsuarioNoEncontradoException exception = assertThrows(UsuarioNoEncontradoException.class,
                () -> recetaService.calificarReceta(usuario.getId(), receta.getId(), 4));
        assertEquals("Usuario no encontrado con id: 1", exception.getMessage());
    }

    @Test
    void calificarReceta_recetaNotFound() {
        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
        when(recetaRepository.findById(receta.getId())).thenReturn(Optional.empty());

        RecetaNoEncontradaException exception = assertThrows(RecetaNoEncontradaException.class,
                () -> recetaService.calificarReceta(usuario.getId(), receta.getId(), 4));
        assertEquals("Receta no encontrada con id: 10", exception.getMessage());
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
    void obtenerCalificacionDeReceta_success() {
        Calificacion existingCalificacion = new Calificacion(1, 4, LocalDateTime.now(), usuario, receta);
        when(usuarioRepository.existsById(usuario.getId())).thenReturn(true);
        when(recetaRepository.existsById(receta.getId())).thenReturn(true);
        when(calificacionRepository.findByUsuarioIdAndRecetaId(usuario.getId(), receta.getId()))
                .thenReturn(Optional.of(existingCalificacion));

        Integer result = recetaService.obtenerCalificacionDeReceta(usuario.getId(), receta.getId());

        assertNotNull(result);
        assertEquals(4, result);
    }

    @Test
    void obtenerCalificacionDeReceta_usuarioNotFound() {
        when(usuarioRepository.existsById(usuario.getId())).thenReturn(false);

        UsuarioNoEncontradoException exception = assertThrows(UsuarioNoEncontradoException.class,
                () -> recetaService.obtenerCalificacionDeReceta(usuario.getId(), receta.getId()));
        assertEquals("Usuario no encontrado con id: 1", exception.getMessage());
    }

    @Test
    void obtenerCalificacionDeReceta_recetaNotFound() {
        when(usuarioRepository.existsById(usuario.getId())).thenReturn(true);
        when(recetaRepository.existsById(receta.getId())).thenReturn(false);

        RecetaNoEncontradaException exception = assertThrows(RecetaNoEncontradaException.class,
                () -> recetaService.obtenerCalificacionDeReceta(usuario.getId(), receta.getId()));
        assertEquals("Receta no encontrada con id: 10", exception.getMessage());
    }

    @Test
    void obtenerCalificacionDeReceta_noRating() {
        when(usuarioRepository.existsById(usuario.getId())).thenReturn(true);
        when(recetaRepository.existsById(receta.getId())).thenReturn(true);
        when(calificacionRepository.findByUsuarioIdAndRecetaId(usuario.getId(), receta.getId()))
                .thenReturn(Optional.empty());

        Integer result = recetaService.obtenerCalificacionDeReceta(usuario.getId(), receta.getId());

        assertNull(result);
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
        receta.getCategorias().add(categoria);
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
