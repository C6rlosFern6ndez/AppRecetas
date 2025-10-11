package com.recetas.backend.controllers;

import com.recetas.backend.dtos.UsuarioDto;
import com.recetas.backend.services.UsuarioService;
import com.recetas.backend.security.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // Obtener todos los usuarios paginados
    @GetMapping
    public ResponseEntity<Page<UsuarioDto>> getAllUsuarios(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(usuarioService.getAllUsuarios(pageable));
    }

    // Obtener usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDto> getUsuarioById(@PathVariable Integer id) {
        return ResponseEntity.ok(usuarioService.getUsuarioById(id));
    }

    // Actualizar usuario
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDto> updateUsuario(
            @PathVariable Integer id,
            @Valid @RequestBody UsuarioDto usuarioDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // Asegurarse de que el usuario autenticado solo pueda actualizar su propio
        // perfil
        if (!id.equals(userDetails.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(usuarioService.updateUsuario(id, usuarioDto));
    }

    // Eliminar usuario (solo ADMIN o SUPERADMIN)
    @PreAuthorize("hasAnyAuthority('ADMIN', 'SUPERADMIN')")
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Integer id) {
        usuarioService.deleteUsuario(id);
        return ResponseEntity.noContent().build();
    }

    // Seguir a un usuario
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/seguir")
    public ResponseEntity<Void> followUsuario(
            @PathVariable("id") Integer seguidoId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // El seguidor es el usuario autenticado
        usuarioService.followUsuario(userDetails.getId(), seguidoId);
        return ResponseEntity.noContent().build();
    }

    // Dejar de seguir a un usuario
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}/seguir")
    public ResponseEntity<Void> unfollowUsuario(
            @PathVariable("id") Integer seguidoId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // El seguidor es el usuario autenticado
        usuarioService.unfollowUsuario(userDetails.getId(), seguidoId);
        return ResponseEntity.noContent().build();
    }

    // Obtener seguidores de un usuario
    @GetMapping("/{id}/seguidores")
    public ResponseEntity<Page<UsuarioDto>> getSeguidores(
            @PathVariable Integer id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(usuarioService.getSeguidores(id, pageable));
    }

    // Obtener usuarios que sigue un usuario
    @GetMapping("/{id}/siguiendo")
    public ResponseEntity<Page<UsuarioDto>> getSiguiendo(
            @PathVariable Integer id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(usuarioService.getSiguiendo(id, pageable));
    }
}
