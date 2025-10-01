package com.recetas.backend.controller;

import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.recetas.backend.domain.entity.Usuario;
import com.recetas.backend.domain.repository.UsuarioRepository;
import com.recetas.backend.exception.UsuarioNoEncontradoException;
import com.recetas.backend.service.UserService;

/**
 * Controlador para la gestión de usuarios y sus relaciones sociales.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final UsuarioRepository usuarioRepository;

    public UserController(UserService userService, UsuarioRepository usuarioRepository) {
        this.userService = userService;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Permite al usuario autenticado seguir a otro usuario.
     *
     * @param seguidoId   El ID del usuario que será seguido.
     * @param userDetails Los detalles del usuario autenticado.
     * @return ResponseEntity indicando el resultado de la operación.
     */
    @PostMapping("/{seguidoId}/follow")
    public ResponseEntity<Void> seguirUsuario(@PathVariable Integer seguidoId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Usuario seguidor = usuarioRepository.findByNombreUsuario(userDetails.getUsername())
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));
        userService.seguirUsuario(seguidor.getId(), seguidoId);
        return ResponseEntity.ok().build();
    }

    /**
     * Permite al usuario autenticado dejar de seguir a otro usuario.
     *
     * @param seguidoId   El ID del usuario que se dejará de seguir.
     * @param userDetails Los detalles del usuario autenticado.
     * @return ResponseEntity indicando el resultado de la operación.
     */
    @DeleteMapping("/{seguidoId}/unfollow")
    public ResponseEntity<Void> dejarDeSeguirUsuario(@PathVariable Integer seguidoId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Usuario seguidor = usuarioRepository.findByNombreUsuario(userDetails.getUsername())
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado"));
        userService.dejarDeSeguirUsuario(seguidor.getId(), seguidoId);
        return ResponseEntity.ok().build();
    }

    /**
     * Obtiene la lista de usuarios que siguen a un usuario específico.
     *
     * @param userId El ID del usuario cuyos seguidores se quieren obtener.
     * @return ResponseEntity con la lista de seguidores.
     */
    @GetMapping("/{userId}/followers")
    public ResponseEntity<Set<Usuario>> obtenerSeguidores(@PathVariable Integer userId) {
        Set<Usuario> seguidores = userService.obtenerSeguidores(userId);
        return ResponseEntity.ok(seguidores);
    }

    /**
     * Obtiene la lista de usuarios a los que sigue un usuario específico.
     *
     * @param userId El ID del usuario cuyos seguidos se quieren obtener.
     * @return ResponseEntity con la lista de seguidos.
     */
    @GetMapping("/{userId}/following")
    public ResponseEntity<Set<Usuario>> obtenerSeguidos(@PathVariable Integer userId) {
        Set<Usuario> seguidos = userService.obtenerSeguidos(userId);
        return ResponseEntity.ok(seguidos);
    }
}
