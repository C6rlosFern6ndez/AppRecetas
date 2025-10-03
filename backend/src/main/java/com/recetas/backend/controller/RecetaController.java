package com.recetas.backend.controller;

import com.recetas.backend.domain.dto.RecetaRequestDto;
import com.recetas.backend.domain.entity.Comentario;
import com.recetas.backend.domain.entity.Receta;
import com.recetas.backend.domain.entity.Usuario;
import com.recetas.backend.domain.model.enums.Dificultad;
import com.recetas.backend.domain.repository.UsuarioRepository;
import com.recetas.backend.exception.RecetaNoEncontradaException;
import com.recetas.backend.exception.UsuarioNoEncontradoException;
import com.recetas.backend.service.RecetaService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Set;

/**
 * Controlador para la gestión de recetas, incluyendo funcionalidades sociales
 * como "me gusta" y comentarios.
 */
@RestController
@RequestMapping("/api/recetas")
public class RecetaController {

    private final RecetaService recetaService;
    private final UsuarioRepository usuarioRepository;

    public RecetaController(RecetaService recetaService, UsuarioRepository usuarioRepository) {
        this.recetaService = recetaService;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Crea una nueva receta.
     *
     * @param recetaDto DTO con los datos de la receta.
     * @param principal El usuario autenticado.
     * @return ResponseEntity con la receta creada.
     */
    @PostMapping
    public ResponseEntity<Receta> crearReceta(@Valid @RequestBody RecetaRequestDto recetaDto,
            @AuthenticationPrincipal Principal principal) {
        Usuario usuario = usuarioRepository.findByNombreUsuario(principal.getName())
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));
        Receta nuevaReceta = recetaService.crearReceta(recetaDto, usuario.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevaReceta);
    }

    /**
     * Actualiza una receta existente.
     *
     * @param id        ID de la receta a actualizar.
     * @param recetaDto DTO con los datos actualizados de la receta.
     * @param principal El usuario autenticado.
     * @return ResponseEntity con la receta actualizada.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Receta> actualizarReceta(@PathVariable Integer id,
            @Valid @RequestBody RecetaRequestDto recetaDto,
            @AuthenticationPrincipal Principal principal) {
        Usuario usuario = usuarioRepository.findByNombreUsuario(principal.getName())
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));
        Receta recetaActualizada = recetaService.actualizarReceta(id, recetaDto, usuario.getId());
        return ResponseEntity.ok(recetaActualizada);
    }

    /**
     * Sube una imagen para una receta.
     *
     * @param recetaId   ID de la receta.
     * @param imagenFile Archivo de imagen.
     * @return ResponseEntity con la URL de la imagen subida.
     */
    @PostMapping("/{recetaId}/imagen")
    public ResponseEntity<String> subirImagenReceta(@PathVariable Integer recetaId,
            @RequestParam("file") MultipartFile imagenFile) {
        String imageUrl = recetaService.subirImagenReceta(recetaId, imagenFile);
        return ResponseEntity.ok(imageUrl);
    }

    /**
     * Elimina una imagen de una receta.
     *
     * @param recetaId ID de la receta.
     * @return ResponseEntity sin contenido.
     */
    @DeleteMapping("/{recetaId}/imagen")
    public ResponseEntity<Void> eliminarImagenReceta(@PathVariable Integer recetaId) {
        recetaService.eliminarImagenReceta(recetaId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Permite a un usuario dar "me gusta" a una receta.
     *
     * @param recetaId  El ID de la receta a la que se da "me gusta".
     * @param principal El usuario autenticado.
     * @return ResponseEntity indicando el resultado de la operación.
     */
    @PostMapping("/{recetaId}/like")
    public ResponseEntity<Void> darMeGusta(@PathVariable Integer recetaId,
            @AuthenticationPrincipal Principal principal) {
        Usuario usuario = usuarioRepository.findByNombreUsuario(principal.getName())
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));
        recetaService.darMeGusta(usuario.getId(), recetaId);
        return ResponseEntity.ok().build();
    }

    /**
     * Permite a un usuario quitar el "me gusta" de una receta.
     *
     * @param recetaId  El ID de la receta a la que se quita el "me gusta".
     * @param principal El usuario autenticado.
     * @return ResponseEntity indicando el resultado de la operación.
     */
    @DeleteMapping("/{recetaId}/like")
    public ResponseEntity<Void> quitarMeGusta(@PathVariable Integer recetaId,
            @AuthenticationPrincipal Principal principal) {
        Usuario usuario = usuarioRepository.findByNombreUsuario(principal.getName())
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));
        recetaService.quitarMeGusta(usuario.getId(), recetaId);
        return ResponseEntity.ok().build();
    }

    /**
     * Añade un comentario a una receta.
     *
     * @param recetaId   El ID de la receta a la que se añade el comentario.
     * @param comentario El objeto Comentario a añadir (solo se usa el texto).
     * @param principal  El usuario autenticado.
     * @return ResponseEntity con el comentario guardado o un código de error.
     */
    @PostMapping("/{recetaId}/comments")
    public ResponseEntity<Comentario> agregarComentario(@PathVariable Integer recetaId,
            @Valid @RequestBody Comentario comentario, @AuthenticationPrincipal Principal principal) {
        Usuario usuario = usuarioRepository.findByNombreUsuario(principal.getName())
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));
        Comentario comentarioGuardado = recetaService.agregarComentario(recetaId, usuario.getId(),
                comentario.getComentario());
        return ResponseEntity.status(HttpStatus.CREATED).body(comentarioGuardado);
    }

    /**
     * Obtiene todos los comentarios de una receta específica.
     *
     * @param recetaId El ID de la receta cuyos comentarios se quieren obtener.
     * @return ResponseEntity con la lista de comentarios o un código de error.
     */
    @GetMapping("/{recetaId}/comments")
    public ResponseEntity<Set<Comentario>> obtenerComentariosDeReceta(@PathVariable Integer recetaId) {
        Set<Comentario> comentarios = recetaService.obtenerComentariosDeReceta(recetaId);
        return ResponseEntity.ok(comentarios);
    }

    /**
     * Obtiene una receta por ID.
     *
     * @param id El ID de la receta a buscar.
     * @return ResponseEntity con la receta encontrada.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Receta> obtenerRecetaPorId(@PathVariable Integer id) {
        Receta receta = recetaService.obtenerRecetaOExcepcion(id);
        return ResponseEntity.ok(receta);
    }

    /**
     * Obtiene todas las recetas con paginación y ordenación.
     *
     * @param pageable Objeto Pageable para la paginación y ordenación.
     * @return ResponseEntity con una página de recetas.
     */
    @GetMapping
    public ResponseEntity<Page<Receta>> obtenerTodasLasRecetas(Pageable pageable) {
        Page<Receta> recetas = recetaService.obtenerTodasLasRecetas(pageable);
        return ResponseEntity.ok(recetas);
    }

    /**
     * Busca recetas por varios criterios con paginación y ordenación.
     *
     * @param titulo               Título de la receta (parcial).
     * @param ingredienteNombre    Nombre de un ingrediente (parcial).
     * @param dificultad           Nivel de dificultad.
     * @param tiempoPreparacionMax Tiempo máximo de preparación.
     * @param categoriaNombre      Nombre de una categoría (parcial).
     * @param pageable             Objeto Pageable para la paginación y ordenación.
     * @return ResponseEntity con una página de recetas que coinciden con los
     *         criterios.
     */
    @GetMapping("/search")
    public ResponseEntity<Page<Receta>> buscarRecetas(
            @RequestParam(required = false) String titulo,
            @RequestParam(required = false) String ingredienteNombre,
            @RequestParam(required = false) Dificultad dificultad,
            @RequestParam(required = false) Integer tiempoPreparacionMax,
            @RequestParam(required = false) String categoriaNombre,
            Pageable pageable) {
        Page<Receta> recetas = recetaService.buscarRecetas(titulo, ingredienteNombre, dificultad,
                tiempoPreparacionMax, categoriaNombre, pageable);
        return ResponseEntity.ok(recetas);
    }

    /**
     * Elimina una receta.
     *
     * @param id        ID de la receta a eliminar.
     * @param principal El usuario autenticado.
     * @return ResponseEntity sin contenido.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarReceta(@PathVariable Integer id,
            @AuthenticationPrincipal Principal principal) {
        Usuario usuario = usuarioRepository.findByNombreUsuario(principal.getName())
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));
        recetaService.eliminarReceta(id, usuario.getId());
        return ResponseEntity.noContent().build();
    }

    /**
     * Califica una receta.
     *
     * @param recetaId   ID de la receta a calificar.
     * @param puntuacion La puntuación (1-5).
     * @param principal  El usuario autenticado.
     * @return ResponseEntity sin contenido.
     */
    @PostMapping("/{recetaId}/calificar")
    public ResponseEntity<Void> calificarReceta(@PathVariable Integer recetaId,
            @RequestParam Integer puntuacion, @AuthenticationPrincipal Principal principal) {
        Usuario usuario = usuarioRepository.findByNombreUsuario(principal.getName())
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));
        recetaService.calificarReceta(usuario.getId(), recetaId, puntuacion);
        return ResponseEntity.ok().build();
    }

    /**
     * Obtiene la calificación de una receta por un usuario.
     *
     * @param recetaId  ID de la receta.
     * @param principal El usuario autenticado.
     * @return ResponseEntity con la calificación o null si no existe.
     */
    @GetMapping("/{recetaId}/calificacion")
    public ResponseEntity<Integer> obtenerCalificacionDeReceta(@PathVariable Integer recetaId,
            @AuthenticationPrincipal Principal principal) {
        Usuario usuario = usuarioRepository.findByNombreUsuario(principal.getName())
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));
        Integer calificacion = recetaService.obtenerCalificacionDeReceta(usuario.getId(), recetaId);
        return ResponseEntity.ok(calificacion);
    }

    /**
     * Agrega una categoría a una receta.
     *
     * @param recetaId    ID de la receta.
     * @param categoriaId ID de la categoría a agregar.
     * @return ResponseEntity con la receta actualizada.
     */
    @PostMapping("/{recetaId}/categorias/{categoriaId}")
    public ResponseEntity<Receta> agregarCategoria(@PathVariable Integer recetaId, @PathVariable Integer categoriaId) {
        Receta recetaActualizada = recetaService.agregarCategoria(recetaId, categoriaId);
        return ResponseEntity.ok(recetaActualizada);
    }

    /**
     * Elimina una categoría de una receta.
     *
     * @param recetaId    ID de la receta.
     * @param categoriaId ID de la categoría a eliminar.
     * @return ResponseEntity con la receta actualizada.
     */
    @DeleteMapping("/{recetaId}/categorias/{categoriaId}")
    public ResponseEntity<Receta> eliminarCategoria(@PathVariable Integer recetaId,
            @PathVariable Integer categoriaId) {
        Receta recetaActualizada = recetaService.eliminarCategoria(recetaId, categoriaId);
        return ResponseEntity.ok(recetaActualizada);
    }
}
