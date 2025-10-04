package com.recetas.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import com.recetas.backend.domain.entity.Notificacion;
import com.recetas.backend.domain.entity.Usuario;
import com.recetas.backend.domain.repository.UsuarioRepository;
import com.recetas.backend.service.NotificacionService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/notificaciones")
@Tag(name = "Notificaciones")
@Slf4j
public class NotificacionController {

    private final NotificacionService notificacionService;
    private final UsuarioRepository usuarioRepository;

    public NotificacionController(NotificacionService notificacionService, UsuarioRepository usuarioRepository) {
        this.notificacionService = notificacionService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public ResponseEntity<List<Notificacion>> obtenerNotificaciones(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("Petición para obtener las notificaciones del usuario: {}", userDetails.getUsername());
        Usuario usuario = usuarioRepository.findByNombreUsuario(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        List<Notificacion> notificaciones = notificacionService.obtenerNotificacionesUsuario(usuario.getId());
        log.info("Se han encontrado {} notificaciones para el usuario: {}", notificaciones.size(),
                userDetails.getUsername());
        return ResponseEntity.ok(notificaciones);
    }

    @PostMapping("/{id}/leida")
    public ResponseEntity<Void> marcarComoLeida(@PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Petición para marcar la notificación con ID: {} como leída", id);
        notificacionService.marcarComoLeida(id);
        log.info("Notificación con ID: {} marcada como leída", id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarNotificacion(@PathVariable Integer id,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Petición para eliminar la notificación con ID: {}", id);
        notificacionService.eliminarNotificacion(id);
        log.info("Notificación con ID: {} eliminada", id);
        return ResponseEntity.noContent().build();
    }
}
