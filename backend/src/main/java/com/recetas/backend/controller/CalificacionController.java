package com.recetas.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.recetas.backend.domain.entity.Usuario;
import com.recetas.backend.domain.repository.UsuarioRepository;
import com.recetas.backend.service.CalificacionService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * Controlador para gestionar las calificaciones de las recetas.
 */
@RestController
@RequestMapping("/calificaciones")
@Tag(name = "Calificaciones")
@Slf4j
public class CalificacionController {

    private final CalificacionService calificacionService;
    private final UsuarioRepository usuarioRepository;

    public CalificacionController(CalificacionService calificacionService, UsuarioRepository usuarioRepository) {
        this.calificacionService = calificacionService;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Endpoint para que un usuario califique una receta.
     * Si ya existe una calificación, se actualiza.
     *
     * @param recetaId    El ID de la receta a calificar.
     * @param puntuacion  La puntuación (ej. 1-5).
     * @param userDetails Los detalles del usuario autenticado.
     * @return Una respuesta vacía con estado 201 (CREATED) si es exitoso.
     * @throws Exception
     */
    @PostMapping("/receta/{recetaId}")
    public ResponseEntity<Void> calificarReceta(
            @PathVariable Long recetaId,
            @RequestParam Integer puntuacion,
            @AuthenticationPrincipal UserDetails userDetails) {

        log.info("Petición para calificar la receta con ID: {} con una puntuación de: {}", recetaId, puntuacion);
        Usuario usuario = usuarioRepository.findByNombreUsuario(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        calificacionService.calificarReceta(usuario.getId(), recetaId.intValue(), puntuacion);

        log.info("Receta con ID: {} calificada correctamente por el usuario: {}", recetaId, usuario.getNombreUsuario());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Endpoint para eliminar la calificación de una receta.
     *
     * @param recetaId    El ID de la receta cuya calificación se eliminará.
     * @param userDetails Los detalles del usuario autenticado.
     * @return Una respuesta vacía con estado 204 (NO CONTENT) si es exitoso.
     * @throws Exception
     */
    @DeleteMapping("/receta/{recetaId}")
    public ResponseEntity<Void> eliminarCalificacion(
            @PathVariable Long recetaId,
            @AuthenticationPrincipal UserDetails userDetails) {

        log.info("Petición para eliminar la calificación de la receta con ID: {}", recetaId);
        Usuario usuario = usuarioRepository.findByNombreUsuario(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        calificacionService.eliminarCalificacion(usuario.getId(), recetaId.intValue());

        log.info("Calificación de la receta con ID: {} eliminada correctamente por el usuario: {}", recetaId,
                usuario.getNombreUsuario());
        return ResponseEntity.noContent().build();
    }
}
