package com.recetas.backend.controller;

import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.recetas.backend.domain.entity.Usuario;
import com.recetas.backend.service.UserService;

/**
 * Controlador para la gestión de usuarios y sus relaciones sociales.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Permite a un usuario seguir a otro usuario.
     *
     * @param seguidorId El ID del usuario que inicia el seguimiento.
     * @param seguidoId  El ID del usuario que será seguido.
     * @return ResponseEntity indicando el resultado de la operación.
     */
    @PostMapping("/{seguidorId}/follow/{seguidoId}")
    public ResponseEntity<Void> seguirUsuario(@PathVariable Integer seguidorId, @PathVariable Integer seguidoId) {
        try {
            userService.seguirUsuario(seguidorId, seguidoId);
            return ResponseEntity.ok().build(); // Éxito, sin contenido de respuesta
        } catch (IllegalArgumentException e) {
            // Manejar errores específicos como usuario no encontrado, ya se sigue, etc.
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // O un mensaje de error más descriptivo
        } catch (Exception e) {
            // Manejar otros errores inesperados
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Permite a un usuario dejar de seguir a otro usuario.
     *
     * @param seguidorId El ID del usuario que deja de seguir.
     * @param seguidoId  El ID del usuario que deja de ser seguido.
     * @return ResponseEntity indicando el resultado de la operación.
     */
    @DeleteMapping("/{seguidorId}/unfollow/{seguidoId}")
    public ResponseEntity<Void> dejarDeSeguirUsuario(@PathVariable Integer seguidorId,
            @PathVariable Integer seguidoId) {
        try {
            userService.dejarDeSeguirUsuario(seguidorId, seguidoId);
            return ResponseEntity.ok().build(); // Éxito, sin contenido de respuesta
        } catch (IllegalArgumentException e) {
            // Manejar errores específicos como usuario no encontrado, no se sigue, etc.
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null); // O un mensaje de error más descriptivo
        } catch (Exception e) {
            // Manejar otros errores inesperados
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Obtiene la lista de usuarios que siguen a un usuario específico.
     *
     * @param userId El ID del usuario cuyos seguidores se quieren obtener.
     * @return ResponseEntity con la lista de seguidores o un código de error.
     */
    @GetMapping("/{userId}/followers")
    public ResponseEntity<Set<Usuario>> obtenerSeguidores(@PathVariable Integer userId) {
        try {
            Set<Usuario> seguidores = userService.obtenerSeguidores(userId);
            return ResponseEntity.ok(seguidores);
        } catch (IllegalArgumentException e) {
            // Manejar error si el usuario no se encuentra
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            // Manejar otros errores inesperados
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Obtiene la lista de usuarios a los que sigue un usuario específico.
     *
     * @param userId El ID del usuario cuyos seguidos se quieren obtener.
     * @return ResponseEntity con la lista de seguidos o un código de error.
     */
    @GetMapping("/{userId}/following")
    public ResponseEntity<Set<Usuario>> obtenerSeguidos(@PathVariable Integer userId) {
        try {
            Set<Usuario> seguidos = userService.obtenerSeguidos(userId);
            return ResponseEntity.ok(seguidos);
        } catch (IllegalArgumentException e) {
            // Manejar error si el usuario no se encuentra
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            // Manejar otros errores inesperados
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
