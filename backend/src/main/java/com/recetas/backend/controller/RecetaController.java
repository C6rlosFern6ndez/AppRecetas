package com.recetas.backend.controller;

import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.recetas.backend.domain.entity.Comentario;
import com.recetas.backend.domain.entity.Receta;
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
     * @param recetaId  El ID de la receta a la que se da "me gusta".
     * @param usuarioId El ID del usuario que da "me gusta".
     * @return ResponseEntity indicando el resultado de la operación.
     */
    @PostMapping("/{recetaId}/like/{usuarioId}")
    public ResponseEntity<Void> darMeGusta(@PathVariable Integer recetaId, @PathVariable Integer usuarioId) {
        try {
            recetaService.darMeGusta(usuarioId, recetaId);
            return ResponseEntity.ok().build(); // Éxito, sin contenido de respuesta
        } catch (IllegalArgumentException e) {
            // Manejar errores específicos como usuario o receta no encontrados, ya dio me
            // gusta, etc.
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // O un mensaje de error más
                                                                          // descriptivo
        } catch (Exception e) {
            // Manejar otros errores inesperados
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Permite a un usuario quitar el "me gusta" de una receta.
     *
     * @param recetaId  El ID de la receta a la que se quita el "me gusta".
     * @param usuarioId El ID del usuario que quita el "me gusta".
     * @return ResponseEntity indicando el resultado de la operación.
     */
    @DeleteMapping("/{recetaId}/unlike/{usuarioId}")
    public ResponseEntity<Void> quitarMeGusta(@PathVariable Integer recetaId, @PathVariable Integer usuarioId) {
        try {
            recetaService.quitarMeGusta(usuarioId, recetaId);
            return ResponseEntity.ok().build(); // Éxito, sin contenido de respuesta
        } catch (IllegalArgumentException e) {
            // Manejar errores específicos como usuario o receta no encontrados, no dio me
            // gusta, etc.
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // O un mensaje de error más
                                                                          // descriptivo
        } catch (Exception e) {
            // Manejar otros errores inesperados
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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
            @RequestBody Comentario comentario) {
        try {
            // Asignar la receta al comentario
            Receta receta = recetaService.findById(recetaId);
            if (receta == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Receta no encontrada
            }
            comentario.setReceta(receta);

            // Asignar el usuario al comentario (esto debería obtenerse del contexto de
            // seguridad)
            // Por ahora, se asume que el comentario ya tiene el usuario asignado o se puede
            // obtener de alguna manera.
            // En una implementación real, se usaría Spring Security para obtener el usuario
            // autenticado.
            // Ejemplo: Usuario usuarioAutenticado =
            // userService.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
            // comentario.setUsuario(usuarioAutenticado);

            Comentario comentarioGuardado = recetaService.agregarComentario(comentario);
            return ResponseEntity.status(HttpStatus.CREATED).body(comentarioGuardado);
        } catch (IllegalArgumentException e) {
            // Manejar errores específicos como usuario o receta no encontrados, etc.
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // O un mensaje de error más descriptivo
        } catch (Exception e) {
            // Manejar otros errores inesperados
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Obtiene todos los comentarios de una receta específica.
     *
     * @param recetaId El ID de la receta cuyos comentarios se quieren obtener.
     * @return ResponseEntity con la lista de comentarios o un código de error.
     */
    @GetMapping("/{recetaId}/comments")
    public ResponseEntity<Set<Comentario>> obtenerComentariosDeReceta(@PathVariable Integer recetaId) {
        try {
            Set<Comentario> comentarios = recetaService.obtenerComentariosDeReceta(recetaId);
            return ResponseEntity.ok(comentarios);
        } catch (IllegalArgumentException e) {
            // Manejar error si la receta no se encuentra
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            // Manejar otros errores inesperados
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // Otros endpoints relacionados con recetas (CRUD, etc.) irían aquí.
    // Por ahora, nos centramos en las funcionalidades sociales.
}
