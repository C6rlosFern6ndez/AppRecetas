package com.recetas.backend.controller;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.recetas.backend.domain.dto.RecetaRequestDto;
import com.recetas.backend.domain.entity.Comentario;
import com.recetas.backend.domain.entity.Receta;
import com.recetas.backend.domain.entity.Usuario;
import com.recetas.backend.domain.mapper.RecetaMapper;
import com.recetas.backend.domain.model.enums.Dificultad;
import com.recetas.backend.domain.repository.UsuarioRepository;
import com.recetas.backend.exception.UsuarioNoEncontradoException;
import com.recetas.backend.service.RecetaService;

/**
 * Controlador para la gestión de recetas, incluyendo funcionalidades sociales
 * como "me gusta" y comentarios.
 */
@RestController
@RequestMapping("/api/recetas")
public class RecetaController {

    private final RecetaService recetaService;
    private final UsuarioRepository usuarioRepository;
    private final RecetaMapper recetaMapper;

    public RecetaController(RecetaService recetaService, UsuarioRepository usuarioRepository,
            RecetaMapper recetaMapper) {
        this.recetaService = recetaService;
        this.usuarioRepository = usuarioRepository;
        this.recetaMapper = recetaMapper;
    }

    /**
     * Permite a un usuario dar "me gusta" a una receta.
     *
     * @param recetaId    El ID de la receta a la que se da "me gusta".
     * @param userDetails El ID del usuario que da "me gusta".
     * @return ResponseEntity indicando el resultado de la operación.
     */
    @PostMapping("/{recetaId}/like")
    public ResponseEntity<Void> darMeGusta(@PathVariable Integer recetaId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = usuarioRepository.findByNombreUsuario(userDetails.getUsername())
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));
        recetaService.darMeGusta(usuario.getId(), recetaId);
        return ResponseEntity.ok().build();
    }

    /**
     * Permite a un usuario quitar el "me gusta" de una receta.
     *
     * @param recetaId    El ID de la receta a la que se quita el "me gusta".
     * @param userDetails El ID del usuario que quita el "me gusta".
     * @return ResponseEntity indicando el resultado de la operación.
     */
    @DeleteMapping("/{recetaId}/like")
    public ResponseEntity<Void> quitarMeGusta(@PathVariable Integer recetaId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = usuarioRepository.findByNombreUsuario(userDetails.getUsername())
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));
        recetaService.quitarMeGusta(usuario.getId(), recetaId);
        return ResponseEntity.ok().build();
    }

    /**
     * Añade un comentario a una receta.
     *
     * @param recetaId   El ID de la receta a la que se añade el comentario.
     * @param comentario El objeto Comentario a añadir.
     * @return ResponseEntity con el comentario guardado o un código de error.
     */
    @PostMapping("/{recetaId}/comments")
    public ResponseEntity<Comentario> agregarComentario(@PathVariable Integer recetaId,
            @RequestBody Comentario comentario, @AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = usuarioRepository.findByNombreUsuario(userDetails.getUsername())
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));
        Receta receta = recetaService.findById(recetaId);
        comentario.setReceta(receta);
        comentario.setUsuario(usuario);
        Comentario comentarioGuardado = recetaService.agregarComentario(comentario);
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

    // --- CRUD de Recetas ---

    @PostMapping
    public ResponseEntity<Receta> crearReceta(@RequestBody RecetaRequestDto recetaDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = usuarioRepository.findByNombreUsuario(userDetails.getUsername())
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));
        Receta nuevaReceta = recetaMapper.toEntity(recetaDto);
        nuevaReceta.setUsuario(usuario);
        Receta recetaGuardada = recetaService.guardarReceta(nuevaReceta);
        return ResponseEntity.status(HttpStatus.CREATED).body(recetaGuardada);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Receta> obtenerRecetaPorId(@PathVariable Integer id) {
        return recetaService.obtenerRecetaPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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

    @PutMapping("/{id}")
    public ResponseEntity<Receta> actualizarReceta(@PathVariable Integer id, @RequestBody RecetaRequestDto recetaDto,
            @AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = usuarioRepository.findByNombreUsuario(userDetails.getUsername())
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));
        Receta recetaExistente = recetaService.findById(id);
        if (recetaExistente == null || !recetaExistente.getUsuario().getId().equals(usuario.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        // Mapear campos del DTO a la entidad existente
        recetaMapper.updateEntityFromDto(recetaDto, recetaExistente);
        Receta recetaActualizada = recetaService.guardarReceta(recetaExistente);
        return ResponseEntity.ok(recetaActualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarReceta(@PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {
        Usuario usuario = usuarioRepository.findByNombreUsuario(userDetails.getUsername())
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));
        Receta recetaExistente = recetaService.findById(id);
        if (recetaExistente == null || !recetaExistente.getUsuario().getId().equals(usuario.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        recetaService.eliminarReceta(id);
        return ResponseEntity.noContent().build();
    }
}
