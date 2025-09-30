package com.recetas.backend.controller;

import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.recetas.backend.domain.entity.Comentario;
import com.recetas.backend.domain.entity.Receta;
import com.recetas.backend.domain.entity.Usuario;
import com.recetas.backend.service.RecetaService;

/**
 * Controlador para la gestión de recetas, incluyendo funcionalidades sociales
 * como "me gusta" y comentarios.
 */
@RestController
@RequestMapping("/api/recetas")
public class RecetaController {

    private final RecetaService recetaService;

    public RecetaController(RecetaService recetaService) {
        this.recetaService = recetaService;
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
        Usuario usuario = (Usuario) userDetails;
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
        Usuario usuario = (Usuario) userDetails;
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
        Usuario usuario = (Usuario) userDetails;
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

    // Otros endpoints relacionados con recetas (CRUD, etc.) irían aquí.
    // Por ahora, nos centramos en las funcionalidades sociales.
}
