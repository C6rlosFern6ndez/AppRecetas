package com.recetas.backend.controllers;

import com.recetas.backend.dtos.NotificacionDto;
import com.recetas.backend.services.NotificacionService;
import com.recetas.backend.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    @Autowired
    private NotificacionService notificacionService;

    // Obtener notificaciones del usuario autenticado
    @GetMapping
    public ResponseEntity<Page<NotificacionDto>> getNotificacionesByUsuario(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(notificacionService.getNotificacionesByUsuario(userDetails.getId(), pageable));
    }

    // Marcar notificación como leída
    @PutMapping("/{id}/leida")
    public ResponseEntity<NotificacionDto> marcarComoLeida(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.ok(notificacionService.marcarComoLeida(id, userDetails.getId()));
    }

    // Marcar todas las notificaciones como leídas
    @PutMapping("/marcar-todas-leidas")
    public ResponseEntity<Void> marcarTodasComoLeidas(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        notificacionService.marcarTodasComoLeidas(userDetails.getId());
        return ResponseEntity.noContent().build();
    }

    // Eliminar notificación
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotificacion(
            @PathVariable Integer id,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        notificacionService.deleteNotificacion(id, userDetails.getId());
        return ResponseEntity.noContent().build();
    }
}
