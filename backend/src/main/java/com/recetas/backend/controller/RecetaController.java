package com.recetas.backend.controller;

import java.util.List;
import java.util.Set;

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
import org.springframework.web.bind.annotation.RestController;

import com.recetas.backend.domain.dto.RecetaRequestDto;
import com.recetas.backend.domain.entity.Comentario;
import com.recetas.backend.domain.entity.Receta;
import com.recetas.backend.domain.entity.Usuario;
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

    public RecetaController(RecetaService recetaService, UsuarioRepository usuarioRepository) {
        this.recetaService = recetaService;
        this.usuarioRepository = usuarioRepository;
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
        Receta nuevaReceta = new Receta();
        // Aquí se mapearía desde el DTO a la entidad.
        // Por simplicidad, asumimos que el DTO tiene los campos necesarios.
        nuevaReceta.setTitulo(recetaDto.getTitulo());
        nuevaReceta.setDescripcion(recetaDto.getDescripcion());
        nuevaReceta.setTiempoPreparacion(recetaDto.getTiempoPreparacion());
        nuevaReceta.setDificultad(recetaDto.getDificultad());
        nuevaReceta.setPorciones(recetaDto.getPorciones());
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

    @GetMapping
    public ResponseEntity<List<Receta>> obtenerTodasLasRecetas() {
        List<Receta> recetas = recetaService.obtenerTodasLasRecetas();
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
        recetaExistente.setTitulo(recetaDto.getTitulo());
        recetaExistente.setDescripcion(recetaDto.getDescripcion());
        // ... otros campos
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
